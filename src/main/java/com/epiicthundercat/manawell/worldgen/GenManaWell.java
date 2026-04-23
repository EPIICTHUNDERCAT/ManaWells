package com.epiicthundercat.manawell.worldgen;

import com.epiicthundercat.manawell.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Places a single Mana Well block at the bedrock layer and clears bedrock above it for access. */
public class GenManaWell extends Feature<NoneFeatureConfiguration> {

    public GenManaWell() {
        super(NoneFeatureConfiguration.CODEC);
        // DEBUG — fires once at mod load if DeferredRegister wired up correctly.
        //System.out.println("[ManaWell-Gen] GenManaWell Feature registered!");
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        BlockPos pos = origin;

        // DEBUG — comment out for release.
      //  System.out.println("[ManaWell-Gen] place() called at origin=" + origin);

        // Scan downward to find the topmost bedrock block.
        // In 1.20.1 bedrock spans Y=-60 to Y=-64. Scan to Y=-64 to handle all cases.
        // The absolute bottom (Y=-64) is left intact; we target Y=-63 or higher.
        while (!level.getBlockState(pos).is(Blocks.BEDROCK) && pos.getY() > -64) {
            pos = pos.below();
        }

        if (!level.getBlockState(pos).is(Blocks.BEDROCK)) {
           // System.out.println("[ManaWell-Gen] SKIPPED — no bedrock found from " + origin + " down to " + pos);
            return false;
        }
        // Don't replace the absolute-bottom layer (Y=-64) — it prevents void holes if the mod is removed.
        if (pos.getY() <= -64) {
            pos = pos.above();
            if (!level.getBlockState(pos).is(Blocks.BEDROCK)) {
               // System.out.println("[ManaWell-Gen] SKIPPED — Y=-64 only, no bedrock at Y=-63");
                return false;
            }
        }

        // Scan the entire bedrock column at this XZ for an existing well.
        // Without this, a well placed at Y=-60 is no longer bedrock, so the downward scan
        // skips it and places a second well at Y=-61 directly underneath.
        for (int checkY = -64; checkY <= -59; checkY++) {
            if (level.getBlockState(new BlockPos(pos.getX(), checkY, pos.getZ()))
                    .is(Registration.MANA_WELL_BEDROCK.get())) return false;
        }

        // Place with fill_level=5 so the block shows the full texture immediately on generation.
        // The BlockEntity spawns with storedMana=MANA_CAP, so state and entity are in sync from the start.
        level.setBlock(pos, Registration.MANA_WELL_BEDROCK.get().defaultBlockState()
                .setValue(com.epiicthundercat.manawell.blocks.manawellblocks.ManaWellBedrockBlock.FILL_LEVEL, 5), 3);

        // Guarantee bedrock under the well so removing the mod never leaves a void hole.
        if (!level.getBlockState(pos.below()).is(Blocks.BEDROCK)) {
            level.setBlock(pos.below(), Blocks.BEDROCK.defaultBlockState(), 2);
        }

        // Clear bedrock above the well for player accessibility and to avoid two-deep holes.
        if (pos.getY() < -59) {
            // Use pos directly; offset(x, 1, z) starts one block above pos, never at pos itself.
            BlockPos clearOrigin = pos;

            // 3x3 one block above
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    replaceIfBedrock(level, clearOrigin.offset(x, 1, z));
                }
            }

            // 5x5 minus corners two blocks above (only needed for the deepest spawn layers)
            if (clearOrigin.getY() < -61) {
                for (int x = -2; x <= 2; x++) {
                    for (int z = -2; z <= 2; z++) {
                        if (!(Math.abs(x) == 2 && Math.abs(z) == 2)) {
                            replaceIfBedrock(level, clearOrigin.offset(x, 2, z));
                        }
                    }
                }
            }
        }

        return true;
    }

    // Replaces a bedrock block with the first non-bedrock block found above it (scan ceiling Y=-58).
    private void replaceIfBedrock(WorldGenLevel level, BlockPos pos) {
        if (!level.getBlockState(pos).is(Blocks.BEDROCK)) {
            return;
        }
        BlockPos replacementPos = pos.above();
        while (level.getBlockState(replacementPos).is(Blocks.BEDROCK) && replacementPos.getY() < -58) {
            replacementPos = replacementPos.above();
        }
        BlockState replacementBlock = level.getBlockState(replacementPos);
        level.setBlock(pos, replacementBlock, 3);
    }
}
