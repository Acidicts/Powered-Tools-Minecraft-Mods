package net.acidicts.poweredtools.compat;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.acidicts.poweredtools.recipe.alloy_smelter.AlloySmelterRecipe;
import net.minecraft.recipe.RecipeEntry;

import java.util.List;

public class AlloySmelterDisplay extends BasicDisplay {


    public AlloySmelterDisplay(RecipeEntry<AlloySmelterRecipe> recipe) {
        super(EntryIngredients.ofIngredients(recipe.value().getIngredients()),
                List.of(EntryIngredient.of(EntryStacks.of(recipe.value().getResult(null)))));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return AlloySmelterCategory.ALLOY_SMELTER;
    }
}
