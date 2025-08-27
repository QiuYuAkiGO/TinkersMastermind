package net.qiuyu.tinkersmastermind.effects;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class FootOdorEffect extends BaseEffect{
    public FootOdorEffect(MobEffectCategory type, int color, boolean isInstant) {
        super(type, color, isInstant);
    }
    public FootOdorEffect(){
        super(MobEffectCategory.HARMFUL, 0x660033, false);
    }
    @Override
    protected boolean canApplyEffect(int remainingTicks, int level) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 1));
    }
}
