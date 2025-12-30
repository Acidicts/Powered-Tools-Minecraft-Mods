package net.acidicts.powered_tools.datagen;

import net.acidicts.powered_tools.block.ModBlocks;
import net.acidicts.powered_tools.item.ModItems;
import net.acidicts.powered_tools.item.custom.BatteryItem;
import net.acidicts.powered_tools.item.custom.BrokenBatteryItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.List;


public class ModModelProvider extends FabricModelProvider {
    public final List<BatteryItem> batteryItems = List.of(
            (BatteryItem) ModItems.BATTERY_TIER_0,
            (BatteryItem) ModItems.BATTERY_TIER_1,
            (BatteryItem) ModItems.BATTERY_TIER_2,
            (BatteryItem) ModItems.BATTERY_TIER_3,
            (BatteryItem) ModItems.BATTERY_TIER_4,
            (BatteryItem) ModItems.BATTERY_TIER_5
    );
    public final List<BrokenBatteryItem> brokenBatteryItems = List.of(
            (BrokenBatteryItem) ModItems.BROKEN_BATTERY_TIER_0,
            (BrokenBatteryItem) ModItems.BROKEN_BATTERY_TIER_1,
            (BrokenBatteryItem) ModItems.BROKEN_BATTERY_TIER_2,
            (BrokenBatteryItem) ModItems.BROKEN_BATTERY_TIER_3,
            (BrokenBatteryItem) ModItems.BROKEN_BATTERY_TIER_4,
            (BrokenBatteryItem) ModItems.BROKEN_BATTERY_TIER_5
    );


    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        Identifier modelId = ModelIds.getBlockModelId(ModBlocks.RECYCLER);
        Identifier modelIdLit = ModelIds.getBlockSubModelId(ModBlocks.RECYCLER, "_lit");

        blockStateModelGenerator.blockStateCollector.accept(
            VariantsBlockStateSupplier.create(ModBlocks.RECYCLER)
                .coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.LIT)
                    .register(Direction.NORTH, false, BlockStateVariant.create().put(VariantSettings.MODEL, modelId).put(VariantSettings.Y, VariantSettings.Rotation.R0))
                    .register(Direction.EAST, false, BlockStateVariant.create().put(VariantSettings.MODEL, modelId).put(VariantSettings.Y, VariantSettings.Rotation.R90))
                    .register(Direction.SOUTH, false, BlockStateVariant.create().put(VariantSettings.MODEL, modelId).put(VariantSettings.Y, VariantSettings.Rotation.R180))
                    .register(Direction.WEST, false, BlockStateVariant.create().put(VariantSettings.MODEL, modelId).put(VariantSettings.Y, VariantSettings.Rotation.R270))
                    .register(Direction.NORTH, true, BlockStateVariant.create().put(VariantSettings.MODEL, modelIdLit).put(VariantSettings.Y, VariantSettings.Rotation.R0))
                    .register(Direction.EAST, true, BlockStateVariant.create().put(VariantSettings.MODEL, modelIdLit).put(VariantSettings.Y, VariantSettings.Rotation.R90))
                    .register(Direction.SOUTH, true, BlockStateVariant.create().put(VariantSettings.MODEL, modelIdLit).put(VariantSettings.Y, VariantSettings.Rotation.R180))
                    .register(Direction.WEST, true, BlockStateVariant.create().put(VariantSettings.MODEL, modelIdLit).put(VariantSettings.Y, VariantSettings.Rotation.R270))
                )
        );
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.POWERED_PICKAXE, Models.HANDHELD);

        for (BatteryItem battery : batteryItems) {
            itemModelGenerator.register(battery, Models.GENERATED);
        }
        for (BrokenBatteryItem brokenBattery : brokenBatteryItems) {
            itemModelGenerator.register(brokenBattery, Models.GENERATED);
        }
    }
}
