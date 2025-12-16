package net.qiuyu.tinkersmastermind.tinkering.modifier.melee;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.tools.modifiers.ability.sling.SlingModifier;

import javax.annotation.Nullable;

public class LeapSlashModifier extends Modifier implements MeleeDamageModifierHook,GeneralInteractionModifierHook {
    private final ResourceLocation KEY = new ResourceLocation("tinkersmastermind", "leap_slash"); 
    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
        hookBuilder.addHook(this,ModifierHooks.GENERAL_INTERACT);
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        LivingEntity attacker = context.getAttacker();
        LivingEntity target = context.getLivingTarget();
        if (target != null && !attacker.onGround()) {
            float fallDistance = attacker.fallDistance;
            float additionalDamage = Math.min(3.0f*modifier.getLevel(),fallDistance);
            return damage + additionalDamage;
        }
        return damage;
    }

    @Override
    public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
            Level level = player.level();
            int levels = modifier.getLevel();
        if (source == InteractionSource.RIGHT_CLICK && !tool.isBroken() && player != null && levels >0) {
        player.push(
          0,
          0.5,
          0);
        if (!level.isClientSide) {
          player.causeFoodExhaustion(0.2F);
          player.getCooldowns().addCooldown(tool.getItem(), 160);
          ToolDamageUtil.damageAnimated(tool, 1, player);
        }
       return InteractionResult.sidedSuccess(player.getCommandSenderWorld().isClientSide);
      }
    return InteractionResult.PASS;
    }
}
