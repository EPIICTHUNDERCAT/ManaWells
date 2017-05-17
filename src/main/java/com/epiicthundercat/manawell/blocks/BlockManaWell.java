package com.epiicthundercat.manawell.blocks;

import java.util.Random;

import com.epiicthundercat.manawell.init.ManaWellBlock;
import com.epiicthundercat.manawell.tileentity.TileEntityManaWell;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class BlockManaWell extends ManaWellBlock implements ITileEntityProvider {

	public static final PropertyInteger FILL_LEVEL = PropertyInteger.create("fill_level", 0, 5);
	public static final int MANA_CAP = 560;
	public static final int DORMANT_TIME = 24000; // one minecraft daye
EntityPlayer player1;
	private int collideTimer = 0;

	public BlockManaWell(String name, Material material) {
		super(name, material);
		/*
		 * set to 5 so they have the correct (full) texture when they are
		 * generated(when a player places one the meta value is 0; handled under
		 * createNewTileEntity).
		 */
		this.setDefaultState(this.blockState.getBaseState().withProperty(FILL_LEVEL, Integer.valueOf(5)));
		this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		this.setBlockUnbreakable();
		this.setResistance(6000000.0F);
		this.setSoundType(SoundType.METAL);
		this.setTickRandomly(true);

	}

	@Override
	// runs only on server
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {

		/*
		 * This method runs on the server only
		 * 
		 * first, make sure the mana well is not more than 5 blocks away from
		 * the void; it gathers mana from the nothingness of the void and must
		 * be close enough or it won't fill up; it also must be in the surface
		 * world
		 */
		// layer 0-4 allow mana to be gathered and only in the surface world
		if (pos.getY() > 4 || !worldIn.provider.isSurfaceWorld()) {
			return;
		}

		// get the tile entity at pos
		TileEntityManaWell manaWell = (TileEntityManaWell) worldIn.getTileEntity(pos);

		if (manaWell.getIsDormant()) {
			long currentTime = worldIn.getTotalWorldTime();
			long dormantStartTime = manaWell.getDormantStartTime();
			if (currentTime < dormantStartTime + this.DORMANT_TIME) {
				return;
			} else {
				manaWell.setIsDormant(false);
				return;
			}
		}

		boolean blockChange = false;
		int fillLevel = ((Integer) state.getValue(FILL_LEVEL)).intValue();

		if (manaWell instanceof TileEntityManaWell) {
			// get the mana data from the manaWell
			int storedMana = manaWell.getStoredMana();

			// get the amount of mana to add to the well
			int generatedMana = 0;
			if (fillLevel < 5)
				generatedMana += 1 + rand.nextInt(7); // 1-7 average of 4
			else if (fillLevel == 5) {
				int chance;
				if (storedMana <= MANA_CAP + (MANA_CAP / 2))
					chance = 1; // 100% (chance that mana will be generated)
				else if (storedMana <= MANA_CAP * 2)
					chance = 2; // 50%
				else if (storedMana <= MANA_CAP * 2 + MANA_CAP / 2)
					chance = 4; // 25%
				else if (storedMana <= MANA_CAP * 3)
					chance = 8; // 12.5%
				else if (storedMana <= MANA_CAP * 3 + MANA_CAP / 2)
					chance = 16; // 6.25%
				else if (storedMana <= MANA_CAP * 4)
					chance = 32; // 3.125%
				else if (storedMana <= MANA_CAP * 4 + MANA_CAP / 2)
					chance = 64; // 1.5625%
				else
					chance = 128; // 0.078125%
				if (rand.nextInt(chance) == 0)
					generatedMana = 1 + rand.nextInt(3); // 1-3 (average of 2);

			}

			storedMana += generatedMana;

			/*
			 * set the blockstate of the mana well to reflect how much mana is
			 * in it
			 */
			if (storedMana >= MANA_CAP) {
				if (fillLevel != 5) {
					fillLevel = 5;
					blockChange = true;
				}
			} else if (storedMana >= (MANA_CAP / 4) * 3) {
				if (fillLevel != 4) {
					fillLevel = 4;
					blockChange = true;
				}
			} else if (storedMana >= (MANA_CAP / 4) * 2) {
				if (fillLevel != 3) {
					fillLevel = 3;
					blockChange = true;
				}
			} else if (storedMana >= (MANA_CAP / 4)) {
				if (fillLevel != 2) {
					fillLevel = 2;
					blockChange = true;
				}
			} else if (storedMana >= 1) {
				if (fillLevel != 1) {
					fillLevel = 1;
					blockChange = true;
				}
			} else {
				if (fillLevel != 0) {
					fillLevel = 0;
					blockChange = true;
				}
			}

			// change the blockstate if needed
			if (blockChange) {
				/*
				 * Note: when the blockstate of a block is changed associated
				 * tile entity is deleted and a new one is created
				 */
				worldIn.setBlockState(pos, state.withProperty(FILL_LEVEL, fillLevel), 3);

				/*
				 * get the tile entity at pos again (the tile entity changed so
				 * it will be a new one)
				 */
				manaWell = (TileEntityManaWell) worldIn.getTileEntity(pos);

				/*
				 * if the blockstate is showing a model other than the empty
				 * mana well, which means there is mana in the well, set
				 * canRelease to true
				 */
				if (fillLevel != 0) {
					manaWell.setCanRelease(true);
				}
			}

			// set the mana data on this (new, if state changed) tile entity
			manaWell.setStoredMana(storedMana);

			/*
			 * play a sound when mana is generated; if no mana is generated
			 * drain mana from nearest player, if any
			 */
			if (generatedMana > 0) {
				
				this.playManaWellFillSound(worldIn, player1, pos);
			} else {
				EntityPlayer player = this.getClosestPlayerWithXP(worldIn, pos.getX(), pos.getY(), pos.getZ(), 16.0D);
				this.drainMana(worldIn, pos, player, manaWell);
			}

			/*
			 * Attempt to attract a witch a number of times equal to the
			 * difficulty level index (0-3); if successful, stop trying
			 */
			if (fillLevel != 0) {
				int i = 0;
				while (i < worldIn.getDifficulty().getDifficultyId()
						&& !this.attemptAttractWitch(worldIn, pos, storedMana, rand)) {
					i++;
					//System.out.println("WITCH SPAWN ATTEMPT (" + i + ") FAILED");
				}
			}

		}
	}

	private boolean attemptAttractWitch(World worldIn, BlockPos pos, int storedMana, Random rand) {

		int numPositionsToCheck = Math.min(storedMana / (MANA_CAP / 10), 40);
		int maxLight;
		if (storedMana >= MANA_CAP)
			maxLight = Math.min(8 + (storedMana - MANA_CAP) / (MANA_CAP / 2), 10);
		else
			maxLight = 7;

		double range = 16;
		double posX = pos.getX();
		double posY = pos.getY();
		double posZ = pos.getZ();
		double x = posX + (rand.nextDouble() - rand.nextDouble()) * range + 0.5D;
		double y = posY + rand.nextDouble() * range + 0.5D;
		double z = posZ + (rand.nextDouble() - rand.nextDouble()) * range + 0.5D;

		BlockPos spawnPos = new BlockPos(x, y, z);

		int i = 0;
		while (i < numPositionsToCheck && !worldIn.isAirBlock(spawnPos)) {
			x = posX + (rand.nextDouble() - rand.nextDouble()) * range + 0.5D;
			y = posY + rand.nextDouble() * range + 0.5D;
			z = posZ + (rand.nextDouble() - rand.nextDouble()) * range + 0.5D;
			spawnPos = new BlockPos(x, y, z);
			i++;

		}

		if (!worldIn.isAirBlock(spawnPos)) {

			return false;
		}

		// Check conditions at spawn position
		while (!worldIn.isBlockFullCube(spawnPos.down()) && spawnPos.getY() > 0) {
			spawnPos = spawnPos.down();
		}
		y = spawnPos.getY();
		// dont spawn a witch directly above a mana well
		if (worldIn.getBlockState(spawnPos.down()).getBlock() == this) {
			return false;
		}

		// get the entity and check if it can spawn at the spawn position
		Entity entity = new EntityWitch(worldIn);

		if (entity == null)
			return false;

		EntityLiving entityliving = entity instanceof EntityLiving ? (EntityLiving) entity : null;

		if (entityliving == null)
			return false;
		entity.setLocationAndAngles(x, y, z, rand.nextFloat() * 360.0F, 0.0F);

		if (this.getCanManaWellWitchSpawnHere(worldIn, spawnPos, maxLight) && entityliving.isNotColliding()) {
			if (entityliving.world != null) {
				// set false for now
				EntityPlayer closestPlayer = worldIn.getClosestPlayer(x, y, z, 128.0D, false);
				if (closestPlayer != null && !closestPlayer.isSpectator()) {
					if (closestPlayer.getDistanceSq(x, y, z) > 576) // 24
																	// squared
					{
						entity.world.spawnEntity(entity);
						if (entityliving != null) {
							entityliving.spawnExplosionParticle();
						}
						return true;
					} else {
						return false;
					}
				}
			}
		}
		return false;

	}

	public boolean getCanManaWellWitchSpawnHere(World world, BlockPos pos, int maxLight) {
		return world.getDifficulty() != EnumDifficulty.PEACEFUL && world.getLight(pos) <= maxLight;
	}

	@Override
	public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
		this.attemptRelease(worldIn, pos, playerIn);
	}

	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, Entity entityIn) {
		if (!worldIn.isRemote) {
			// do a check to make sure its a player. monsters/animals shouldn't
			// trigger this
			if (entityIn instanceof EntityPlayer) {
				this.collideTimer++;
				if (this.collideTimer == 3) {
					EntityPlayer player = (EntityPlayer) entityIn;
					TileEntityManaWell manaWell = (TileEntityManaWell) worldIn.getTileEntity(pos);
					if (manaWell.getIsDormant()) {
						// 3 second delay before starting to suck mana
						if (worldIn.getTotalWorldTime() > manaWell.getDormantStartTime() + 60) {
							this.drainMana(worldIn, pos, player, manaWell);
						}
					} else {
						this.attemptRelease(worldIn, pos, player);
					}
					this.collideTimer = 0;
				}
			}
			// if it is a witch, it can steal the mana
			else if (entityIn instanceof EntityWitch) {
				TileEntityManaWell manaWell = (TileEntityManaWell) worldIn.getTileEntity(pos);
				if (manaWell.getCanRelease()) {
					// power up the witch
					int duration = 80 + manaWell.getStoredMana() * 2;
					((EntityWitch) entityIn).addPotionEffect(new PotionEffect(Potion.getPotionById(10), duration, 4));
					((EntityWitch) entityIn).addPotionEffect(new PotionEffect(Potion.getPotionById(11), duration, 0));
					((EntityWitch) entityIn).addPotionEffect(new PotionEffect(Potion.getPotionById(1), duration, 4));

					worldIn.playSound((EntityPlayer) null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
							SoundCategory.PLAYERS, 0.1F,
							0.5F * ((worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.7F + 2F));
					worldIn.playSound((EntityPlayer) null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_TOUCH,
							SoundCategory.PLAYERS, 0.1F,
							0.5F * ((worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.7F + 2F));
					// note: this deletes the tile entity and creates a new one
					worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(FILL_LEVEL, 0), 3);

					// Get the new tile entity
					manaWell = (TileEntityManaWell) worldIn.getTileEntity(pos);

					manaWell.setIsDormant(true);
					// get the total world time (in tecks) at the time of
					// release
					manaWell.setDormantStartTime(worldIn.getTotalWorldTime());
					manaWell.setCanRelease(false);
					manaWell.setStoredMana(0);

				}
			}
		}
	}

	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		this.attemptRelease(worldIn, pos, playerIn);
		return true;
	}

	public void drainMana(World world, BlockPos pos, EntityPlayer player, TileEntityManaWell manaWell) {
		if (player != null && !player.capabilities.isCreativeMode) {
			if (player.experienceTotal > 0) {
				int amountToDrain = 7 + world.rand.nextInt(8); // 7-14
				this.removeExperience(player, amountToDrain);

				if (!manaWell.getIsDormant()) {
					manaWell.setStoredMana(manaWell.getStoredMana() + amountToDrain);
				}

				// visual and audio effect that occur in either case
				if (!world.isRemote) // this is a redundant check - all this
										// stuff runs only on the server
				{
					WorldServer worldserver = (WorldServer) world;
					if (!world.getBlockState(pos.up()).getBlock().isOpaqueCube(getDefaultState())) {
						worldserver.spawnParticle(EnumParticleTypes.SMOKE_LARGE, false, pos.getX() + 0.5D,
								pos.getY() + 1.0D, pos.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D, new int[0]);
					} else if (!world.getBlockState(pos.up(2)).getBlock().isOpaqueCube(getDefaultState())) {
						worldserver.spawnParticle(EnumParticleTypes.SMOKE_LARGE, false, pos.getX() + 0.5D,
								pos.getY() + 1.0D, pos.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D, new int[0]);
					}

					worldserver.spawnParticle(EnumParticleTypes.SPELL_INSTANT, false, player.posX, player.posY,
							player.posZ, 18, 0.33D, 1.33D, 0.33D, 0.06D, new int[0]);
				}

				this.playManaWellDrainSound(world, player, pos);

			}
		}
	}

	public EntityPlayer getClosestPlayerWithXP(World world, double x, double y, double z, double distance) {
		double d4 = -1.0D;
		EntityPlayer entityplayer = null;

		for (int i = 0; i < world.playerEntities.size(); ++i) {
			EntityPlayer entityplayer1 = (EntityPlayer) world.playerEntities.get(i);

			if (entityplayer1.experienceTotal > 0 && !entityplayer1.capabilities.isCreativeMode
					&& !entityplayer1.isSpectator()) {
				double d5 = entityplayer1.getDistanceSq(x, y, z);

				if ((distance < 0.0D || d5 < distance * distance) && (d4 == -1.0D || d5 < d4)) {
					d4 = d5;
					entityplayer = entityplayer1;
				}
			}
		}
		return entityplayer;
	}

	private void attemptRelease(World worldIn, BlockPos pos, EntityPlayer playerIn) {
		TileEntityManaWell manaWell = (TileEntityManaWell) worldIn.getTileEntity(pos);
		if (manaWell.getCanRelease() && playerIn.capabilities.allowEdit) {
			releaseXP(worldIn, pos, manaWell.getStoredMana());
			// note: this deletes the tile entity and creates a new one
			worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(FILL_LEVEL, 0), 3);

			// Get the new tile entity
			manaWell = (TileEntityManaWell) worldIn.getTileEntity(pos);

			manaWell.setStoredMana(0);
			manaWell.setCanRelease(false);
			manaWell.setIsDormant(true);
			// get the total world time (in ticks) at the time of release
			manaWell.setDormantStartTime(worldIn.getTotalWorldTime());
		}
	}

	public void releaseXP(World worldIn, BlockPos pos, int amount) {

		if (!worldIn.isRemote) {
			while (amount > 0) {
				int j = EntityXPOrb.getXPSplit(amount);
				amount -= j;

				worldIn.spawnEntity(new EntityXPOrb(worldIn, (double) pos.getX() + 0.5D,
						(double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, j));
			}
		}
	}

	/**
	 * Sets the light level of the block based on how full of mana it is
	 */

	public int setLightLevel(IBlockState state, IBlockAccess world, BlockPos pos) {

		Block block = world.getBlockState(pos).getBlock();
		if (block != this) {
			return block.getLightValue(state, world, pos);
		}

		IBlockState state1 = world.getBlockState(pos);

		int fillLevel = ((Integer) state1.getValue(FILL_LEVEL)).intValue();
		switch (fillLevel) {
		case 0:
			return 0;
		case 1:
			return 6;
		case 2:
			return 8;
		case 3:
			return 10;
		case 4:
			return 12;
		case 5:
			return 14;
		}
		return 0;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		TileEntityManaWell manaWell = new TileEntityManaWell();
		if (meta == 0) // meta will be 0 if the player placed the block; in that
						// case the mana well should start out empty
		{
			manaWell.setStoredMana(0);
			manaWell.setCanRelease(false);
		}
		return manaWell;
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FILL_LEVEL, Integer.valueOf(meta));
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state) {
		return ((Integer) state.getValue(FILL_LEVEL)).intValue();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FILL_LEVEL });
	}

	/**
	 * Remove experience points from the player.
	 */
	public void removeExperience(EntityPlayer playerIn, int amount) {

		if (playerIn.experienceTotal - amount < 0) {
			amount = playerIn.experienceTotal;
		}

		if (amount == 0)
			return;

		playerIn.experience -= (float) amount / (float) playerIn.xpBarCap();

		for (playerIn.experienceTotal -= amount; playerIn.experience < 0.0F; playerIn.experience = 1.0F
				- playerIn.experience / (float) playerIn.xpBarCap()) {
			playerIn.experience = Math.abs(playerIn.experience) * (float) playerIn.xpBarCap();
			playerIn.removeExperienceLevel(1);
		}

		if (playerIn.experienceTotal == 0) {
			playerIn.experienceLevel = 0;
			playerIn.experience = 0.0F;
		}
	}

	private void playManaWellFillSound(World world, EntityPlayer player, BlockPos pos) {

		float volume = 0.12f;// 0.033f;
		float pitch = 0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.8F);
		world.playSound((EntityPlayer) player, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_TOUCH, SoundCategory.PLAYERS, 0.1F,
				0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 2F));
	}

	private void playManaWellDrainSound(World world, EntityPlayer player, BlockPos pos) {
		this.playManaWellFillSound(world, player, pos);
		world.playSound((EntityPlayer) player, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F,
				0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 2F));
	}

}
