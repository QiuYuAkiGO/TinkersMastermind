package net.qiuyu.tinkersmastermind.tinkering.modifier.defense;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class HungryModifier extends Modifier implements ModifyDamageModifierHook, EquipmentChangeModifierHook {
    private static final Logger LOGGER = LoggerFactory.getLogger(HungryModifier.class);
    private final ResourceLocation KEY = new ResourceLocation("tinkersmastermind", "hungry");
    public int getPriority() {
        return 90;
    }
    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MODIFY_DAMAGE, ModifierHooks.EQUIPMENT_CHANGE);
    }
    @Override
    public float modifyDamageTaken(IToolStackView stackView, ModifierEntry entry, EquipmentContext context, EquipmentSlot slot, DamageSource source, float v, boolean b) {
        Level world = context.getEntity().level();
        Entity entity = context.getEntity();
        world.playSound(null, entity.getX(), entity.getY(), entity.getZ()
                        , SoundEvents.ZOMBIE_HURT, SoundSource.HOSTILE, 0.3F * entry.getLevel()
                        , 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
//        穿戴者受到攻击时 获得1/2/2.5格(即2/4/5点)的饱食度回复(与0点饱和) CD 0.5s 根据1/2/3级触发概率为20％/40％/60％=
        if (RANDOM.nextInt(10) < entry.getLevel() * 2 && context.getEntity() instanceof Player) {
            int level = entry.getLevel();
            ((Player)context.getEntity()).getFoodData().eat(2 * level, 0);
        }
        return v;
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
