package com.darkliz.manawell;

import com.darkliz.manawell.init.ManaWellBlocks;
import com.darkliz.manawell.init.ManaWellTileEntities;
import com.darkliz.manawell.proxy.CommonProxy;
import com.darkliz.manawell.worldgen.ManaWellWorldGen;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, acceptableRemoteVersions = "[1.1]", acceptedMinecraftVersions = "[1.8,)", 
dependencies = "required-after:Forge@[11.14.4.1563,)")
public class ManaWell {
	
	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static CommonProxy proxy;
	
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ManaWellBlocks.init();
		ManaWellTileEntities.init();
		
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.registerRenders();
		ManaWellWorldGen.init();
		
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		
	}

}
