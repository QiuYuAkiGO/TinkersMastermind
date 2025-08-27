package net.qiuyu.tinkersmastermind.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BaseEffect extends MobEffect{
    private boolean instant;
    private boolean isRegistered = false;
    protected float resistancePerLevel = 0.5f;//for any buff

    public BaseEffect(MobEffectCategory type, int color, boolean isInstant) {
        super(type, color);
        this.instant = isInstant;
    }

    public boolean isInstantenous() {
        return false;
    }

    @Override
    public boolean isDurationEffectTick(int remainingTicks, int level) {
        if (isInstantenous()) {
            return true;
        }
        return canApplyEffect(remainingTicks, level);
    }

    protected boolean canApplyEffect(int remainingTicks, int level) {
        if (!isInstantenous()) {
            //Log.w("Non-instant effects should override canApplyEffect!");

        }
        return false;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (isInstantenous()) {
            applyInstantenousEffect(null, null, entity, amplifier, 1.0d);
        }
    }

    public BaseEffect onRegister() {
        isRegistered = true;
        return this;
    }

    public boolean isRegistered() {
        return isRegistered;
    }
}

