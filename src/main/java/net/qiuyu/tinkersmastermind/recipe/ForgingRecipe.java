package net.qiuyu.tinkersmastermind.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.qiuyu.tinkersmastermind.blockentity.ForgingTableBlockEntity;
import net.qiuyu.tinkersmastermind.register.ModBlocks;

public class ForgingRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final NonNullList<Ingredient> ingredients;
    private final int targetTemperature;
    private final int tolerance;
    private final ItemStack result;
    private final ItemStack byproduct;

    public ForgingRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, int targetTemperature, int tolerance,
                         ItemStack result, ItemStack byproduct) {
        this.id = id;
        this.ingredients = ingredients;
        this.targetTemperature = targetTemperature;
        this.tolerance = tolerance;
        this.result = result;
        this.byproduct = byproduct;
    }

    @Override
    public boolean matches(Container container, Level level) {
        if (countItems(container) != ingredients.size()) {
            return false;
        }

        int[] used = new int[container.getContainerSize()];
        for (Ingredient ingredient : ingredients) {
            boolean matched = false;
            for (int slot = 0; slot < container.getContainerSize(); slot++) {
                ItemStack stack = container.getItem(slot);
                if (!stack.isEmpty() && used[slot] < stack.getCount() && ingredient.test(stack)) {
                    used[slot]++;
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }
        return true;
    }

    private int countItems(Container container) {
        int count = 0;
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            count += container.getItem(slot).getCount();
        }
        return count;
    }

    public boolean isTemperatureClose(double currentTemperature) {
        return Math.abs(currentTemperature - targetTemperature) <= tolerance;
    }

    public int getTargetTemperature() {
        return targetTemperature;
    }

    public int getTolerance() {
        return tolerance;
    }

    public int getMinimumTemperature() {
        return targetTemperature - tolerance;
    }

    public int getMaximumTemperature() {
        return targetTemperature + tolerance;
    }

    public ItemStack getResult() {
        return result.copy();
    }

    public ItemStack getByproduct() {
        return byproduct;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= ingredients.size();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.FORGING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.FORGING_TYPE.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.FORGING_TABLE.get());
    }

    public static class Serializer implements RecipeSerializer<ForgingRecipe> {
        @Override
        public ForgingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            JsonArray ingredientJson = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> ingredients = NonNullList.create();
            ingredientJson.forEach(element -> {
                Ingredient ingredient = Ingredient.fromJson(element);
                if (!ingredient.isEmpty()) {
                    ingredients.add(ingredient);
                }
            });

            if (ingredients.isEmpty()) {
                throw new JsonParseException("Forging recipe requires at least one ingredient");
            }
            if (ingredients.size() > ForgingTableBlockEntity.SLOT_COUNT) {
                throw new JsonParseException("Forging recipe cannot have more than "
                        + ForgingTableBlockEntity.SLOT_COUNT + " ingredients");
            }

            int targetTemperature = json.has("target_temperature")
                    ? GsonHelper.getAsInt(json, "target_temperature")
                    : GsonHelper.getAsInt(json, "temperature");
            int tolerance = GsonHelper.getAsInt(json, "tolerance", 50);
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            ItemStack byproduct = ItemStack.EMPTY;
            if (json.has("byproduct")) {
                byproduct = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "byproduct"));
            }

            return new ForgingRecipe(recipeId, ingredients, targetTemperature, tolerance, result, byproduct);
        }

        @Override
        public ForgingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int ingredientCount = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
            for (int i = 0; i < ingredientCount; i++) {
                ingredients.set(i, Ingredient.fromNetwork(buffer));
            }

            int targetTemperature = buffer.readVarInt();
            int tolerance = buffer.readVarInt();
            ItemStack result = buffer.readItem();
            ItemStack byproduct = buffer.readItem();
            return new ForgingRecipe(recipeId, ingredients, targetTemperature, tolerance, result, byproduct);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ForgingRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                ingredient.toNetwork(buffer);
            }
            buffer.writeVarInt(recipe.targetTemperature);
            buffer.writeVarInt(recipe.tolerance);
            buffer.writeItem(recipe.result);
            buffer.writeItem(recipe.byproduct);
        }
    }
}
