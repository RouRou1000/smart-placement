package dev.smartplacement.mixin;

import dev.smartplacement.SmartPlacementServerState;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.jetbrains.annotations.Nullable;

/**
 * Intercepts {@link BlockItem#getPlacementState(ItemPlacementContext)} at its RETURN point.
 *
 * <p>If the placing player has a pending FACING-flip registered in
 * {@link SmartPlacementServerState}, the returned {@link BlockState}'s {@code FACING}
 * property is inverted before it is used to place the block.
 */
@Mixin(BlockItem.class)
public abstract class BlockItemServerMixin {

    /**
     * Injects at the RETURN of {@link BlockItem#getPlacementState} to post-process
     * the computed state and flip FACING if a flip is pending for the placing player.
     */
    @Inject(
            method = "getPlacementState",
            at = @At("RETURN"),
            cancellable = true
    )
    private void smartPlacement_onGetPlacementState(ItemPlacementContext ctx,
                                                    CallbackInfoReturnable<BlockState> cir) {
        @Nullable BlockState natural = cir.getReturnValue();
        if (natural == null || !natural.contains(Properties.FACING)) return;

        @Nullable PlayerEntity player = ctx.getPlayer();
        if (player == null) return;

        boolean clientSide = ctx.getWorld().isClient();
        if (!SmartPlacementServerState.consumePendingFlip(player.getUuid(), clientSide)) return;

        Direction flipped = natural.get(Properties.FACING).getOpposite();
        cir.setReturnValue(natural.with(Properties.FACING, flipped));
    }
}

