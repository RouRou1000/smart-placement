package dev.smartplacement.mixin.client;

import dev.smartplacement.client.config.SmartPlacementConfig;
import dev.smartplacement.client.feature.ScrollRotation;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Intercepts mouse scroll events so that, when Scroll Rotation is enabled and the
 * player is sneaking while holding a directional block, the scroll wheel cycles through
 * the block's possible facing directions instead of scrolling the hotbar.
 */
@Mixin(Mouse.class)
public abstract class MouseHandlerScrollMixin {

    @Inject(
            method = "onMouseScroll",
            at = @At("HEAD"),
            cancellable = true
    )
    private void smartPlacement_onScroll(long window, double horizontal, double vertical,
                                         CallbackInfo ci) {
        if (!SmartPlacementConfig.get().scrollRotation) return;
        if (ScrollRotation.handleScroll(vertical)) {
            ci.cancel(); // prevent hotbar scroll
        }
    }
}
