package net.qiuyu.tinkersmastermind.register.item.misc;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public class Vomit {
    public static Item register_item() {
        return new Item(new Item.Properties()
                .stacksTo(64)
                .food(new FoodProperties.Builder()
                        .nutrition(2)
                        .meat()
                        .effect(() -> new MobEffectInstance(MobEffects.BAD_OMEN, 100, 0), 0.4F)
                        .build())
        );
    }
}
