package net.qiuyu.tinkersmastermind.tinkering.modifier.defense;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class CoralHardeningModifier extends Modifier implements OnAttackedModifierHook {
    private final ResourceLocation KEY = new ResourceLocation("tinkersmastermind", "coral_hardening");

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
    }

    @Override
    public void onAttacked(IToolStackView view, ModifierEntry entry, EquipmentContext context, EquipmentSlot slot, DamageSource damageSource, float damageGet, boolean damageValid) {
        LivingEntity entity = context.getEntity();
        if (entity instanceof Player && slot.isArmor() && !entity.hasEffect(MobEffects.ABSORPTION)) {
            entity.setAbsorptionAmount(0.25F * entry.getLevel() * damageGet);
        }
    }
}
