package net.qiuyu.tinkersmastermind;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ModConfig {
    public static final CommonConfig COMMON;
    public static ForgeConfigSpec config;

    static {
        final Pair<CommonConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON = pair.getLeft();
        config = pair.getRight();
    }

    public static class CommonConfig {
        CommonConfig(ForgeConfigSpec.Builder builder) {
            // 可用于世界内方块生成.
//            builder.comment("Void Crystal Ore Worldgen").push("void_crystal_ore");
//            voidcrystalOre = new VoidCrystalOreConfig(builder);
//            builder.pop();
        }
    }

    public static class OreConfig {
        public ForgeConfigSpec.BooleanValue enabled;
        public ForgeConfigSpec.IntValue minY;
        public ForgeConfigSpec.IntValue maxY;
        public ForgeConfigSpec.IntValue count;
        public ForgeConfigSpec.IntValue size;

        public OreConfig(ForgeConfigSpec.Builder builder) {
        }

        public boolean isEnabled() {
            return enabled.get();
        }

        public int getCount() {
            return count.get();
        }

        public int getSize() {
            return size.get();
        }

        public int getMaxY() {
            return maxY.get();
        }

        public int getMinY() {
            return minY.get();
        }
    }
}
