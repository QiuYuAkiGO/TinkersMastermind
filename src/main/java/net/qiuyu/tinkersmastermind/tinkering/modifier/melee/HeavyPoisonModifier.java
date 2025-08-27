package net.qiuyu.tinkersmastermind.tinkering.modifier.melee;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class HeavyPoisonModifier extends Modifier implements MeleeHitModifierHook, MeleeDamageModifierHook {
    private final ResourceLocation KEY = new ResourceLocation("tinkersmastermind", "heavy_poison");

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT, ModifierHooks.MELEE_DAMAGE);
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        // 根据工具等级赋予对方中毒效果，等级越高每次增加时间越长。
        int level = modifier.getLevel();
        LivingEntity target = context.getLivingTarget();
        if (target != null && !target.isDeadOrDying() && context.isFullyCharged()){
            MobEffectInstance existingPosion = target.getEffect(MobEffects.POISON);
            int newDuration = 100;
            int newAmplifier = level-1;
            if(existingPosion!=null){
                newDuration = Math.min(existingPosion.getDuration()+level*25,2400);
                newAmplifier = Math.max(existingPosion.getAmplifier(),newAmplifier);
            }
            context.getLivingTarget().addEffect(new MobEffectInstance(MobEffects.POISON, newDuration, newAmplifier));
        }
    }

    @Override
    public float getMeleeDamage(IToolStackView view, ModifierEntry entry, ToolAttackContext context, float baseDamage, float damage) {
        //  如果攻击时目标有中毒效果,则根据中毒剩余时间增加造成的伤害,最高20％  每3秒1％(也就是一分钟为最大值).
        LivingEntity target = context.getLivingTarget();
        if (target.hasEffect(MobEffects.POISON)){
           float amplifier = 1F;
           amplifier += Math.min((float)target.getEffect(MobEffects.POISON).getDuration() * 0.2F / 1200 ,0.2F);
           return damage * amplifier;
        }
        return damage;
    }
}
