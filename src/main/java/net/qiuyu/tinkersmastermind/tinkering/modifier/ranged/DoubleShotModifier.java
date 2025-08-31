package net.qiuyu.tinkersmastermind.tinkering.modifier.ranged;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class DoubleShotModifier extends Modifier implements BowAmmoModifierHook,ProjectileLaunchModifierHook, ProjectileHitModifierHook, ToolStatsModifierHook {
    private final ResourceLocation KEY = new ResourceLocation("modid", "double_shot"); 

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.BOW_AMMO, ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.PROJECTILE_HIT);
        hookBuilder.addHook(this, ModifierHooks.TOOL_STATS);
    }
    @Override
    
    public ItemStack findAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter,
                          ItemStack standardAmmo, Predicate<ItemStack> ammoPredicate) {
    ModDataNBT data = tool.getPersistentData();

    if (shooter instanceof Player player) {
        int total = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && ammoPredicate.test(stack)) {
                total += stack.getCount();
                if (total >= 2) {
                    data.putBoolean(KEY, true);
                    return stack.copyWithCount(2); // 虚拟返回2个箭
                }
            }
        }

        if (total == 1) {
            data.putBoolean(KEY, false);
            return standardAmmo.copyWithCount(1);
        }
    }

    return ItemStack.EMPTY;
}

    @Override
    public void shrinkAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter,
                       ItemStack ammo, int needed) {
    ModDataNBT data = tool.getPersistentData();
    boolean doubleShot = data.getBoolean(KEY);

    if (shooter instanceof Player player) {
        int toRemove = doubleShot ? 2 : 1;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && ItemStack.isSameItemSameTags(stack, ammo)) {
                int removed = Math.min(toRemove, stack.getCount());
                stack.shrink(removed);
                toRemove -= removed;
                if (toRemove <= 0) {
                    break;
                }
            }
        }
    }
}

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, 
                                 Projectile projectile, @Nullable AbstractArrow arrow, 
                                 ModDataNBT persistentData, boolean primary) {
           if (primary) {
      boolean doubleShot = tool.getPersistentData().getBoolean(KEY);
      persistentData.putBoolean(KEY, doubleShot);
    }
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, 
                                       Projectile projectile, EntityHitResult hit, 
                                       @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        if (target != null && attacker != null
                && target.isAlive() && projectile instanceof AbstractArrow arrow 
                && persistentData.getBoolean(KEY)) { // 判断是否双射成功
           float damageDealt = (float) ((float) arrow.getBaseDamage() * arrow.getDeltaMovement().length());
            target.playSound(SoundEvents.GLASS_BREAK);
            target.invulnerableTime = 0;
            target.hurt(
                    new DamageSource(attacker.getCommandSenderWorld().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.PLAYER_ATTACK)),
                    damageDealt);
            target.invulnerableTime = 0;
        }
        return false;
    }

    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifierEntry, ModifierStatsBuilder builder) {
        if (context.hasTag(TinkerTags.Items.RANGED)) {
            ToolStats.ATTACK_SPEED.multiply(builder, 0.85f);
        }
    }
}