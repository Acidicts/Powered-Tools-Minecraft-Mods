package net.acidicts.powered_tools.particle;

import net.acidicts.powered_tools.Powered_tools;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {
    public static final SimpleParticleType ELECTRIC_SPARK = register("electric_spark", FabricParticleTypes.simple());

    public static void registerParticles() {
        Powered_tools.LOGGER.info("Registering Mod Particles for " + Powered_tools.MOD_ID);
    }

    private static SimpleParticleType register(String name, SimpleParticleType particleType) {
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Powered_tools.MOD_ID, name), particleType);
    }
}
