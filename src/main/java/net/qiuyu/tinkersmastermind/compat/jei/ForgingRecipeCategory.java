package net.qiuyu.tinkersmastermind.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.qiuyu.tinkersmastermind.TinkersMastermind;
import net.qiuyu.tinkersmastermind.recipe.ForgingRecipe;
import net.qiuyu.tinkersmastermind.register.ModBlocks;

public class ForgingRecipeCategory implements IRecipeCategory<ForgingRecipe> {
    public static final RecipeType<ForgingRecipe> TYPE =
            RecipeType.create(TinkersMastermind.MOD_ID, "forging", ForgingRecipe.class);

    private static final int WIDTH = 158;
    private static final int HEIGHT = 88;
    private static final int INPUT_X = 1;
    private static final int INPUT_Y = 1;
    private static final int INPUT_COLUMNS = 4;
    private static final int SLOT_SPACING = 18;
    private static final int ARROW_X = 84;
    private static final int ARROW_Y = 26;
    private static final int RESULT_X = 126;
    private static final int RESULT_Y = 18;
    private static final int BYPRODUCT_Y = 50;
    private static final int TEXT_Y = 76;

    private final Component title = Component.translatable("jei.tinkersmastermind.forging");
    private final IDrawable icon;
    private final IDrawable arrow;

    public ForgingRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.FORGING_TABLE.get()));
        this.arrow = guiHelper.getRecipeArrow();
    }

    @Override
    public RecipeType<ForgingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ForgingRecipe recipe, IFocusGroup focuses) {
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            Ingredient ingredient = recipe.getIngredients().get(i);
            if (ingredient.isEmpty()) {
                continue;
            }

            int x = INPUT_X + i % INPUT_COLUMNS * SLOT_SPACING;
            int y = INPUT_Y + i / INPUT_COLUMNS * SLOT_SPACING;
            builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                    .addIngredients(ingredient)
                    .setStandardSlotBackground();
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, RESULT_X, RESULT_Y)
                .addItemStack(recipe.getResult())
                .setOutputSlotBackground();

        if (!recipe.getByproduct().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, RESULT_X, BYPRODUCT_Y)
                    .addItemStack(recipe.getByproduct().copy())
                    .setOutputSlotBackground()
                    .addRichTooltipCallback((slotView, tooltip) ->
                            tooltip.add(Component.translatable("jei.tinkersmastermind.forging.byproduct")));
        }
    }

    @Override
    public void draw(ForgingRecipe recipe, mezz.jei.api.gui.ingredient.IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {
        arrow.draw(guiGraphics, ARROW_X, ARROW_Y);

        Font font = Minecraft.getInstance().font;
        Component temperature = Component.translatable("jei.tinkersmastermind.forging.temperature",
                recipe.getMinimumTemperature(), recipe.getMaximumTemperature());
        guiGraphics.drawString(font, temperature, INPUT_X, TEXT_Y, 0x404040, false);
    }
}
