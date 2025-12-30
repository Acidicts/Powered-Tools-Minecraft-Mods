package net.acidicts.poweredtools.particle;

import net.acidicts.poweredtools.PoweredTools;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {
    public static final SimpleParticleType ELECTRIC_SPARK = register("electric_spark", FabricParticleTypes.simple());

    public static void registerParticles() {
        PoweredTools.LOGGER.info("Registering Mod Particles for " + PoweredTools.MOD_ID);
    }

    private static SimpleParticleType register(String name, SimpleParticleType particleType) {
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(PoweredTools.MOD_ID, name), particleType);
    }
}
