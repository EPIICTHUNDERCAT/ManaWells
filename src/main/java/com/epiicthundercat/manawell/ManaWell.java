package com.epiicthundercat.manawell;

import com.epiicthundercat.manawell.setup.MWConfig;
import com.epiicthundercat.manawell.setup.ModSetup;
import com.epiicthundercat.manawell.setup.Registration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reference.MODID)
public class ManaWell {

    public static final Logger LOGGER = LogManager.getLogger();


    public ManaWell() {
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();

        Registration.init();
        MWConfig.register();

        // ManaWellWorldGen uses @Mod.EventBusSubscriber(bus=FORGE) so Forge registers
        // its @SubscribeEvent methods automatically — no explicit registration needed here.
        MinecraftForge.EVENT_BUS.register(this);
    }

}
