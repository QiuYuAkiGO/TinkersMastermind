package net.qiuyu.tinkersmastermind.register.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.MapColor;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.tconstruct.fluids.block.BurningLiquidBlock;

import java.util.function.Function;
import java.util.function.Supplier;


public class JimsonweedFluid extends LiquidBlock {

    public JimsonweedFluid(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity && entity.getFluidTypeHeight(this.getFluid().getFluidType()) > 0.0) {
            ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.POISON, 200, 3));
        }
    }

    public static Function<Supplier<? extends FlowingFluid>, LiquidBlock> createPoison(MapColor color, int lightLevel) {
        return (fluid) -> {
            return new JimsonweedFluid(fluid.get(), FluidDeferredRegister.createProperties(color, lightLevel));
        };
    }
}
