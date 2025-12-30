package net.acidicts.poweredtools;

import net.acidicts.poweredtools.block.entity.ModBlockEntities;
import net.acidicts.poweredtools.block.entity.renderer.charger.ChargerBlockRenderer;
import net.acidicts.poweredtools.particle.ModParticles;
import net.acidicts.poweredtools.screen.ModScreenHandlers;
import net.acidicts.poweredtools.screen.custom.RecyclerScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class PoweredToolsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(ModBlockEntities.CHARGER_BLOCK_ENTITY, ChargerBlockRenderer::new);

        HandledScreens.register(ModScreenHandlers.RECYCLER_SCREEN_HANDLER, RecyclerScreen::new);

        ParticleFactoryRegistry.getInstance().register(ModParticles.ELECTRIC_SPARK, FlameParticle.Factory::new);
    }
}

