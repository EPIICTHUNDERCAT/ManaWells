package com.epiicthundercat.manawell.datagen;

import com.epiicthundercat.manawell.Reference;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// GatherDataEvent import moved from net.minecraftforge.forge.event.lifecycle to net.minecraftforge.data.event in 1.20.1.
@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        if (event.includeServer()) {
        }

        if (event.includeClient()) {
            // addProvider now requires a boolean "run" flag and PackOutput instead of DataGenerator.
            generator.addProvider(true, new ManaWellsBlockStates(generator.getPackOutput(), event.getExistingFileHelper()));
            generator.addProvider(true, new ManaWellsLanguageProvider(generator.getPackOutput(), "en_us"));
            generator.addProvider(true, new ManaWellsLanguageProvider(generator.getPackOutput(), "pt_br"));
        }
    }
}
