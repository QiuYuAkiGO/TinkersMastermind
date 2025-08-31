package net.qiuyu.tinkersmastermind.tinkering.modifier.defense;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockBreakModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class CoralHealingModifier extends Modifier implements MeleeHitModifierHook, BlockBreakModifierHook {
    private final ResourceLocation KEY = new ResourceLocation("tinkersmastermind", "coral_healing");

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT, ModifierHooks.BLOCK_BREAK);
    }
    @Override
    public void afterBlockBreak(IToolStackView iToolStackView, ModifierEntry entry, ToolHarvestContext context) {
        LivingEntity entity = context.getLiving();
        if (entity instanceof Player){
            // 攻击或挖掘后有35%概率获得2秒生命回复3效果，每级增加2秒
            if (RANDOM.nextInt(100) < 35){
                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, entry.getLevel()*40, 2));
            }
        }
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity entity = context.getAttacker();
        if (entity instanceof Player){
            // 攻击或挖掘后有35%概率获得2秒生命回复3效果，每级增加2秒
            if (RANDOM.nextInt(100) < 35){
                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, modifier.getLevel()*40, 2));
            }
        }
    }
}
