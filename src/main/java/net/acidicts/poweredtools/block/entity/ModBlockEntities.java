package net.acidicts.poweredtools.block.entity;

import net.acidicts.poweredtools.PoweredTools;
import net.acidicts.poweredtools.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<ChargerBlockEntity> CHARGER_BLOCK_ENTITY =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(PoweredTools.MOD_ID, "charger_block_entity"),
                    BlockEntityType.Builder.create(ChargerBlockEntity::new, ModBlocks.CHARGER).build(null)
            );

    public static final BlockEntityType<RecyclerBlockEntity> RECYCLER_BLOCK_ENTITY =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(PoweredTools.MOD_ID, "recycler_block_entity"),
                    BlockEntityType.Builder.create(RecyclerBlockEntity::new, ModBlocks.RECYCLER).build(null)
            );

    public static void registerBlockEntities() {
        PoweredTools.LOGGER.info("Registering Block Entities for {}", PoweredTools.MOD_ID);
    }
}

