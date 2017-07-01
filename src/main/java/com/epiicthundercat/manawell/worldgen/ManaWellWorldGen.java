package com.epiicthundercat.manawell.worldgen;

import java.util.Random;

import com.epiicthundercat.manawell.MGlobals;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ManaWellWorldGen implements IWorldGenerator{

	static ManaWellWorldGen eventWorldGen = new ManaWellWorldGen();
	
	public static void init() 
	{
		GameRegistry.registerWorldGenerator(eventWorldGen, 0);
	}
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
			IChunkProvider chunkProvider) 
	{
		switch(world.provider.getDimension())
		{
		case 0: 
			//generate surface world
			generateSurface(world, random, chunkX*16, chunkZ*16);
			
		case -1: 
			//generate Nether
			generateNether(world, random, chunkX*16, chunkZ*16);
			
		case 1: 
			//generate end
			generateEnd(world, random, chunkX*16, chunkZ*16);
			
		default:
			if(world.provider.getDimension() != 0 && world.provider.isSurfaceWorld())
			{
				generateSurface(world, random, chunkX+ 4, chunkZ+ 4);
			}
		}
		
	}

	private void generateSurface(World world, Random random, int x, int z) {
		
		this.addManaWells(world, random, x, z);
		
	}
	
	private void generateNether(World world, Random random, int x, int z) {
		
		
	}
	
	private void generateEnd(World world, Random random, int x, int z) {
		
		
	}
	
	
	private void addManaWells(World world, Random random, int x, int z) {
		if(random.nextInt(MGlobals.MANAWELL_RARITY) == 0)
		{
			int posX = x + 8 + random.nextInt(16);
			int posY = 4;
			int posZ = z + 8 + random.nextInt(16);
			BlockPos pos = new BlockPos(posX, posY, posZ);
			new GenManaWell().generate(world, random, pos);
		}
	}


	
	
}
