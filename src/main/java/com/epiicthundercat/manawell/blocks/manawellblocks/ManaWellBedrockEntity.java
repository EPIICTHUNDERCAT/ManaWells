package com.epiicthundercat.manawell.blocks.manawellblocks;

import com.epiicthundercat.manawell.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static com.epiicthundercat.manawell.blocks.manawellblocks.ManaWellBedrockBlock.*;

public class ManaWellBedrockEntity extends BlockEntity {


    private boolean canRelease = true;
    private boolean isDormant = false;
    private long dormantStartTime;
    private int storedMana = MANA_CAP; //mana wells are 'full' when they spawn
    private int collideTimer = 0;

    public ManaWellBedrockEntity(BlockPos pos, BlockState state) {
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

    public int getCollideTimer() {
        return collideTimer;
    }

    public void setCollideTimer(int collideTimer) {
        this.collideTimer = collideTimer;
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
    public static void tick(Level level, BlockPos pos, BlockState pState, ManaWellBedrockEntity pBlockEntity) {
        // DEBUG — uncomment to log fill/mana state every 200 ticks (~10s). Comment out for release.
        // boolean debugLog = level.getGameTime() % 200 == 0;

        pBlockEntity = (ManaWellBedrockEntity) level.getBlockEntity(pos);
        if (pBlockEntity == null) return;

        int dormantTime = com.epiicthundercat.manawell.setup.MWConfig.MANAWELL_DORMANT_TIME.get();
        if (pBlockEntity.getIsDormant()) {
            long currentTime = level.getGameTime();
            long dormStart   = pBlockEntity.getDormantStartTime();
            // if (debugLog) System.out.println("[ManaWell] DORMANT at " + pos + " started=" + dormStart + " current=" + currentTime + " endsAt=" + (dormStart + dormantTime));
            if (dormantTime == 0 || currentTime >= dormStart + dormantTime) {
                pBlockEntity.setIsDormant(false);
            }
            return;
        }

        int manaCap    = com.epiicthundercat.manawell.setup.MWConfig.MANAWELL_MANA_CAP.get();
        int fillLevel  = pState.getValue(FILL_LEVEL);
        int storedMana = pBlockEntity.getStoredMana();

        // generate mana each tick
        int generatedMana = 0;
        int fillSpeed = com.epiicthundercat.manawell.setup.MWConfig.MANAWELL_FILL_SPEED.get();
        if (fillLevel < 5) {
            if (level.getRandom().nextInt(fillSpeed) == 0) // 1-in-fillSpeed chance per tick
                generatedMana = 1 + level.getRandom().nextInt(7); // 1-7 mana on a successful tick
        } else {
            // above manaCap, generation slows exponentially
            int chance;
            if      (storedMana <= manaCap + (manaCap / 2))        chance = 1;
            else if (storedMana <= manaCap * 2)                     chance = 2;
            else if (storedMana <= manaCap * 2 + manaCap / 2)      chance = 4;
            else if (storedMana <= manaCap * 3)                     chance = 8;
            else if (storedMana <= manaCap * 3 + manaCap / 2)      chance = 16;
            else if (storedMana <= manaCap * 4)                     chance = 32;
            else if (storedMana <= manaCap * 4 + manaCap / 2)      chance = 64;
            else                                                     chance = 128;
            if (level.getRandom().nextInt(chance) == 0)
                generatedMana = 1 + level.getRandom().nextInt(3);
        }
        storedMana += generatedMana;

        // determine new fill level from storedMana thresholds
        int newFillLevel;
        if      (storedMana >= manaCap)               newFillLevel = 5;
        else if (storedMana >= (manaCap / 4) * 3)     newFillLevel = 4;
        else if (storedMana >= (manaCap / 4) * 2)     newFillLevel = 3;
        else if (storedMana >= (manaCap / 4))          newFillLevel = 2;
        else if (storedMana >= 1)                      newFillLevel = 1;
        else                                           newFillLevel = 0;

        boolean blockChange = newFillLevel != fillLevel;
        if (blockChange) fillLevel = newFillLevel;

        // if (debugLog) System.out.println("[ManaWell] pos=" + pos + " fillLevel=" + fillLevel + " storedMana=" + storedMana + " generated=" + generatedMana + " changed=" + blockChange + " t=" + level.getGameTime());

        if (blockChange) {
            level.setBlock(pos, pState.setValue(FILL_LEVEL, fillLevel), 3);
            pBlockEntity = (ManaWellBedrockEntity) level.getBlockEntity(pos);
            if (pBlockEntity == null) return;
            if (fillLevel != 0) pBlockEntity.setCanRelease(true);
            if (fillLevel == 5) playManaWellFillSound(level, null, pos);
        }


        // ambient hum at max fill — staggered per position so multiple wells don't sync
        if (fillLevel == 5 && (level.getGameTime() + pos.getX() + pos.getZ()) % 60 == 0) {
            level.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL,
                    SoundSource.BLOCKS, 0.08F, 0.5F + level.getRandom().nextFloat() * 0.1F);
        }

        pBlockEntity.setStoredMana(storedMana);
        pBlockEntity.setChanged();

        // passive nearby drain — rate and amount config-driven
        int drainAmount = com.epiicthundercat.manawell.setup.MWConfig.MANAWELL_DRAIN_AMOUNT.get();
        int drainSpeed  = com.epiicthundercat.manawell.setup.MWConfig.MANAWELL_DRAIN_SPEED.get();
        if (drainAmount > 0 && level.getGameTime() % drainSpeed == 0) {
            Player player = getClosestPlayerWithXP(level, pos.getX(), pos.getY(), pos.getZ(), 16.0D);
            if (player != null)
                drainMana(level, pos, player, pBlockEntity, level.getRandom().nextInt(drainAmount) + 1);
        }

        // witch attraction — rate-limited to ~once per 800 ticks to prevent mass spawning
        if (fillLevel != 0 && level.getRandom().nextInt(800) == 0) {
            int i = 0;
            while (i < level.getDifficulty().getId()
                    && !attemptAttractWitch(pos, storedMana, level.random, level)) {
                i++;
            }
        }
        // Witch mana-steal fires from ManaWellBedrockBlock.stepOn(), not from the ticker.
        // The proactive AABB scan was removed — stepOn is the correct trigger (same as 1.18.2).
    }

}
