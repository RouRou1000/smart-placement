package dev.smartplacement.client.network;

import dev.smartplacement.SmartPlacementServerState;
import dev.smartplacement.network.FlipPlacementC2SPayload;
import dev.smartplacement.network.SmartPlacementCapabilityS2CPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * Manages client-side networking state and S2C packet handling.
 *
 * <p>Registers the S2C capability receiver and provides the helper used by
 * {@link dev.smartplacement.client.feature.PlacementHandler} to send flip requests.
 */
public final class SmartPlacementClientNetwork {

    private SmartPlacementClientNetwork() {}

    /**
     * True once the server has sent the capability packet, indicating the server companion
     * is installed and the reliable C2S packet flow should be used.
     */
    private static volatile boolean serverHasMod = false;

    /** Returns true if the current server has the Smart Placement companion installed. */
    public static boolean isServerCompanionPresent() {
        return serverHasMod;
    }

    /**
     * Registers the S2C capability receiver.
     * Must be called once from the client entrypoint.
     */
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(
                SmartPlacementCapabilityS2CPayload.ID,
                (payload, ctx) -> {
                    serverHasMod = true;
                }
        );
    }

    /**
     * Resets the server-companion flag.
     * Must be called when the client disconnects from a server so the next connection
     * starts with the correct (unknown) state.
     *
     * @param playerUuid the local player's UUID, used to purge any stale pending flips
     */
    public static void onDisconnect(java.util.UUID playerUuid) {
        serverHasMod = false;
        if (playerUuid != null) {
            SmartPlacementServerState.clearAll(playerUuid);
        }
    }

    /**
     * Sends the {@link FlipPlacementC2SPayload} to the server, requesting that the
     * next block placement by this player has its FACING inverted by the server mixin.
     *
     * <p>Only call this when {@link #isServerCompanionPresent()} is true; the server
     * must have the mixin registered to act on the packet.
     */
    public static void sendFlipRequest() {
        ClientPlayNetworking.send(new FlipPlacementC2SPayload());
    }
}
