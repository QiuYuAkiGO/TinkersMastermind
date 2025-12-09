package net.qiuyu.tinkersmastermind.register;

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.qiuyu.tinkersmastermind.TinkersMastermind;
import net.qiuyu.tinkersmastermind.tinkering.modifier.defense.*;
import net.qiuyu.tinkersmastermind.tinkering.modifier.melee.*;
import net.qiuyu.tinkersmastermind.tinkering.modifier.ranged.BlusterModifier;
import net.qiuyu.tinkersmastermind.tinkering.modifier.ranged.ChargeModifier;
import net.qiuyu.tinkersmastermind.tinkering.modifier.ranged.DoubleShotModifier;
import net.qiuyu.tinkersmastermind.tinkering.modifier.ranged.FeedingModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;
import slimeknights.tconstruct.library.modifiers.Modifier;
public class ModModifiers {
    public static final ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(TinkersMastermind.MOD_ID);
    public ModModifiers() {
        MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
    public static final StaticModifier<RottenModifier> Rotten = MODIFIERS.register("rotten", RottenModifier::new);
    public static final StaticModifier<HungryModifier> Hungry = MODIFIERS.register("hungry", HungryModifier::new);
    public static final StaticModifier<FootOdorModifier> FootOdor = MODIFIERS.register("foot_odor", FootOdorModifier::new);
    public static final StaticModifier<BlusterModifier> Bluster = MODIFIERS.register("bluster", BlusterModifier::new);
    public static final StaticModifier<HeavyPoisonModifier> HeavyPoison = MODIFIERS.register("heavy_poison", HeavyPoisonModifier::new);
    public static final StaticModifier<FeedingModifier> Feeding = MODIFIERS.register("feeding", FeedingModifier::new);
    public static final StaticModifier<PoisonSparkleModifier> PoisonSparkle = MODIFIERS.register("poison_sparkle", PoisonSparkleModifier::new);
    public static final StaticModifier<PoisonErosionModifier> PoisonErosion = MODIFIERS.register("poison_erosion", PoisonErosionModifier::new);
    public static final StaticModifier<Modifier> DoubleShot = MODIFIERS.register("double_shot", DoubleShotModifier::new);
    public static final StaticModifier<Modifier> CoralHealing = MODIFIERS.register("coral_healing", CoralHealingModifier::new);
    public static final StaticModifier<Modifier> CoralHardening = MODIFIERS.register("coral_hardening", CoralHardeningModifier::new);
    public static final StaticModifier<Modifier> Charge = MODIFIERS.register("charge", ChargeModifier::new);
    public static final StaticModifier<Modifier> ShieldBreaking = MODIFIERS.register("shield_breaking", ShieldBreaking::new);
    public static final StaticModifier<Modifier> Smash = MODIFIERS.register("smash",SmashModifier::new);
    public static final StaticModifier<Modifier> Stickin = MODIFIERS.register("stickin",StickinModifier::new);
    public static final StaticModifier<Modifier> LeapSlash = MODIFIERS.register("leap_slash",LeapSlashModifier::new);
}
