package com.epiicthundercat.manawell.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class ManaWellBlock extends Block {
	public ManaWellBlock(){
		super(Properties.of(Material.METAL)
				.sound(SoundType.METAL)
				.strength(2.0f)
				.noOcclusion()
				.requiresCorrectToolForDrops()
		);
		//this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(IS_OPEN, Boolean.FALSE).setValue(IS_ON, Boolean.FALSE));

	}


}