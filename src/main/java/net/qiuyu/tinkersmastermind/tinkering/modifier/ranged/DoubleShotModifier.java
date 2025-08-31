package net.qiuyu.tinkersmastermind.tinkering.modifier.ranged;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;


public class DoubleShotModifier extends Modifier implements ProjectileLaunchModifierHook, ProjectileHitModifierHook, ToolStatsModifierHook {
    private final ResourceLocation KEY = new ResourceLocation("tinkersmastermind", "double_shot");

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this,  ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.PROJECTILE_HIT);
    }

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, 
                                 ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, 
                                 ModDataNBT persistentData, boolean primary) {
        //只在主箭矢上触发双射，可更改
        if (primary) {
            boolean consumedTwo = false;
            if (!ammo.isEmpty() && ammo.getCount() >= 2) {
                ammo.shrink(2);
                consumedTwo = true;
            } else if (!ammo.isEmpty() && ammo.getCount() == 1) {
                ammo.shrink(1);
                consumedTwo = false;
            }
            
            // 在箭矢的持久数据中标记是否消耗了2发
            persistentData.putBoolean(KEY, consumedTwo);
        }
    }


    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        // 此处参考了匠魂校准的双折效果.
        if (target != null && attacker != null
                && target.isAlive() && projectile instanceof AbstractArrow arrow 
                && persistentData.getBoolean(KEY,false)){
            float damageDealt = (float) ((float) arrow.getBaseDamage() * arrow.getDeltaMovement().length());
            target.playSound(SoundEvents.GLASS_BREAK);
            target.hurt(
                    new DamageSource(attacker.getCommandSenderWorld().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.PLAYER_ATTACK)),
                    damageDealt);
            target.invulnerableTime = 0;
        }
        return false;
    }
    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifierEntry, ModifierStatsBuilder builder) {
        if (context.hasTag(TinkerTags.Items.RANGED)){
            ToolStats.ATTACK_SPEED.multiply(builder,0.85f);
        }
    }
}
