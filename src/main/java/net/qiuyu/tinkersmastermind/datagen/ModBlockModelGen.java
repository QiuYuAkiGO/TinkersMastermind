package net.qiuyu.tinkersmastermind.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.qiuyu.tinkersmastermind.TinkersMastermind;
import net.qiuyu.tinkersmastermind.register.ModBlocks;


public class ModBlockModelGen extends BlockStateProvider {

    public ModBlockModelGen(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, TinkersMastermind.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        this.registerBlockModelAndItem(ModBlocks.ZOMBIE_IRON_BLOCK.get());
        this.registerBlockModelAndItem(ModBlocks.JIMSONWEED_BLOCK.get());
//        simpleBlockItem(TinkersMastermindBlocks.STRIPPED_EBONY_WOOD.get(), models().withExistingParent("tutorialmod:stripped_ebony_wood", "minecraft:block/cube_column"));
    }

    public void  registerBlockModelAndItem(Block block){
        this.simpleBlockWithItem(block,this.cubeAll(block));
    }
    public void  registerBlockModelAndItemOriented(Block block){
//        this.simpleBlockWithItem(block,this.models().orientable(block.getName(),));
    }

    public ResourceLocation blockTexture(Block block) {
        ResourceLocation name = key(block);
        return new ResourceLocation(name.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + name.getPath());
    }

    private ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    private String name(Block block) {
        return key(block).getPath();
    }
}