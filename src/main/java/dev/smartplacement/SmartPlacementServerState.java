package dev.smartplacement;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe registry that tracks pending "flip FACING" requests for block placements.
 *
 * <p>When set, the {@code BlockItemServerMixin} will invert the FACING property of any
 * {@link net.minecraft.block.BlockState} returned by
 * {@link net.minecraft.block.Block#getPlacementState} before it is applied to the world.
 *
 * <p>Two separate sets exist:
 * <ul>
 *   <li><b>CLIENT_PENDING</b> — consumed during client-side block-state prediction
 *   <li><b>SERVER_PENDING</b> — consumed on the authoritative server thread
 * </ul>
 *
 * <p>In singleplayer / integrated server, both are set so client prediction and server
 * placement agree. On a multiplayer server with the companion mod, only SERVER_PENDING is
 * set (via the C2S packet handler). On a vanilla server, neither is set — the rotation
 * trick is used instead.
 */
public final class SmartPlacementServerState {

    private SmartPlacementServerState() {}

    // UUIDs whose next directional block placement should have FACING flipped (client prediction)
    private static final Set<UUID> CLIENT_PENDING = ConcurrentHashMap.newKeySet();

    // UUIDs whose next directional block placement should have FACING flipped (server authority)
    private static final Set<UUID> SERVER_PENDING = ConcurrentHashMap.newKeySet();

    /**
     * Registers a pending FACING-flip for the player's next block placement.
     *
     * @param playerId     the player about to place a block
     * @param isClientSide {@code true} for client-prediction flip, {@code false} for server flip
     */
    public static void setPendingFlip(UUID playerId, boolean isClientSide) {
        (isClientSide ? CLIENT_PENDING : SERVER_PENDING).add(playerId);
    }

    /**
     * Returns {@code true} and removes the pending flip entry if one exists for this player.
     * This is a test-and-clear operation; calling it a second time returns {@code false}.
     */
    public static boolean consumePendingFlip(UUID playerId, boolean isClientSide) {
        return (isClientSide ? CLIENT_PENDING : SERVER_PENDING).remove(playerId);
    }

    /**
     * Returns {@code true} if a pending flip is registered (without consuming it).
     */
    public static boolean hasPendingFlip(UUID playerId, boolean isClientSide) {
        return (isClientSide ? CLIENT_PENDING : SERVER_PENDING).contains(playerId);
    }

    /**
     * Clears all pending flips for the player — call on disconnect or death to prevent leaks.
     */
    public static void clearAll(UUID playerId) {
        CLIENT_PENDING.remove(playerId);
        SERVER_PENDING.remove(playerId);
    }
}
