package net.qiuyu.tinkersmastermind.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.qiuyu.tinkersmastermind.register.ModEffects;
import net.qiuyu.tinkersmastermind.register.ModTags;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CoralEffect extends BaseEffect {
    public static final TagKey<Item> drinks = ItemTags.create(new ResourceLocation("tinkersmastermind", "drinks"));
    public CoralEffect(MobEffectCategory type, int color, boolean isInstant) {
        super(type, color, isInstant);
    }
    public CoralEffect(){
        super(MobEffectCategory.BENEFICIAL, 0xFFB6C1, false);
    }

    @SubscribeEvent
    public void drinkingEvent(LivingEntityUseItemEvent event){
        LivingEntity entity = event.getEntity();
        if (event.getItem().is(drinks) && entity instanceof Player){
            event.getEntity().addEffect(new MobEffectInstance(ModEffects.CORAL_HARDEN.get(), 2400, 0, true, true));
        }
    }
}
