package net.acidicts.powered_tools.item;


import net.acidicts.powered_tools.Powered_tools;
import net.acidicts.powered_tools.item.custom.BatteryItem;
import net.acidicts.powered_tools.item.custom.ModBatteryMaterials;
import net.acidicts.powered_tools.item.custom.Powered_Pickaxe;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item POWERED_PICKAXE = registerItem("powered_pickaxe",
            new Powered_Pickaxe(ModToolMaterials.PoweredTool, new Item.Settings()));
    public static final Item BATTERY_TIER_0 = registerItem("battery_tier_0",
            new BatteryItem(ModBatteryMaterials.Stone, new Item.Settings()));
    public static final Item BATTERY_TIER_1 = registerItem("battery_tier_1",
            new BatteryItem(ModBatteryMaterials.Iron, new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        Item registeredItem = Registry.register(Registries.ITEM, Identifier.of(Powered_tools.MOD_ID, name), item);

        String translationKey = "item." + Powered_tools.MOD_ID + "." + name;
        String translatedName = Text.translatable(translationKey).getString();

        if (translatedName.equals(translationKey)) {
            translatedName = formatRegistryName(name);
        }

        Powered_tools.LOGGER.info("Registering {} !", translatedName);
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

    public static void registerItems() {
        Powered_tools.LOGGER.info("Registering Items for " + Powered_tools.MOD_ID);
    }
}
