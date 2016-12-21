package com.darkliz.manawell.init;

import com.darkliz.manawell.Reference;
import com.darkliz.manawell.blocks.BlockManaWell;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
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
		register(item, 0, new ModelResourceLocation(Reference.MOD_ID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
	}
	
}
