package net.qiuyu.tinkersmastermind.datagen;


import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import net.qiuyu.tinkersmastermind.TinkersMastermind;
import net.qiuyu.tinkersmastermind.register.ModBlocks;
import net.qiuyu.tinkersmastermind.register.ModEffects;
import net.qiuyu.tinkersmastermind.register.ModItems;


public class ModLangGenEN extends LanguageProvider {
    public ModLangGenEN(PackOutput output, String locale) {
        super(output, TinkersMastermind.MOD_ID, locale);
    }

    @Override
    protected void addTranslations() {
        add(ModItems.ZOMBIE_IRON.get(), "Zombie Iron");
        add(ModBlocks.ZOMBIE_IRON_BLOCK.get(), "Zombie Iron Block");
        add(ModBlocks.JIMSONWEED_BLOCK.get(), "Jimsonweed Block");
        add(ModItems.JIMSONWEED.get(), "Jimsonweed");
        add(ModItems.VOMIT.get(), "Vomit things");
        add(ModItems.INSOLE.get(), "Insole");
        add(ModEffects.POISON_RESIST.get(),"Poison Resistance");
        add(ModEffects.FOOT_CLEAN.get(),"Clean Feet");
        add(ModEffects.FOOT_ODOR.get(),"Feet Odor");
        add("modifier.tinkersmastermind.insole.description","Remember to wash your feet.");
        add("fluid_type.tinkersmastermind.molten_zombie_iron", "Molten Zombie Iron");
        add("item.tinkersmastermind.molten_zombie_iron_bucket", "Molten Zombie Iron Bucket");
        add("fluid_type.tinkersmastermind.molten_jimsonweed", "Molten Jimsonweed");
        add("item.tinkersmastermind.molten_jimsonweed_bucket", "Molten Jimsonweed Bucket");
        add("itemGroup.tinkersmastermind.tab","Tinkers' Mastermind");
        add("modifier.tinkersmastermind.rotten","Rotten");
        add("modifier.tinkersmastermind.hungry","I'm Hungry");
        add("modifier.tinkersmastermind.bluster","Bluster");
        add("modifier.tinkersmastermind.feeding","feeding");
        add("modifier.tinkersmastermind.foot_odor","Feet Odor");
        add("modifier.tinkersmastermind.poison_sparkle","Poison Sparkle");
        add("modifier.tinkersmastermind.poison_erosion","Poison Erosion");
        add("modifier.tinkersmastermind.heavy_poison","Heavy Poison");
    }

}
