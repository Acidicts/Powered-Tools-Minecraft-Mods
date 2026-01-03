package net.acidicts.poweredtools.recipe;

import net.acidicts.poweredtools.PoweredTools;
import net.acidicts.poweredtools.recipe.alloy_smelter.AlloySmelterRecipe;
import net.acidicts.poweredtools.recipe.recycler.RecyclerRecipe;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;

import static net.minecraft.data.server.recipe.RecipeProvider.*;


public class ModRecipes {
    public static final RecipeSerializer<RecyclerRecipe> RECYCLER_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER, Identifier.of(PoweredTools.MOD_ID, "recycling"), new RecyclerRecipe.Serializer()
    );
    public static final RecipeSerializer<AlloySmelterRecipe> ALLOY_SMELTER_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER, Identifier.of(PoweredTools.MOD_ID, "alloying"), new AlloySmelterRecipe.Serializer()
    );

    public static RecipeType<RecyclerRecipe> RECYCLER_TYPE = Registry.register(
            Registries.RECIPE_TYPE, Identifier.of(PoweredTools.MOD_ID, "recycling"), new RecipeType<>() {
                @Override
                public String toString() {
                    return "recycling";
                }
            }
    );
    public static RecipeType<AlloySmelterRecipe> ALLOYING_TYPE = Registry.register(
            Registries.RECIPE_TYPE, Identifier.of(PoweredTools.MOD_ID, "alloying"), new RecipeType<>() {
                @Override
                public String toString() {
                    return "alloying";
                }
            }
    );

    public static void registerRecipes() {
        PoweredTools.LOGGER.info("Registering Mod Recipes for " + PoweredTools.MOD_ID);

        Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(PoweredTools.MOD_ID, "recycling"), RECYCLER_SERIALIZER);
        Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(PoweredTools.MOD_ID, "alloying"), ALLOY_SMELTER_SERIALIZER);
    }

    public static void offerRecycling(RecipeExporter exporter, List<ItemConvertible> inputs, ModRecipeCategory category, ItemConvertible output, float experience, int cookingTime, String group) {
        for(ItemConvertible input : inputs) {
            RecyclerRecipe recipe = new RecyclerRecipe(
                    Ingredient.ofItems(input),
                    new net.minecraft.item.ItemStack(output.asItem()),
                    cookingTime
            );
            exporter.accept(
                    Identifier.of(PoweredTools.MOD_ID, getItemPath(output) + "_from_recycling_" + getItemPath(input)),
                    recipe,
                    null
            );
        }

    }

    public static void offerAlloying(RecipeExporter exporter, ItemConvertible input1, ItemConvertible input2, ItemConvertible input3, ModRecipeCategory category, ItemConvertible output, float experience, int cookingTime, String group) {
        AlloySmelterRecipe recipe = new AlloySmelterRecipe(
                Ingredient.ofItems(input1),
                Ingredient.ofItems(input2),
                Ingredient.ofItems(input3),
                new net.minecraft.item.ItemStack(output.asItem()),
                cookingTime
        );
        exporter.accept(
                Identifier.of(PoweredTools.MOD_ID, getItemPath(output) + "_from_alloying"),
                recipe,
                null
        );

    }
}
