package com.epiicthundercat.manawell.setup;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

/**
 * Mod configuration using the 1.18.2 ForgeConfigSpec system.
 *
 * 1.10.2 equivalent: MGlobals.MANAWELL_RARITY + MConfigHandler
 *   MConfigHandler used net.minecraftforge.common.config.Configuration with a .cfg file.
 *   1.18.2 uses ForgeConfigSpec.Builder, registered via ModLoadingContext.
 *   Server config is loaded per-world save (appropriate for world-gen settings).
 *
 * Access: MWConfig.MANAWELL_RARITY.get()  (returns Integer)
 */
public class MWConfig {

    // 1.10.2 MGlobals.MANAWELL_RARITY (static int, default 32)
    // 1.18.2: ForgeConfigSpec.IntValue — config is loaded from file, not hardcoded
    public static ForgeConfigSpec.IntValue MANAWELL_RARITY;
    public static ForgeConfigSpec.IntValue MANAWELL_DRAIN_AMOUNT;
    public static ForgeConfigSpec.IntValue MANAWELL_DRAIN_SPEED;
    public static ForgeConfigSpec.IntValue MANAWELL_MANA_CAP;
    public static ForgeConfigSpec.IntValue MANAWELL_DORMANT_TIME;
    public static ForgeConfigSpec.IntValue MANAWELL_FILL_SPEED;
    public static void register() {
        registerServerConfigs();

    }

    private static void registerServerConfigs() {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

        SERVER_BUILDER.comment("World Generation Settings");
        MANAWELL_RARITY = SERVER_BUILDER
                .comment("Average number of chunks between Mana Well spawns.",
                         "Higher = rarer. Range: 1-64. Default: 32.")
                .defineInRange("manawellRarity", 32, 1, 64);

        MANAWELL_DRAIN_AMOUNT = SERVER_BUILDER
                .comment("Max XP drained from nearby players per drain tick (every ~10s).",
                         "0 = disabled. Default: 2. Range: 0-14.")
                .defineInRange("manawellDrainAmount", 2, 0, 14);
        MANAWELL_DRAIN_SPEED = SERVER_BUILDER
                .comment("Ticks between passive drain pulses. Default: 150 (~7s). Range: 20-800.")
                .defineInRange("manawellDrainSpeed", 150, 20, 800);

        MANAWELL_MANA_CAP = SERVER_BUILDER
                .comment("Max mana stored before well is full (= XP released on step). Default: 560. Range: 280-1120.")
                .defineInRange("manawellManaCap", 560, 280, 1120);

        MANAWELL_DORMANT_TIME = SERVER_BUILDER
                .comment("Ticks the well rests after releasing mana before refilling. Default: 24000 (1 day). 0 = no dormancy. Range: 0-72000.")
                .defineInRange("manawellDormantTime", 24000, 0, 72000);

        MANAWELL_FILL_SPEED = SERVER_BUILDER
                .comment("1-in-N chance per tick that mana generates while the well is filling (fillLevel < 5).",
                         "1 = every tick (~7s to full), 3 = 33% chance (~21s), 6 = 17% chance (~42s). Range: 1-20. Default: 6.")
                .defineInRange("manawellFillSpeed", 6, 1, 20);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_BUILDER.build());
    }


}
