package net.acidicts.poweredtools.datagen;

import net.acidicts.poweredtools.item.ModItems;
import net.acidicts.poweredtools.tags.ModTags;
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
                .add(ModItems.BATTERY_TIER_0)
                .add(ModItems.BATTERY_TIER_1)
                .add(ModItems.BATTERY_TIER_2)
                .add(ModItems.BATTERY_TIER_3)
                .add(ModItems.BATTERY_TIER_4)
                .add(ModItems.BATTERY_TIER_5);

        getOrCreateTagBuilder(ModTags.Items.ModifierItems)
                .add(ModItems.SILK_TOUCH_MODIFIER)
                .add(ModItems.EFFICIENCY_MODIFIER)
                .add(ModItems.FORTUNE_MODIFIER);

        getOrCreateTagBuilder(ModTags.Items.SwordModifierItems)
                .add(ModItems.FIRE_ASPECT_MODIFIER)
                .add(ModItems.BANE_OF_ARTHROPODS_MODIFIER)
                .add(ModItems.SWEEPING_EDGE_MODIFIER);
    }
}
