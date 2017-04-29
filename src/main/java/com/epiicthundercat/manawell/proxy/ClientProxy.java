package com.epiicthundercat.manawell.proxy;

import com.epiicthundercat.manawell.init.ManaWellBlocks;
import com.epiicthundercat.manawell.init.ManaWellTileEntities;
import com.epiicthundercat.manawell.worldgen.ManaWellWorldGen;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy{

	public void preInit(FMLPreInitializationEvent preEvent) {
		super.preInit(preEvent);
		
		
		

	}

	public void init(FMLInitializationEvent event) {
		super.init(event);
		
		ManaWellWorldGen.init();

	}

	
	@Override
	public void registerRenders(FMLInitializationEvent event)
	{
	ManaWellBlocks.registerRender(event);
		
	}
	

}
