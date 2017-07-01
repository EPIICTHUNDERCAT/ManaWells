package com.epiicthundercat.manawell.init;

import java.io.File;

import com.epiicthundercat.manawell.MGlobals;
import com.epiicthundercat.manawell.Reference;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MConfigHandler {
	public static Configuration CONFIG;
    private static String DEF_CAT = "Options";

    @SubscribeEvent
    public void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent e) {
        if (e.getModID().equals(Reference.ID)) {
            init();
        }
    }

    public static void init() {
        if (CONFIG == null) {
            CONFIG = new Configuration(new File(MGlobals.CONFIG_FILE));
            MinecraftForge.EVENT_BUS.register(new MConfigHandler());
        }

        MGlobals.MANAWELL_RARITY = CONFIG.getInt("ManaWell Rarity", DEF_CAT, 32, 1, 64, "The Higher The Rarer the ManaWells are!(Note: Really small numbers can increase worldgen lag!)");
      
        if (CONFIG.hasChanged()) {
            CONFIG.save();
        }
    }
}