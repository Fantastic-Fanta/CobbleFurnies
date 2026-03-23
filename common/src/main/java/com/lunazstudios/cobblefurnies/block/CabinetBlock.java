package com.lunazstudios.cobblefurnies.block;

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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CabinetBlock extends Block {
    public static final MapCodec<CabinetBlock> CODEC = simpleCodec(CabinetBlock::new);

    @Override
    public MapCodec<CabinetBlock> codec() {
        return CODEC;
    }

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;

    protected static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(0, 0, 2, 16, 16, 16),
            Block.box(0, 0, 0, 16, 16, 2)
    );
    protected static final VoxelShape SHAPE_EAST = ShapeUtil.rotateShape(SHAPE_NORTH, Direction.EAST);
    protected static final VoxelShape SHAPE_SOUTH = ShapeUtil.rotateShape(SHAPE_NORTH, Direction.SOUTH);
    protected static final VoxelShape SHAPE_WEST = ShapeUtil.rotateShape(SHAPE_NORTH, Direction.WEST);

    protected static final VoxelShape SHAPE_OPEN_LEFT_NORTH = Shapes.or(
            Block.box(0, 0, 2, 16, 16, 16),
            Block.box(14, 0, -14, 16, 16, 2)
    );
    protected static final VoxelShape SHAPE_OPEN_LEFT_EAST = ShapeUtil.rotateShape(SHAPE_OPEN_LEFT_NORTH, Direction.EAST);
    protected static final VoxelShape SHAPE_OPEN_LEFT_SOUTH = ShapeUtil.rotateShape(SHAPE_OPEN_LEFT_NORTH, Direction.SOUTH);
    protected static final VoxelShape SHAPE_OPEN_LEFT_WEST = ShapeUtil.rotateShape(SHAPE_OPEN_LEFT_NORTH, Direction.WEST);

    protected static final VoxelShape SHAPE_OPEN_RIGHT_NORTH = Shapes.or(
            Block.box(0, 0, 2, 16, 16, 16),
            Block.box(0, 0, -14, 2, 16, 2)
    );
    protected static final VoxelShape SHAPE_OPEN_RIGHT_EAST = ShapeUtil.rotateShape(SHAPE_OPEN_RIGHT_NORTH, Direction.EAST);
    protected static final VoxelShape SHAPE_OPEN_RIGHT_SOUTH = ShapeUtil.rotateShape(SHAPE_OPEN_RIGHT_NORTH, Direction.SOUTH);
    protected static final VoxelShape SHAPE_OPEN_RIGHT_WEST = ShapeUtil.rotateShape(SHAPE_OPEN_RIGHT_NORTH, Direction.WEST);

    public CabinetBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false)
                .setValue(OPEN, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        boolean open = state.getValue(OPEN);
        DoorHingeSide hinge = state.getValue(HINGE);

        if (!open) {
            return switch (facing) {
                case EAST -> SHAPE_EAST;
                case SOUTH -> SHAPE_SOUTH;
                case WEST -> SHAPE_WEST;
                default -> SHAPE_NORTH;
            };
        }
        if (hinge == DoorHingeSide.LEFT) {
            return switch (facing) {
                case EAST -> SHAPE_OPEN_LEFT_EAST;
                case SOUTH -> SHAPE_OPEN_LEFT_SOUTH;
                case WEST -> SHAPE_OPEN_LEFT_WEST;
                default -> SHAPE_OPEN_LEFT_NORTH;
            };
        }
        return switch (facing) {
            case EAST -> SHAPE_OPEN_RIGHT_EAST;
            case SOUTH -> SHAPE_OPEN_RIGHT_SOUTH;
            case WEST -> SHAPE_OPEN_RIGHT_WEST;
            default -> SHAPE_OPEN_RIGHT_NORTH;
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
                .setValue(HINGE, hinge);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, OPEN, HINGE);
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
        Direction facing = state.getValue(FACING);
        DoorHingeSide hinge = state.getValue(HINGE);

        switch (mirror) {
            case LEFT_RIGHT:
                if (facing.getAxis() == Direction.Axis.Z) {
                    facing = mirror.mirror(facing);
                    hinge = hinge == DoorHingeSide.LEFT ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT;
                }
                break;
            case FRONT_BACK:
                if (facing.getAxis() == Direction.Axis.X) {
                    facing = mirror.mirror(facing);
                    hinge = hinge == DoorHingeSide.LEFT ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT;
                }
                break;
        }

        return state.setValue(FACING, facing).setValue(HINGE, hinge);
    }
}
