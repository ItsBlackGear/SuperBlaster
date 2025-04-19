package com.blackgear.superblaster.common.level.item;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public record StatHolder(Component display, Supplier<DataComponentType<Integer>> data) {}