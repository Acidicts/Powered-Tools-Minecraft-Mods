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
                    .icon(() -> new ItemStack(ModItems.POWERED_PICKAXE))
                    .entries(((displayContext, entries) -> {
                        entries.add(ModItems.POWERED_PICKAXE);

                        entries.add(ModBlocks.CHARGER);
                        entries.add(ModBlocks.RECYCLER);
                        entries.add(ModBlocks.COAL_GENERATOR);

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
                    }))
                    .build());


    public static void registerItemGroups() {
        PoweredTools.LOGGER.info("Registering Item Groups for " + PoweredTools.MOD_ID);
    }
}
