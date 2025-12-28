package net.acidicts.powered_tools.datagen;

import net.acidicts.powered_tools.item.ModItems;
import net.acidicts.powered_tools.item.custom.Powered_Pickaxe;
import net.acidicts.powered_tools.tags.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ModTags.Items.chargeable)
                .add(ModItems.POWERED_PICKAXE)
                .add(ModItems.BATTERY_TIER_0)
                .add(ModItems.BATTERY_TIER_1)
                .add(ModItems.BATTERY_TIER_2)
                .add(ModItems.BATTERY_TIER_3)
                .add(ModItems.BATTERY_TIER_4)
                .add(ModItems.BATTERY_TIER_5);
    }
}
