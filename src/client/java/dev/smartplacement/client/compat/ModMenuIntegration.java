package dev.smartplacement.client.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.smartplacement.client.config.SmartPlacementConfigScreen;

/**
 * Registers the Smart Placement config screen with Mod Menu.
 *
 * <p>This class is referenced in {@code fabric.mod.json} under the {@code modmenu}
 * entrypoint. If Mod Menu is not installed, this class is never loaded.
 */
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return SmartPlacementConfigScreen::create;
    }
}
