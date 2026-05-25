package dev.smartplacement.client;

import dev.smartplacement.SmartPlacementMod;
import dev.smartplacement.client.config.SmartPlacementConfig;
import dev.smartplacement.client.feature.PlacementPreview;
import dev.smartplacement.client.keybind.SmartPlacementKeys;
import dev.smartplacement.client.network.SmartPlacementClientNetwork;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

/**
 * Client-only mod initializer — MC 1.21.4 variant.
 * Uses {@code GameProfile.getId()} (pre-1.21.11 authlib API).
 */
public class SmartPlacementClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        SmartPlacementConfig.load();
        SmartPlacementKeys.register();
        SmartPlacementClientNetwork.register();
        PlacementPreview.register();

        // GameProfile.id() → getId() in MC ≤1.21.4 authlib
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
                SmartPlacementClientNetwork.onDisconnect(
                        handler.getProfile() != null ? handler.getProfile().getId() : null
                )
        );

        SmartPlacementMod.LOGGER.info("[SmartPlacement] Client init complete");
    }
}
