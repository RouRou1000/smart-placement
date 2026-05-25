package dev.smartplacement.client.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.smartplacement.client.config.SmartPlacementConfig;
import dev.smartplacement.util.BlockHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/**
 * MC 1.21.5 – 1.21.10 version: cyan wireframe box + action-bar hint.
 * Uses WorldRenderEvents.LAST (old package) and RenderLayer.getLines()
 * instead of the 1.21.11 APIs (WorldRenderEvents.END_MAIN, RenderLayers.lines()).
 * Uses Direction.asString() — getName() was removed in 1.21.5.
 */
public final class PlacementPreview {

    private static final float R = 0.0f;
    private static final float G = 0.9f;
    private static final float B = 1.0f;
    private static final float A = 0.75f;
    private static final float EXPAND = 0.002f;

    private PlacementPreview() {}

    public static void register() {
        WorldRenderEvents.LAST.register(PlacementPreview::render);
    }

    private static void render(WorldRenderContext ctx) {
        SmartPlacementConfig cfg = SmartPlacementConfig.get();
        if (!cfg.enabled || !cfg.showPreview) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null || !player.isSneaking()) return;

        ItemStack held = player.getMainHandStack();
        if (!BlockHelper.isDirectionalBlock(held) && !BlockHelper.isStairs(held)) return;

        HitResult hit = mc.crosshairTarget;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) return;
        BlockHitResult blockHit = (BlockHitResult) hit;
        BlockPos targetPos = blockHit.getBlockPos().offset(blockHit.getSide());

        if (cfg.showMessages) {
            Direction naturalFacing = player.getHorizontalFacing();
            if (Math.abs(player.getPitch()) > 45.0f) {
                naturalFacing = player.getPitch() > 0 ? Direction.DOWN : Direction.UP;
            }
            player.sendMessage(
                    Text.translatable("smart_placement.message.preview_facing",
                            naturalFacing.getOpposite().asString().toUpperCase()),
                    true);
        }

        VertexConsumerProvider consumers = ctx.consumers();
        if (consumers == null) return;

        Camera camera = ctx.camera();
        Vec3d cam = camera.getPos();
        MatrixStack matrices = ctx.matrixStack();
        matrices.push();
        matrices.translate(
                targetPos.getX() - cam.x,
                targetPos.getY() - cam.y,
                targetPos.getZ() - cam.z);

        float lo = -EXPAND;
        float hi = 1.0f + EXPAND;

        RenderSystem.lineWidth(2.5f);
        VertexConsumer lines = consumers.getBuffer(RenderLayer.getLines());
        drawBoxEdges(matrices, lines, lo, lo, lo, hi, hi, hi);

        matrices.pop();
    }

    private static void drawBoxEdges(MatrixStack matrices, VertexConsumer vc,
                                     float x0, float y0, float z0,
                                     float x1, float y1, float z1) {
        MatrixStack.Entry e = matrices.peek();
        line(vc, e, x0, y0, z0, x1, y0, z0,  1, 0, 0);
        line(vc, e, x1, y0, z0, x1, y0, z1,  0, 0, 1);
        line(vc, e, x1, y0, z1, x0, y0, z1, -1, 0, 0);
        line(vc, e, x0, y0, z1, x0, y0, z0,  0, 0,-1);
        line(vc, e, x0, y1, z0, x1, y1, z0,  1, 0, 0);
        line(vc, e, x1, y1, z0, x1, y1, z1,  0, 0, 1);
        line(vc, e, x1, y1, z1, x0, y1, z1, -1, 0, 0);
        line(vc, e, x0, y1, z1, x0, y1, z0,  0, 0,-1);
        line(vc, e, x0, y0, z0, x0, y1, z0,  0, 1, 0);
        line(vc, e, x1, y0, z0, x1, y1, z0,  0, 1, 0);
        line(vc, e, x1, y0, z1, x1, y1, z1,  0, 1, 0);
        line(vc, e, x0, y0, z1, x0, y1, z1,  0, 1, 0);
    }

    private static void line(VertexConsumer vc, MatrixStack.Entry e,
                             float x0, float y0, float z0,
                             float x1, float y1, float z1,
                             float nx, float ny, float nz) {
        vc.vertex(e, x0, y0, z0).color(R, G, B, A).normal(e, nx, ny, nz);
        vc.vertex(e, x1, y1, z1).color(R, G, B, A).normal(e, nx, ny, nz);
    }
}
