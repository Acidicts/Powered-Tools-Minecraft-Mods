package net.acidicts.powered_tools.block.entity;

import net.acidicts.powered_tools.Powered_tools;
import net.acidicts.powered_tools.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<ChargerBlockEntity> CHARGER_BLOCK_ENTITY =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(Powered_tools.MOD_ID, "charger_block_entity"),
                    BlockEntityType.Builder.create(ChargerBlockEntity::new, ModBlocks.CHARGER).build()
            );

    public static void registerBlockEntities() {
        Powered_tools.LOGGER.info("Registering Block Entities for {}", Powered_tools.MOD_ID);
    }
}

