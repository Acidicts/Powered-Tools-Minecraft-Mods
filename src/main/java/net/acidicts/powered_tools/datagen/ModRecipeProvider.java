package net.acidicts.powered_tools.datagen;


import net.acidicts.powered_tools.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.acidicts.powered_tools.recipe.ModRecipes.offerRecycling;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        offerRecycling(exporter, List.of(ModItems.BROKEN_BATTERY_TIER_0), RecipeCategory.MISC, ModItems.BATTERY_TIER_0, 0.1f, 72, "battery");
        offerRecycling(exporter, List.of(ModItems.BROKEN_BATTERY_TIER_1), RecipeCategory.MISC, ModItems.BATTERY_TIER_1, 0.1f, 80, "battery");
        offerRecycling(exporter, List.of(ModItems.BROKEN_BATTERY_TIER_2), RecipeCategory.MISC, ModItems.BATTERY_TIER_2, 0.1f, 88, "battery");
        offerRecycling(exporter, List.of(ModItems.BROKEN_BATTERY_TIER_3), RecipeCategory.MISC, ModItems.BATTERY_TIER_3, 0.1f, 96, "battery");
        offerRecycling(exporter, List.of(ModItems.BROKEN_BATTERY_TIER_4), RecipeCategory.MISC, ModItems.BATTERY_TIER_4, 0.1f, 104, "battery");
        offerRecycling(exporter, List.of(ModItems.BROKEN_BATTERY_TIER_5), RecipeCategory.MISC, ModItems.BATTERY_TIER_5, 0.1f, 112, "battery");
    }
}
