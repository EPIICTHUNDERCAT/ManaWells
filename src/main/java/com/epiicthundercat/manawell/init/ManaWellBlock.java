package com.epiicthundercat.manawell.init;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class ManaWellBlock extends Block {
	public ManaWellBlock(String name, Material material) {
		super(material);
		this.setRegistryName(name.toLowerCase());
		this.setUnlocalizedName(name.toLowerCase());
		this.setCreativeTab(CreativeTabs.DECORATIONS);
		addToBlocks(this);
	}

	private void addToBlocks(Block block) {
		ManaWellBlocks.blocks.add(block);
	}

}