package net.acidicts.poweredtools;

import net.acidicts.poweredtools.block.ModBlocks;
import net.acidicts.poweredtools.block.entity.ModBlockEntities;
import net.acidicts.poweredtools.item.ModItemGroups;
import net.acidicts.poweredtools.item.ModItems;
import net.acidicts.poweredtools.particle.ModParticles;
import net.acidicts.poweredtools.recipe.ModRecipes;
import net.acidicts.poweredtools.world.gen.ModWorldGeneration;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PoweredTools implements ModInitializer {
    public static final String MOD_ID = "poweredtools";
    public static final String MOD_INFO = "Tools that run on energy rather than durability.";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initialising " + MOD_ID + " !");
        LOGGER.info(MOD_INFO);

        ModItemGroups.registerItemGroups();

        ModItems.registerItems();
        ModBlocks.registerBlocks();

        ModWorldGeneration.generateModWorldGeneration();

        ModBlockEntities.registerBlockEntities();
        ModParticles.registerParticles();

        ModRecipes.registerRecipes();
    }
}
