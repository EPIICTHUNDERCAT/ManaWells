package com.epiicthundercat.manawell.blocks.manawellblocks;

import com.epiicthundercat.manawell.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class ManaWellBedrockBlock extends BaseEntityBlock {
    /**
     * NOTES:
     * <p>
     * // SoundCategory.PLAYERS, 0.1F, ==== SoundSource.XX
     * level.rand === level.random
     * level.rand === level.getRandom
     */
    public static final IntegerProperty FILL_LEVEL = IntegerProperty.create("fill_level", 0, 5);

    // Used only as the spawn-time storedMana initializer — must match manawellManaCap config default
    public static final int MANA_CAP = 560;


    public ManaWellBedrockBlock(Properties properties) {

        /*
         * set to 5, so they have the correct (full) texture when they are
         * generated(when a player places one the meta value is 0; handled under
         * createNewTileEntity).
         */
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(FILL_LEVEL, 0));


    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {


        return new ManaWellBedrockEntity(pPos, pState);
    }


    public static boolean attemptAttractWitch(BlockPos pos, int storedMana, Random rand, Level level) {
        if (!(level instanceof ServerLevel serverLevel)) return false;

        int manaCap = com.epiicthundercat.manawell.setup.MWConfig.MANAWELL_MANA_CAP.get();
        int numPositionsToCheck = Math.min(storedMana / (manaCap / 10), 40);
        int maxLight = storedMana >= manaCap ? Math.min(8 + (storedMana - manaCap) / (manaCap / 2), 10) : 7;

        double range = 16;
        double posX = pos.getX(), posY = pos.getY(), posZ = pos.getZ();
        double x = posX + (rand.nextDouble() - rand.nextDouble()) * range + 0.5D;
        double y = posY + rand.nextDouble() * range + 0.5D;
        double z = posZ + (rand.nextDouble() - rand.nextDouble()) * range + 0.5D;
        BlockPos spawnPos = new BlockPos(x, y, z);

        int i = 0;
        while (i < numPositionsToCheck && !level.isEmptyBlock(spawnPos)) {
            x = posX + (rand.nextDouble() - rand.nextDouble()) * range + 0.5D;
            y = posY + rand.nextDouble() * range + 0.5D;
            z = posZ + (rand.nextDouble() - rand.nextDouble()) * range + 0.5D;
            spawnPos = new BlockPos(x, y, z);
            i++;
        }

        if (!level.isEmptyBlock(spawnPos)) return false;

        while (!level.getBlockState(spawnPos.below()).isCollisionShapeFullBlock(level, spawnPos.below()) && spawnPos.getY() > 0) {
            spawnPos = spawnPos.below();
        }
        y = spawnPos.getY();

        if (level.getBlockState(spawnPos.below()).getBlock() == Registration.MANA_WELL_BEDROCK.get()) return false;
        if (!getCanManaWellWitchSpawnHere(level, spawnPos, maxLight)) return false;

        Player closestPlayer = level.getNearestPlayer(x, y, z, 128.0D, false);
        if (closestPlayer == null || closestPlayer.isSpectator() || closestPlayer.distanceToSqr(x, y, z) <= 576) return false;

        // EntityType.spawn already adds the entity to the world
        Entity entity = EntityType.WITCH.spawn(serverLevel, null, null, null, spawnPos, MobSpawnType.STRUCTURE, false, false);
        if (entity == null) return false;

        entity.moveTo(x, y, z, rand.nextFloat() * 360.0F, 0.0F);

        serverLevel.sendParticles(ParticleTypes.POOF, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                5, 0.2D, 0.2D, 0.2D, 0.05D);
        return true;
    }


    public static boolean getCanManaWellWitchSpawnHere(Level world, BlockPos pos, int maxLight) {
        return world.getDifficulty() != Difficulty.PEACEFUL && world.getLightEmission(pos) <= maxLight;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entityIn) {
        if (!level.isClientSide()) {
            if (entityIn instanceof Player player) {
                ManaWellBedrockEntity manaWell = (ManaWellBedrockEntity) level.getBlockEntity(pos);
                if (manaWell == null) return;

                manaWell.setCollideTimer(manaWell.getCollideTimer() + 1);
                if (manaWell.getCollideTimer() >= 3) {
                    if (manaWell.getIsDormant()) {
                        if (level.getGameTime() > manaWell.getDormantStartTime() + 60) {
                            drainMana(level, pos, player, manaWell);
                        }
                    } else {
                        attemptRelease(level, pos, player);
                    }
                    manaWell.setCollideTimer(0);
                }
            } else if (entityIn instanceof Witch witch) {
                ManaWellBedrockEntity manaWell = (ManaWellBedrockEntity) level.getBlockEntity(pos);
                if (manaWell == null || !manaWell.getCanRelease()) return;
                witchStealMana(level, pos, state, witch, manaWell);
            }
        }
    }



    public static void witchStealMana(Level level, BlockPos pos, BlockState state, Witch witch, ManaWellBedrockEntity manaWell) {
        int duration = 80 + manaWell.getStoredMana() * 2;
        witch.addEffect(new MobEffectInstance(MobEffects.REGENERATION, duration, 4));
        witch.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, 0));
        witch.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, 4));

        level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F,
                0.5F * ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 2F));
        level.playSound(null, pos, SoundEvents.EXPERIENCE_BOTTLE_THROW, SoundSource.PLAYERS, 0.1F,
                0.5F * ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 2F));

        level.setBlock(pos, state.setValue(FILL_LEVEL, 0), 3);
        ManaWellBedrockEntity freshManaWell = (ManaWellBedrockEntity) level.getBlockEntity(pos);
        if (freshManaWell == null) return;
        freshManaWell.setIsDormant(true);
        freshManaWell.setDormantStartTime(level.getGameTime());
        freshManaWell.setCanRelease(false);
        freshManaWell.setStoredMana(0);
    }

    public static void drainMana(Level world, BlockPos pos, Player player, ManaWellBedrockEntity manaWell) {
        drainMana(world, pos, player, manaWell, 7 + world.getRandom().nextInt(8)); // stepOn: 7-14
    }

    public static void drainMana(Level world, BlockPos pos, Player player, ManaWellBedrockEntity manaWell, int amountToDrain) {
        if (player != null && !player.isCreative()) {
            if (player.totalExperience > 0) {
                removeExperience(player, amountToDrain);

                if (!manaWell.getIsDormant()) {
                    manaWell.setStoredMana(manaWell.getStoredMana() + amountToDrain);
                }

                // visual and audio effect that occur in either case
                ServerLevel worldServer = (ServerLevel) world;
                if (!world.getBlockState(pos.above()).canOcclude() || !world.getBlockState(pos.above(2)).canOcclude()) {
                    worldServer.sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5D,
                            pos.getY() + 1.0D, pos.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                worldServer.sendParticles(ParticleTypes.INSTANT_EFFECT, player.position().x(), player.position().y(),
                        player.position().z(), 18, 0.33D, 1.33D, 0.33D, 0.06D);

                playManaWellDrainSound(world, player, pos);

            }
        }
    }

    public static Player getClosestPlayerWithXP(Level world, double x, double y, double z, double distance) {
        double d4 = -1.0D;
        Player entityplayer = null;
//Check is player is nearest.
        for (int i = 0; i < world.players().size(); ++i) {
            Player entityPlayer1 = world.players().get(i);

            if (entityPlayer1.totalExperience > 0 && !entityPlayer1.isCreative()
                    && !entityPlayer1.isSpectator()) {
                double d5 = entityPlayer1.distanceToSqr(x, y, z);

                if ((distance < 0.0D || d5 < distance * distance) && (d4 == -1.0D || d5 < d4)) {
                    d4 = d5;
                    entityplayer = entityPlayer1;
                }
            }
        }
        return entityplayer;
    }

    private void attemptRelease(Level level, BlockPos pos, Player playerIn) {
        ManaWellBedrockEntity manaWell = (ManaWellBedrockEntity) level.getBlockEntity(pos);
        if (manaWell.getCanRelease() && playerIn.mayBuild()) {
            releaseXP(level, pos, manaWell.getStoredMana());
            // note: this deletes the tile entity and creates a new one
            // level.setBlockAndUpdate(pos, level.getBlockState(pos));
            level.setBlock(pos, level.getBlockState(pos).setValue(FILL_LEVEL, 0), 3);
            // Get the new tile entity
            manaWell = (ManaWellBedrockEntity) level.getBlockEntity(pos);

            manaWell.setStoredMana(0);
            manaWell.setCanRelease(false);
            manaWell.setIsDormant(true);
            // get the total world time (in ticks) at the time of release
            manaWell.setDormantStartTime(level.getGameTime());
        }
    }

    public void releaseXP(Level level, BlockPos pos, int amount) {

        if (!level.isClientSide()) {
            while (amount > 0) {
                int j = ExperienceOrb.getExperienceValue(amount);
                amount -= j;

                level.addFreshEntity(new ExperienceOrb(level, (double) pos.getX() + 0.5D,
                        (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, j));
            }
        }
    }

    @Override
    public int getLightEmission(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos) {
        return switch (state.getValue(FILL_LEVEL)) {
            case 1 -> 6;
            case 2 -> 8;
            case 3 -> 10;
            case 4 -> 12;
            case 5 -> 14;
            default -> 0;
        };
    }


    @Deprecated
    @Override
    public void onRemove(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pNewState, boolean pIsMoving) {
        // Only remove the block entity when the block TYPE changes (e.g. broken/replaced).
        // Changing fill_level keeps the same block — removing the entity here would kill the ticker.
        if (!pState.is(pNewState.getBlock())) {
            pLevel.removeBlockEntity(pPos);
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(FILL_LEVEL);
        super.createBlockStateDefinition(blockStateBuilder);
    }



    /**
     * Remove experience points from the player.
     */
    public static void removeExperience(Player playerIn, int amount) {
        amount = Math.min(amount, playerIn.totalExperience);
        if (amount <= 0) return;
        playerIn.giveExperiencePoints(-amount);
    }

    public static void playManaWellFillSound(Level world, Player player, BlockPos pos) {

        float volume = 0.12f;// 0.033f;
        float pitch = 0.5F * ((world.random.nextFloat() - world.random.nextFloat()) * 0.7F + 1.8F);
        world.playSound((Player) player, pos, SoundEvents.EXPERIENCE_BOTTLE_THROW, SoundSource.PLAYERS, 0.1F,
                0.5F * ((world.random.nextFloat() - world.random.nextFloat()) * 0.7F + 2F));
    }

    private static void playManaWellDrainSound(Level world, Player player, BlockPos pos) {
        playManaWellFillSound(world, player, pos);
        world.playSound((Player) player, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F,
                0.5F * ((world.random.nextFloat() - world.random.nextFloat()) * 0.7F + 2F));
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide() ? null : createTickerHelper(pBlockEntityType, Registration.MANA_WELL_BEDROCK_BE.get(), ManaWellBedrockEntity::tick);
    }

}
