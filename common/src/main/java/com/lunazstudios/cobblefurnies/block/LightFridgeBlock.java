package com.lunazstudios.cobblefurnies.block;

import com.lunazstudios.cobblefurnies.registry.CFBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;

public class LightFridgeBlock extends AbstractFridgeBlock {
    public static final MapCodec<LightFridgeBlock> CODEC = simpleCodec(LightFridgeBlock::new);

    public LightFridgeBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected Block freezerBlock() {
        return CFBlocks.LIGHT_FREEZER.get();
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }
}
