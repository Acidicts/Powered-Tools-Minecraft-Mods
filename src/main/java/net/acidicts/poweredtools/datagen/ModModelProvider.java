package net.acidicts.poweredtools.datagen;

import me.shedaniel.errornotifier.launch.early.Texture;
import net.acidicts.poweredtools.block.ModBlocks;
import net.acidicts.poweredtools.block.custom.Recycler;
import net.acidicts.poweredtools.item.ModItems;
import net.acidicts.poweredtools.item.custom.BatteryItem;
import net.acidicts.poweredtools.item.custom.BrokenBatteryItem;
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

        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.COAL_GENERATOR);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ALLOY_SMELTER);
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
