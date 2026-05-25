package dev.smartplacement.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.smartplacement.SmartPlacementMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Persistent configuration for Smart Placement.
 *
 * <p>Serialised as JSON to {@code config/smart_placement.json} using the Gson instance
 * bundled with Minecraft. Call {@link #load()} once at startup and {@link #save()} after
 * any mutation (e.g. from the config screen).
 */
public class SmartPlacementConfig {

    // -------------------------------------------------------------------------
    // Singleton
    // -------------------------------------------------------------------------

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("smart_placement.json");

    private static SmartPlacementConfig INSTANCE;

    public static SmartPlacementConfig get() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    /** Reads config from disk; if missing or corrupt, returns and saves defaults. */
    public static SmartPlacementConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                SmartPlacementConfig loaded = GSON.fromJson(reader, SmartPlacementConfig.class);
                if (loaded != null) {
                    INSTANCE = loaded;
                    return INSTANCE;
                }
            } catch (IOException e) {
                SmartPlacementMod.LOGGER.warn("[SmartPlacement] Failed to read config, using defaults", e);
            }
        }
        INSTANCE = new SmartPlacementConfig();
        INSTANCE.save();
        return INSTANCE;
    }

    /** Writes the current config to disk. */
    public void save() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            SmartPlacementMod.LOGGER.warn("[SmartPlacement] Failed to save config", e);
        }
    }

    // -------------------------------------------------------------------------
    // General
    // -------------------------------------------------------------------------

    /** Master on/off switch — disables ALL smart placement behaviour when false. */
    public boolean enabled = true;

    /**
     * If true, holding Sneak while placing a directional block inverts its FACING.
     * This is the core feature of Smart Placement.
     */
    public boolean sneakInversion = true;

    /**
     * If true, a configurable keybind toggles inversion mode on/off independently
     * of the sneak key, allowing free camera movement while inverted.
     */
    public boolean invertOnKeybind = false;

    // -------------------------------------------------------------------------
    // Per-block toggles
    // -------------------------------------------------------------------------

    public boolean perBlock_observer       = true;
    public boolean perBlock_piston         = true;
    public boolean perBlock_stickyPiston   = true;
    public boolean perBlock_dispenser      = true;
    public boolean perBlock_dropper        = true;
    public boolean perBlock_crafter        = true;
    /** Catch-all: apply inversion to every block with a {@code FACING} property. */
    public boolean perBlock_allDirectional = true;

    // -------------------------------------------------------------------------
    // Preview
    // -------------------------------------------------------------------------

    /** Show a ghost-block outline indicating the block's final facing direction. */
    public boolean showPreview   = true;
    /** ARGB colour for the placement preview outline. */
    public int     previewColor  = 0xFF00FF00; // opaque green
    /** Opacity multiplier applied on top of the ARGB alpha channel (0.0–1.0). */
    public float   previewOpacity = 0.5f;

    // -------------------------------------------------------------------------
    // Feedback
    // -------------------------------------------------------------------------

    /** Show a text message when Smart Placement triggers. */
    public boolean showMessages  = true;
    /** Display messages in the action bar instead of the chat area. */
    public boolean useActionBar  = true;
    /** Play a subtle sound on placement. */
    public boolean enableSounds  = true;
    /** Emit debug log lines (only relevant during development / bug reports). */
    public boolean debugLogging  = false;

    // -------------------------------------------------------------------------
    // Bonus features
    // -------------------------------------------------------------------------

    /**
     * Remember the last-used facing for each block type and repeat it automatically,
     * without needing to hold Sneak on subsequent placements.
     */
    public boolean placementMemory  = false;

    /**
     * Allow the scroll wheel to cycle through directional block facings while sneaking,
     * instead of switching hotbar slots.
     */
    public boolean scrollRotation   = false;

    /**
     * When placing Stairs, automatically choose the correct half (top/bottom)
     * based on where on the target block face the player clicked.
     */
    public boolean smartStairs      = false;
}
