package com.epiicthundercat.manawell.setup;

import com.epiicthundercat.manawell.blocks.manawellblocks.ManaWellBedrockBlock;
import com.epiicthundercat.manawell.blocks.manawellblocks.ManaWellBedrockEntity;
import com.epiicthundercat.manawell.worldgen.GenManaWell;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.epiicthundercat.manawell.Reference.MODID;

public class Registration {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MODID);

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        FEATURES.register(bus);
    }

    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties().tab(ModSetup.ITEM_GROUP);

    // --- Blocks & Items ---

    public static final RegistryObject<ManaWellBedrockBlock> MANA_WELL_BEDROCK = BLOCKS.register("manawell_bedrock",
            () -> new ManaWellBedrockBlock(BlockBehaviour.Properties.copy(Blocks.BEDROCK)
                    .noOcclusion()
                    .noDrops()
                    .sound(SoundType.METAL)
                    .randomTicks()));

    public static final RegistryObject<Item> MANA_WELL_BEDROCK_ITEM = ITEMS.register("manawell_bedrock",
            () -> new BlockItem(MANA_WELL_BEDROCK.get(), ITEM_PROPERTIES));


    // --- Block Entities ---

    public static final RegistryObject<BlockEntityType<ManaWellBedrockEntity>> MANA_WELL_BEDROCK_BE =
            BLOCK_ENTITIES.register("manawell_bedrock_block_entity",
                    () -> BlockEntityType.Builder.of(ManaWellBedrockEntity::new,
                            MANA_WELL_BEDROCK.get()).build(null));

    // --- World Generation Features ---

    // 1.10.2 equivalent: GameRegistry.registerWorldGenerator(new ManaWellWorldGen(), 0)
    // In 1.18.2, features are registered here and injected into biomes via BiomeLoadingEvent.
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> MANA_WELL_FEATURE =
            FEATURES.register("mana_well_feature", GenManaWell::new);
}
