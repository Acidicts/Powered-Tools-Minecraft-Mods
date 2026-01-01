package net.acidicts.poweredtools.data;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;

import java.util.Map;

public class Burnables {
    public Map<Item, Integer> map2 = Maps.newLinkedHashMap();

    public Burnables() {

        addFuel(map2, Items.LAVA_BUCKET, 20000);
        addFuel(map2, Blocks.COAL_BLOCK, 16000);
        addFuel(map2, Items.BLAZE_ROD, 2400);
        addFuel(map2, Items.COAL, 1600);
        addFuel(map2, Items.CHARCOAL, 1600);
        addFuel(map2, ItemTags.LOGS, 300);
        addFuel(map2, ItemTags.BAMBOO_BLOCKS, 300);
        addFuel(map2, ItemTags.PLANKS, 300);
        addFuel(map2, Blocks.BAMBOO_MOSAIC, 300);
        addFuel(map2, ItemTags.WOODEN_STAIRS, 300);
        addFuel(map2, Blocks.BAMBOO_MOSAIC_STAIRS, 300);
        addFuel(map2, ItemTags.WOODEN_SLABS, 150);
        addFuel(map2, Blocks.BAMBOO_MOSAIC_SLAB, 150);
        addFuel(map2, ItemTags.WOODEN_TRAPDOORS, 300);
        addFuel(map2, ItemTags.WOODEN_PRESSURE_PLATES, 300);
        addFuel(map2, ItemTags.WOODEN_FENCES, 300);
        addFuel(map2, ItemTags.FENCE_GATES, 300);
        addFuel(map2, Blocks.NOTE_BLOCK, 300);
        addFuel(map2, Blocks.BOOKSHELF, 300);
        addFuel(map2, Blocks.CHISELED_BOOKSHELF, 300);
        addFuel(map2, Blocks.LECTERN, 300);
        addFuel(map2, Blocks.JUKEBOX, 300);
        addFuel(map2, Blocks.CHEST, 300);
        addFuel(map2, Blocks.TRAPPED_CHEST, 300);
        addFuel(map2, Blocks.CRAFTING_TABLE, 300);
        addFuel(map2, Blocks.DAYLIGHT_DETECTOR, 300);
        addFuel(map2, ItemTags.BANNERS, 300);
        addFuel(map2, Items.BOW, 300);
        addFuel(map2, Items.FISHING_ROD, 300);
        addFuel(map2, Blocks.LADDER, 300);
        addFuel(map2, ItemTags.SIGNS, 200);
        addFuel(map2, ItemTags.HANGING_SIGNS, 800);
        addFuel(map2, Items.WOODEN_SHOVEL, 200);
        addFuel(map2, Items.WOODEN_SWORD, 200);
        addFuel(map2, Items.WOODEN_HOE, 200);
        addFuel(map2, Items.WOODEN_AXE, 200);
        addFuel(map2, Items.WOODEN_PICKAXE, 200);
        addFuel(map2, ItemTags.WOODEN_DOORS, 200);
        addFuel(map2, ItemTags.BOATS, 1200);
        addFuel(map2, ItemTags.WOOL, 100);
        addFuel(map2, ItemTags.WOODEN_BUTTONS, 100);
        addFuel(map2, Items.STICK, 100);
        addFuel(map2, ItemTags.SAPLINGS, 100);
        addFuel(map2, Items.BOWL, 100);
        addFuel(map2, ItemTags.WOOL_CARPETS, 67);
        addFuel(map2, Blocks.DRIED_KELP_BLOCK, 4001);
        addFuel(map2, Items.CROSSBOW, 300);
        addFuel(map2, Blocks.BAMBOO, 50);
        addFuel(map2, Blocks.DEAD_BUSH, 100);
        addFuel(map2, Blocks.SCAFFOLDING, 50);
        addFuel(map2, Blocks.LOOM, 300);
        addFuel(map2, Blocks.BARREL, 300);
        addFuel(map2, Blocks.CARTOGRAPHY_TABLE, 300);
        addFuel(map2, Blocks.FLETCHING_TABLE, 300);
        addFuel(map2, Blocks.SMITHING_TABLE, 300);
        addFuel(map2, Blocks.COMPOSTER, 300);
        addFuel(map2, Blocks.AZALEA, 100);
        addFuel(map2, Blocks.FLOWERING_AZALEA, 100);
        addFuel(map2, Blocks.MANGROVE_ROOTS, 300);
    }

    private static boolean isNonFlammableWood(Item item) {
        return item.getDefaultStack().isIn(ItemTags.NON_FLAMMABLE_WOOD);
    }

    private static void addFuel(Map<Item, Integer> map, TagKey<Item> tag, int fuelTime) {
        for(RegistryEntry<Item> registryEntry : Registries.ITEM.iterateEntries(tag)) {
            if (!isNonFlammableWood(registryEntry.value())) {
                map.put(registryEntry.value(), fuelTime);
            }
        }
    }

    private static void addFuel(Map<Item, Integer> map, Item item, int fuelTime) {
        map.put(item, fuelTime);
    }

    private static void addFuel(Map<Item, Integer> map, Block block, int fuelTime) {
        Item item = block.asItem();
        if (item != Items.AIR) {
            map.put(item, fuelTime);
        }
    }

    public int getEnergyValue(Item item, int maxProgress) {
        return (int) map2.getOrDefault(item, 0)/maxProgress;
    }
}
