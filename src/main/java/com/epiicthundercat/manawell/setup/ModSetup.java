package com.epiicthundercat.manawell.setup;

import com.epiicthundercat.manawell.Reference;
import com.epiicthundercat.manawell.advancement.ModAdvancements;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Creative tab name constant and item population event.
 * In 1.20.1 the old new CreativeModeTab(name){makeIcon()} constructor is gone.
 * The tab is built via DeferredRegister in Registration; items are populated here.
 */
@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSetup {

    public static final String TAB_NAME = "manawells";

    // CriteriaTriggers.register() must run on the main thread after basic MC init.
    // enqueueWork() defers execution to the main thread during common setup.
    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ModAdvancements::init);
    }

    @SubscribeEvent
    public static void buildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == Registration.MANA_WELLS_TAB.getKey()) {
            event.accept(Registration.MANA_WELL_BEDROCK_ITEM.get());
        }
    }
}
