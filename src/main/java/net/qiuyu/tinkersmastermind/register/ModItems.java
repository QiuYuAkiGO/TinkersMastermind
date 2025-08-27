package net.qiuyu.tinkersmastermind.register;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.qiuyu.tinkersmastermind.register.item.Jimsonweed;
import net.qiuyu.tinkersmastermind.register.item.misc.Insole;
import net.qiuyu.tinkersmastermind.register.item.misc.Vomit;
import net.qiuyu.tinkersmastermind.register.item.ZombieIron;
import net.qiuyu.tinkersmastermind.register.item.Coral;
import net.qiuyu.tinkersmastermind.register.item.CoralCopper;

import static net.qiuyu.tinkersmastermind.TinkersMastermind.*;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static RegistryObject<Item> ZOMBIE_IRON = ITEMS.register("zombie_iron", ZombieIron::register_item);
    public static RegistryObject<Item> JIMSONWEED = ITEMS.register("jimsonweed", Jimsonweed::register_item);
    public static RegistryObject<Item> VOMIT = ITEMS.register("vomit", Vomit::register_item);
    public static RegistryObject<Item> INSOLE = ITEMS.register("insole", Insole::register_item);
    public static RegistryObject<Item> CORAL = ITEMS.register("coral", Coral::register_item);
    public static RegistryObject<Item> CORAL_COPPER = ITEMS.register("coral_copper", CoralCopper::register_item);

    public static BlockItem register_block(Block block) {
        return new BlockItem(block, new Item.Properties());
    }

    public static Item register_item() {
        return new Item(new Item.Properties());
    }


}
