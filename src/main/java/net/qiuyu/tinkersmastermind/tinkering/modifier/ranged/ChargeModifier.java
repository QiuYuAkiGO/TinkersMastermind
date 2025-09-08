package net.qiuyu.tinkersmastermind.tinkering.modifier.ranged;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import net.minecraftforge.fml.common.Mod;
import net.qiuyu.tinkersmastermind.TinkersMastermind;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = TinkersMastermind.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChargeModifier extends Modifier implements ProjectileHitModifierHook, ProjectileLaunchModifierHook {

    @SubscribeEvent
    public static void onArrowLoose(net.minecraftforge.event.entity.player.ArrowLooseEvent event) {
        int drawn = event.getCharge(); // 实际已蓄力刻数
        boolean fullyCharged = drawn >= 20; // 原版弓的满蓄力判定
    }

    @Override
    public void onProjectileLaunch(IToolStackView iToolStackView, ModifierEntry modifierEntry, LivingEntity livingEntity, Projectile projectile, @Nullable AbstractArrow abstractArrow, ModDataNBT modDataNBT, boolean b) {

    }

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        ProjectileLaunchModifierHook.super.onProjectileLaunch(tool, modifier, shooter, ammo, projectile, arrow, persistentData, primary);
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        return ProjectileHitModifierHook.super.onProjectileHitEntity(modifiers, persistentData, modifier, projectile, hit, attacker, target);
    }

    @Override
    public void onProjectileHitBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity attacker) {
        ProjectileHitModifierHook.super.onProjectileHitBlock(modifiers, persistentData, modifier, projectile, hit, attacker);
    }
}
