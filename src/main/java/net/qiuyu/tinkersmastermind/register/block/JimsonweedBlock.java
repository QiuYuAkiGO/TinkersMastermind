package net.qiuyu.tinkersmastermind.register.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class JimsonweedBlock extends Block {
    public JimsonweedBlock(Properties properties) {
        super(properties);
    }
    public static Properties properties() {
        return Properties.copy(Blocks.DIAMOND_BLOCK);
    }
}
