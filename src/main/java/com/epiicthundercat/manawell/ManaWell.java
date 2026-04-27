package com.epiicthundercat.manawell;

import com.epiicthundercat.manawell.datagen.DataGenerators;
import com.epiicthundercat.manawell.setup.MWConfig;
import com.epiicthundercat.manawell.setup.ModSetup;
import com.epiicthundercat.manawell.setup.Registration;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.data.event.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reference.MODID)
public class ManaWell {

    public static final Logger LOGGER = LogManager.getLogger();

    // Forge 64 (26.1.2): IEventBus removed. Use BusGroup from context.getModBusGroup().
    // @Mod.EventBusSubscriber + @SubscribeEvent replaced by direct .getBus(modBusGroup).addListener()
    // for IModBusEvents, and Event.BUS.addListener() for global game events.
    public ManaWell(FMLJavaModLoadingContext context) {
        BusGroup modBusGroup = context.getModBusGroup();

        Registration.init(modBusGroup);
        MWConfig.register();

        // MOD lifecycle events — per-mod bus via getBus(modBusGroup)
        FMLCommonSetupEvent.getBus(modBusGroup).addListener(ModSetup::commonSetup);
        GatherDataEvent.getBus(modBusGroup).addListener(DataGenerators::gatherData);

        // Global bus event — not per-mod, uses static BUS field
        BuildCreativeModeTabContentsEvent.BUS.addListener(ModSetup::buildCreativeTab);
    }

}
