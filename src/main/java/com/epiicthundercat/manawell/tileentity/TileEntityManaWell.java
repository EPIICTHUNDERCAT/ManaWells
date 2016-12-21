package com.epiicthundercat.manawell.tileentity;

import com.epiicthundercat.manawell.blocks.BlockManaWell;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityManaWell extends TileEntity {


	private boolean canRelease = true;
	private boolean isDormant = false;
	private long dormantStartTime;
	private int storedMana = BlockManaWell.MANA_CAP; //mana wells are 'full' when they spawn
	
	
	
	public long getDormantStartTime() {
		return dormantStartTime;
	}

	public void setDormantStartTime(long dormantStartTime) {
		this.dormantStartTime = dormantStartTime;
	}
	
	public boolean getCanRelease() {
		return canRelease;
	}

	public void setCanRelease(boolean canRelease) {
		this.canRelease = canRelease;
	}
	
	public boolean getIsDormant() {
		return isDormant;
	}

	public void setIsDormant(boolean isDormant) {
		this.isDormant = isDormant;
	}

	public int getStoredMana() {
		return storedMana;
	}
	
	public void setStoredMana(int storedMana) {
		this.storedMana = storedMana;
	}
	
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.canRelease = compound.getBoolean("canRelease");
		this.isDormant = compound.getBoolean("isDormant");
		this.dormantStartTime = compound.getLong("dormantStartTime");
		this.storedMana = compound.getInteger("storedMana");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		
		compound.setBoolean("canRelease", canRelease);
		compound.setBoolean("isDormant", isDormant);
		compound.setLong("dormantStartTime", dormantStartTime);
		compound.setInteger("storedMana", storedMana);
		return compound;
	}
	
	
}
