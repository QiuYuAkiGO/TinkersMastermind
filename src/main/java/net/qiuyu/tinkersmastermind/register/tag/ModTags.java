package net.qiuyu.tinkersmastermind.register.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

/**
 * Tag helpers for this mod.
 * Provides access to tags used by the mod. Currently includes a tag that covers all Minecraft coral blocks.
 */
public final class ModTags {
  private ModTags() {}

  public static final class Blocks {
    private Blocks() {}

    /**
     * Tag that includes all vanilla coral blocks. References the existing vanilla tag "minecraft:coral_blocks".
     */
    public static final TagKey<Block> CORAL_BLOCKS = tag(new ResourceLocation("minecraft", "coral_blocks"));

    private static TagKey<Block> tag(ResourceLocation id) {
      return TagKey.create(Registries.BLOCK, id);
    }
  }
}
