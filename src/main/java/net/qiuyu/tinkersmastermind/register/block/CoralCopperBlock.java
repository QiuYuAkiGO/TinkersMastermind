package net.qiuyu.tinkersmastermind.register.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class CoralCopperBlock extends Block {
    public CoralCopperBlock(Properties properties) {
        super(properties);
    }
    public static Properties properties() {
        return Properties.copy(Blocks.COPPER_BLOCK);
    }
}
