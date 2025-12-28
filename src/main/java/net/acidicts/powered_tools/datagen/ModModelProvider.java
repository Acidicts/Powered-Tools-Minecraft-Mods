package net.acidicts.powered_tools.datagen;

import net.acidicts.powered_tools.block.ModBlocks;
import net.acidicts.powered_tools.item.ModItems;
import net.acidicts.powered_tools.item.custom.BatteryItem;
import net.acidicts.powered_tools.item.custom.BrokenBatteryItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;

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
            (BrokenBatteryItem) ModItems.BROKEN_BATTER_TIER_1,
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
