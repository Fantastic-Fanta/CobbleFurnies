package com.lunazstudios.cobblefurnies.block;

import com.lunazstudios.cobblefurnies.registry.CFBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;

public class DarkFridgeBlock extends AbstractFridgeBlock {
    public static final MapCodec<DarkFridgeBlock> CODEC = simpleCodec(DarkFridgeBlock::new);

    public DarkFridgeBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected Block freezerBlock() {
        return CFBlocks.DARK_FREEZER.get();
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }
}
