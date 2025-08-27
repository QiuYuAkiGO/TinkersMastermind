package net.qiuyu.tinkersmastermind.register.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class ZombieIronBlock extends Block{
    public ZombieIronBlock(Properties properties) {
        super(properties);
    }

    public static Properties properties() {
        return Properties.copy(Blocks.IRON_BLOCK);
    }
}
