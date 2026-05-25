package dev.smartplacement.client.keybind;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Constructor;

/**
 * Registers all keybindings for Smart Placement.
 *
 * <p>Uses reflection to support both the pre-1.21.11 {@code String}-based
 * {@link KeyBinding} constructor and the 1.21.11+ {@code KeyBinding.Category}-based one,
 * so the same JAR works across the full 1.21 – 1.21.11 range without a startup crash.
 */
public final class SmartPlacementKeys {

    private static final String CATEGORY_KEY = "key.category.smart_placement";

    /** Toggles keybind-based inversion on/off (unbound by default). */
    public static KeyBinding TOGGLE_INVERSION;

    /** Cycles the facing direction of the held directional block (unbound by default). */
    public static KeyBinding CYCLE_FACING;

    private SmartPlacementKeys() {}

    /** Registers all bindings. Call once from the client entrypoint. */
    public static void register() {
        TOGGLE_INVERSION = KeyBindingHelper.registerKeyBinding(
                createBinding("key.smart_placement.toggle_inversion", GLFW.GLFW_KEY_UNKNOWN));
        CYCLE_FACING = KeyBindingHelper.registerKeyBinding(
                createBinding("key.smart_placement.cycle_facing", GLFW.GLFW_KEY_UNKNOWN));
    }

    /**
     * Creates a {@link KeyBinding} compatible with both 1.21.11+ (KeyBinding.Category) and
     * older 1.21.x versions (String category).
     *
     * <p>Strategy: inspect {@code KeyBinding}'s declared inner classes at runtime.
     * In 1.21.11+, {@code KeyBinding.Category} is present → use it.
     * In older versions, no inner class exists → fall back to the String constructor.
     */
    private static KeyBinding createBinding(String translationKey, int defaultKey) {
        // ── 1.21.11+ path: KeyBinding(String, InputUtil.Type, int, KeyBinding.Category) ──
        for (Class<?> inner : KeyBinding.class.getDeclaredClasses()) {
            try {
                // Instantiate the Category with an Identifier
                Constructor<?> catCtor = inner.getDeclaredConstructors()[0];
                catCtor.setAccessible(true);
                Object category = catCtor.newInstance(
                        Identifier.of("smart_placement", CATEGORY_KEY));

                Constructor<KeyBinding> kbCtor = KeyBinding.class.getDeclaredConstructor(
                        String.class, InputUtil.Type.class, int.class, inner);
                kbCtor.setAccessible(true);
                return kbCtor.newInstance(translationKey, InputUtil.Type.KEYSYM, defaultKey, category);
            } catch (Exception ignored) {
                // inner class found but constructor mismatch — try next
            }
        }

        // ── Pre-1.21.11 path: KeyBinding(String, InputUtil.Type, int, String) ──
        try {
            Constructor<KeyBinding> kbCtor = KeyBinding.class.getDeclaredConstructor(
                    String.class, InputUtil.Type.class, int.class, String.class);
            kbCtor.setAccessible(true);
            return kbCtor.newInstance(translationKey, InputUtil.Type.KEYSYM, defaultKey, CATEGORY_KEY);
        } catch (Exception e) {
            throw new RuntimeException(
                    "No compatible KeyBinding constructor found for MC version", e);
        }
    }
}


