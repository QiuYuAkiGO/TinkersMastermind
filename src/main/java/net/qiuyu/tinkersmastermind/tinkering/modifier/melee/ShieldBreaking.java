package net.qiuyu.tinkersmastermind.tinkering.modifier.melee;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class ShieldBreaking extends Modifier implements MeleeHitModifierHook{
    private final ResourceLocation KEY = new ResourceLocation("tinkersmastermind", "shield_breaking");

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
    }

    @Override
    public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
        LivingEntity attacker = context.getAttacker();
        LivingEntity target = context.getLivingTarget();
        if (!attacker.getCommandSenderWorld().isClientSide
                && !tool.isBroken()
                && target instanceof Player player
                && target.isUsingItem()
                && target.isBlocking()
        ) {
            // 计算盾牌的耐久消耗（至少 3 点）
            EquipmentSlot slot = player.getUsedItemHand() == InteractionHand.MAIN_HAND
                    ? EquipmentSlot.MAINHAND
                    : EquipmentSlot.OFFHAND;
            int amount = Math.max(3, (int) (tool.getDamage()*0.5f));
            float baseDamage = tool.getStats().get(ToolStats.ATTACK_DAMAGE);
            player.getUseItem().hurtAndBreak(amount, attacker, (entity) -> entity.broadcastBreakEvent(slot));
            // 使对方的盾牌进入冷却5秒
            player.getCooldowns().addCooldown(target.getUseItem().getItem(), 60+(int) baseDamage*5);
            player.stopUsingItem();
            // 播放破盾音效
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SHIELD_BREAK, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }

}
