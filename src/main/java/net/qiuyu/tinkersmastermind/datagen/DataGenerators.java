package net.qiuyu.tinkersmastermind.datagen;


import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.qiuyu.tinkersmastermind.TinkersMastermind;


@Mod.EventBusSubscriber(modid = TinkersMastermind.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        generator.addProvider(true,new ModItemModelGen(generator.getPackOutput(), helper));
        generator.addProvider(true,new ModBlockModelGen(generator.getPackOutput(), helper));
        generator.addProvider(true,new ModLangGenEN(generator.getPackOutput(), "en_us"));
        generator.addProvider(true,new ModLangGenCN(generator.getPackOutput(), "zh_cn"));
    }
}