package net.acidicts.poweredtools.item;


import net.acidicts.poweredtools.PoweredTools;
import net.acidicts.poweredtools.item.custom.BatteryItem;
import net.acidicts.poweredtools.item.custom.BrokenBatteryItem;
import net.acidicts.poweredtools.item.custom.ModBatteryMaterials;
import net.acidicts.poweredtools.item.custom.Powered_Pickaxe;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item POWERED_PICKAXE = registerItem("powered_pickaxe",
            new Powered_Pickaxe(new Item.Settings()));

    public static final Item BATTERY_TIER_0 = registerItem("battery_0",
            new BatteryItem(ModBatteryMaterials.Stone, new Item.Settings()));
    public static final Item BATTERY_TIER_1 = registerItem("battery_1",
            new BatteryItem(ModBatteryMaterials.Iron, new Item.Settings()));
    public static final Item BATTERY_TIER_2 = registerItem("battery_2",
            new BatteryItem(ModBatteryMaterials.Gold, new Item.Settings()));
    public static final Item BATTERY_TIER_3 = registerItem("battery_3",
            new BatteryItem(ModBatteryMaterials.Diamond, new Item.Settings()));
    public static final Item BATTERY_TIER_4 = registerItem("battery_4",
            new BatteryItem(ModBatteryMaterials.Netherite, new Item.Settings()));
    public static final Item BATTERY_TIER_5 = registerItem("battery_5",
            new BatteryItem(ModBatteryMaterials.Diamond_Gold, new Item.Settings()));


    public static final Item BROKEN_BATTERY_TIER_0 = registerItem("broken_battery_0",
            new BrokenBatteryItem(ModBatteryMaterials.Stone, new Item.Settings()));
    public static final Item BROKEN_BATTERY_TIER_1 = registerItem("broken_battery_1",
            new BrokenBatteryItem(ModBatteryMaterials.Iron, new Item.Settings()));
    public static final Item BROKEN_BATTERY_TIER_2 = registerItem("broken_battery_2",
            new BrokenBatteryItem(ModBatteryMaterials.Gold, new Item.Settings()));
    public static final Item BROKEN_BATTERY_TIER_3 = registerItem("broken_battery_3",
            new BrokenBatteryItem(ModBatteryMaterials.Diamond, new Item.Settings()));
    public static final Item BROKEN_BATTERY_TIER_4 = registerItem("broken_battery_4",
            new BrokenBatteryItem(ModBatteryMaterials.Netherite, new Item.Settings()));
    public static final Item BROKEN_BATTERY_TIER_5 = registerItem("broken_battery_5",
            new BrokenBatteryItem(ModBatteryMaterials.Diamond_Gold, new Item.Settings()));


    // Ingredients

    public static final Item LITHIUM_DUST = registerItem("lithium_dust",
            new Item(new Item.Settings()));

    public static final Item IMPURE_LITHIUM_INGOT = registerItem("impure_lithium",
            new Item(new Item.Settings()));

    public static final Item LITHIUM_INGOT = registerItem("lithium_ingot",
            new Item(new Item.Settings()));

    public static final Item STEEL_DUST = registerItem("steel_dust",
            new Item(new Item.Settings()));

    public static final Item STEEL_INGOT = registerItem("steel_ingot",
            new Item(new Item.Settings()));


    // High-Tier Tools Ingredients

    public static final Item DIAMOND_GOLD_INGOT = registerItem("diamond_gold_ingot",
            new Item(new Item.Settings()));

    // Modifiers

    public static final Item EFFICIENCY_MODIFIER = registerItem("efficiency_modifier",
            new Item(new Item.Settings().maxCount(5)));

    public static final Item FORTUNE_MODIFIER = registerItem("fortune_modifier",
            new Item(new Item.Settings().maxCount(3)));

    public static final Item SILK_TOUCH_MODIFIER = registerItem("silk_touch_modifier",
            new Item(new Item.Settings().maxCount(1)));


    private static Item registerItem(String name, Item item) {
        Item registeredItem = Registry.register(Registries.ITEM, Identifier.of(PoweredTools.MOD_ID, name), item);

        String translationKey = "item." + PoweredTools.MOD_ID + "." + name;
        String translatedName = Text.translatable(translationKey).getString();

        if (translatedName.equals(translationKey)) {
            translatedName = formatRegistryName(name);
        }

        PoweredTools.LOGGER.info("Registering {} !", translatedName);
        return registeredItem;
    }

    public static String formatRegistryName(String name) {
        String[] words = name.split("_");
        StringBuilder formatted = new StringBuilder();
        for (String word : words) {
            if (!formatted.isEmpty()) {
                formatted.append(" ");
            }
            formatted.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1));
        }
        return formatted.toString();
    }

    public static Item getBrokenBatteryByTier(String tier) {
        return switch (tier) {
            case "Iron" -> BROKEN_BATTERY_TIER_1;
            case "Gold" -> BROKEN_BATTERY_TIER_2;
            case "Diamond" -> BROKEN_BATTERY_TIER_3;
            case "Netherite" -> BROKEN_BATTERY_TIER_4;
            case "Diamond_Gold" -> BROKEN_BATTERY_TIER_5;
            default -> BROKEN_BATTERY_TIER_0;
        };
    }

    public static void registerItems() {
        PoweredTools.LOGGER.info("Registering Items for " + PoweredTools.MOD_ID);
    }
}
