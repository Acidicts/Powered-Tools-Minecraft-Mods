package net.acidicts.powered_tools.tags;

import net.minecraft.block.Block;
import net.acidicts.powered_tools.Powered_tools;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> chargeable = createTagKey("chargeable");

        private static TagKey<Item> createTagKey(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(Powered_tools.MOD_ID, name));
        }
    }
    public static class Blocks {


        private static TagKey<Block> createTagKey(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(Powered_tools.MOD_ID, name));
        }
    }
}
