package com.epiicthundercat.manawell.blocks;

import com.epiicthundercat.manawell.blocks.manawellblocks.ManaWellBedrockBlock;

import com.epiicthundercat.manawell.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ManaWellBE extends BlockEntity {


    private boolean canRelease = true;
    private boolean isDormant = false;
    private long dormantStartTime;
    private int storedMana = ManaWellBedrockBlock.MANA_CAP; //mana wells are 'full' when they spawn

    public ManaWellBE(BlockPos pos, BlockState state) {
        super(Registration.MANA_WELL_BEDROCK_BE.get(), pos, state);
    }


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


    /**
     * These are the data components that save to the block - how it maintains energy and items after being broken and placed!
     */
    @Override
    public void load(CompoundTag tag) {

        if (tag.contains("canRelease")) {
            this.canRelease = tag.getBoolean("canRelease");
        }
        if (tag.contains("isDormant")) {
            this.isDormant = tag.getBoolean("isDormant");
        }
        if (tag.contains("dormantStartTime", LongTag.TAG_LONG)) {
            this.dormantStartTime = tag.getLong("dormantStartTime");
        }
        if (tag.contains("storedMana", IntTag.TAG_INT)) {
            this.storedMana = tag.getInt("storedMana");
        }
        super.load(tag);
    }

    /**
     * These are the data components that save to the block - how it maintains energy and items after being broken and placed!
     */

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.putBoolean("canRelease", this.canRelease);
        tag.putBoolean("isDormant", this.isDormant);
        tag.putLong("dormantStartTime", this.dormantStartTime);
        tag.putInt("storedMana", this.storedMana);


    }


}
