package net.acidicts.poweredtools.block;

import net.acidicts.poweredtools.PoweredTools;
import net.acidicts.poweredtools.block.custom.Charger;
import net.acidicts.poweredtools.block.custom.CoalGenerator;
import net.acidicts.poweredtools.block.custom.Recycler;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static net.acidicts.poweredtools.item.ModItems.formatRegistryName;


public class ModBlocks {

    public static final Block CHARGER = registerBlock("charger",
            new Charger(AbstractBlock.Settings.create().nonOpaque().requiresTool().strength(2f).resistance(12f)));

    public static final Block RECYCLER = registerBlock("recycler",
            new Recycler(AbstractBlock.Settings.create().nonOpaque().requiresTool().strength(2f).resistance(12f)));

    public static final Block COAL_GENERATOR = registerBlock("coal_generator",
            new CoalGenerator(AbstractBlock.Settings.create().nonOpaque().requiresTool().strength(2f).resistance(12f)));


    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        Block registeredBlock = Registry.register(Registries.BLOCK, Identifier.of(PoweredTools.MOD_ID, name), block);

        String translationKey = "block." + PoweredTools.MOD_ID + "." + name;
        String translatedName = net.minecraft.text.Text.translatable(translationKey).getString();

        if (translatedName.equals(translationKey)) {
            translatedName = formatRegistryName(name);
        }

        PoweredTools.LOGGER.info("Registering {} !", translatedName);
        return registeredBlock;
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(PoweredTools.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerBlocks() {
        PoweredTools.LOGGER.info("Registering Blocks for {}", PoweredTools.MOD_ID);
    }
}
