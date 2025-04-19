package com.blackgear.superblaster.client.level.particle;

import com.blackgear.superblaster.client.registries.ModParticleTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlastExplosionParticle extends NoRenderParticle {
    private static final float SPREAD = 1.5F;
    private final boolean sparks;

    protected BlastExplosionParticle(ClientLevel level, double x, double y, double z, boolean sparks) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.lifetime = 8;
        this.sparks = sparks;
    }

    private void addParticle(SimpleParticleType particle, int count) {
        for (int i = 0; i < count; i++) {
            double x = this.x + (this.random.nextDouble() - this.random.nextDouble()) * SPREAD;
            double y = this.y + (this.random.nextDouble() - this.random.nextDouble()) * SPREAD;
            double z = this.z + (this.random.nextDouble() - this.random.nextDouble()) * SPREAD;
            this.level.addParticle(particle, x, y, z, 1.0 + ((float) this.age / (float) this.lifetime), 0.0, 0.0);
        }
    }

    @Override
    public void tick() {
        this.addParticle(ParticleTypes.EXPLOSION, 3);
        this.addParticle(ModParticleTypes.BOOM.get(), 8);

        if (this.sparks) {
            this.addParticle(ModParticleTypes.SPARK.get(), 8);
        }

        this.age++;
        if (this.age == this.lifetime) {
            this.remove();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class DefaultProvider implements ParticleProvider<SimpleParticleType> {
        public Particle createParticle(
            SimpleParticleType type,
            ClientLevel level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed
        ) {
            return new BlastExplosionParticle(level, x, y, z, false);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class SuccessfulProvider implements ParticleProvider<SimpleParticleType> {
        public Particle createParticle(
            SimpleParticleType type,
            ClientLevel level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed
        ) {
            return new BlastExplosionParticle(level, x, y, z, true);
        }
    }
}