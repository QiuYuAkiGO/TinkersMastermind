package net.qiuyu.tinkersmastermind;


import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.qiuyu.tinkersmastermind.recipe.ModRecipes;
import net.qiuyu.tinkersmastermind.register.*;

@Mod(TinkersMastermind.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TinkersMastermind {
    public static final String MOD_ID = "tinkersmastermind";

    public TinkersMastermind() {
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, ModConfig.config);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        ModItems.ITEMS.register(bus);
        ModBlocks.BLOCKS.register(bus);
        ModFluids.FLUIDS.register(bus);
        ModModifiers.MODIFIERS.register(bus);
        ModEffects.EFFECTS.register(bus);
        CreativeModeTab.builder()
                .title(CreativeModeTab.builder().title(net.minecraft.network.chat.Component.translatable("itemGroup.tinkersmastermind.tab")).build().getDisplayName())
                .icon(() -> new ItemStack(ModItems.ZOMBIE_IRON.get()))
                .displayItems((parameters, output) -> {
                    output.accept(ModItems.ZOMBIE_IRON.get());
                })
                .build();
        ModRecipes.register(bus);
    }

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

    }
}
