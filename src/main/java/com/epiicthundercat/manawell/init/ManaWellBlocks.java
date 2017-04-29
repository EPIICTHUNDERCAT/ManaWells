package com.epiicthundercat.manawell.init;

import java.util.ArrayList;
import java.util.List;

import com.epiicthundercat.manawell.Reference;
import com.epiicthundercat.manawell.blocks.BlockManaWell;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;


public class ManaWellBlocks {
	
public static List<Block> blocks = new ArrayList();
	
	public static Block mana_well = new BlockManaWell("mana_well", Material.ROCK);
	
	
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
	
}
