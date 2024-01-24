package com.epiicthundercat.manawell.setup;

import com.epiicthundercat.manawell.blocks.ManaWellBlock;
import com.epiicthundercat.manawell.blocks.ManaWellBE;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);


    }

    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties().tab(ModSetup.ITEM_GROUP);


    //BlocksBelow

    //Mana Well Registrations - , Block Entity, Block, and Item //
    public static final RegistryObject<ManaWellBlock> MANA_WELL_BEDROCK = BLOCKS.register("manawell_bedrock", ManaWellBlock::new);
    public static final RegistryObject<Item> MANA_WELL_BEDROCK_ITEM = fromBlock(MANA_WELL_BEDROCK);
    public static final RegistryObject<BlockEntityType<ManaWellBE>> MANA_WELL_BEDROCK_BE = BLOCK_ENTITIES.register("manawell_bedrock",
            () -> BlockEntityType.Builder.of(ManaWellBE::new, MANA_WELL_BEDROCK.get()).build(null));





    public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {

        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));

    }

    public static int getIdFromBlock(BlockState blockState) {
        int id = Block.getId(blockState);
        return id;
    }


}
