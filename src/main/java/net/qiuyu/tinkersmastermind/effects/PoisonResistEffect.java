package net.qiuyu.tinkersmastermind.effects;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PoisonResistEffect extends BaseEffect{

    public PoisonResistEffect(MobEffectCategory type, int color, boolean isInstant) {
        super(type, color, isInstant);
    }
    public PoisonResistEffect(){
        super(MobEffectCategory.BENEFICIAL, 0x660033, false);
    }

    @Override
    protected boolean canApplyEffect(int remainingTicks, int level) {
        return true;
    }
}
