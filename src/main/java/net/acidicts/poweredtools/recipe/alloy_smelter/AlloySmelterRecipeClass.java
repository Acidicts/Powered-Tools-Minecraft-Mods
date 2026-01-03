package net.acidicts.poweredtools.recipe.alloy_smelter;

import net.acidicts.poweredtools.block.ModBlocks;
import net.acidicts.poweredtools.recipe.ModRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CookingRecipeCategory;

public class AlloySmelterRecipeClass extends AbstractCookingRecipe {
    public AlloySmelterRecipeClass(String group, CookingRecipeCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
        super(ModRecipes.RECYCLER_TYPE, group, category, ingredient, result, experience, cookingTime);
    }

    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.RECYCLER);
    }

    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.RECYCLER_SERIALIZER;
    }
}