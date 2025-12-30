package net.acidicts.poweredtools.compat;


import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.acidicts.poweredtools.block.ModBlocks;
import net.acidicts.poweredtools.recipe.ModRecipes;
import net.acidicts.poweredtools.recipe.RecyclerRecipe;
import net.acidicts.poweredtools.screen.custom.RecyclerScreen;

public class PoweredToolsModREIClient implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new RecyclerCategory());

        registry.addWorkstations(RecyclerCategory.RECYCLER, EntryStacks.of(ModBlocks.RECYCLER));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(RecyclerRecipe.class, ModRecipes.RECYCLER_TYPE,
                RecyclerDisplay::new);
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerClickArea(screen ->
                new Rectangle(((screen.width - 176) / 2) + 78,
                        ((screen.height - 166) / 2) + 30, 20, 25),
                RecyclerScreen.class, RecyclerCategory.RECYCLER);
    }
}
