package com.epiicthundercat.manawell.datagen;

import com.epiicthundercat.manawell.Reference;
import com.epiicthundercat.manawell.blocks.manawellblocks.ManaWellBedrockBlock;
import com.epiicthundercat.manawell.setup.Registration;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;

// Constructor takes PackOutput instead of DataGenerator in 1.20.1.
public class ManaWellsBlockStates extends BlockStateProvider {

    public ManaWellsBlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Reference.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // Generate blockstate JSON with one variant per fill_level (0-5)
        getVariantBuilder(Registration.MANA_WELL_BEDROCK.get())
                .forAllStates(state -> {
                    int fillLevel = state.getValue(ManaWellBedrockBlock.FILL_LEVEL);
                    return ConfiguredModel.builder()
                            .modelFile(models().getExistingFile(modLoc("block/manawell_bedrock" + fillLevel)))
                            .build();
                });

        // Item model — display as the full (level 5) variant in inventory
        itemModels().withExistingParent("manawell_bedrock", modLoc("block/manawell_bedrock5"));
    }
}
