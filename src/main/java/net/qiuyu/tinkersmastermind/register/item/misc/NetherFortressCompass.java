package net.qiuyu.tinkersmastermind.register.item.misc;

import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;

public class NetherFortressCompass extends Item {
    private static final String TAG_TARGET = "FortressTarget";
    private static final String TAG_TARGET_X = "X";
    private static final String TAG_TARGET_Y = "Y";
    private static final String TAG_TARGET_Z = "Z";
    private static final int SEARCH_RADIUS = 100;

    public NetherFortressCompass(Properties properties) {
        super(properties);
    }

    public static Item register_item() {
        return new NetherFortressCompass(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            if (level instanceof ServerLevel serverLevel) {
                if (level.dimension() == Level.NETHER) {
                    BlockPos fortressPos = findNearestFortress(serverLevel, player.blockPosition());
                    if (fortressPos != null) {
                        setTarget(stack, fortressPos);
                    } else {
                        clearTarget(stack);
                    }
                } else {
                    clearTarget(stack);
                }
            }
            player.getCooldowns().addCooldown(this, 20);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    public static Optional<BlockPos> getTarget(ItemStack stack) {
        CompoundTag rootTag = stack.getTag();
        if (rootTag == null || !rootTag.contains(TAG_TARGET, Tag.TAG_COMPOUND)) {
            return Optional.empty();
        }

        CompoundTag targetTag = rootTag.getCompound(TAG_TARGET);
        if (!targetTag.contains(TAG_TARGET_X, Tag.TAG_INT)
                || !targetTag.contains(TAG_TARGET_Y, Tag.TAG_INT)
                || !targetTag.contains(TAG_TARGET_Z, Tag.TAG_INT)) {
            return Optional.empty();
        }

        return Optional.of(new BlockPos(
                targetTag.getInt(TAG_TARGET_X),
                targetTag.getInt(TAG_TARGET_Y),
                targetTag.getInt(TAG_TARGET_Z)));
    }

    private static BlockPos findNearestFortress(ServerLevel level, BlockPos origin) {
        Registry<Structure> structureRegistry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        Holder<Structure> fortress = structureRegistry.getHolder(BuiltinStructures.FORTRESS).orElse(null);
        if (fortress == null) {
            return null;
        }

        Pair<BlockPos, Holder<Structure>> result = level.getChunkSource().getGenerator().findNearestMapStructure(
                level,
                HolderSet.direct(fortress),
                origin,
                SEARCH_RADIUS,
                false);
        return result == null ? null : result.getFirst();
    }

    private static void setTarget(ItemStack stack, BlockPos target) {
        CompoundTag targetTag = new CompoundTag();
        targetTag.putInt(TAG_TARGET_X, target.getX());
        targetTag.putInt(TAG_TARGET_Y, target.getY());
        targetTag.putInt(TAG_TARGET_Z, target.getZ());
        stack.getOrCreateTag().put(TAG_TARGET, targetTag);
    }

    private static void clearTarget(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            tag.remove(TAG_TARGET);
            if (tag.isEmpty()) {
                stack.setTag(null);
            }
        }
    }
}
