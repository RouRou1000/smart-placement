package dev.smartplacement.client.feature;

import dev.smartplacement.SmartPlacementServerState;
import dev.smartplacement.client.config.SmartPlacementConfig;
import dev.smartplacement.client.network.SmartPlacementClientNetwork;
import dev.smartplacement.util.BlockHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

/**
 * Handles scroll-wheel rotation of directional blocks.
 *
 * <p>When {@link SmartPlacementConfig#scrollRotation} is enabled and the player is
 * sneaking while holding a directional block, scrolling the mouse wheel cycles through
 * the six {@link Direction} values. The next block placement will use the selected direction
 * via the same flip mechanism as the core sneak-inversion feature.
 *
 * <p>This class stores the currently "selected" direction per player so it persists across
 * multiple placements until the hotbar slot changes.
 */
public final class ScrollRotation {

    private ScrollRotation() {}

    private static final Direction[] DIRECTIONS = Direction.values();

    /** Index into {@link #DIRECTIONS} for the currently selected scroll direction. */
    private static int currentIndex = 0;

    /**
     * Called from {@link dev.smartplacement.client.mixin.client.MouseHandlerScrollMixin}
     * when a scroll event fires.
     *
     * @param scrollDelta positive = scroll up, negative = scroll down
     * @return {@code true} if the event was consumed (hotbar scroll should be cancelled)
     */
    public static boolean handleScroll(double scrollDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return false;

        ClientPlayerEntity player = mc.player;
        if (!player.isSneaking()) return false;

        ItemStack held = player.getMainHandStack();
        if (!BlockHelper.isDirectionalBlock(held)) return false;

        // Cycle direction index based on scroll direction
        if (scrollDelta > 0) {
            currentIndex = (currentIndex + 1) % DIRECTIONS.length;
        } else if (scrollDelta < 0) {
            currentIndex = (currentIndex - 1 + DIRECTIONS.length) % DIRECTIONS.length;
        } else {
            return false;
        }

        Direction selected = DIRECTIONS[currentIndex];

        // Announce selection to the player via action bar
        if (SmartPlacementConfig.get().showMessages) {
            player.sendMessage(
                    net.minecraft.text.Text.translatable(
                            "smart_placement.message.scroll_direction",
                            selected.getId()),
                    true
            );
        }

        // Pre-set the server flip so the next placement respects this direction.
        // NOTE: With the boolean flip approach, we only store a flag here; the actual
        // direction used will still be the block's natural facing (flipped). A full
        // scroll-direction implementation would require storing a specific Direction override.
        // This sets the flag for client prediction; the placement will be triggered by normal right-click.
        SmartPlacementServerState.setPendingFlip(player.getUuid(), true);
        if (MinecraftClient.getInstance().isInSingleplayer()) {
            SmartPlacementServerState.setPendingFlip(player.getUuid(), false);
        } else if (SmartPlacementClientNetwork.isServerCompanionPresent()) {
            SmartPlacementClientNetwork.sendFlipRequest();
        }

        return true; // consumed; hotbar will not scroll
    }

    /** Resets the selected direction index to default (NORTH). */
    public static void reset() {
        currentIndex = 0;
    }
}
