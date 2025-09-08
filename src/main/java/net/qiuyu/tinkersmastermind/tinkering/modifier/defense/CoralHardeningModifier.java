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
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.module.*;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class CoralHardeningModifier extends Modifier implements OnAttackedModifierHook {
    private final ResourceLocation KEY = new ResourceLocation("tinkersmastermind", "coral_hardening");

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
    }

    // 当玩家受到溺水或脱水伤害时,回复最大值25%/50%的氧气值与5/10秒的速度2效果,上限2级.
    @Override
    public void onAttacked(IToolStackView view, ModifierEntry entry, EquipmentContext context, EquipmentSlot slot, DamageSource damageSource, float damageGet, boolean damageValid) {
        int modifierLevel = Math.min(entry.getLevel(), 2);
        LivingEntity entity = context.getEntity();
        if (entity instanceof Player && slot.isArmor()
                && (damageSource.type() == entity.getCommandSenderWorld().damageSources().drown().type()
                || damageSource.type() == entity.getCommandSenderWorld().damageSources().dryOut().type())) {
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100 * modifierLevel, 0));
            entity.setAirSupply((int) (entity.getMaxAirSupply() * (0.25 * modifierLevel)) + entity.getAirSupply());
        }
    }

}
