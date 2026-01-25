package net.acidicts.poweredtools.datagen;

import net.acidicts.poweredtools.block.ModBlocks;
import net.acidicts.poweredtools.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.CHARGER);
        addDrop(ModBlocks.RECYCLER);
        addDrop(ModBlocks.OPEN_CHARGER);
        addDrop(ModBlocks.ALLOY_SMELTER);
        addDrop(ModBlocks.COAL_GENERATOR);
        addDrop(ModBlocks.LITHIUM_ORE, ModItems.LITHIUM_DUST);
    }
}
