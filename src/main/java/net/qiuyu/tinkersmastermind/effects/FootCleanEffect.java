package net.qiuyu.tinkersmastermind.effects;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class FootCleanEffect extends BaseEffect{

    public FootCleanEffect(){
        super(MobEffectCategory.BENEFICIAL, 0x660033, false);
    }
    public FootCleanEffect(MobEffectCategory type, int color, boolean isInstant) {
        super(type, color, isInstant);
    }
    @Override
    protected boolean canApplyEffect(int remainingTicks, int level) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        entity.setSpeed(entity.getSpeed()+0.03F);
    }
}
