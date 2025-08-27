package net.qiuyu.tinkersmastermind.register;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.qiuyu.tinkersmastermind.TinkersMastermind;
import net.qiuyu.tinkersmastermind.register.fluid.JimsonweedFluid;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.mantle.registration.object.FlowingFluidObject;

import static slimeknights.tconstruct.fluids.block.BurningLiquidBlock.createBurning;

public class ModFluids {
    public static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(TinkersMastermind.MOD_ID);

    public static final FlowingFluidObject<ForgeFlowingFluid> MOLTEN_ZOMBIE_IRON = FLUIDS.register("molten_zombie_iron").type(hot().temperature(1000).lightLevel(15)).block(createBurning(MapColor.COLOR_GREEN, 10, 5,4f)).bucket().flowing();
    public static final FlowingFluidObject<ForgeFlowingFluid> MOLTEN_JIMSONWEED = FLUIDS.register("molten_jimsonweed").type(hot()).block(JimsonweedFluid.createPoison(MapColor.COLOR_GREEN, 10)).bucket().flowing();
    public static final FlowingFluidObject<ForgeFlowingFluid> MOLTEN_CORAL = FLUIDS.register("molten_coral").type(hot()).bucket().flowing();
    private static FluidType.Properties common() {
       return FluidType.Properties.create().density(2000).viscosity(10000).temperature(1000).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                .motionScale(0.0023333333333333335D)
                .canExtinguish(true);
    }
    private static FluidType.Properties hot() {
        return FluidType.Properties.create().density(2000).viscosity(10000).temperature(1000)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                .motionScale(0.0023333333333333335D)
                .canSwim(false).canDrown(false)
                .pathType(BlockPathTypes.LAVA).adjacentPathType(null);
    }
}
