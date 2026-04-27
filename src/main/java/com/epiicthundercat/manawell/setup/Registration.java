package com.epiicthundercat.manawell.setup;

import com.epiicthundercat.manawell.advancement.PlayerSteppedOnManaWellTrigger;
import com.epiicthundercat.manawell.advancement.WitchStoleManaProximityTrigger;
import com.epiicthundercat.manawell.blocks.manawellblocks.ManaWellBedrockBlock;
import com.epiicthundercat.manawell.blocks.manawellblocks.ManaWellBedrockEntity;
import com.epiicthundercat.manawell.worldgen.GenManaWell;
import com.epiicthundercat.manawell.worldgen.ManaWellBiomeModifier;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraft.core.registries.Registries;
import java.util.Set;
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
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.epiicthundercat.manawell.Reference.MODID;

public class Registration {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MODID);
    // MC 26.1.2: CriterionTriggers must be registered via DeferredRegister on Registries.TRIGGER_TYPE
    // so they are added BEFORE BuiltInRegistries are frozen. Using CriteriaTriggers.register() from
    // enqueueWork throws "Registry is already frozen".
    private static final DeferredRegister<CriterionTrigger<?>> CRITERION_TRIGGERS =
            DeferredRegister.create(Registries.TRIGGER_TYPE, MODID);
    // Forge 52+: BiomeModifier serializer registry uses MapCodec instead of Codec.
    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, MODID);

    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Forge 64 (26.1.2): IEventBus removed — DeferredRegister.register() now takes BusGroup.
    public static void init(BusGroup bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        FEATURES.register(bus);
        BIOME_MODIFIER_SERIALIZERS.register(bus);
        CREATIVE_TABS.register(bus);
        CRITERION_TRIGGERS.register(bus);
    }

    // MC 26.1.2: BlockBehaviour.Properties and Item.Properties both require .setId() before
    // the block/item constructor runs. DeferredRegister does not set it automatically.
    public static final RegistryObject<ManaWellBedrockBlock> MANA_WELL_BEDROCK = BLOCKS.register("manawell_bedrock",
            () -> new ManaWellBedrockBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK)
                    .setId(BLOCKS.key("manawell_bedrock"))
                    .noOcclusion()
                    .noLootTable()
                    .sound(SoundType.METAL)
                    .randomTicks()
                    .lightLevel(state -> switch (state.getValue(ManaWellBedrockBlock.FILL_LEVEL)) {
                        case 1 -> 6;
                        case 2 -> 8;
                        case 3 -> 10;
                        case 4 -> 12;
                        case 5 -> 14;
                        default -> 0;
                    })));

    public static final RegistryObject<Item> MANA_WELL_BEDROCK_ITEM = ITEMS.register("manawell_bedrock",
            () -> new BlockItem(MANA_WELL_BEDROCK.get(), new Item.Properties().setId(ITEMS.key("manawell_bedrock"))));

    public static final RegistryObject<CreativeModeTab> MANA_WELLS_TAB = CREATIVE_TABS.register(ModSetup.TAB_NAME, () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + ModSetup.TAB_NAME))
                    .icon(() -> new ItemStack(MANA_WELL_BEDROCK_ITEM.get()))
                    .build()
    );

    // MC 26.1.2: BlockEntityType.Builder removed. Use constructor directly: new BlockEntityType<>(factory, Set.of(blocks)).
    // The back-reference ManaWellBedrockEntity.blockEntityType is set in the static init below to break
    // the circular compile dependency (entity constructor → Registration → entity constructor body).
    public static final RegistryObject<BlockEntityType<ManaWellBedrockEntity>> MANA_WELL_BEDROCK_BE =
            BLOCK_ENTITIES.register("manawell_bedrock_block_entity",
                    () -> new BlockEntityType<>(ManaWellBedrockEntity::new, Set.of(MANA_WELL_BEDROCK.get())));

    static {
        ManaWellBedrockEntity.blockEntityType = MANA_WELL_BEDROCK_BE;
    }

    public static final RegistryObject<WitchStoleManaProximityTrigger> WITCH_STOLE_MANA_PROXIMITY =
            CRITERION_TRIGGERS.register("witch_stole_mana_proximity", WitchStoleManaProximityTrigger::new);

    public static final RegistryObject<PlayerSteppedOnManaWellTrigger> PLAYER_STEPPED_ON_WELL =
            CRITERION_TRIGGERS.register("player_stepped_on_well", PlayerSteppedOnManaWellTrigger::new);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> MANA_WELL_FEATURE =
            FEATURES.register("mana_well_feature", GenManaWell::new);

    public static final RegistryObject<MapCodec<ManaWellBiomeModifier>> MANA_WELL_BIOME_MODIFIER_CODEC =
            BIOME_MODIFIER_SERIALIZERS.register("mana_well_biome_modifier",
                    () -> MapCodec.unit(ManaWellBiomeModifier.INSTANCE));

}
