package net.acidicts.poweredtools.recipe;

import net.acidicts.poweredtools.block.ModBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CookingRecipeCategory;

public class RecyclerRecipeClass extends AbstractCookingRecipe {
    public RecyclerRecipeClass(String group, CookingRecipeCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
        super(ModRecipes.RECYCLER_TYPE, group, category, ingredient, result, experience, cookingTime);
    }

    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.RECYCLER);
    }

    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.RECYCLER_SERIALIZER;
    }
}