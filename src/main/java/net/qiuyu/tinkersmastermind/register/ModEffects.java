package net.qiuyu.tinkersmastermind.register;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.qiuyu.tinkersmastermind.TinkersMastermind;
import net.qiuyu.tinkersmastermind.effects.FootCleanEffect;
import net.qiuyu.tinkersmastermind.effects.FootOdorEffect;
import net.qiuyu.tinkersmastermind.effects.NoHealEffect;
import net.qiuyu.tinkersmastermind.effects.PoisonResistEffect;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, TinkersMastermind.MOD_ID);
    public static RegistryObject<MobEffect> NO_HEAL = EFFECTS.register("no_heal",()->
    {
        //3个参数:药水类型(好/坏/中)、药水粒子颜色、是否为即时效果(类似于瞬间伤害效果)
        return new NoHealEffect(MobEffectCategory.HARMFUL, 0x660033, false);
    });
    public static RegistryObject<MobEffect> POISON_RESIST = EFFECTS.register("poison_resist",()->
    {
        return new PoisonResistEffect(MobEffectCategory.BENEFICIAL, 0x660033, false);
    });
    public static RegistryObject<MobEffect> FOOT_CLEAN = EFFECTS.register("foot_clean",()->
    {
        return new FootCleanEffect(MobEffectCategory.BENEFICIAL, 0x660033, false);
    });
    public static RegistryObject<MobEffect> FOOT_ODOR = EFFECTS.register("foot_odor",()->
    {
        return new FootOdorEffect(MobEffectCategory.HARMFUL, 0x660033, false);
    });
}
