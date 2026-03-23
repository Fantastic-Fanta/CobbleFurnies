package com.lunazstudios.cobblefurnies.block;

import com.lunazstudios.cobblefurnies.block.properties.CFBlockStateProperties;
import com.lunazstudios.cobblefurnies.util.block.ShapeUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class UpperCabinetBlock extends Block {
    public static final MapCodec<UpperCabinetBlock> CODEC = simpleCodec(UpperCabinetBlock::new);

    @Override
    public MapCodec<UpperCabinetBlock> codec() {
        return CODEC;
    }

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty HAS_GLASS = CFBlockStateProperties.HAS_GLASS;
    public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;

    protected static final VoxelShape SHAPE_NORTH = Block.box(0, 0, 6, 16, 16, 16);
    protected static final VoxelShape SHAPE_EAST = ShapeUtil.rotateShape(SHAPE_NORTH, Direction.EAST);
    protected static final VoxelShape SHAPE_SOUTH = ShapeUtil.rotateShape(SHAPE_NORTH, Direction.SOUTH);
    protected static final VoxelShape SHAPE_WEST = ShapeUtil.rotateShape(SHAPE_NORTH, Direction.WEST);

    public UpperCabinetBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false)
                .setValue(OPEN, false)
                .setValue(HAS_GLASS, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST -> SHAPE_EAST;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean waterlogged = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockPos pos = context.getClickedPos();
        Vec3 clickVec = context.getClickLocation().subtract(Vec3.atLowerCornerOf(pos));

        Direction right = facing.getClockWise();
        double side = right.getAxis().choose(clickVec.x, 0, clickVec.z);
        side = Math.abs(Math.min(right.getAxisDirection().getStep(), 0) + side);
        DoorHingeSide hinge = side > 0.5 ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT;

        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(WATERLOGGED, waterlogged)
                .setValue(OPEN, false)
                .setValue(HINGE, hinge)
                .setValue(HAS_GLASS, false);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, OPEN, HINGE, HAS_GLASS);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
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
