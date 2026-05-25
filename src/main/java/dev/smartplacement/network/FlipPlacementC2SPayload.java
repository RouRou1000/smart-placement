package dev.smartplacement.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Client-to-server packet that signals the player's next directional block placement
 * should have its FACING property flipped by the server companion mixin.
 *
 * <p>This packet carries no data — it is a fire-and-forget signal sent immediately
 * before the vanilla "use item" packet so the server's {@code BlockItemServerMixin}
 * can apply the flip in the same game tick.
 */
public record FlipPlacementC2SPayload() implements CustomPayload {

    public static final Id<FlipPlacementC2SPayload> ID =
            new Id<>(Identifier.of("smart_placement", "flip_placement"));

    /**
     * A unit codec: always writes nothing, always reads back the same singleton.
     */
    public static final PacketCodec<RegistryByteBuf, FlipPlacementC2SPayload> CODEC =
            PacketCodec.unit(new FlipPlacementC2SPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
