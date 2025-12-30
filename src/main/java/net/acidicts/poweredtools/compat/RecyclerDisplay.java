package net.acidicts.poweredtools.compat;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.acidicts.poweredtools.recipe.RecyclerRecipe;
import net.minecraft.recipe.RecipeEntry;

import java.util.List;

public class RecyclerDisplay extends BasicDisplay {


    public RecyclerDisplay(RecipeEntry<RecyclerRecipe> recipe) {
        super(List.of(EntryIngredients.ofIngredient(recipe.value().getIngredients().getFirst())),
                List.of(EntryIngredient.of(EntryStacks.of(recipe.value().getResult(null)))));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return RecyclerCategory.RECYCLER;
    }
}
