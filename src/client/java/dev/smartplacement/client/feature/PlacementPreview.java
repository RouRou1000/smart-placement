package dev.smartplacement.client.feature;

import dev.smartplacement.client.config.SmartPlacementConfig;
import dev.smartplacement.util.BlockHelper;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayers;
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
 * Renders a clean wireframe outline at the block position that will be placed
 * (with inverted FACING) while the player is sneaking and holding a directional block.
 */
public final class PlacementPreview {

    // Outline colour: cyan, slightly transparent
    private static final float R = 0.0f;
    private static final float G = 0.9f;
    private static final float B = 1.0f;
    private static final float A = 0.75f;

    // Small inward offset so the preview doesn't z-fight with the vanilla selection box
    private static final float EXPAND = 0.002f;

    private PlacementPreview() {}

    /** Registers the render callback. Call once from the client entrypoint. */
    public static void register() {
        WorldRenderEvents.END_MAIN.register(PlacementPreview::render);
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

        // Action-bar direction hint
        if (cfg.showMessages) {
            Direction naturalFacing = player.getHorizontalFacing();
            if (Math.abs(player.getPitch()) > 45.0f) {
                naturalFacing = player.getPitch() > 0 ? Direction.DOWN : Direction.UP;
            }
            player.sendMessage(
                    Text.translatable("smart_placement.message.preview_facing",
                            naturalFacing.getOpposite().getId().toUpperCase()),
                    true);
        }

        // Wireframe outline
        VertexConsumerProvider consumers = ctx.consumers();
        if (consumers == null) return;

        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        Vec3d cam = camera.getCameraPos();
        MatrixStack matrices = ctx.matrices();
        matrices.push();
        matrices.translate(
                targetPos.getX() - cam.x,
                targetPos.getY() - cam.y,
                targetPos.getZ() - cam.z);

        float lo = -EXPAND;
        float hi = 1.0f + EXPAND;
        VertexConsumer lines = consumers.getBuffer(RenderLayers.lines());
        drawBoxEdges(matrices, lines, lo, lo, lo, hi, hi, hi);

        matrices.pop();
    }

    /**
     * Draws all 12 edges of an axis-aligned box from (x0,y0,z0) to (x1,y1,z1).
     * Uses the LINES render layer — each edge is two vertices with matching normals.
     */
    private static void drawBoxEdges(MatrixStack matrices, VertexConsumer vc,
                                     float x0, float y0, float z0,
                                     float x1, float y1, float z1) {
        MatrixStack.Entry e = matrices.peek();
        // Bottom face
        line(vc, e, x0, y0, z0, x1, y0, z0,  1, 0, 0);
        line(vc, e, x1, y0, z0, x1, y0, z1,  0, 0, 1);
        line(vc, e, x1, y0, z1, x0, y0, z1, -1, 0, 0);
        line(vc, e, x0, y0, z1, x0, y0, z0,  0, 0,-1);
        // Top face
        line(vc, e, x0, y1, z0, x1, y1, z0,  1, 0, 0);
        line(vc, e, x1, y1, z0, x1, y1, z1,  0, 0, 1);
        line(vc, e, x1, y1, z1, x0, y1, z1, -1, 0, 0);
        line(vc, e, x0, y1, z1, x0, y1, z0,  0, 0,-1);
        // Vertical edges
        line(vc, e, x0, y0, z0, x0, y1, z0,  0, 1, 0);
        line(vc, e, x1, y0, z0, x1, y1, z0,  0, 1, 0);
        line(vc, e, x1, y0, z1, x1, y1, z1,  0, 1, 0);
        line(vc, e, x0, y0, z1, x0, y1, z1,  0, 1, 0);
    }

    /** Emits two vertices forming one line segment. Normal points from start → end. */
    private static void line(VertexConsumer vc, MatrixStack.Entry e,
                             float x0, float y0, float z0,
                             float x1, float y1, float z1,
                             float nx, float ny, float nz) {
        vc.vertex(e, x0, y0, z0).color(R, G, B, A).normal(e, nx, ny, nz).lineWidth(2.5f);
        vc.vertex(e, x1, y1, z1).color(R, G, B, A).normal(e, nx, ny, nz).lineWidth(2.5f);
    }
}

