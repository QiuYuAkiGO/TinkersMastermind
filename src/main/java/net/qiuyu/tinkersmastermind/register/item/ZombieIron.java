package net.qiuyu.tinkersmastermind.register.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;


public class ZombieIron {
    public static Item register_item() {
        return new Item(new Item.Properties()
                .stacksTo(64)
                .food(new FoodProperties.Builder()
                        .nutrition(6)
                        .meat()
                        .effect(() -> new MobEffectInstance(net.minecraft.world.effect.MobEffects.HUNGER, 600, 0), 0.3F)
                        .build())
        );
    }
}
