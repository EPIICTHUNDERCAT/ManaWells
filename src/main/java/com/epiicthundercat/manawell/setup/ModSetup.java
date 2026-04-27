package com.epiicthundercat.manawell.setup;

import com.epiicthundercat.manawell.advancement.ModAdvancements;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

// Forge 64 (26.1.2): @Mod.EventBusSubscriber + @SubscribeEvent removed.
// Listeners are registered directly in ManaWell constructor via getBus(modBusGroup).addListener().
public class ModSetup {

    public static final String TAB_NAME = "manawells";

    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ModAdvancements::init);
    }

    public static void buildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == Registration.MANA_WELLS_TAB.getKey()) {
            event.accept(Registration.MANA_WELL_BEDROCK_ITEM.get());
        }
    }
}
