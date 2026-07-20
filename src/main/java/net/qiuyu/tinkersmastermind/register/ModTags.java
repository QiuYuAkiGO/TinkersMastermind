package net.qiuyu.tinkersmastermind.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.qiuyu.tinkersmastermind.TinkersMastermind;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> FORGING_HAMMERS = tag("forging_hammers");

        private static TagKey<Item> tag(String name) {
            return TagKey.create(Registries.ITEM, new ResourceLocation(TinkersMastermind.MOD_ID, name));
        }
    }
}
