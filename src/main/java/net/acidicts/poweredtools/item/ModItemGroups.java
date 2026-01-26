package net.acidicts.poweredtools.item;

import net.acidicts.poweredtools.PoweredTools;
import net.acidicts.poweredtools.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    @SuppressWarnings("unused")
    public static final ItemGroup POWERED_TOOLS_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(PoweredTools.MOD_ID, "powered_tools_group"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.poweredtools.powered_tools_group"))
                    .icon(() -> new ItemStack(ModItems.POWERED_PICKAXE_1))
                    .entries(((displayContext, entries) -> {
                        entries.add(ModItems.POWERED_PICKAXE_1);

                        entries.add(ModBlocks.CHARGER);
                        entries.add(ModBlocks.RECYCLER);
                        entries.add(ModBlocks.COAL_GENERATOR);
                        entries.add(ModBlocks.ALLOY_SMELTER);
                        entries.add(ModBlocks.LITHIUM_ORE);

                        entries.add(ModItems.BATTERY_TIER_0);
                        entries.add(ModItems.BATTERY_TIER_1);
                        entries.add(ModItems.BATTERY_TIER_2);
                        entries.add(ModItems.BATTERY_TIER_3);
                        entries.add(ModItems.BATTERY_TIER_4);
                        entries.add(ModItems.BATTERY_TIER_5);

                        entries.add(ModItems.BROKEN_BATTERY_TIER_0);
                        entries.add(ModItems.BROKEN_BATTERY_TIER_1);
                        entries.add(ModItems.BROKEN_BATTERY_TIER_2);
                        entries.add(ModItems.BROKEN_BATTERY_TIER_3);
                        entries.add(ModItems.BROKEN_BATTERY_TIER_4);
                        entries.add(ModItems.BROKEN_BATTERY_TIER_5);

                        entries.add(ModItems.UNCANNED_BATTERY_TIER_0);
                        entries.add(ModItems.UNCANNED_BATTERY_TIER_1);
                        entries.add(ModItems.UNCANNED_BATTERY_TIER_2);
                        entries.add(ModItems.UNCANNED_BATTERY_TIER_3);
                        entries.add(ModItems.UNCANNED_BATTERY_TIER_4);
                        entries.add(ModItems.UNCANNED_BATTERY_TIER_5);

                        entries.add(ModItems.IMPURE_LITHIUM_INGOT);
                        entries.add(ModItems.LITHIUM_DUST);
                        entries.add(ModItems.LITHIUM_INGOT);
                        entries.add(ModItems.STEEL_DUST);
                        entries.add(ModItems.STEEL_INGOT);
                        entries.add(ModItems.DIAMOND_GOLD_INGOT);
                        entries.add(ModItems.POWERED_INGOT);
                        entries.add(ModItems.TOOL_ROD);

                        entries.add(ModItems.EFFICIENCY_MODIFIER);
                        entries.add(ModItems.FORTUNE_MODIFIER);
                        entries.add(ModItems.SILK_TOUCH_MODIFIER);

                        entries.add(ModItems.FIRE_SHIELD_CORE);
                        entries.add(ModItems.WATER_SHIELD_CORE);
                        entries.add(ModItems.DAMAGE_SHIELD_CORE);
                    }))
                    .build());


    public static void registerItemGroups() {
        PoweredTools.LOGGER.info("Registering Item Groups for " + PoweredTools.MOD_ID);
    }
}
