package net.qiuyu.tinkersmastermind.tinkering.modifier.defense;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.qiuyu.tinkersmastermind.register.ModEffects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Random;

public class PoisonSparkleModifier extends Modifier implements OnAttackedModifierHook, EquipmentChangeModifierHook, ModifyDamageModifierHook {
    private static final Logger log = LoggerFactory.getLogger(PoisonSparkleModifier.class);
    private final ResourceLocation KEY = new ResourceLocation("tinkersmastermind", "poison_sparkle");

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED, ModifierHooks.EQUIPMENT_CHANGE,ModifierHooks.MODIFY_DAMAGE);
    }

    @Override
    public void onAttacked(IToolStackView iToolStackView, ModifierEntry modifierEntry, EquipmentContext equipmentContext, EquipmentSlot equipmentSlot, DamageSource damageSource, float v, boolean b) {
        Entity attacker = damageSource.getEntity();
        int level = modifierEntry.getLevel();
        if (attacker instanceof LivingEntity && attacker.isAlive()) {
            if (!((LivingEntity) attacker).hasEffect(ModEffects.POISON_RESIST.get())) {
                ((LivingEntity) attacker).addEffect(new MobEffectInstance(ModEffects.POISON_RESIST.get(), 50, 0));
                attacker.hurt(damageSource, level);
            }
            // 有10％/20％/30％/40％的概率使敌人获得中毒II效果
            Random random = new Random();
            if (random.nextFloat() < 0.1 * level) {
                ((LivingEntity) attacker).addEffect(new MobEffectInstance(MobEffects.POISON, 100, level - 1));
            }
        }
    }
    @Override
    public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        LivingEntity entity = context.getEntity();
        if (context.getChangedSlot() == EquipmentSlot.MAINHAND || context.getChangedSlot() == EquipmentSlot.OFFHAND) {
            return;
        }
        if (!entity.hasEffect(ModEffects.POISON_RESIST.get())) {
            entity.addEffect(new MobEffectInstance(ModEffects.POISON_RESIST.get(), -1, 0, true, true));
        }
    }

    @Override
    public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        LivingEntity entity = context.getEntity();
        if (context.getChangedSlot() == EquipmentSlot.MAINHAND || context.getChangedSlot() == EquipmentSlot.OFFHAND) {
            return;
        }
        if (entity.hasEffect(ModEffects.POISON_RESIST.get())) {
            entity.removeEffect(ModEffects.POISON_RESIST.get());
        }
    }

    @Override
    public float modifyDamageTaken(IToolStackView iToolStackView, ModifierEntry modifierEntry, EquipmentContext equipmentContext, EquipmentSlot equipmentSlot, DamageSource damageSource, float v, boolean b) {
        if (damageSource.type() == equipmentContext.getEntity().getCommandSenderWorld().damageSources().magic().type()) {
            return v * 0.5F;
        }
        return v;
    }
}
