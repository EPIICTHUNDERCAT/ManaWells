package com.epiicthundercat.manawell.proxy;

import com.epiicthundercat.manawell.init.ManaWellBlocks;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
	
	public void preInit(FMLPreInitializationEvent preEvent) {
		register(preEvent);
		

	}

	public void init(FMLInitializationEvent event) {
		registerRenders(event);

	}

	private void register(FMLPreInitializationEvent preEvent) {
		ManaWellBlocks.register(preEvent);
		
		
		
	}

	public void registerRenders(FMLInitializationEvent event) {

	}

	


}
