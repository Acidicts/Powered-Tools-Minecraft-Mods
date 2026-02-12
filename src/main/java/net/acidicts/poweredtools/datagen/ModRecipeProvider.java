package net.acidicts.poweredtools.datagen;


import net.acidicts.poweredtools.block.ModBlocks;
import net.acidicts.poweredtools.item.ModItems;
import net.acidicts.poweredtools.recipe.ModRecipeCategory;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ItemConvertible;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.book.RecipeCategory;
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

        offerRecycling(exporter, List.of(ModItems.UNCANNED_BATTERY_TIER_0), ModRecipeCategory.RECYCLING, ModItems.BATTERY_TIER_0, 0.0f, 48, "battery");
        offerRecycling(exporter, List.of(ModItems.UNCANNED_BATTERY_TIER_1), ModRecipeCategory.RECYCLING, ModItems.BATTERY_TIER_1, 0.0f, 48, "battery");
        offerRecycling(exporter, List.of(ModItems.UNCANNED_BATTERY_TIER_2), ModRecipeCategory.RECYCLING, ModItems.BATTERY_TIER_2, 0.0f, 48, "battery");
        offerRecycling(exporter, List.of(ModItems.UNCANNED_BATTERY_TIER_3), ModRecipeCategory.RECYCLING, ModItems.BATTERY_TIER_3, 0.0f, 48, "battery");
        offerRecycling(exporter, List.of(ModItems.UNCANNED_BATTERY_TIER_4), ModRecipeCategory.RECYCLING, ModItems.BATTERY_TIER_4, 0.0f, 48, "battery");
        offerRecycling(exporter, List.of(ModItems.UNCANNED_BATTERY_TIER_5), ModRecipeCategory.RECYCLING, ModItems.BATTERY_TIER_5, 0.0f, 48, "battery");
    }

    private void generate_alloy_smelting(RecipeExporter exporter) {
        offerAlloying(exporter, ModItems.LITHIUM_DUST, Items.AIR, Items.AIR, ModRecipeCategory.ALLOYING, ModItems.IMPURE_LITHIUM_INGOT, 0.1f, 48, "lithium");
        offerAlloying(exporter, ModItems.IMPURE_LITHIUM_INGOT, Items.AIR, Items.AIR, ModRecipeCategory.ALLOYING, ModItems.LITHIUM_INGOT, 0.1f, 48, "lithium");

        offerAlloying(exporter, Items.IRON_INGOT, List.<ItemConvertible>of(Items.CHARCOAL, Items.COAL), Items.AIR, ModRecipeCategory.ALLOYING, ModItems.STEEL_DUST, 0.1f, 48, "coal_alloying");
        offerAlloying(exporter, ModItems.STEEL_DUST, Items.AIR, Items.AIR, ModRecipeCategory.ALLOYING, ModItems.STEEL_INGOT, 0.1f, 48, "alloying");

        offerAlloying(exporter, Items.GOLD_INGOT, Items.REDSTONE, Items.DIAMOND, ModRecipeCategory.ALLOYING, ModItems.POWERED_INGOT, 0.1f, 48, "alloying");
        offerAlloying(exporter, Items.GOLD_INGOT, Items.AIR, Items.DIAMOND, ModRecipeCategory.ALLOYING, ModItems.DIAMOND_GOLD_INGOT, 0.1f, 48, "alloying");
    }

    private void generate_3x3_crafting(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.TOOL_ROD)
                .pattern("  R")
                .pattern(" O ")
                .pattern("R  ")
                .input('R', Items.REDSTONE)
                .input('O', Items.OBSIDIAN)
                .criterion(hasItem(Blocks.OBSIDIAN), conditionsFromItem(Blocks.OBSIDIAN))
                .criterion(hasItem(Items.REDSTONE), conditionsFromItem(Items.REDSTONE))
                .offerTo(exporter);
    }

    private void generate_tool_recipes(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.POWERED_PICKAXE_1)
                .pattern("III")
                .pattern(" R ")
                .pattern(" R ")
                .input('R', ModItems.TOOL_ROD)
                .input('I', ModItems.POWERED_INGOT)
                .criterion(hasItem(Blocks.OBSIDIAN), conditionsFromItem(Blocks.OBSIDIAN))
                .criterion(hasItem(Items.REDSTONE), conditionsFromItem(Items.REDSTONE))
                .offerTo(exporter);
    }

    private void generate_uncanned_batteries(RecipeExporter exporter) {
        List<Item> tiers_items = List.of(
            Items.STONE,
            Items.IRON_INGOT,
            Items.GOLD_INGOT,
            Items.DIAMOND,
            Items.NETHERITE_INGOT,
            ModItems.DIAMOND_GOLD_INGOT
        );
        List<Item> batteryItems = List.of(
            ModItems.UNCANNED_BATTERY_TIER_0,
            ModItems.UNCANNED_BATTERY_TIER_1,
            ModItems.UNCANNED_BATTERY_TIER_2,
            ModItems.UNCANNED_BATTERY_TIER_3,
            ModItems.UNCANNED_BATTERY_TIER_4,
            ModItems.UNCANNED_BATTERY_TIER_5
        );
        List<String> recipePath = List.of(
            "uncanned_battery_tier_0",
            "uncanned_battery_tier_1",
            "uncanned_battery_tier_2",
            "uncanned_battery_tier_3",
            "uncanned_battery_tier_4",
            "uncanned_battery_tier_5"
        );

        for (Item item : tiers_items) {
            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, batteryItems.get(tiers_items.indexOf(item)))
                    .pattern(" C ")
                    .pattern("CBC")
                    .pattern(" C ")
                    .input('C', ModItems.STEEL_INGOT)
                    .input('B', item)
                    .criterion(hasItem(ModItems.LITHIUM_INGOT), conditionsFromItem(ModItems.LITHIUM_INGOT))
                    .offerTo(exporter, recipePath.get(tiers_items.indexOf(item)));
        }
    }

    private void generate_blocks_recipes(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.CHARGER)
                .pattern("SSS")
                .pattern("CRC")
                .pattern("SSS")
                .input('S', ModItems.STEEL_INGOT)
                .input('R', Items.DIAMOND)
                .input('C', Items.COPPER_INGOT)
                .criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
                .criterion(hasItem(ModItems.STEEL_DUST), conditionsFromItem(ModItems.STEEL_DUST))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.COAL_GENERATOR)
                .pattern("SSS")
                .pattern("CRC")
                .pattern("SSS")
                .input('S', ModItems.STEEL_INGOT)
                .input('R', Items.COAL)
                .input('C', Items.COPPER_INGOT)
                .criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
                .criterion(hasItem(ModItems.STEEL_DUST), conditionsFromItem(ModItems.STEEL_DUST))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.RECYCLER)
                .pattern("SSS")
                .pattern("RCR")
                .pattern("SSS")
                .input('S', ModItems.STEEL_INGOT)
                .input('R', Items.DIAMOND)
                .input('C', Items.COPPER_INGOT)
                .criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
                .criterion(hasItem(ModItems.STEEL_DUST), conditionsFromItem(ModItems.STEEL_DUST))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.ALLOY_SMELTER)
                .pattern("SSS")
                .pattern("CCC")
                .pattern("SSS")
                .input('S', Items.IRON_INGOT)
                .input('C', Items.COPPER_INGOT)
                .criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
                .criterion(hasItem(ModItems.STEEL_DUST), conditionsFromItem(ModItems.STEEL_DUST))
                .offerTo(exporter);
    }

    private void generate_shield_cores_recipes(RecipeExporter exporter) {
        List<Item> shieldCoreItems = List.of(
            Items.WATER_BUCKET,
            Items.LAVA_BUCKET,
            Items.NETHERITE_SWORD
        );
        List<Item> shieldCores = List.of(
            ModItems.WATER_SHIELD_CORE,
            ModItems.FIRE_SHIELD_CORE,
            ModItems.DAMAGE_SHIELD_CORE
        );
        for (Item item : shieldCoreItems) {
            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, shieldCores.get(shieldCoreItems.indexOf(item)))
                    .pattern("SSS")
                    .pattern("SRS")
                    .pattern("SSS")
                    .input('R', item)
                    .input('S', ModItems.STEEL_INGOT)
                    .criterion(hasItem(ModItems.STEEL_INGOT), conditionsFromItem(ModItems.STEEL_INGOT))
                    .offerTo(exporter);
        }
    }

    @Override
    public void generate(RecipeExporter exporter) {
        generate_battery_recycling(exporter);
        generate_alloy_smelting(exporter);
        generate_3x3_crafting(exporter);
        generate_tool_recipes(exporter);
        generate_uncanned_batteries(exporter);
        generate_blocks_recipes(exporter);
        generate_shield_cores_recipes(exporter);
    }
}
