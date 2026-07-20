package net.qiuyu.tinkersmastermind.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static net.qiuyu.tinkersmastermind.TinkersMastermind.MOD_ID;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<CreativeModeTab> TINKERS_MASTERMIND = CREATIVE_MODE_TABS.register("tinkersmastermind",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.tinkersmastermind.tab"))
                    .icon(() -> new ItemStack(ModItems.ZOMBIE_IRON.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.ZOMBIE_IRON.get());
                        output.accept(ModItems.JIMSONWEED.get());
                        output.accept(ModItems.CORAL.get());
                        output.accept(ModItems.CORAL_COPPER.get());
                        output.accept(ModItems.VOMIT.get());
                        output.accept(ModItems.INSOLE.get());
                        output.accept(ModItems.NETHER_FORTRESS_COMPASS.get());

                        output.accept(ModBlocks.ZOMBIE_IRON_BLOCK.get());
                        output.accept(ModBlocks.JIMSONWEED_BLOCK.get());
                        output.accept(ModBlocks.FORGING_TABLE.get());
                        output.accept(ModBlocks.MODULAR_DISPLAY_FRAME.get());

                        output.accept(ModFluids.MOLTEN_ZOMBIE_IRON.getBucket());
                        output.accept(ModFluids.MOLTEN_JIMSONWEED.getBucket());
                        output.accept(ModFluids.MOLTEN_CORAL.getBucket());
                    })
                    .build());
}
