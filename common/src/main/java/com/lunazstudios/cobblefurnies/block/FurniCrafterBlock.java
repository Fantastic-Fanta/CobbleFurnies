package com.lunazstudios.cobblefurnies.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class FurniCrafterBlock extends Block {
    public static final MapCodec<FurniCrafterBlock> CODEC = simpleCodec(FurniCrafterBlock::new);

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    public FurniCrafterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
