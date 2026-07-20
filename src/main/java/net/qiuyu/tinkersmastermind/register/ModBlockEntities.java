package net.qiuyu.tinkersmastermind.register;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.qiuyu.tinkersmastermind.blockentity.ForgingTableBlockEntity;
import net.qiuyu.tinkersmastermind.blockentity.ModularDisplayFrameBlockEntity;

import static net.qiuyu.tinkersmastermind.TinkersMastermind.MOD_ID;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);

    public static final RegistryObject<BlockEntityType<ForgingTableBlockEntity>> FORGING_TABLE =
            BLOCK_ENTITIES.register("forging_table",
                    () -> BlockEntityType.Builder.of(ForgingTableBlockEntity::new, ModBlocks.FORGING_TABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<ModularDisplayFrameBlockEntity>> MODULAR_DISPLAY_FRAME =
            BLOCK_ENTITIES.register("modular_display_frame",
                    () -> BlockEntityType.Builder.of(ModularDisplayFrameBlockEntity::new, ModBlocks.MODULAR_DISPLAY_FRAME.get()).build(null));
}
