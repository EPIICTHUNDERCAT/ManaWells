package com.epiicthundercat.manawell.setup;

import com.epiicthundercat.manawell.Reference;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Creative tab name constant and item population event.
 * In 1.20.1 the old new CreativeModeTab(name){makeIcon()} constructor is gone.
 * The tab is built via DeferredRegister in Registration; items are populated here.
 */
@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSetup {

    public static final String TAB_NAME = "manawells";

    @SubscribeEvent
    public static void buildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == Registration.MANA_WELLS_TAB.getKey()) {
            event.accept(Registration.MANA_WELL_BEDROCK_ITEM.get());
        }
    }
}
