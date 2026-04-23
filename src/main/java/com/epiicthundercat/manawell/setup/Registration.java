package com.epiicthundercat.manawell.setup;

import com.epiicthundercat.manawell.blocks.manawellblocks.ManaWellBedrockBlock;
import com.epiicthundercat.manawell.blocks.manawellblocks.ManaWellBedrockEntity;
import com.epiicthundercat.manawell.worldgen.GenManaWell;
import com.epiicthundercat.manawell.worldgen.ManaWellBiomeModifier;
import com.mojang.serialization.MapCodec;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.epiicthundercat.manawell.Reference.MODID;

public class Registration {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // BLOCK_ENTITIES renamed to BLOCK_ENTITY_TYPES in 1.20.1
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MODID);
    // Forge 52: BiomeModifier serializer registry uses MapCodec instead of Codec.
    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, MODID);

    // Creative tabs use Registries.CREATIVE_MODE_TAB in 1.20.1 (replacing the old new CreativeModeTab() constructor).
    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);


    // Forge 52: bus is injected in the @Mod constructor and passed here (FMLJavaModLoadingContext.get() removed).
    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        FEATURES.register(bus);
        BIOME_MODIFIER_SERIALIZERS.register(bus);
        CREATIVE_TABS.register(bus);
    }

    // Item.Properties no longer has .tab() in 1.20.1 — items are added to tabs via BuildCreativeModeTabContentsEvent.
    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties();

    // --- Blocks & Items ---
    // Declared BEFORE MANA_WELLS_TAB so the tab's icon() lambda can reference MANA_WELL_BEDROCK_ITEM.
    // (Mirrors the forward-reference fix from the HempFarmer 1.18.2->1.20.1 guide.)

    public static final RegistryObject<ManaWellBedrockBlock> MANA_WELL_BEDROCK = BLOCKS.register("manawell_bedrock",
            () -> new ManaWellBedrockBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK)
                    .noOcclusion()
                    // noDrops() was removed in 1.20.1; noLootTable() is the replacement.
                    .noLootTable()
                    .sound(SoundType.METAL)
                    .randomTicks()));

    public static final RegistryObject<Item> MANA_WELL_BEDROCK_ITEM = ITEMS.register("manawell_bedrock",
            () -> new BlockItem(MANA_WELL_BEDROCK.get(), ITEM_PROPERTIES));

    // --- Creative Tab ---

    public static final RegistryObject<CreativeModeTab> MANA_WELLS_TAB = CREATIVE_TABS.register(ModSetup.TAB_NAME, () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + ModSetup.TAB_NAME))
                    .icon(() -> new ItemStack(MANA_WELL_BEDROCK_ITEM.get()))
                    .build()
    );

    // --- Block Entities ---

    public static final RegistryObject<BlockEntityType<ManaWellBedrockEntity>> MANA_WELL_BEDROCK_BE =
            BLOCK_ENTITIES.register("manawell_bedrock_block_entity",
                    () -> BlockEntityType.Builder.of(ManaWellBedrockEntity::new,
                            MANA_WELL_BEDROCK.get()).build(null));

    // --- World Generation Features ---

    // The Feature is still registered here. Biome injection is handled by ManaWellBiomeModifier
    // (codec registered below) and the JSON at data/manawell/forge/biome_modifiers/mana_well.json.
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> MANA_WELL_FEATURE =
            FEATURES.register("mana_well_feature", GenManaWell::new);

    // Codec.unit always returns INSTANCE; the JSON only needs to declare the type, no extra fields.
    public static final RegistryObject<MapCodec<ManaWellBiomeModifier>> MANA_WELL_BIOME_MODIFIER_CODEC =
            BIOME_MODIFIER_SERIALIZERS.register("mana_well_biome_modifier",
                    () -> MapCodec.unit(ManaWellBiomeModifier.INSTANCE));

}
