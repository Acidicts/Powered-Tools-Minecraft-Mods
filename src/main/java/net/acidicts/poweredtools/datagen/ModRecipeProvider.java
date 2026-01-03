package net.acidicts.poweredtools.datagen;


import net.acidicts.poweredtools.item.ModItems;
import net.acidicts.poweredtools.recipe.ModRecipeCategory;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.acidicts.poweredtools.recipe.ModRecipes.offerAlloying;
import static net.acidicts.poweredtools.recipe.ModRecipes.offerRecycling;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    private void generate_battery_recycling(RecipeExporter exporter) {
        offerRecycling(exporter, List.of(ModItems.BROKEN_BATTERY_TIER_0), ModRecipeCategory.RECYCLING, ModItems.BATTERY_TIER_0, 0.1f, 72, "battery");
        offerRecycling(exporter, List.of(ModItems.BROKEN_BATTERY_TIER_1), ModRecipeCategory.RECYCLING, ModItems.BATTERY_TIER_1, 0.1f, 80, "battery");
        offerRecycling(exporter, List.of(ModItems.BROKEN_BATTERY_TIER_2), ModRecipeCategory.RECYCLING, ModItems.BATTERY_TIER_2, 0.1f, 88, "battery");
        offerRecycling(exporter, List.of(ModItems.BROKEN_BATTERY_TIER_3), ModRecipeCategory.RECYCLING, ModItems.BATTERY_TIER_3, 0.1f, 96, "battery");
        offerRecycling(exporter, List.of(ModItems.BROKEN_BATTERY_TIER_4), ModRecipeCategory.RECYCLING, ModItems.BATTERY_TIER_4, 0.1f, 104, "battery");
        offerRecycling(exporter, List.of(ModItems.BROKEN_BATTERY_TIER_5), ModRecipeCategory.RECYCLING, ModItems.BATTERY_TIER_5, 0.1f, 112, "battery");
    }

    private void generate_alloy_smelting(RecipeExporter exporter) {
//        offerAlloying(exporter, Items.IRON_INGOT, Items.GOLD_INGOT, Items.COPPER_INGOT, ModRecipeCategory.ALLOYING, Items.NETHERITE_SCRAP, 0.5f, 200, "alloying");
    }

    @Override
    public void generate(RecipeExporter exporter) {
        generate_battery_recycling(exporter);
        generate_alloy_smelting(exporter);
    }
}
