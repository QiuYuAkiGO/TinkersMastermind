package net.qiuyu.tinkersmastermind.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.qiuyu.tinkersmastermind.register.ModBlockEntities;
import net.qiuyu.tinkersmastermind.register.block.ModularDisplayFrameBlock;

public class ModularDisplayFrameBlockEntity extends BlockEntity {
    private static final String TAG_DISPLAY_ITEM = "DisplayItem";
    private static final String TAG_HAS_DISPLAY_ITEM = "HasDisplayItem";
    private static final String TAG_ROTATION = "Rotation";
    private static final int ROTATION_STEPS = 8;

    private ItemStack displayStack = ItemStack.EMPTY;
    private int rotation;

    public ModularDisplayFrameBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MODULAR_DISPLAY_FRAME.get(), pos, state);
    }

    public static boolean placeInGroup(Level level, Player player, InteractionHand hand,
                                       ModularDisplayFrameBlock.FrameGroup group) {
        if (findDisplayFrame(level, group) != null) {
            return false;
        }

        BlockEntity blockEntity = level.getBlockEntity(group.anchor());
        if (!(blockEntity instanceof ModularDisplayFrameBlockEntity frame)) {
            return false;
        }

        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) {
            return false;
        }

        ItemStack placed = held.copy();
        placed.setCount(1);
        frame.rotation = 0;
        frame.setDisplayStack(placed);
        if (!player.getAbilities().instabuild) {
            held.shrink(1);
        }
        frame.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM);
        return true;
    }

    public static boolean takeFromGroup(Level level, Player player, ModularDisplayFrameBlock.FrameGroup group) {
        ModularDisplayFrameBlockEntity frame = findDisplayFrame(level, group);
        if (frame == null) {
            return false;
        }

        ItemStack stack = frame.displayStack.copy();
        frame.rotation = 0;
        frame.setDisplayStack(ItemStack.EMPTY);
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
        frame.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM);
        return true;
    }

    public static boolean rotateInGroup(Level level, ModularDisplayFrameBlock.FrameGroup group) {
        ModularDisplayFrameBlockEntity frame = findDisplayFrame(level, group);
        if (frame == null) {
            return false;
        }

        frame.rotation = (frame.rotation + 1) % ROTATION_STEPS;
        frame.setChanged();
        frame.sync();
        frame.playSound(SoundEvents.ITEM_FRAME_ROTATE_ITEM);
        return true;
    }

    public static ModularDisplayFrameBlockEntity findDisplayFrame(Level level, ModularDisplayFrameBlock.FrameGroup group) {
        for (int y = 0; y < group.size(); y++) {
            for (int x = 0; x < group.size(); x++) {
                BlockEntity blockEntity = level.getBlockEntity(group.posAt(x, y));
                if (blockEntity instanceof ModularDisplayFrameBlockEntity frame && !frame.displayStack.isEmpty()) {
                    return frame;
                }
            }
        }
        return null;
    }

    public ItemStack getDisplayStack() {
        return displayStack;
    }

    public int getRotation() {
        return rotation;
    }

    public void dropContents() {
        if (level != null && !displayStack.isEmpty()) {
            Block.popResource(level, worldPosition, displayStack.copy());
            setDisplayStack(ItemStack.EMPTY);
        }
    }

    private void setDisplayStack(ItemStack stack) {
        displayStack = stack.copy();
        setChanged();
        sync();
        updateHasItemState();
    }

    private void updateHasItemState() {
        if (level == null || level.isClientSide) {
            return;
        }

        BlockState state = getBlockState();
        boolean hasItem = !displayStack.isEmpty();
        if (state.hasProperty(ModularDisplayFrameBlock.HAS_ITEM)
                && state.getValue(ModularDisplayFrameBlock.HAS_ITEM) != hasItem) {
            level.setBlock(worldPosition, state.setValue(ModularDisplayFrameBlock.HAS_ITEM, hasItem),
                    Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateHasItemState();
    }

    private void playSound(net.minecraft.sounds.SoundEvent sound) {
        if (level != null) {
            level.playSound(null, worldPosition, sound, SoundSource.BLOCKS, 0.8F, 1.0F);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!displayStack.isEmpty()) {
            tag.put(TAG_DISPLAY_ITEM, displayStack.save(new CompoundTag()));
            tag.putInt(TAG_ROTATION, rotation);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        displayStack = tag.contains(TAG_DISPLAY_ITEM, Tag.TAG_COMPOUND)
                ? ItemStack.of(tag.getCompound(TAG_DISPLAY_ITEM))
                : ItemStack.EMPTY;
        rotation = tag.contains(TAG_ROTATION, Tag.TAG_INT)
                ? Math.floorMod(tag.getInt(TAG_ROTATION), ROTATION_STEPS)
                : 0;
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        // An entirely empty tag is converted to null by ClientboundBlockEntityDataPacket,
        // which would leave the old displayed stack cached on the client after removal.
        tag.putBoolean(TAG_HAS_DISPLAY_ITEM, !displayStack.isEmpty());
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        CompoundTag tag = packet.getTag();
        if (tag != null) {
            load(tag);
        }
    }

    private void sync() {
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }
}
