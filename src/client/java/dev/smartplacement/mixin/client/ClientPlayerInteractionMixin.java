package dev.smartplacement.mixin.client;

import dev.smartplacement.client.feature.PlacementHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Client mixin that hooks into {@link ClientPlayerInteractionManager#interactBlock} to
 * apply the Smart Placement inversion logic immediately before the vanilla placement packet
 * is sent, and to restore state immediately after.
 */
@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionMixin {

    /**
     * HEAD inject — called before any placement logic runs.
     * Registers the pending flip and/or applies the rotation trick.
     */
    @Inject(
            method = "interactBlock",
            at = @At("HEAD")
    )
    private void smartPlacement_beforeInteract(ClientPlayerEntity player,
                                               Hand hand,
                                               BlockHitResult hitResult,
                                               CallbackInfoReturnable<ActionResult> cir) {
        PlacementHandler.handleBeforePlacement(player, hand, hitResult);
    }

    /**
     * RETURN inject — called after the placement attempt (whether successful or not).
     * Restores player rotation if the rotation trick was in use.
     */
    @Inject(
            method = "interactBlock",
            at = @At("RETURN")
    )
    private void smartPlacement_afterInteract(ClientPlayerEntity player,
                                              Hand hand,
                                              BlockHitResult hitResult,
                                              CallbackInfoReturnable<ActionResult> cir) {
        PlacementHandler.handleAfterPlacement(player);
    }
}
