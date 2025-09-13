package net.qiuyu.tinkersmastermind.tinkering.modifier.defense;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.module.*;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class CoralHardeningModifier extends Modifier implements ModifyDamageModifierHook {
    private final ResourceLocation KEY = new ResourceLocation("tinkersmastermind", "coral_hardening");

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MODIFY_DAMAGE);
    }


    // 受到溺水/干旱伤害时，减少40%/80%的伤害，并且5/10秒的速度2效果和25%/50%的氧气回复
    @Override
    public float modifyDamageTaken(IToolStackView iToolStackView, ModifierEntry entry, EquipmentContext context, EquipmentSlot slot, DamageSource source, float v, boolean b) {
        int modifierLevel = Math.min(entry.getLevel(), 2);
        LivingEntity entity = context.getEntity();
        if (entity instanceof Player && slot.isArmor()
                && (source.type() == entity.getCommandSenderWorld().damageSources().drown().type()
                || source.type() == entity.getCommandSenderWorld().damageSources().dryOut().type())) {
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100 * modifierLevel, 0));
            entity.setAirSupply(Math.min((int) (entity.getMaxAirSupply() * (0.25 * modifierLevel)) + entity.getAirSupply(),entity.getMaxAirSupply()));
            return v * (1.0f - 0.4f * modifierLevel);
        }
        return v;
    }
}
