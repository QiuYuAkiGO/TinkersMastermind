package net.qiuyu.tinkersmastermind.client;

import java.util.Optional;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.qiuyu.tinkersmastermind.register.ModItems;
import net.qiuyu.tinkersmastermind.register.item.misc.NetherFortressCompass;

public final class NetherFortressCompassItemProperties {
    private static final ResourceLocation ANGLE = new ResourceLocation("angle");

    private NetherFortressCompassItemProperties() {
    }

    public static void register() {
        ItemProperties.register(ModItems.NETHER_FORTRESS_COMPASS.get(), ANGLE, NetherFortressCompassItemProperties::getAngle);
    }

    private static float getAngle(ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
        if (level == null || entity == null || level.dimension() != Level.NETHER) {
            return getSpinningAngle(level);
        }

        Optional<BlockPos> target = NetherFortressCompass.getTarget(stack);
        return target.map(pos -> getTargetAngle(entity, pos)).orElseGet(() -> getSpinningAngle(level));
    }

    private static float getTargetAngle(LivingEntity entity, BlockPos target) {
        double yaw = positiveModulo(entity.getYRot() / 360.0D, 1.0D);
        double targetAngle = Math.atan2(target.getZ() + 0.5D - entity.getZ(), target.getX() + 0.5D - entity.getX()) / (Math.PI * 2.0D);
        return (float) positiveModulo(0.5D - (yaw - 0.25D - targetAngle), 1.0D);
    }

    private static float getSpinningAngle(ClientLevel level) {
        if (level == null) {
            return 0.0F;
        }
        return (level.getGameTime() % 32L) / 32.0F;
    }

    private static double positiveModulo(double value, double divisor) {
        return (value % divisor + divisor) % divisor;
    }
}
