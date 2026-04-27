package com.epiicthundercat.manawell.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;

// Forge 64 (26.1.2): @Mod.EventBusSubscriber + @SubscribeEvent removed.
// Registered via GatherDataEvent.getBus(modBusGroup).addListener() in ManaWell constructor.
//
// NOTE: ManaWellsBlockStates (BlockStateProvider datagen) removed —
// net.minecraftforge.client.model.generators was removed in 26.1.2.
// Blockstate and item model JSONs already exist in src/generated/resources and src/main/resources.
public class DataGenerators {

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        if (event.includeClient()) {
            generator.addProvider(true, new ManaWellsLanguageProvider(generator.getPackOutput(), "en_us"));
            generator.addProvider(true, new ManaWellsLanguageProvider(generator.getPackOutput(), "pt_br"));
        }
    }
}
