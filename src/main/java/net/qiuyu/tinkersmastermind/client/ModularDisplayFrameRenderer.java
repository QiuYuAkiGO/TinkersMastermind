package net.qiuyu.tinkersmastermind.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.qiuyu.tinkersmastermind.blockentity.ModularDisplayFrameBlockEntity;
import net.qiuyu.tinkersmastermind.register.ModBlocks;
import net.qiuyu.tinkersmastermind.register.block.ModularDisplayFrameBlock;

public class ModularDisplayFrameRenderer implements BlockEntityRenderer<ModularDisplayFrameBlockEntity> {
    private static final float ITEM_SCALE_PER_FRAME = 0.55F;
    private static final float DEGREES_PER_ROTATION = 45.0F;
    private static final double ITEM_SURFACE_OFFSET = 0.125D;

    private final ItemRenderer itemRenderer;
    private final BlockRenderDispatcher blockRenderer;

    public ModularDisplayFrameRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(ModularDisplayFrameBlockEntity frame, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Level level = frame.getLevel();
        if (level == null) {
            return;
        }

        BlockPos pos = frame.getBlockPos();
        Direction facing = frame.getBlockState().getValue(ModularDisplayFrameBlock.FACING);
        ModularDisplayFrameBlock.FrameGroup group = ModularDisplayFrameBlock.findGroup(level, pos, facing);
        if (!group.isAnchor(pos)) {
            return;
        }

        ModularDisplayFrameBlockEntity displayFrame = ModularDisplayFrameBlockEntity.findDisplayFrame(level, group);
        if (displayFrame == null) {
            if (group.isMerged()) {
                renderFrame(group, poseStack, buffer, packedLight, packedOverlay);
            }
            return;
        }

        ItemStack stack = displayFrame.getDisplayStack();
        if (stack.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        translateToGroupCenter(poseStack, group);
        rotateToFace(poseStack, facing);
        poseStack.mulPose(Axis.ZP.rotationDegrees(displayFrame.getRotation() * DEGREES_PER_ROTATION));
        float scale = ITEM_SCALE_PER_FRAME * group.size();
        poseStack.scale(scale, scale, scale);
        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, buffer,
                level, (int) pos.asLong());
        poseStack.popPose();
    }

    private void renderFrame(ModularDisplayFrameBlock.FrameGroup group, PoseStack poseStack,
                             MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        translateToGroupBlockCenter(poseStack, group);
        scaleAlongFramePlane(poseStack, group);
        poseStack.translate(-0.5D, -0.5D, -0.5D);
        BlockState renderState = ModBlocks.MODULAR_DISPLAY_FRAME.get().defaultBlockState()
                .setValue(ModularDisplayFrameBlock.FACING, group.facing())
                .setValue(ModularDisplayFrameBlock.GROUP_SIZE, 1)
                .setValue(ModularDisplayFrameBlock.HAS_ITEM, false);
        blockRenderer.renderSingleBlock(renderState, poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    private void translateToGroupBlockCenter(PoseStack poseStack, ModularDisplayFrameBlock.FrameGroup group) {
        double distance = (group.size() - 1) * 0.5D;
        double x = 0.5D + group.right().getStepX() * distance + group.down().getStepX() * distance;
        double y = 0.5D + group.right().getStepY() * distance + group.down().getStepY() * distance;
        double z = 0.5D + group.right().getStepZ() * distance + group.down().getStepZ() * distance;
        poseStack.translate(x, y, z);
    }

    private void scaleAlongFramePlane(PoseStack poseStack, ModularDisplayFrameBlock.FrameGroup group) {
        float scale = group.size();
        switch (group.facing().getAxis()) {
            case X -> poseStack.scale(1.0F, scale, scale);
            case Y -> poseStack.scale(scale, 1.0F, scale);
            case Z -> poseStack.scale(scale, scale, 1.0F);
        }
    }

    private void translateToGroupCenter(PoseStack poseStack, ModularDisplayFrameBlock.FrameGroup group) {
        Direction facing = group.facing();
        double x = 0.5D;
        double y = 0.5D;
        double z = 0.5D;

        double faceOffset = facing.getAxisDirection() == Direction.AxisDirection.POSITIVE
                ? ITEM_SURFACE_OFFSET
                : 1.0D - ITEM_SURFACE_OFFSET;
        switch (facing.getAxis()) {
            case X -> x = faceOffset;
            case Y -> y = faceOffset;
            case Z -> z = faceOffset;
        }

        double distance = (group.size() - 1) * 0.5D;
        x += group.right().getStepX() * distance + group.down().getStepX() * distance;
        y += group.right().getStepY() * distance + group.down().getStepY() * distance;
        z += group.right().getStepZ() * distance + group.down().getStepZ() * distance;
        poseStack.translate(x, y, z);
    }

    private void rotateToFace(PoseStack poseStack, Direction facing) {
        switch (facing) {
            case NORTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            case SOUTH -> {
            }
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
            case UP -> poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        }
    }

    @Override
    public boolean shouldRenderOffScreen(ModularDisplayFrameBlockEntity blockEntity) {
        return true;
    }
}
