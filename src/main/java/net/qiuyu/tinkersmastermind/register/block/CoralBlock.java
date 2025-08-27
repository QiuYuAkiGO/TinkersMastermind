package net.qiuyu.tinkersmastermind.register.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class CoralBlock extends Block {
    public CoralBlock(Properties properties) {
        super(properties);
    }
    public static Properties properties() {
        return Properties.copy(Blocks.BONE_BLOCK);
    }
}
