package dev.smartplacement;

import dev.smartplacement.network.FlipPlacementC2SPayload;
import dev.smartplacement.network.SmartPlacementCapabilityS2CPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Server-side (and common) mod initializer.
 *
 * <p>Registers custom networking payload types and their handlers.
 * Client-only logic lives in {@code SmartPlacementClient}.
 */
public class SmartPlacementMod implements ModInitializer {

    public static final String MOD_ID = "smart_placement";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // --- Payload type registration (must happen on both sides) ---

        // C2S: client asks server to flip the next block placement
        PayloadTypeRegistry.playC2S().register(
                FlipPlacementC2SPayload.ID,
                FlipPlacementC2SPayload.CODEC
        );

        // S2C: server announces that the companion mod is installed
        PayloadTypeRegistry.playS2C().register(
                SmartPlacementCapabilityS2CPayload.ID,
                SmartPlacementCapabilityS2CPayload.CODEC
        );

        // --- Server-side receivers ---

        // When client sends a flip request, mark the player for server-side FACING inversion.
        // The actual flip is applied in BlockItemServerMixin on the very next BlockItem.place() call.
        ServerPlayNetworking.registerGlobalReceiver(FlipPlacementC2SPayload.ID, (payload, ctx) -> {
            ctx.server().execute(() ->
                    SmartPlacementServerState.setPendingFlip(ctx.player().getUuid(), false)
            );
        });

        // When a player joins, tell their client that this server has Smart Placement installed.
        // The client will then use the reliable C2S packet flow instead of the rotation trick.
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                ServerPlayNetworking.send(handler.player, new SmartPlacementCapabilityS2CPayload())
        );

        // Clean up any leftover pending flips when a player disconnects (prevents state leaks).
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                SmartPlacementServerState.clearAll(handler.player.getUuid())
        );

        LOGGER.info("[SmartPlacement] Server/common init complete");
    }
}
