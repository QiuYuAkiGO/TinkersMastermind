package net.qiuyu.tinkersmastermind.effects;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class NoHealEffect extends BaseEffect  {
    public NoHealEffect(MobEffectCategory type, int color, boolean isInstant) {
        super(type, color, isInstant);
    }
    public NoHealEffect(){
        super(MobEffectCategory.HARMFUL, 0x660033, false);
    }
    @SubscribeEvent
    public void noHealingEvent(LivingHealEvent event){
        event.setResult(Event.Result.DENY);
    }
}
