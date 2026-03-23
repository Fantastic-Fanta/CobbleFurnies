package com.lunazstudios.cobblefurnies.block;

import com.lunazstudios.cobblefurnies.util.block.ShapeUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FreezerBlock extends Block {
    public static final MapCodec<FreezerBlock> CODEC = simpleCodec(FreezerBlock::new);

    @Override
    public MapCodec<FreezerBlock> codec() {
        return CODEC;
    }

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    protected static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(13, 2, -1, 14, 14, 0),
            Block.box(0, 0, 0, 16, 16, 2),
            Block.box(15, 0, 2, 16, 16, 16),
            Block.box(0, 0, 2, 1, 16, 16),
            Block.box(1, 0, 2, 15, 1, 16),
            Block.box(1, 8, 2, 15, 8, 16),
            Block.box(1, 1, 16, 15, 15, 16),
            Block.box(1, 15, 2, 15, 16, 16)
    );
    protected static final VoxelShape SHAPE_EAST = ShapeUtil.rotateShape(SHAPE_NORTH, Direction.EAST);
    protected static final VoxelShape SHAPE_SOUTH = ShapeUtil.rotateShape(SHAPE_NORTH, Direction.SOUTH);
    protected static final VoxelShape SHAPE_WEST = ShapeUtil.rotateShape(SHAPE_NORTH, Direction.WEST);

    protected static final VoxelShape SHAPE_OPEN_NORTH = Shapes.or(
            Block.box(15, 0, 2, 16, 16, 16),
            Block.box(1, 8, 2, 15, 8, 16),
            Block.box(0, 0, 2, 1, 16, 16),
            Block.box(1, 0, 2, 15, 1, 16),
            Block.box(1, 1, 16, 15, 15, 16),
            Block.box(1, 15, 2, 15, 16, 16)
    );
    protected static final VoxelShape SHAPE_OPEN_EAST = ShapeUtil.rotateShape(SHAPE_OPEN_NORTH, Direction.EAST);
    protected static final VoxelShape SHAPE_OPEN_SOUTH = ShapeUtil.rotateShape(SHAPE_OPEN_NORTH, Direction.SOUTH);
    protected static final VoxelShape SHAPE_OPEN_WEST = ShapeUtil.rotateShape(SHAPE_OPEN_NORTH, Direction.WEST);

    public FreezerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, false));
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockPos fridgePos = pos.below();
            if (level.getBlockState(fridgePos).getBlock() instanceof AbstractFridgeBlock) {
                level.removeBlock(fridgePos, false);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) {
        Direction facing = state.getValue(FACING);
        boolean open = state.getValue(OPEN);
        return open
                ? switch (facing) {
            case EAST -> SHAPE_OPEN_EAST;
            case SOUTH -> SHAPE_OPEN_SOUTH;
            case WEST -> SHAPE_OPEN_WEST;
            default -> SHAPE_OPEN_NORTH;
        }
                : switch (facing) {
            case EAST -> SHAPE_EAST;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(OPEN, false);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return this.rotate(state, mirror.getRotation(state.getValue(FACING)));
    }
}
