package net.qiuyu.tinkersmastermind.register.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.BlockHitResult;
import net.qiuyu.tinkersmastermind.blockentity.ForgingTableBlockEntity;
import net.qiuyu.tinkersmastermind.register.ModBlockEntities;
import net.qiuyu.tinkersmastermind.register.ModTags;

public class ForgingTableBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(2.0D, 0.0D, 3.0D, 14.0D, 1.75D, 13.0D),
            Block.box(5.0D, 1.5D, 5.5D, 11.0D, 6.75D, 10.5D),
            Block.box(2.0D, 6.5D, 3.0D, 14.0D, 11.0D, 13.0D)
    );

    public ForgingTableBlock(Properties properties) {
        super(properties);
    }

    public static Properties properties() {
        return Properties.copy(Blocks.ANVIL).noOcclusion();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos,
                               CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos,
                                        CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ForgingTableBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof ForgingTableBlockEntity forgingTable)) {
            return InteractionResult.PASS;
        }

        ItemStack held = player.getItemInHand(hand);
        if (!held.isEmpty() && held.is(ModTags.Items.FORGING_HAMMERS)) {
            if (!level.isClientSide) {
                forgingTable.tryForge(player, hand);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (held.is(Items.WATER_BUCKET)) {
            if (!level.isClientSide) {
                forgingTable.coolWithWater(player, hand);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (held.isEmpty()) {
            if (hit.getDirection() != Direction.UP) {
                return InteractionResult.PASS;
            }
            if (!level.isClientSide) {
                forgingTable.takeLastItem(player);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!level.isClientSide) {
            forgingTable.placeOneItem(player, hand);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof ForgingTableBlockEntity forgingTable) {
            forgingTable.dropContents();
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return createTickerHelper(type, ModBlockEntities.FORGING_TABLE.get(), ForgingTableBlockEntity::serverTick);
    }
}
