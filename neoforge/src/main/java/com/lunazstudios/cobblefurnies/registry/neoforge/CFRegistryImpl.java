package com.lunazstudios.cobblefurnies.registry.neoforge;

import com.lunazstudios.cobblefurnies.CobbleFurnies;
import com.lunazstudios.cobblefurnies.registry.CFRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class CFRegistryImpl {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CobbleFurnies.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(CobbleFurnies.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> MOD_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CobbleFurnies.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CobbleFurnies.MOD_ID);
    public static final HashMap<String, List<Supplier<? extends ItemLike>>> ITEMS_TAB_MAP = new HashMap<>();

    public static <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    public static <T extends Item> Supplier<T> registerItem(String name, Supplier<T> item, String tab_id) {
        if (tab_id != null) {
            ITEMS_TAB_MAP.computeIfAbsent(tab_id, k -> new ArrayList<>());
            ITEMS_TAB_MAP.get(tab_id).add(item);
        }
        return ITEMS.register(name, item);
    }

    public static <T extends BlockEntityType<E>, E extends BlockEntity> Supplier<T> registerBlockEntityType(String name, Supplier<T> blockEntity) {
        return BLOCK_ENTITY_TYPES.register(name, blockEntity);
    }

    public static <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(CFRegistry.BlockEntitySupplier<T> blockEntity, Block... validBlocks) {
        return BlockEntityType.Builder.of(blockEntity::create, validBlocks).build(null);
    }

    public static <T extends BlockEntity> void registerBlockEntityRenderer(Supplier<BlockEntityType<T>> type, BlockEntityRendererProvider<T> renderProvider) {
        BlockEntityRenderers.register(type.get(), renderProvider);
    }

    public static Collection<ItemStack> getAllModItems() {
        List<ItemStack> itemList = new ArrayList<>();
        for (DeferredHolder<Item, ? extends Item> itemRegistryObject : ITEMS.getEntries()) {
            itemList.add(itemRegistryObject.get().getDefaultInstance());
        }
        return itemList;
    }
}
