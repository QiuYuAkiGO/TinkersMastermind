package net.qiuyu.tinkersmastermind.tinkering.modifier.defense;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.Potion;
import net.qiuyu.tinkersmastermind.register.ModEffects;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class FootOdorModifier extends Modifier implements EquipmentChangeModifierHook {
    private final ResourceLocation KEY = new ResourceLocation("tinkersmastermind", "foot_odor");

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
    }

    @Override
    public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        LivingEntity entity = context.getEntity();
        if (context.getChangedSlot() == EquipmentSlot.FEET && !entity.hasEffect(ModEffects.FOOT_CLEAN.get())) {
            AreaEffectCloud cloud = new AreaEffectCloud(entity.level(), entity.getX(), entity.getY(), entity.getZ());
            cloud.setRadius(12F);
            cloud.setParticle(ParticleTypes.EFFECT);
            cloud.setPotion(new Potion(new MobEffectInstance(ModEffects.FOOT_ODOR.get(), 200, 0)));
            entity.getCommandSenderWorld().addFreshEntity(cloud);
        }
        entity.removeEffect(ModEffects.FOOT_CLEAN.get());
    }

    @Override
    public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        if (context.getChangedSlot() == EquipmentSlot.FEET){
            LivingEntity entity = context.getEntity();
            entity.addEffect(new MobEffectInstance(ModEffects.FOOT_CLEAN.get(), 6000, 0));
        }
    }
}
