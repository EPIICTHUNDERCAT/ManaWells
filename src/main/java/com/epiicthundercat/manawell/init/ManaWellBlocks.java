package com.epiicthundercat.manawell.init;

import com.epiicthundercat.manawell.Reference;
import com.epiicthundercat.manawell.blocks.BlockManaWell;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;


public class ManaWellBlocks {
	//Block Declaration
		public static Block mana_well;
		
		//Block Registration
		public static void init()
		{
			mana_well = new BlockManaWell().setUnlocalizedName("mana_well");
			register(mana_well);
		}
		
		//Render Registers
		public static void registerRenders()
		{
			registerRender(mana_well);
		}
		
		
		public static void register(Block block)
		{
			GameRegistry.registerBlock(block, block.getUnlocalizedName().substring(5));
		}
		
		public static void registerRender(Block block)
		{
			Item item = Item.getItemFromBlock(block);
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().
			register(item, 0, new ModelResourceLocation(Reference.ID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
		}
	/*
public static List<Block> blocks = new ArrayList();
	
	public static Block mana_well = new BlockManaWell().setRegistryName("mana_well");
	
	
	public static List<Block> blockList() {
		return blocks;		
	}
	
	public static void register(FMLPreInitializationEvent preEvent) {
		for (Block block : blockList()){
			ItemBlock iBlock = new ItemBlock(block);
			GameRegistry.register(block);
		    GameRegistry.register(iBlock, block.getRegistryName());
		}
	}
	
	public static void registerRender(FMLInitializationEvent event) {
		for (Block block : blockList()){
		Item item = new Item().getItemFromBlock(block);
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
    	renderItem.getItemModelMesher().register(item, 0, new ModelResourceLocation(Reference.ID + ":"+ block.getRegistryName().toString(), "inventory"));
		}
	}
	*/
}
