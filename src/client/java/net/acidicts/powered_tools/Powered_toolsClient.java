package net.acidicts.powered_tools;

import net.acidicts.powered_tools.block.entity.ModBlockEntities;
import net.acidicts.powered_tools.block.entity.renderer.ChargerBlockRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class Powered_toolsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register the block entity renderer
        BlockEntityRendererFactories.register(ModBlockEntities.CHARGER_BLOCK_ENTITY, ChargerBlockRenderer::new);
    }
}

