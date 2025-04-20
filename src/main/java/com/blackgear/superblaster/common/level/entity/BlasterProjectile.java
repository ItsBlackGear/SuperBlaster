package com.blackgear.superblaster.common.level.entity;

import com.blackgear.superblaster.client.registries.ModParticleTypes;
import com.blackgear.superblaster.common.registries.ModDataComponents;
import com.blackgear.superblaster.common.registries.ModEntityTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class BlasterProjectile extends AbstractArrow {
    public static final EntityDataAccessor<Float> PROJECTILE_DAMAGE = SynchedEntityData.defineId(BlasterProjectile.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> EXPLOSION_SIZE = SynchedEntityData.defineId(BlasterProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> HEAL_ON_KILL = SynchedEntityData.defineId(BlasterProjectile.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> HOMING_SPEED = SynchedEntityData.defineId(BlasterProjectile.class, EntityDataSerializers.FLOAT);

    // Homing settings
    static final double TARGET_DISTANCE = 1.5;
    static final double HOMING_RANGE = 12.0;
    static final int HOMING_DELAY = 5;
    static final int MAX_LIFETIME = 100;

    // Base stats
    static final float BASE_DAMAGE = 8.0F;
    static final int BASE_EXPLOSION_SIZE = 2;
    static final float BASE_HEAL = 0.0F;
    static final float BASE_HOMING = 0.2F;

    // Upgrade multipliers
    static final float DAMAGE_MULTIPLIER = 0.5F;
    static final float EXPLOSION_MULTIPLIER = 0.2F;
    static final float HEAL_MULTIPLIER = 0.2F;
    static final float HOMING_MULTIPLIER = 0.05F;

    public BlasterProjectile(EntityType<? extends BlasterProjectile> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public BlasterProjectile(Level level, LivingEntity shooter) {
        this(ModEntityTypes.BLASTER_PROJECTILE.get(), level);
        this.setOwner(shooter);
        this.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PROJECTILE_DAMAGE, BASE_DAMAGE);
        builder.define(EXPLOSION_SIZE, BASE_EXPLOSION_SIZE);
        builder.define(HEAL_ON_KILL, BASE_HEAL);
        builder.define(HOMING_SPEED, BASE_HOMING);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            Vec3 motion = this.getDeltaMovement();
            double x = this.getX() - motion.x * 0.25;
            double y = this.getY() - motion.y * 0.25;
            double z = this.getZ() - motion.z * 0.25;

            this.level().addParticle(ModParticleTypes.SUPER_BLASTER_TRAIL.get(), x, y, z, 0, 0, 0);
        }

        // Apply homing behavior after initial delay
        if (this.tickCount > HOMING_DELAY && this.getHomingSpeed() > 0.0F) {
            this.findNearestHostileTarget().ifPresent(this::adjustTrajectoryTowardsTarget);
        }

        // Discard if exceeded lifetime
        if (this.tickCount > MAX_LIFETIME) {
            this.discard();
        }
    }

    private void adjustTrajectoryTowardsTarget(Entity target) {
        Vec3 currentMotion = this.getDeltaMovement();
        double speed = currentMotion.length();
        double distanceToTarget = this.distanceTo(target);

        Vec3 targetVector = new Vec3(
            target.getX() - this.getX(),
            (target.getY() + target.getBbHeight() / 2) - this.getY(),
            target.getZ() - this.getZ()
        );

        if (distanceToTarget < TARGET_DISTANCE) {
            // Direct trajectory when very close to target
            this.setDeltaMovement(targetVector.normalize().scale(speed));
        } else {
            // Gradual homing when further away
            double intensity = Math.min(1.0, 3.0 / distanceToTarget);
            float homingSpeed = this.getHomingSpeed();

            Vec3 newMotion = currentMotion.scale(1 - homingSpeed * intensity).add(targetVector.normalize().scale(homingSpeed * speed * 1.5));
            this.setDeltaMovement(newMotion.normalize().scale(speed));
        }
    }

    private Optional<Entity> findNearestHostileTarget() {
        AABB searchBox = new AABB(
            this.getX() - HOMING_RANGE, this.getY() - HOMING_RANGE, this.getZ() - HOMING_RANGE,
            this.getX() + HOMING_RANGE, this.getY() + HOMING_RANGE, this.getZ() + HOMING_RANGE
        );

        List<Entity> entities = this.level().getEntities(
            this,
            searchBox,
            entity -> entity instanceof LivingEntity
                      && entity.isAlive()
                      && entity instanceof Enemy
                      && !(entity instanceof Player)
                      && entity != this.getOwner()
        );

        return entities.stream().min(Comparator.comparingDouble(this::distanceToSqr));
    }

    @Override
    protected void onHit(HitResult hit) {
        super.onHit(hit);

        if (!this.level().isClientSide) {
            createExplosionEffect(hit.getType() == HitResult.Type.ENTITY);
            this.discard();
        }
    }

    private void createExplosionEffect(boolean isDirectHit) {
        ParticleOptions particle = isDirectHit ? ModParticleTypes.SUCCESSFUL_EXPLOSION_EMITTER.get() : ModParticleTypes.DEFAULT_EXPLOSION_EMITTER.get();

        int size = this.getExplosionSize();
        if (size > 0) {
            this.level().explode(
                this,
                Explosion.getDefaultDamageSource(this.level(), this),
                null,
                this.getX(), this.getY(), this.getZ(),
                size,
                false,
                Level.ExplosionInteraction.NONE,
                particle,
                particle,
                SoundEvents.GENERIC_EXPLODE
            );
        }
    }

    public void initializeWithWeaponStats(ItemStack stack) {
        if (stack.isEmpty()) return;

        int damage = stack.getOrDefault(ModDataComponents.PROJECTILE_DAMAGE.get(), 0);
        int explosion = stack.getOrDefault(ModDataComponents.EXPLOSION_SIZE.get(), 0);
        int heal = stack.getOrDefault(ModDataComponents.HEAL_ON_KILL.get(), 0);
        int home = stack.getOrDefault(ModDataComponents.HOMING_SPEED.get(), 0);

        this.setProjectileDamage(BASE_DAMAGE + (damage * DAMAGE_MULTIPLIER));
        this.setExplosionSize(BASE_EXPLOSION_SIZE + (int)(explosion * EXPLOSION_MULTIPLIER));
        this.setHealOnKill(BASE_HEAL + (heal * HEAL_MULTIPLIER));
        this.setHomingSpeed(BASE_HOMING + (home * HOMING_MULTIPLIER));
    }

    public float getProjectileDamage() {
        return this.getEntityData().get(PROJECTILE_DAMAGE);
    }

    public void setProjectileDamage(float damage) {
        this.getEntityData().set(PROJECTILE_DAMAGE, damage);
        this.setBaseDamage(damage);
    }

    public int getExplosionSize() {
        return this.getEntityData().get(EXPLOSION_SIZE);
    }

    public void setExplosionSize(int size) {
        this.getEntityData().set(EXPLOSION_SIZE, size);
    }

    public float getHealOnKill() {
        return this.getEntityData().get(HEAL_ON_KILL);
    }

    public void setHealOnKill(float healAmount) {
        this.getEntityData().set(HEAL_ON_KILL, healAmount);
    }

    public float getHomingSpeed() {
        return this.getEntityData().get(HOMING_SPEED);
    }

    public void setHomingSpeed(float speed) {
        this.getEntityData().set(HOMING_SPEED, speed);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(Items.ARROW); // Can't keep as null since it may crash the game, so added fallback to arrow.
    }
}