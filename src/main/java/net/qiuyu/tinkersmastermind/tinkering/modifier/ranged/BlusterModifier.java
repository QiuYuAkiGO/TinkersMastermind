package net.qiuyu.tinkersmastermind.tinkering.modifier.ranged;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;

public class BlusterModifier extends Modifier implements ProjectileHitModifierHook, ToolStatsModifierHook {
    private final ResourceLocation KEY = new ResourceLocation("tinkersmastermind", "bluster");

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT, ModifierHooks.TOOL_STATS);
    }

    @Override
    public void onProjectileHitBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity attacker) {
        // 接触到方块时产生范围为6.5格,不破坏方块的爆炸,,3级会额外附着火焰
        Level level = projectile.level();
        level.explode(
                projectile.getVehicle(),
                projectile.level().damageSources().explosion(projectile.getOwner(), null),
                new ExplosionDamageCalculator(),
                projectile.getX(), projectile.getY(), projectile.getZ(),
                3,
                false,
                Level.ExplosionInteraction.MOB,
                true
        );
        applyExplosionParticles(level, hit.getLocation());
        // 播放声音
        level.playSound(null, hit.getBlockPos(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 0.3f * modifier.getLevel(), 1.0f);
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        // 接触到方块时产生范围为3格,不破坏方块的爆炸
        // 如果命中目标为存活玩家,则施加5秒2级的混乱效果
        Level level = projectile.level();
        level.explode(
                projectile.getVehicle(),
                projectile.level().damageSources().explosion(projectile.getOwner(), null),
                new ExplosionDamageCalculator(),
                projectile.getX(), projectile.getY(), projectile.getZ(),
                3,
                false,
                Level.ExplosionInteraction.MOB,
                true
        );
        applyExplosionParticles(level, hit.getLocation());
        // 播放声音
        level.playSound(null, projectile.getOnPos(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 0.3f * modifier.getLevel(), 1.0f);
        if (target instanceof Player) {
            target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 1));
        }
        return true;
    }

    private void applyExplosionParticles(Level level, Vec3 location) {
        if (level instanceof ServerLevel) {
            ((ServerLevel) level).sendParticles(
                    ParticleTypes.EXPLOSION,
                    location.x, location.y, location.z,
                    1, 0, 0, 0, 1
            );
        } else {
            level.addParticle(
                    ParticleTypes.EXPLOSION,
                    true,
                    location.x, location.y, location.z,
                    0, 0, 1
            );
        }
    }


    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifierEntry, ModifierStatsBuilder builder) {
        if (context.hasTag(TinkerTags.Items.RANGED)){
            ToolStats.DRAW_SPEED.multiply(builder,0.2f);
        }
    }
}
