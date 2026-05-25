package dev.smartplacement.util;

import net.minecraft.util.math.Direction;

/**
 * Utility methods for direction math used by placement logic.
 */
public final class DirectionHelper {

    private DirectionHelper() {}

    /**
     * Returns the opposite of the given direction.
     * UP↔DOWN, NORTH↔SOUTH, EAST↔WEST.
     */
    public static Direction invert(Direction direction) {
        return direction.getOpposite();
    }

    /**
     * Returns the yaw delta (degrees) needed to flip a horizontal direction.
     * Add 180° to the player's current yaw to mirror horizontal facing.
     */
    public static float invertedYaw(float originalYaw) {
        return originalYaw + 180.0f;
    }

    /**
     * Returns the pitch delta (degrees) needed to flip a vertical look.
     * Negating pitch mirrors the vertical component of the look vector.
     */
    public static float invertedPitch(float originalPitch) {
        return -originalPitch;
    }

    /**
     * Given a player yaw (degrees, Minecraft convention) returns the primary
     * horizontal {@link Direction} the player is facing.
     *
     * <p>Minecraft yaw: 0=South, 90=West, 180=North, 270=East</p>
     */
    public static Direction horizontalFromYaw(float yaw) {
        // Normalize to [0, 360). Minecraft: 0=South, 90=West, 180=North, 270=East
        float n = ((yaw % 360) + 360) % 360;
        if (n < 45 || n >= 315) return Direction.SOUTH;
        if (n < 135) return Direction.WEST;
        if (n < 225) return Direction.NORTH;
        return Direction.EAST;
    }
}
