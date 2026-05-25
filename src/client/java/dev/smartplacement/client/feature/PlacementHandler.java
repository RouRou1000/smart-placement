package dev.smartplacement.client.feature;

import dev.smartplacement.SmartPlacementMod;
import dev.smartplacement.SmartPlacementServerState;
import dev.smartplacement.client.config.SmartPlacementConfig;
import dev.smartplacement.client.keybind.SmartPlacementKeys;
import dev.smartplacement.client.network.SmartPlacementClientNetwork;
import dev.smartplacement.util.BlockHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

/**
 * Central controller for client-side placement inversion logic.
 *
 * <p>Called from {@link dev.smartplacement.client.mixin.client.ClientPlayerInteractionMixin}
 * immediately before and after {@code ClientPlayerInteractionManager.interactBlock()}.
 *
 * <h3>Flow summary</h3>
 * <ol>
 *   <li>{@link #handleBeforePlacement} — decide whether to invert, set the flip flag,
 *       send C2S packet or apply the rotation trick as appropriate.
 *   <li>Vanilla {@code interactBlock()} runs, consuming the flip flag in
 *       {@code BlockItemServerMixin} for client-side prediction.
 *   <li>{@link #handleAfterPlacement} — restore player rotation if the trick was used,
 *       clear any residual state.
 * </ol>
 */
public final class PlacementHandler {

    private PlacementHandler() {}

    // -------------------------------------------------------------------------
    // Per-placement transient state (reset after every call pair)
    // -------------------------------------------------------------------------

    /** Whether the rotation trick was applied for the current placement. */
    private static boolean rotationTrickActive = false;
    /** Saved pitch before rotation trick. */
    private static float savedPitch;
    /** Saved yaw before rotation trick. */
    private static float savedYaw;

    // -------------------------------------------------------------------------
    // Keybind-toggle state
    // -------------------------------------------------------------------------

    private static boolean keybindToggleActive = false;

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Called at the HEAD of {@code ClientPlayerInteractionManager.interactBlock()}.
     * Applies inversion logic if all conditions are met.
     */
    public static void handleBeforePlacement(ClientPlayerEntity player,
                                             Hand hand,
                                             BlockHitResult hitResult) {
        // Reset transient state from any previous (possibly failed) placement
        rotationTrickActive = false;

        SmartPlacementConfig cfg = SmartPlacementConfig.get();
        if (!cfg.enabled) return;

        // Handle keybind toggle
        if (SmartPlacementKeys.TOGGLE_INVERSION != null && cfg.invertOnKeybind) {
            while (SmartPlacementKeys.TOGGLE_INVERSION.wasPressed()) {
                keybindToggleActive = !keybindToggleActive;
            }
        }

        // Determine whether inversion should trigger
        boolean shouldInvert = (cfg.sneakInversion && player.isSneaking())
                || (cfg.invertOnKeybind && keybindToggleActive);

        if (!shouldInvert) return;

        // Check that the player is holding a directional block we care about
        ItemStack held = player.getStackInHand(hand);
        if (!shouldInvertBlock(held, cfg)) return;

        if (cfg.debugLogging) {
            SmartPlacementMod.LOGGER.debug(
                    "[SmartPlacement] Inverting placement for {} (hand={})", player.getName().getString(), hand);
        }

        // --- Client-side prediction flip ---
        SmartPlacementServerState.setPendingFlip(player.getUuid(), true);

        boolean singleplayer = MinecraftClient.getInstance().isInSingleplayer();

        if (singleplayer) {
            // Integrated server shares the JVM — set server flip directly
            SmartPlacementServerState.setPendingFlip(player.getUuid(), false);
        } else if (SmartPlacementClientNetwork.isServerCompanionPresent()) {
            // Dedicated server with companion mod — reliable C2S packet
            SmartPlacementClientNetwork.sendFlipRequest();
        } else {
            // Vanilla server fallback — temporarily mirror player rotation
            applyRotationTrick(player);
        }

        // Optional feedback
        if (cfg.showMessages) {
            Text msg = Text.translatable("smart_placement.message.inverted");
            if (cfg.useActionBar) {
                player.sendMessage(msg, true);
            }
        }
    }

    /**
     * Called at the RETURN of {@code ClientPlayerInteractionManager.interactBlock()}.
     * Cleans up any transient state.
     */
    public static void handleAfterPlacement(ClientPlayerEntity player) {
        if (rotationTrickActive) {
            restoreRotation(player);
        }
        // Clear any leftover client flip (in case placement was cancelled/failed)
        SmartPlacementServerState.consumePendingFlip(player.getUuid(), true);
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    /**
     * Returns true if the given item stack is a directional block that should be
     * inverted according to the current per-block config.
     */
    private static boolean shouldInvertBlock(ItemStack stack, SmartPlacementConfig cfg) {
        if (stack.isEmpty()) return false;

        // Check per-block toggles first (more specific wins)
        if (stack.getItem() instanceof net.minecraft.item.BlockItem bi) {
            net.minecraft.block.Block block = bi.getBlock();

            if (BlockHelper.isObserver(block))     return cfg.perBlock_observer;
            if (BlockHelper.isRegularPiston(block)) return cfg.perBlock_piston;
            if (BlockHelper.isStickyPiston(block)) return cfg.perBlock_stickyPiston;
            if (BlockHelper.isDispenser(block))    return cfg.perBlock_dispenser;
            if (BlockHelper.isDropper(block))      return cfg.perBlock_dropper;
            if (BlockHelper.isCrafter(block))      return cfg.perBlock_crafter;
        }

        // Fall through to catch-all
        return cfg.perBlock_allDirectional && BlockHelper.isDirectionalBlock(stack);
    }

    /** Saves player rotation and applies the mirrored rotation for the trick. */
    private static void applyRotationTrick(ClientPlayerEntity player) {
        savedPitch = player.getPitch();
        savedYaw   = player.getYaw();

        // Mirror: negate pitch (flips vertical) and add 180° (flips horizontal)
        player.setPitch(-savedPitch);
        player.setYaw(savedYaw + 180.0f);
        rotationTrickActive = true;
    }

    /** Restores saved rotation after the rotation trick. */
    private static void restoreRotation(ClientPlayerEntity player) {
        player.setPitch(savedPitch);
        player.setYaw(savedYaw);
        rotationTrickActive = false;
    }
}
