package net.qiuyu.tinkersmastermind.register;

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.qiuyu.tinkersmastermind.TinkersMastermind;
import net.qiuyu.tinkersmastermind.tinkering.modifier.defense.FootOdorModifier;
import net.qiuyu.tinkersmastermind.tinkering.modifier.defense.HungryModifier;
import net.qiuyu.tinkersmastermind.tinkering.modifier.defense.PoisonErosionModifier;
import net.qiuyu.tinkersmastermind.tinkering.modifier.defense.PoisonSparkleModifier;
import net.qiuyu.tinkersmastermind.tinkering.modifier.melee.HeavyPoisonModifier;
import net.qiuyu.tinkersmastermind.tinkering.modifier.melee.RottenModifier;
import net.qiuyu.tinkersmastermind.tinkering.modifier.ranged.BlusterModifier;
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
}
