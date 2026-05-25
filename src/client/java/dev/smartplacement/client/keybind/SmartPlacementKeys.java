package dev.smartplacement.client.keybind;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

/**
 * Registers all keybindings for Smart Placement.
 *
 * <p>Bindings are unbound by default so they never conflict with vanilla controls.
 * Players configure them via Options → Controls.
 */
public final class SmartPlacementKeys {

    private SmartPlacementKeys() {}

    /** Key category shown in the Controls screen. */
    public static final KeyBinding.Category KEYBIND_CATEGORY =
            new KeyBinding.Category(Identifier.of("smart_placement", "smart_placement"));

    /**
     * Toggles the keybind-based inversion mode on/off.
     * Only active when {@code config.invertOnKeybind} is true.
     */
    public static KeyBinding TOGGLE_INVERSION;

    /** Cycles the facing of the held directional block (bonus: scroll rotation support). */
    public static KeyBinding CYCLE_FACING;

    /** Registers all bindings. Call once from the client entrypoint. */
    public static void register() {
        TOGGLE_INVERSION = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.smart_placement.toggle_inversion",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN, // unbound by default
                KEYBIND_CATEGORY
        ));

        CYCLE_FACING = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.smart_placement.cycle_facing",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN, // unbound by default
                KEYBIND_CATEGORY
        ));
    }
}

