package net.acidicts.poweredtools;

import net.acidicts.poweredtools.block.entity.ModBlockEntities;
import net.acidicts.poweredtools.block.entity.renderer.charger.ChargerBlockRenderer;
import net.acidicts.poweredtools.particle.ModParticles;
import net.acidicts.poweredtools.screen.ModScreenHandlers;
import net.acidicts.poweredtools.screen.custom.alloy_smelter.AlloySmelterScreen;
import net.acidicts.poweredtools.screen.custom.charger.ChargerScreen;
import net.acidicts.poweredtools.screen.custom.coal_generator.CoalGeneratorScreen;
import net.acidicts.poweredtools.screen.custom.power_pickaxe.PoweredPickaxeScreen;
import net.acidicts.poweredtools.screen.custom.recycler.RecyclerScreen;
import net.acidicts.poweredtools.screen.custom.shieldcore.ShieldCoreScreen;
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
        HandledScreens.register(ModScreenHandlers.ALLOY_SMELTER_SCREEN_HANDLER, AlloySmelterScreen::new);
        HandledScreens.register(ModScreenHandlers.COAL_GENERATOR_SCREEN_HANDLER, CoalGeneratorScreen::new);
        HandledScreens.register(ModScreenHandlers.CHARGER_SCREEN_HANDLER, ChargerScreen::new);

        HandledScreens.register(ModScreenHandlers.POWERED_PICKAXE_SCREEN_HANDLER, PoweredPickaxeScreen::new);


        HandledScreens.register(ModScreenHandlers.SHIELD_CORE_SCREEN_HANDLER, ShieldCoreScreen::new);

        ParticleFactoryRegistry.getInstance().register(ModParticles.ELECTRIC_SPARK, FlameParticle.Factory::new);
    }
}

