package com.blackgear.superblaster.client.registries;

import com.blackgear.superblaster.core.SuperBlaster;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, SuperBlaster.MODID);

    public static final Supplier<SimpleParticleType> SUPER_BLASTER_TRAIL = register("super_blaster_trail", false);
    public static final Supplier<SimpleParticleType> DEFAULT_EXPLOSION_EMITTER = register("default_explosion_emitter", true);
    public static final Supplier<SimpleParticleType> SUCCESSFUL_EXPLOSION_EMITTER = register("successful_explosion_emitter", true);
    public static final Supplier<SimpleParticleType> BOOM = register("boom", true);
    public static final Supplier<SimpleParticleType> SPARK = register("spark", true);

    private static Supplier<SimpleParticleType> register(String name, boolean overrideLimiter) {
        return PARTICLES.register(name, () -> new SimpleParticleType(overrideLimiter));
    }
}