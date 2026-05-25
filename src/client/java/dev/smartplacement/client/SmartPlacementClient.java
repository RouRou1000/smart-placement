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
 * Client-only mod initializer.
 *
 * <p>Loads config, registers keybindings, networking receivers, and the render preview.
 */
public class SmartPlacementClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Load config from disk (initialises INSTANCE for all client features)
        SmartPlacementConfig.load();

        // Register keybindings
        SmartPlacementKeys.register();

        // Register S2C networking (capability announcement from server)
        SmartPlacementClientNetwork.register();

        // Register ghost-block placement preview renderer
        PlacementPreview.register();

        // Reset server-companion flag on disconnect; pass the UUID to clean up state
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
                SmartPlacementClientNetwork.onDisconnect(
                        handler.getProfile() != null ? handler.getProfile().id() : null
                )
        );

        SmartPlacementMod.LOGGER.info("[SmartPlacement] Client init complete");
    }
}
