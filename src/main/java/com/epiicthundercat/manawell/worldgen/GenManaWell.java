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
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();

        // Scan downward to find the topmost bedrock block.
        // Stop at Y=-63 so the absolute bottom bedrock layer is never replaced (prevents void holes).
        while (!level.getBlockState(pos).is(Blocks.BEDROCK) && pos.getY() > -63) {
            pos = pos.below();
        }

        if (!level.getBlockState(pos).is(Blocks.BEDROCK)) {
            return false;
        }

        level.setBlock(pos, Registration.MANA_WELL_BEDROCK.get().defaultBlockState(), 3);

        // Guarantee bedrock under the well so removing the mod never leaves a void hole.
        if (!level.getBlockState(pos.below()).is(Blocks.BEDROCK)) {
            level.setBlock(pos.below(), Blocks.BEDROCK.defaultBlockState(), 2);
        }

        // Clear bedrock above the well for player accessibility and to avoid two-deep holes.
        if (pos.getY() < -59) {
            // When the well is at the lowest allowed Y, shift the clear origin up one so it
            // doesn't accidentally target the well block itself.
            BlockPos clearOrigin = pos.getY() == -63 ? pos.above() : pos;

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
