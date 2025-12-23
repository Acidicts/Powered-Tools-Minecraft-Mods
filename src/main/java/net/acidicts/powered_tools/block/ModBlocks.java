package net.acidicts.powered_tools.block;

import net.acidicts.powered_tools.Powered_tools;
import net.acidicts.powered_tools.block.custom.Charger;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static net.acidicts.powered_tools.item.ModItems.formatRegistryName;


public class ModBlocks {

    public static final Block CHARGER = registerBlock("charger",
            new Charger(AbstractBlock.Settings.create().nonOpaque().requiresTool().strength(4f).resistance(12f)));


    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        Block registeredBlock = Registry.register(Registries.BLOCK, Identifier.of(Powered_tools.MOD_ID, name), block);

        String translationKey = "block." + Powered_tools.MOD_ID + "." + name;
        String translatedName = net.minecraft.text.Text.translatable(translationKey).getString();

        if (translatedName.equals(translationKey)) {
            translatedName = formatRegistryName(name);
        }

        Powered_tools.LOGGER.info("Registering {} !", translatedName);
        return registeredBlock;
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(Powered_tools.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerBlocks() {
        Powered_tools.LOGGER.info("Registering Blocks for {}", Powered_tools.MOD_ID);
    }
}
