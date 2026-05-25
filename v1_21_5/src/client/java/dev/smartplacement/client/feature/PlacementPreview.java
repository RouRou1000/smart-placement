package dev.smartplacement.client.feature;

import dev.smartplacement.client.config.SmartPlacementConfig;
import dev.smartplacement.util.BlockHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;

/**
 * MC 1.21.5 – 1.21.10 version: action-bar direction hint only.
 * The wireframe outline preview requires render-pipeline APIs (RenderLayers.lines(),
 * Camera.getCameraPos(), WorldRenderContext.matrices()) first available in MC 1.21.11.
 */
public final class PlacementPreview {

    private PlacementPreview() {}

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(PlacementPreview::onTick);
    }

    private static void onTick(MinecraftClient mc) {
        SmartPlacementConfig cfg = SmartPlacementConfig.get();
        if (!cfg.enabled) return;

        ClientPlayerEntity player = mc.player;
        if (player == null || !player.isSneaking()) return;

        ItemStack held = player.getMainHandStack();
        if (!BlockHelper.isDirectionalBlock(held) && !BlockHelper.isStairs(held)) return;

        HitResult hit = mc.crosshairTarget;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) return;

        // Action-bar hint: show which direction the block will face after inversion
        if (cfg.showMessages) {
            Direction natural = player.getHorizontalFacing();
            if (Math.abs(player.getPitch()) > 45.0f) {
                natural = player.getPitch() > 0 ? Direction.DOWN : Direction.UP;
            }
            player.sendMessage(
                    Text.translatable("smart_placement.message.preview_facing",
                            natural.getOpposite().asString().toUpperCase()),
                    true  // action bar
            );
        }
        // Note: block-outline wireframe preview requires MC 1.21.11+
    }
}
