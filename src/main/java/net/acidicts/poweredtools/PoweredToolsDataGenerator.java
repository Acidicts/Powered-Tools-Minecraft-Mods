package net.acidicts.poweredtools;

import net.acidicts.poweredtools.datagen.*;
import net.acidicts.poweredtools.world.ModConfiguredFeatures;
import net.acidicts.poweredtools.world.ModPlacedFeatures;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

public class PoweredToolsDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        // Tags
        pack.addProvider(ModBlockTagProvider::new);
        pack.addProvider(ModItemTagProvider::new);

        // Drops
        pack.addProvider(ModLootTableProvider::new);
        pack.addProvider(ModEntityLootTableProvider::new);

        // Models
        pack.addProvider(ModModelProvider::new);

        // Recipes
        pack.addProvider(ModRecipeProvider::new);

        //Particles
        pack.addProvider(ModParticleProvider::new);

        // World Gen
        pack.addProvider(ModWorldGenerator::new);
	}

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap);
        registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, ModPlacedFeatures::bootstrap);
    }
}
