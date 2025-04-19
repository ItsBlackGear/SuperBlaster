package com.blackgear.superblaster.common.registries;

import com.blackgear.superblaster.core.SuperBlaster;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, SuperBlaster.MODID);

    public static final Supplier<DataComponentType<Integer>> WEAPON_LEVEL = COMPONENTS.registerComponentType(
        "weapon_level",
        builder -> builder.persistent(Codec.INT)
    );
    public static final Supplier<DataComponentType<Integer>> PROJECTILE_DAMAGE = COMPONENTS.registerComponentType(
        "projectile_damage",
        builder -> builder.persistent(Codec.INT)
    );
    public static final Supplier<DataComponentType<Integer>> EXPLOSION_SIZE = COMPONENTS.registerComponentType(
        "explosion_size",
        builder -> builder.persistent(Codec.INT)
    );
    public static final Supplier<DataComponentType<Integer>> HEAL_ON_KILL = COMPONENTS.registerComponentType(
        "heal_on_kill",
        builder -> builder.persistent(Codec.INT)
    );
    public static final Supplier<DataComponentType<Integer>> HOMING_SPEED = COMPONENTS.registerComponentType(
        "homing_speed",
        builder -> builder.persistent(Codec.INT)
    );
}