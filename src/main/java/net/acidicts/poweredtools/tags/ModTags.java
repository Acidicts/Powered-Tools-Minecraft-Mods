package net.acidicts.poweredtools.tags;

import net.acidicts.poweredtools.PoweredTools;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> chargeable = createTagKey("chargeable");
        public static final TagKey<Item> ModifierItems = createTagKey("modifiers");
        public static final TagKey<Item> SwordModifierItems = createTagKey("sword_modifiers");

        private static TagKey<Item> createTagKey(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(PoweredTools.MOD_ID, name));
        }
    }
    public static class Blocks {


        private static TagKey<Block> createTagKey(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(PoweredTools.MOD_ID, name));
        }
    }
}
