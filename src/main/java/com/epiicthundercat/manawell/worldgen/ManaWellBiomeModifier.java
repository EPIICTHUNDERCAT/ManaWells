package com.epiicthundercat.manawell.worldgen;

import com.epiicthundercat.manawell.setup.MWConfig;
import com.epiicthundercat.manawell.setup.Registration;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

import java.util.List;

/**
 * Custom BiomeModifier that injects the ManaWell feature into all overworld biomes.
 * Builds PlacedFeature inline via Holder.direct() to bypass the dynamic registry JSON chain,
 * which silently failed when using forge:add_features with a modded PlacedFeature ID.
 * Rarity is config-driven (MWConfig.MANAWELL_RARITY), restoring the 1.18.2 behavior.
 * BiomeFilter.biome() is excluded from placement modifiers because it compares by registry
 * key and would reject Holder.direct() instances; overworld filtering is done here instead.
 */
public class ManaWellBiomeModifier implements BiomeModifier {

    public static final ManaWellBiomeModifier INSTANCE = new ManaWellBiomeModifier();

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        // DEBUG — fires once per overworld biome during world load. Remove for release.
      //  if (phase == Phase.ADD) System.out.println("[ManaWell-Biome] modify() phase=ADD biome=" + biome.unwrapKey().map(Object::toString).orElse("?") + " isOverworld=" + biome.is(BiomeTags.IS_OVERWORLD));
      //  if (phase != Phase.ADD || !biome.is(BiomeTags.IS_OVERWORLD)) return;

        int rarity = MWConfig.MANAWELL_RARITY.get();
      //  System.out.println("[ManaWell-Biome] Adding feature to overworld biome, rarity=" + rarity);
        PlacedFeature placed = new PlacedFeature(
            Holder.direct(new ConfiguredFeature<>(
                Registration.MANA_WELL_FEATURE.get(), NoneFeatureConfiguration.INSTANCE)),
            List.of(
                RarityFilter.onAverageOnceEvery(rarity),
                InSquarePlacement.spread(),
                // Bedrock layer spans Y=-60 to Y=-64. Uniform over this range mirrors how vanilla
                // places ORE_GOLD_LOWER (absolute(-64) to absolute(-48)). GenManaWell.place() scans
                // downward from the origin to find the topmost bedrock block in that layer.
                HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(-60))
            )
        );
        builder.getGenerationSettings().addFeature(
            GenerationStep.Decoration.UNDERGROUND_STRUCTURES,
            Holder.direct(placed)
        );
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return Registration.MANA_WELL_BIOME_MODIFIER_CODEC.get();
    }
}
