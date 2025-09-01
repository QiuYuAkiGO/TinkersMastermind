package net.qiuyu.tinkersmastermind.tinkering.modifier.defense;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

public class CoralHardeningModifier extends Modifier implements OnAttackedModifierHook, EquipmentChangeModifierHook {
    private final ResourceLocation KEY = new ResourceLocation("tinkersmastermind", "coral_hardening");

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED,ModifierHooks.EQUIPMENT_CHANGE);
    }

    @Override
    public void onAttacked(IToolStackView view, ModifierEntry entry, EquipmentContext context, EquipmentSlot slot, DamageSource damageSource, float damageGet, boolean damageValid) {
        LivingEntity entity = context.getEntity();
        if (entity instanceof Player && slot.isArmor() && !entity.hasEffect(MobEffects.ABSORPTION)) {
            entity.setAbsorptionAmount(0.25F * entry.getLevel() * damageGet);
        }
    }

    @Override
    public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        if (context.getChangedSlot() == EquipmentSlot.MAINHAND || context.getChangedSlot() == EquipmentSlot.OFFHAND) {
            return;
        }
        Entity entity = context.getEntity();
        if (entity instanceof LivingEntity &&((LivingEntity) entity).getEffect(MobEffects.HUNGER) == null) {
            int level = modifier.getLevel();
            ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.HUNGER, -1, level-1, true, true));
        }
    }

    @Override
    public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        if (context.getChangedSlot() == EquipmentSlot.MAINHAND || context.getChangedSlot() == EquipmentSlot.OFFHAND) {
            return;
        }
        Entity entity = context.getEntity();
        if (entity instanceof LivingEntity && ((LivingEntity) entity).hasEffect(MobEffects.HUNGER)) {
            ((LivingEntity) entity).removeEffect(MobEffects.HUNGER);
        }
    }
}
