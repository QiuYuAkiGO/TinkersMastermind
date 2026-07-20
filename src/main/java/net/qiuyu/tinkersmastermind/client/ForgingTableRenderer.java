package net.qiuyu.tinkersmastermind.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.qiuyu.tinkersmastermind.blockentity.ForgingTableBlockEntity;
import net.qiuyu.tinkersmastermind.recipe.ForgingRecipe;
import org.joml.Matrix4f;

import java.util.Optional;

public class ForgingTableRenderer implements BlockEntityRenderer<ForgingTableBlockEntity> {
    private static final int BAR_WIDTH = 96;
    private static final int BAR_HEIGHT = 8;
    private static final int BAR_SEGMENTS = BAR_WIDTH;
    private static final float ITEM_BASE_HEIGHT = 0.72F;
    private static final float ITEM_LAYER_HEIGHT = 0.026F;
    private static final float ITEM_STACK_SCALE = 0.34F;

    private final ItemRenderer itemRenderer;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private final Font font;

    public ForgingTableRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
        this.entityRenderDispatcher = context.getEntityRenderer();
        this.font = context.getFont();
    }

    @Override
    public void render(ForgingTableBlockEntity forgingTable, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        renderItems(forgingTable, poseStack, buffer, packedLight, packedOverlay);
        if (forgingTable.shouldShowTemperatureBar()) {
            renderTemperatureBar(forgingTable, poseStack, buffer, packedLight);
        }
    }

    private void renderItems(ForgingTableBlockEntity forgingTable, PoseStack poseStack, MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {
        int rendered = 0;
        for (int slot = 0; slot < ForgingTableBlockEntity.SLOT_COUNT; slot++) {
            ItemStack stack = forgingTable.getDisplayStack(slot);
            if (stack.isEmpty()) {
                continue;
            }

            poseStack.pushPose();
            poseStack.translate(0.5F + stackOffset(rendered, 0.018F), ITEM_BASE_HEIGHT + rendered * ITEM_LAYER_HEIGHT,
                    0.5F + stackOffset(rendered + 3, 0.014F));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees((forgingTable.getBlockPos().asLong() + slot * 37) % 360));
            poseStack.scale(ITEM_STACK_SCALE, ITEM_STACK_SCALE, ITEM_STACK_SCALE);
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, buffer,
                    forgingTable.getLevel(), slot);
            poseStack.popPose();
            rendered++;
        }
    }

    private void renderTemperatureBar(ForgingTableBlockEntity forgingTable, PoseStack poseStack, MultiBufferSource buffer,
                                      int packedLight) {
        int start = (int) Math.round(getBarMinimumTemperature(forgingTable));
        int end = (int) Math.round(getBarMaximumTemperature(forgingTable));
        int current = (int) Math.round(forgingTable.getTemperature());
        String left = start + "C";
        String right = end + "C";
        String value = current + "C";

        poseStack.pushPose();
        poseStack.translate(0.5D, 1.2D, 0.5D);
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        poseStack.scale(-0.01F, -0.01F, 0.01F);

        renderBarFrame(poseStack, buffer);
        renderBarFill(forgingTable, poseStack, buffer);
        renderCursor(forgingTable, poseStack, buffer);

        font.drawInBatch(value, -font.width(value) / 2.0F, -14.0F, 0xFFFFFFFF, false,
                poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, packedLight);
        font.drawInBatch(left, -BAR_WIDTH / 2.0F, BAR_HEIGHT + 5.0F, 0xFFE8FFE8, false,
                poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, packedLight);
        font.drawInBatch(right, BAR_WIDTH / 2.0F - font.width(right), BAR_HEIGHT + 5.0F, 0xFFFFE3E0, false,
                poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, packedLight);
        poseStack.popPose();
    }

    private void renderBarFrame(PoseStack poseStack, MultiBufferSource buffer) {
        int color = 0xFF303030;
        fill(poseStack, buffer, -BAR_WIDTH / 2.0F - 1.0F, -1.0F, BAR_WIDTH / 2.0F + 1.0F, 0.0F, 0.04F, color);
        fill(poseStack, buffer, -BAR_WIDTH / 2.0F - 1.0F, BAR_HEIGHT, BAR_WIDTH / 2.0F + 1.0F, BAR_HEIGHT + 1.0F, 0.04F, color);
        fill(poseStack, buffer, -BAR_WIDTH / 2.0F - 1.0F, 0.0F, -BAR_WIDTH / 2.0F, BAR_HEIGHT, 0.04F, color);
        fill(poseStack, buffer, BAR_WIDTH / 2.0F, 0.0F, BAR_WIDTH / 2.0F + 1.0F, BAR_HEIGHT, 0.04F, color);
    }

    private void renderBarFill(ForgingTableBlockEntity forgingTable, PoseStack poseStack, MultiBufferSource buffer) {
        float rangeLeft = Float.NaN;
        float rangeRight = Float.NaN;
        Optional<ForgingRecipe> recipe = forgingTable.findMatchingRecipe();
        if (recipe.isPresent()) {
            ForgingRecipe forgingRecipe = recipe.get();
            rangeLeft = temperatureToX(forgingTable, forgingRecipe.getTargetTemperature() - forgingRecipe.getTolerance());
            rangeRight = temperatureToX(forgingTable, forgingRecipe.getTargetTemperature() + forgingRecipe.getTolerance());
            rangeLeft = clamp(rangeLeft, -BAR_WIDTH / 2.0F, BAR_WIDTH / 2.0F);
            rangeRight = clamp(rangeRight, -BAR_WIDTH / 2.0F, BAR_WIDTH / 2.0F);
        }

        for (int segment = 0; segment < BAR_SEGMENTS; segment++) {
            float start = -BAR_WIDTH / 2.0F + BAR_WIDTH * segment / (float) BAR_SEGMENTS;
            float end = -BAR_WIDTH / 2.0F + BAR_WIDTH * (segment + 1) / (float) BAR_SEGMENTS;
            float center = (start + end) * 0.5F;
            float progress = segment / (float) (BAR_SEGMENTS - 1);
            int color = !Float.isNaN(rangeLeft) && center >= rangeLeft && center <= rangeRight
                    ? 0xFFFFD83D
                    : blend(0xFF35D06F, 0xFFE0443E, progress);
            fill(poseStack, buffer, start, 0.0F, end, BAR_HEIGHT, 0.02F, color);
        }
    }

    private void renderCursor(ForgingTableBlockEntity forgingTable, PoseStack poseStack, MultiBufferSource buffer) {
        float x = temperatureToX(forgingTable, forgingTable.getTemperature());
        fill(poseStack, buffer, x - 0.9F, -5.0F, x + 0.9F, BAR_HEIGHT + 5.0F, -0.02F, 0xFFFFFFFF);
    }

    private float temperatureToX(ForgingTableBlockEntity forgingTable, double temperature) {
        double min = getBarMinimumTemperature(forgingTable);
        double max = getBarMaximumTemperature(forgingTable);
        if (Math.abs(max - min) < 0.001D) {
            return -BAR_WIDTH / 2.0F;
        }
        double progress = (temperature - min) / (max - min);
        return -BAR_WIDTH / 2.0F + (float) Math.max(0.0D, Math.min(1.0D, progress)) * BAR_WIDTH;
    }

    private double getBarMinimumTemperature(ForgingTableBlockEntity forgingTable) {
        return Math.min(forgingTable.getHeatStartTemperature(), forgingTable.getFuelTemperature());
    }

    private double getBarMaximumTemperature(ForgingTableBlockEntity forgingTable) {
        return Math.max(forgingTable.getHeatStartTemperature(), forgingTable.getFuelTemperature());
    }

    private void fill(PoseStack poseStack, MultiBufferSource buffer, float x1, float y1, float x2, float y2, float z, int color) {
        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer consumer = buffer.getBuffer(RenderType.gui());
        int alpha = color >>> 24 & 0xFF;
        int red = color >>> 16 & 0xFF;
        int green = color >>> 8 & 0xFF;
        int blue = color & 0xFF;
        consumer.vertex(matrix, x1, y2, z).color(red, green, blue, alpha).endVertex();
        consumer.vertex(matrix, x2, y2, z).color(red, green, blue, alpha).endVertex();
        consumer.vertex(matrix, x2, y1, z).color(red, green, blue, alpha).endVertex();
        consumer.vertex(matrix, x1, y1, z).color(red, green, blue, alpha).endVertex();
    }

    private float stackOffset(int layer, float scale) {
        return ((layer % 5) - 2) * scale;
    }

    private int blend(int left, int right, float progress) {
        int alpha = 0xFF;
        int red = blendChannel(left >>> 16 & 0xFF, right >>> 16 & 0xFF, progress);
        int green = blendChannel(left >>> 8 & 0xFF, right >>> 8 & 0xFF, progress);
        int blue = blendChannel(left & 0xFF, right & 0xFF, progress);
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    private int blendChannel(int left, int right, float progress) {
        return Math.round(left + (right - left) * progress);
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
