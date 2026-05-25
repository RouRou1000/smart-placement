package dev.smartplacement.util;

import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;

/**
 * Utility methods for inspecting blocks and items involved in placement.
 */
public final class BlockHelper {

    private BlockHelper() {}

    /**
     * Returns true if the held item is a block with a {@link Properties#FACING} property,
     * meaning it is a 6-directional block (Observer, Piston, Dispenser, etc.).
     */
    public static boolean isDirectionalBlock(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem bi)) return false;
        return bi.getBlock().getDefaultState().contains(Properties.FACING);
    }

    /**
     * Returns true if the held item is a StairsBlock (uses HORIZONTAL_FACING + HALF).
     */
    public static boolean isStairs(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem bi)) return false;
        return bi.getBlock() instanceof StairsBlock;
    }

    /** Returns true if the block is an Observer. */
    public static boolean isObserver(Block block) {
        return block instanceof ObserverBlock;
    }

    /** Returns true if the block is a Piston (sticky or normal). */
    public static boolean isPiston(Block block) {
        return block instanceof PistonBlock;
    }

    /** Returns true if the block is a normal (non-sticky) Piston. */
    public static boolean isRegularPiston(Block block) {
        return block == Blocks.PISTON;
    }

    /** Returns true if the block is a Sticky Piston. */
    public static boolean isStickyPiston(Block block) {
        return block == Blocks.STICKY_PISTON;
    }

    /** Returns true if the block is a Dispenser (but not a Dropper). */
    public static boolean isDispenser(Block block) {
        return block instanceof DispenserBlock && !(block instanceof DropperBlock);
    }

    /** Returns true if the block is a Dropper. */
    public static boolean isDropper(Block block) {
        return block instanceof DropperBlock;
    }

    /**
     * Returns true if the block is a Crafter (added in 1.21).
     * Uses class name comparison to avoid hard-dependency issues across versions.
     */
    public static boolean isCrafter(Block block) {
        return block.getClass().getSimpleName().equals("CrafterBlock");
    }
}
