package com.blackgear.superblaster.common.registries;

import com.blackgear.superblaster.common.level.entity.BlasterProjectile;
import com.blackgear.superblaster.core.SuperBlaster;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, SuperBlaster.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<BlasterProjectile>> BLASTER_PROJECTILE = ENTITY_TYPES.register(
        "blaster_projectile",
        () -> EntityType.Builder.<BlasterProjectile>of(BlasterProjectile::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(64)
            .updateInterval(1)
            .build("blaster_projectile")
    );
}