package net.qiuyu.tinkersmastermind.register;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.qiuyu.tinkersmastermind.register.block.JimsonweedBlock;
import net.qiuyu.tinkersmastermind.register.block.ZombieIronBlock;

import java.util.function.Supplier;

import static net.qiuyu.tinkersmastermind.TinkersMastermind.MOD_ID;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);

    public static final RegistryObject<Block> ZOMBIE_IRON_BLOCK = registerBlock("zombie_iron_block",
            () -> new Block(ZombieIronBlock.properties()));
    public static final RegistryObject<Block> JIMSONWEED_BLOCK = registerBlock("jimsonweed_block",
            () -> new Block(JimsonweedBlock.properties()));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

}
