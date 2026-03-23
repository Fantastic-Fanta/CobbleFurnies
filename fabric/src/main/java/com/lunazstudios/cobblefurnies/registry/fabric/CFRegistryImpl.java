package com.lunazstudios.cobblefurnies.registry.fabric;

import com.lunazstudios.cobblefurnies.CobbleFurnies;
import com.lunazstudios.cobblefurnies.registry.CFRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class CFRegistryImpl {

    public static <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> block) {
        T registry = Registry.register(BuiltInRegistries.BLOCK, CobbleFurnies.id(name), block.get());
        return () -> registry;
    }

    public static <T extends Item> Supplier<T> registerItem(String name, Supplier<T> item, String tab_id) {
        T registry = Registry.register(BuiltInRegistries.ITEM, CobbleFurnies.id(name), item.get());
        itemList.add(registry.getDefaultInstance());
        return () -> registry;
    }

    public static <T extends BlockEntityType<E>, E extends BlockEntity> Supplier<T> registerBlockEntityType(String name, Supplier<T> blockEntity) {
        T registry = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, CobbleFurnies.id(name), blockEntity.get());
        return () -> registry;
    }

    public static <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(CFRegistry.BlockEntitySupplier<T> blockEntity, Block... validBlocks) {
        return BlockEntityType.Builder.of(blockEntity::create, validBlocks).build();
    }

    public static <T extends BlockEntity> void registerBlockEntityRenderer(Supplier<BlockEntityType<T>> type, BlockEntityRendererProvider<T> renderProvider) {
        BlockEntityRenderers.register(type.get(), renderProvider);
    }

    static final List<ItemStack> itemList = new ArrayList<>();

    public static Collection<ItemStack> getAllModItems() {
        return itemList;
    }
}
