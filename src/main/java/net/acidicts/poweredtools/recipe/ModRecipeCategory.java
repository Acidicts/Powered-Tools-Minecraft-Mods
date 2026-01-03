package net.acidicts.poweredtools.recipe;

import net.minecraft.recipe.book.RecipeCategory;

public enum ModRecipeCategory {
    RECYCLING("recycling"),
    ALLOYING("alloying");

    private final String name;

    private ModRecipeCategory(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
