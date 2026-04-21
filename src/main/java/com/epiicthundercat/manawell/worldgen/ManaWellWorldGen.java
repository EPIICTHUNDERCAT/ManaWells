package com.epiicthundercat.manawell.worldgen;

import com.epiicthundercat.manawell.Reference;
import com.epiicthundercat.manawell.setup.MWConfig;
import com.epiicthundercat.manawell.setup.Registration;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/** Injects Mana Well generation into overworld biomes via BiomeLoadingEvent. */
@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ManaWellWorldGen {

    @SubscribeEvent
    public static void onBiomeLoading(BiomeLoadingEvent event) {
        // Skip Nether, End, and unclassified biomes — only the overworld has the right bedrock layout.
        Biome.BiomeCategory category = event.getCategory();
        if (category == Biome.BiomeCategory.NETHER
                || category == Biome.BiomeCategory.THEEND
                || category == Biome.BiomeCategory.NONE) {
            return;
        }

        event.getGeneration().addFeature(
                GenerationStep.Decoration.UNDERGROUND_STRUCTURES,
                buildManaWellHolder()
        );
    }

    private static Holder<PlacedFeature> buildManaWellHolder() {
        // Pair the registered Feature with its configuration (none needed).
        // Second type param must be Feature<FC>, not GenManaWell — DeferredRegister erases to Feature<FC>.
        ConfiguredFeature<NoneFeatureConfiguration, Feature<NoneFeatureConfiguration>> configuredFeature =
                new ConfiguredFeature<>(
                        Registration.MANA_WELL_FEATURE.get(),
                        NoneFeatureConfiguration.INSTANCE
                );

        PlacedFeature placedFeature = new PlacedFeature(
                Holder.direct(configuredFeature),
                List.of(
                        RarityFilter.onAverageOnceEvery(MWConfig.MANAWELL_RARITY.get()), // 1-in-N chance per chunk
                        InSquarePlacement.spread(),                                        // random XZ within the chunk
                        HeightRangePlacement.uniform(                                      // fixed start Y = -64+5 = -59
                                VerticalAnchor.aboveBottom(5),
                                VerticalAnchor.aboveBottom(5)
                        ),
                        BiomeFilter.biome()                                                // respect datapack blacklists
                )
        );

        return Holder.direct(placedFeature);
    }
}
