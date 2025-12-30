package net.acidicts.powered_tools;

import net.acidicts.powered_tools.block.entity.ModBlockEntities;
import net.acidicts.powered_tools.block.entity.renderer.charger.ChargerBlockRenderer;
import net.acidicts.powered_tools.particle.ModParticles;
import net.acidicts.powered_tools.screen.ModScreenHandlers;
import net.acidicts.powered_tools.screen.custom.RecyclerScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class Powered_toolsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(ModBlockEntities.CHARGER_BLOCK_ENTITY, ChargerBlockRenderer::new);

        HandledScreens.register(ModScreenHandlers.RECYCLER_SCREEN_HANDLER, RecyclerScreen::new);

        ParticleFactoryRegistry.getInstance().register(ModParticles.ELECTRIC_SPARK, FlameParticle.Factory::new);
    }
}

