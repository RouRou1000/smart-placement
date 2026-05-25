package dev.smartplacement.client.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Builds and returns the Cloth Config 2 configuration screen.
 *
 * <p>Call {@link #create(Screen)} from {@code ModMenuIntegration} or from the
 * in-game config button. All changes are written to disk immediately when the
 * player clicks "Save &amp; Close".
 */
public final class SmartPlacementConfigScreen {

    private SmartPlacementConfigScreen() {}

    /**
     * Creates and returns the config screen backed by the current {@link SmartPlacementConfig}.
     *
     * @param parent the screen to return to when closed
     * @return the ready-to-display Cloth Config screen
     */
    public static Screen create(Screen parent) {
        SmartPlacementConfig cfg = SmartPlacementConfig.get();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("config.smart_placement.title"))
                .setSavingRunnable(cfg::save);

        ConfigEntryBuilder eb = builder.entryBuilder();

        // -----------------------------------------------------------------
        // Category: General
        // -----------------------------------------------------------------
        ConfigCategory general = builder.getOrCreateCategory(
                Text.translatable("config.smart_placement.category.general"));

        general.addEntry(eb.startBooleanToggle(
                        Text.translatable("config.smart_placement.enabled"), cfg.enabled)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("config.smart_placement.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.enabled = v)
                .build());

        general.addEntry(eb.startBooleanToggle(
                        Text.translatable("config.smart_placement.sneakInversion"), cfg.sneakInversion)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("config.smart_placement.sneakInversion.tooltip"))
                .setSaveConsumer(v -> cfg.sneakInversion = v)
                .build());

        general.addEntry(eb.startBooleanToggle(
                        Text.translatable("config.smart_placement.invertOnKeybind"), cfg.invertOnKeybind)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("config.smart_placement.invertOnKeybind.tooltip"))
                .setSaveConsumer(v -> cfg.invertOnKeybind = v)
                .build());

        // -----------------------------------------------------------------
        // Category: Per-Block
        // -----------------------------------------------------------------
        ConfigCategory perBlock = builder.getOrCreateCategory(
                Text.translatable("config.smart_placement.category.perBlock"));

        perBlock.addEntry(eb.startBooleanToggle(
                        Text.translatable("config.smart_placement.perBlock_allDirectional"), cfg.perBlock_allDirectional)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("config.smart_placement.perBlock_allDirectional.tooltip"))
                .setSaveConsumer(v -> cfg.perBlock_allDirectional = v)
                .build());

        perBlock.addEntry(eb.startBooleanToggle(
                        Text.translatable("config.smart_placement.perBlock_observer"), cfg.perBlock_observer)
                .setDefaultValue(true)
                .setSaveConsumer(v -> cfg.perBlock_observer = v)
                .build());

        perBlock.addEntry(eb.startBooleanToggle(
                        Text.translatable("config.smart_placement.perBlock_piston"), cfg.perBlock_piston)
                .setDefaultValue(true)
                .setSaveConsumer(v -> cfg.perBlock_piston = v)
                .build());

        perBlock.addEntry(eb.startBooleanToggle(
                        Text.translatable("config.smart_placement.perBlock_stickyPiston"), cfg.perBlock_stickyPiston)
                .setDefaultValue(true)
                .setSaveConsumer(v -> cfg.perBlock_stickyPiston = v)
                .build());

        perBlock.addEntry(eb.startBooleanToggle(
                        Text.translatable("config.smart_placement.perBlock_dispenser"), cfg.perBlock_dispenser)
                .setDefaultValue(true)
                .setSaveConsumer(v -> cfg.perBlock_dispenser = v)
                .build());

        perBlock.addEntry(eb.startBooleanToggle(
                        Text.translatable("config.smart_placement.perBlock_dropper"), cfg.perBlock_dropper)
                .setDefaultValue(true)
                .setSaveConsumer(v -> cfg.perBlock_dropper = v)
                .build());

        perBlock.addEntry(eb.startBooleanToggle(
                        Text.translatable("config.smart_placement.perBlock_crafter"), cfg.perBlock_crafter)
                .setDefaultValue(true)
                .setSaveConsumer(v -> cfg.perBlock_crafter = v)
                .build());

        // -----------------------------------------------------------------
        // Category: Preview
        // -----------------------------------------------------------------
        ConfigCategory preview = builder.getOrCreateCategory(
                Text.translatable("config.smart_placement.category.preview"));

        preview.addEntry(eb.startBooleanToggle(
                        Text.translatable("config.smart_placement.showPreview"), cfg.showPreview)
                .setDefaultValue(true)
                .setSaveConsumer(v -> cfg.showPreview = v)
                .build());

        preview.addEntry(eb.startColorField(
                        Text.translatable("config.smart_placement.previewColor"), cfg.previewColor & 0xFFFFFF)
                .setDefaultValue(0x00FF00)
                .setTooltip(Text.translatable("config.smart_placement.previewColor.tooltip"))
                .setSaveConsumer(v -> cfg.previewColor = (0xFF000000 | v))
                .build());

        preview.addEntry(eb.startFloatField(
                        Text.translatable("config.smart_placement.previewOpacity"), cfg.previewOpacity)
                .setDefaultValue(0.5f)
                .setMin(0.0f)
                .setMax(1.0f)
                .setSaveConsumer(v -> cfg.previewOpacity = v)
                .build());

        // -----------------------------------------------------------------
        // Category: Feedback
        // -----------------------------------------------------------------
        ConfigCategory feedback = builder.getOrCreateCategory(
                Text.translatable("config.smart_placement.category.feedback"));

        feedback.addEntry(eb.startBooleanToggle(
                        Text.translatable("config.smart_placement.showMessages"), cfg.showMessages)
                .setDefaultValue(true)
                .setSaveConsumer(v -> cfg.showMessages = v)
                .build());

        feedback.addEntry(eb.startBooleanToggle(
                        Text.translatable("config.smart_placement.useActionBar"), cfg.useActionBar)
                .setDefaultValue(true)
                .setSaveConsumer(v -> cfg.useActionBar = v)
                .build());

        feedback.addEntry(eb.startBooleanToggle(
                        Text.translatable("config.smart_placement.enableSounds"), cfg.enableSounds)
                .setDefaultValue(true)
                .setSaveConsumer(v -> cfg.enableSounds = v)
                .build());

        feedback.addEntry(eb.startBooleanToggle(
                        Text.translatable("config.smart_placement.debugLogging"), cfg.debugLogging)
                .setDefaultValue(false)
                .setSaveConsumer(v -> cfg.debugLogging = v)
                .build());

        // -----------------------------------------------------------------
        // Category: Bonus Features
        // -----------------------------------------------------------------
        ConfigCategory bonus = builder.getOrCreateCategory(
                Text.translatable("config.smart_placement.category.bonus"));

        bonus.addEntry(eb.startBooleanToggle(
                        Text.translatable("config.smart_placement.scrollRotation"), cfg.scrollRotation)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("config.smart_placement.scrollRotation.tooltip"))
                .setSaveConsumer(v -> cfg.scrollRotation = v)
                .build());

        bonus.addEntry(eb.startBooleanToggle(
                        Text.translatable("config.smart_placement.smartStairs"), cfg.smartStairs)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("config.smart_placement.smartStairs.tooltip"))
                .setSaveConsumer(v -> cfg.smartStairs = v)
                .build());

        bonus.addEntry(eb.startBooleanToggle(
                        Text.translatable("config.smart_placement.placementMemory"), cfg.placementMemory)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("config.smart_placement.placementMemory.tooltip"))
                .setSaveConsumer(v -> cfg.placementMemory = v)
                .build());

        return builder.build();
    }
}
