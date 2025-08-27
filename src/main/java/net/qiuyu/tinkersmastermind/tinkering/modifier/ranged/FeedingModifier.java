package net.qiuyu.tinkersmastermind.tinkering.modifier.ranged;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

public class FeedingModifier extends Modifier implements ProjectileHitModifierHook, ProjectileLaunchModifierHook {
    private final ResourceLocation KEY = new ResourceLocation("tinkersmastermind", "feeding");

    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT, ModifierHooks.PROJECTILE_LAUNCH);
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        if (target == null){
            return false;
        }
        projectile.playSound(SoundEvents.GENERIC_EAT);
        int level = modifier.getLevel();
        if (target instanceof LivingEntity && !target.isDeadOrDying()) {
            if (target instanceof Player) {
                ((Player) target).getFoodData().eat((int) Math.pow(2,level), 0);
                ModifierUtil.foodConsumer.onConsume((Player) target, new ItemStack(Items.CAKE), level, 2F * level);
                if (!((Player) target).canEat(false)) {
                    applyEffect(attacker, target);
                }
            } else {
                applyEffect(attacker, target);
            }
        }
        return false;
    }

    private void applyEffect(LivingEntity attacker, LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.HUNGER, 200, 9));
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 3));
        target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 3));
        if (attacker != null) {
            // TODO 找到生成掉落物的正确方法
//            Collection<ItemEntity> drops = new HashSet<ItemEntity>() {
//            };
//            drops.add(new ItemEntity(target.level(), target.getX(), target.getY(), target.getZ(), new ItemStack(ModItems.ZOMBIE_IRON.get())));
//            int lootingLevel = ForgeHooks.getLootingLevel(target, attacker, target.getLastDamageSource());
//            ForgeHooks.onLivingDrops(target, target.getLastDamageSource(), drops, lootingLevel,true);
        }
    }

    @Override
    public void onProjectileHitBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity attacker) {
        projectile.playSound(SoundEvents.SNOW_BREAK);
    }

    @Override
    public void onProjectileLaunch(IToolStackView iToolStackView, ModifierEntry modifierEntry, LivingEntity livingEntity, Projectile projectile, @Nullable AbstractArrow abstractArrow, ModDataNBT modDataNBT, boolean b) {
        projectile.playSound(SoundEvents.DISPENSER_LAUNCH);
    }
}
