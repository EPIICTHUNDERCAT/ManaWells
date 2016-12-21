package com.darkliz.lizzymod.lizzy_mana_stuff.worldgen;

import java.util.Random;

import com.darkliz.lizzymod.init.LMBlocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class GenManaWell extends WorldGenerator {

	
	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		
		while(world.getBlockState(pos) != Blocks.bedrock.getDefaultState() && pos.getY() > 1) //don't go lower than layer 1 so we don't replace the bottom layer of bedrock (this is to prevent making void holes if the mod should be removed)
        {
            pos = pos.down();
        }
		
		world.setBlockState(pos, LMBlocks.mana_well.getDefaultState(), 3);
		
		if(world.getBlockState(pos.down()) != Blocks.bedrock.getDefaultState())
		{
			world.setBlockState(pos.down(), Blocks.bedrock.getDefaultState(), 2); //place a block of bedrock under the mana well if there is none (just an extra check to prevent void holes)
		}
		
		//System.out.println("pos = " + pos);
		
		//make an area above the mana well free of bedrock to prevent two-deep holes with a mana well at the bottom, and also just to make access easier once found
		if(pos.getY() < 4)
		{
			if(pos.getY() == 1) pos = pos.up();
			
			for(int x = -1; x <= 1; x++)
			{
				for(int z = -1; z <= 1; z++)
				{
					this.replaceIfBedrock(world, pos.add(x, 1, z));
				}
			}
			
			if(pos.getY() < 3)
			{
				for(int x = -2; x <= 2; x++)
				{
					for(int z = -2; z <= 2; z++)
					{
						if(!(Math.abs(x) == 2 && Math.abs(z) == 2))
						{
							this.replaceIfBedrock(world, pos.add(x, 2, z));
						}
					}
				}
			}
		}
		

		return true;
	}

	private void replaceIfBedrock(World world, BlockPos pos) 
	{
		if(world.getBlockState(pos) == Blocks.bedrock.getDefaultState())
		{
			BlockPos replacementPos = pos.up();
			while(world.getBlockState(replacementPos) == Blocks.bedrock.getDefaultState() && replacementPos.getY() < 6)
			{
				replacementPos = replacementPos.up();
			}
			IBlockState replacementBlock = world.getBlockState(replacementPos);
			world.setBlockState(pos, replacementBlock);
		}
		
	}

}
