package net.qiuyu.tinkersmastermind.register.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.qiuyu.tinkersmastermind.blockentity.ModularDisplayFrameBlockEntity;
import net.qiuyu.tinkersmastermind.register.ModBlocks;

import javax.annotation.Nullable;

public class ModularDisplayFrameBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final IntegerProperty GROUP_SIZE = IntegerProperty.create("group_size", 1, 3);
    public static final BooleanProperty HAS_ITEM = BooleanProperty.create("has_item");

    private static final VoxelShape NORTH_SHAPE = Block.box(2.0D, 2.0D, 15.0D, 14.0D, 14.0D, 16.0D);
    private static final VoxelShape SOUTH_SHAPE = Block.box(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 1.0D);
    private static final VoxelShape WEST_SHAPE = Block.box(15.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D);
    private static final VoxelShape EAST_SHAPE = Block.box(0.0D, 2.0D, 2.0D, 1.0D, 14.0D, 14.0D);
    private static final VoxelShape UP_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 1.0D, 14.0D);
    private static final VoxelShape DOWN_SHAPE = Block.box(2.0D, 15.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public ModularDisplayFrameBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(GROUP_SIZE, 1)
                .setValue(HAS_ITEM, false));
    }

    public static Properties properties() {
        return Properties.copy(Blocks.GLASS).strength(0.3F).sound(SoundType.GLASS).noOcclusion();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState().setValue(FACING, context.getClickedFace());
        return state.canSurvive(context.getLevel(), context.getClickedPos()) ? state : null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos supportPos = pos.relative(facing.getOpposite());
        return level.getBlockState(supportPos).isFaceSturdy(level, supportPos, facing);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level,
                                  BlockPos currentPos, BlockPos neighborPos) {
        return direction == state.getValue(FACING).getOpposite() && !state.canSurvive(level, currentPos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
            case UP -> UP_SHAPE;
            case DOWN -> DOWN_SHAPE;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, GROUP_SIZE, HAS_ITEM);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide && !oldState.is(state.getBlock())) {
            refreshMergedStates(level, pos);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        FrameGroup group = findGroup(level, pos, state.getValue(FACING));
        if (!level.isClientSide) {
            if (held.isEmpty()) {
                ModularDisplayFrameBlockEntity.takeFromGroup(level, player, group);
            } else if (ModularDisplayFrameBlockEntity.findDisplayFrame(level, group) != null) {
                ModularDisplayFrameBlockEntity.rotateInGroup(level, group);
            } else {
                ModularDisplayFrameBlockEntity.placeInGroup(level, player, hand, group);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        boolean removed = !state.is(newState.getBlock());
        if (removed && level.getBlockEntity(pos) instanceof ModularDisplayFrameBlockEntity frame) {
            frame.dropContents();
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (removed && !level.isClientSide) {
            refreshMergedStates(level, pos);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ModularDisplayFrameBlockEntity(pos, state);
    }

    public static FrameGroup findGroup(LevelReader level, BlockPos pos, Direction facing) {
        Direction right = getRight(facing);
        Direction down = getDown(facing);
        for (int size = 3; size >= 2; size--) {
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    BlockPos anchor = offset(offset(pos, right, -x), down, -y);
                    if (isCompleteSquare(level, anchor, facing, right, down, size)
                            && isCanonicalSquare(level, anchor, facing, right, down, size)) {
                        return new FrameGroup(anchor, facing, right, down, size);
                    }
                }
            }
        }
        return new FrameGroup(pos, facing, right, down, 1);
    }

    public static void refreshMergedStates(LevelAccessor level, BlockPos center) {
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-4, -4, -4), center.offset(4, 4, 4))) {
            BlockState state = level.getBlockState(pos);
            if (!state.is(ModBlocks.MODULAR_DISPLAY_FRAME.get())) {
                continue;
            }

            FrameGroup group = findGroup(level, pos, state.getValue(FACING));
            int desiredSize = group.size();
            if (state.getValue(GROUP_SIZE) != desiredSize) {
                level.setBlock(pos, state.setValue(GROUP_SIZE, desiredSize), Block.UPDATE_CLIENTS);
            }
        }
    }

    private static boolean isCompleteSquare(LevelReader level, BlockPos anchor, Direction facing, Direction right,
                                            Direction down, int size) {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                BlockPos testPos = offset(offset(anchor, right, x), down, y);
                BlockState state = level.getBlockState(testPos);
                if (!state.is(ModBlocks.MODULAR_DISPLAY_FRAME.get()) || state.getValue(FACING) != facing) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isCanonicalSquare(LevelReader level, BlockPos anchor, Direction facing, Direction right,
                                             Direction down, int size) {
        for (int dy = -(size - 1); dy <= size - 1; dy++) {
            for (int dx = -(size - 1); dx <= size - 1; dx++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }
                if (!isEarlierAnchor(dx, dy) || !squaresOverlap(dx, dy, size)) {
                    continue;
                }

                BlockPos otherAnchor = offset(offset(anchor, right, dx), down, dy);
                if (isCompleteSquare(level, otherAnchor, facing, right, down, size)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isEarlierAnchor(int dx, int dy) {
        return dy < 0 || dy == 0 && dx < 0;
    }

    private static boolean squaresOverlap(int dx, int dy, int size) {
        return Math.abs(dx) < size && Math.abs(dy) < size;
    }

    public static Direction getRight(Direction facing) {
        return switch (facing) {
            case NORTH -> Direction.EAST;
            case SOUTH -> Direction.WEST;
            case WEST -> Direction.NORTH;
            case EAST -> Direction.SOUTH;
            case UP, DOWN -> Direction.EAST;
        };
    }

    public static Direction getDown(Direction facing) {
        return switch (facing) {
            case UP, DOWN -> Direction.SOUTH;
            default -> Direction.DOWN;
        };
    }

    public static BlockPos offset(BlockPos pos, Direction direction, int amount) {
        if (amount >= 0) {
            return pos.relative(direction, amount);
        }
        return pos.relative(direction.getOpposite(), -amount);
    }

    public record FrameGroup(BlockPos anchor, Direction facing, Direction right, Direction down, int size) {
        public boolean isAnchor(BlockPos pos) {
            return anchor.equals(pos);
        }

        public boolean isMerged() {
            return size > 1;
        }

        public BlockPos posAt(int x, int y) {
            return ModularDisplayFrameBlock.offset(ModularDisplayFrameBlock.offset(anchor, right, x), down, y);
        }
    }
}
