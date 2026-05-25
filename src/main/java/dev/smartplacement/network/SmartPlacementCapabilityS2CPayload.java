package dev.smartplacement.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Server-to-client packet sent once on login to announce that this server has the
 * Smart Placement companion mod installed.
 *
 * <p>Upon receipt, the client will switch from the rotation-trick fallback to the
 * reliable C2S packet flow for all subsequent block placements.
 */
public record SmartPlacementCapabilityS2CPayload() implements CustomPayload {

    public static final Id<SmartPlacementCapabilityS2CPayload> ID =
            new Id<>(Identifier.of("smart_placement", "capability"));

    /** Unit codec — no data, just the channel ID. */
    public static final PacketCodec<RegistryByteBuf, SmartPlacementCapabilityS2CPayload> CODEC =
            PacketCodec.unit(new SmartPlacementCapabilityS2CPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
