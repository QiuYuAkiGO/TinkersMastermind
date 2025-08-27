package net.qiuyu.tinkersmastermind.register.item.misc;

import net.minecraft.world.item.Item;

public class Insole {
    public static Item register_item() {
        return new Item(new Item.Properties()
                .stacksTo(2));
    }
}
