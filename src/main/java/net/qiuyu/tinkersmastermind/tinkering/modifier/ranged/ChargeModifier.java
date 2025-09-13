package net.qiuyu.tinkersmastermind.tinkering.modifier.ranged;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.qiuyu.tinkersmastermind.TinkersMastermind;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = TinkersMastermind.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChargeModifier extends Modifier implements ProjectileHitModifierHook, ProjectileLaunchModifierHook {
  private final ResourceLocation KEY = new ResourceLocation("tinkersmastermind", "charge");
  private static final ResourceLocation KEY_DRAWN = new ResourceLocation("tinkersmastermind", "charge_drawn");
  private static final ResourceLocation KEY_FULLY = new ResourceLocation("tinkersmastermind", "charge_full");
  private static final ResourceLocation KEY_MAX_TICKS = new ResourceLocation("tinkersmastermind", "charge_max_ticks");

   @Override
   protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
     hookBuilder.addHook(this, ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.PROJECTILE_HIT);
   }

  /**
   * 超蓄力时以每秒5点速度消耗玩家氧气
   */
  @SubscribeEvent
  public static void onUseItemTick(LivingEntityUseItemEvent.Tick event) {
    LivingEntity entity = event.getEntity();
    if (!(entity instanceof Player player)) return;
    if (player.level().isClientSide) return; // 仅服务端执行扣氧
    if (!player.isUsingItem()) return;

    ItemStack stack = event.getItem();
    if (stack.isEmpty()) return;
    if (stack.getUseAnimation() != UseAnim.BOW) return; // 仅针对弓

    // 计算该弓的最大蓄力刻数（默认20tick，按DRAW_SPEED修正）
    float maxTicks = 20.0f;
    try {
      ToolStack tool = ToolStack.from(stack);
      float drawSpeed = 1.0f;
      try {
        drawSpeed = tool.getStats().get(ToolStats.DRAW_SPEED);
        if (drawSpeed <= 0) drawSpeed = 1.0f;
      } catch (Throwable ignored) {
      }
      maxTicks = 20.0f / drawSpeed;
    } catch (IllegalArgumentException ignored) {
      // 非匠魂可修改工具，使用默认值
    }

    // 计算当前已蓄力刻数：以物品自身使用时长为准
    int remaining = player.getUseItemRemainingTicks();
    int useDuration = stack.getUseDuration();
    int drawn = Math.max(0, useDuration - remaining);

    if (drawn > maxTicks) {
      // 每4tick减少1点氧气（约等于每秒5点）
      if ((player.tickCount & 3) == 0) {
        int air = player.getAirSupply();
        if (air > 0) {
          player.setAirSupply(Math.max(0, air - 1));
        }
      }
    }
  }

  /**
   * 蓄力过程中若遭受溺水伤害（窒息），则伤害翻倍
   */
  @SubscribeEvent
  public static void onLivingHurt(LivingHurtEvent event) {
    LivingEntity entity = event.getEntity();
    if (!(entity instanceof Player player)) return;
    DamageSource source = event.getSource();
    if (source == null
            || !(source.is(DamageTypes.DROWN)
            || source.is(DamageTypes.IN_WALL)
            || source.is(DamageTypes.DRY_OUT))) return;
    if (!player.isUsingItem()) return;

    ItemStack using = player.getUseItem();
    if (using.isEmpty()) return;
    if (using.getUseAnimation() != UseAnim.BOW) return; // 仅对弓生效

    float maxTicks = 20.0f;
    try {
      ToolStack tool = ToolStack.from(using);
      float drawSpeed = 1.0f;
      try {
        drawSpeed = tool.getStats().get(ToolStats.DRAW_SPEED);
        if (drawSpeed <= 0) drawSpeed = 1.0f;
      } catch (Throwable ignored) {
      }
      maxTicks = 20.0f / drawSpeed;
    } catch (IllegalArgumentException ignored) {
      // 非匠魂武器，保持默认
    }

    int useDuration = using.getUseDuration();
    int drawn = Math.max(0, useDuration - player.getUseItemRemainingTicks());
    if (drawn > maxTicks) {
      event.setAmount(event.getAmount() * 2.0F);
    }
  }

  @SubscribeEvent
  public static void onArrowLoose(net.minecraftforge.event.entity.player.ArrowLooseEvent event) {
    int drawn = event.getCharge(); // 实际已蓄力刻数
    boolean fullyCharged = drawn >= 20; // 原版弓的满蓄力判定

    ItemStack bow = event.getBow();
    if (!bow.isEmpty()) {
      try {
        // 将数据写入工具的持久化数据，供发射时读取
        ToolStack tool = ToolStack.from(bow);
        ModDataNBT data = tool.getPersistentData();
        // 计算该弓的最大蓄力刻数: 基于DRAW_SPEED, 基准为20tick
        float drawSpeed = 1.0f;
        try {
          drawSpeed = tool.getStats().get(ToolStats.DRAW_SPEED);
          if (drawSpeed <= 0) drawSpeed = 1.0f;
        } catch (Throwable ignored) {
          // 兼容性保护: 若API发生变化或异常, 使用默认1.0
          drawSpeed = 1.0f;
        }
        float maxTicks = 20.0f / drawSpeed;
        data.putFloat(KEY_MAX_TICKS, maxTicks);
        data.putInt(KEY_DRAWN, drawn);
        data.putBoolean(KEY_FULLY, fullyCharged);
      } catch (IllegalArgumentException ignored) {
        // 非匠魂可修改工具，忽略
      }
    }
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
    // 从工具持久化数据读取在松弦事件中记录的值
    ModDataNBT toolData = tool.getPersistentData();
    int drawn = toolData.getInt(KEY_DRAWN);
    boolean fully = toolData.getBoolean(KEY_FULLY);

    // 读取在 onArrowLoose 阶段存储的该弓最大蓄力刻数
    float maxChargeTicks = toolData.getFloat(KEY_MAX_TICKS);
    if (maxChargeTicks <= 0) {
      maxChargeTicks = 20.0f; // 兜底: 按原版 20 tick 计算
    }

    // 以相同公式(与原版BowItem一致)计算旧/新力度, 用比值修正初速度
    float oldPower = computeBowPower(drawn, 20.0f);
    float newPower = computeBowPower(drawn, maxChargeTicks);
    if (oldPower > 0 && newPower >= 0) {
      double ratio = newPower / oldPower;
      projectile.setDeltaMovement(projectile.getDeltaMovement().scale(ratio));
    }

    // 如果充分蓄力则增伤15%
    if (fully && arrow != null) {
      arrow.setBaseDamage(arrow.getBaseDamage() * 1.15);
    }
    // 未蓄满力则减少20%伤害
    if (!fully && arrow != null) {
      arrow.setBaseDamage(arrow.getBaseDamage() * 0.8);
    }

    // 持久化到本次弹射体，供后续事件读取
    persistentData.putInt(KEY_DRAWN, drawn);
    persistentData.putBoolean(KEY_FULLY, fully);

    // 可选：清理避免下次误读
    toolData.putInt(KEY_DRAWN, 0);
    toolData.putBoolean(KEY_FULLY, false);
    toolData.putFloat(KEY_MAX_TICKS, 0f);
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
    // 委托到无弹药重载，确保逻辑一致
    onProjectileLaunch(tool, modifier, shooter, projectile, arrow, persistentData, primary);
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
      return false;
  }

  @Override
  public void onProjectileHitBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity attacker) {
    ProjectileHitModifierHook.super.onProjectileHitBlock(modifiers, persistentData, modifier, projectile, hit, attacker);
  }

  /**
   * 原版BowItem的力度计算, 允许自定义最大蓄力时间
   */
  private static float computeBowPower(int drawnTicks, float maxChargeTicks) {
    if (maxChargeTicks <= 0) return 0f;
    float f = drawnTicks / maxChargeTicks;
    f = (f * f + f * 2.0F) / 3.0F;
    return Mth.clamp(f, 0.0F, 1.0F);
  }
}
