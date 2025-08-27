package net.qiuyu.tinkersmastermind.datagen;


import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.qiuyu.tinkersmastermind.TinkersMastermind;
import net.qiuyu.tinkersmastermind.register.ModItems;


public class ModItemModelGen extends ItemModelProvider {

    public static final String GENERATED = "item/generated";
    public static final String HANDHELD = "item/handheld";


    public ModItemModelGen(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TinkersMastermind.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        itemGeneratedModel(ModItems.ZOMBIE_IRON.get(), resourceItem((itemName(ModItems.ZOMBIE_IRON.get()))));
        itemGeneratedModel(ModItems.JIMSONWEED.get(), resourceItem((itemName(ModItems.JIMSONWEED.get()))));
        itemGeneratedModel(ModItems.VOMIT.get(), resourceItem((itemName(ModItems.VOMIT.get()))));
        itemGeneratedModel(ModItems.INSOLE.get(), resourceItem((itemName(ModItems.INSOLE.get()))));
    }

    public void itemGeneratedModel(Item item, ResourceLocation texture) {
        withExistingParent(itemName(item), GENERATED).texture("layer0", texture);
    }

    private String itemName(Item item) {
        return ForgeRegistries.ITEMS.getKey(item).getPath();
    }

    public ResourceLocation resourceBlock(String path) {
        return new ResourceLocation(TinkersMastermind.MOD_ID, "block/" + path);
    }

    public ResourceLocation resourceItem(String path) {
        return new ResourceLocation(TinkersMastermind.MOD_ID, "item/" + path);
    }
}
