package net.acidicts.powered_tools;

import net.acidicts.powered_tools.block.ModBlocks;
import net.acidicts.powered_tools.block.entity.ModBlockEntities;
import net.acidicts.powered_tools.item.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Powered_tools implements ModInitializer {
    public static final String MOD_ID = "powered_tools";
    public static final String MOD_INFO = "Tools that run on energy rather than durability.";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initialising " + MOD_ID + " !");
        LOGGER.info(MOD_INFO);

        ModItems.registerItems();
        ModBlocks.registerBlocks();
        ModBlockEntities.registerBlockEntities();
    }
}
