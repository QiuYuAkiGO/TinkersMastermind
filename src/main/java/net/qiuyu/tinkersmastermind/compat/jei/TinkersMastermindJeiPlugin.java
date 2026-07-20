package net.qiuyu.tinkersmastermind.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.qiuyu.tinkersmastermind.TinkersMastermind;
import net.qiuyu.tinkersmastermind.recipe.ForgingRecipe;
import net.qiuyu.tinkersmastermind.recipe.ModRecipes;
import net.qiuyu.tinkersmastermind.register.ModBlocks;

import java.util.List;

@JeiPlugin
public class TinkersMastermindJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_UID = new ResourceLocation(TinkersMastermind.MOD_ID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new ForgingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }

        List<ForgingRecipe> recipes = minecraft.level.getRecipeManager().getAllRecipesFor(ModRecipes.FORGING_TYPE.get());
        registration.addRecipes(ForgingRecipeCategory.TYPE, recipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalysts(ForgingRecipeCategory.TYPE, ModBlocks.FORGING_TABLE.get());
    }
}
