package com.blackgear.superblaster.common;

import com.blackgear.superblaster.common.level.entity.BlasterProjectile;
import com.blackgear.superblaster.core.network.UpgradeWeaponLevel;
import com.blackgear.superblaster.core.network.UpgradeWeaponStats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class CommonSetup {
    public static void onSync(IEventBus bus) {
        bus.addListener(CommonSetup::onASync);
        bus.addListener(CommonSetup::networking);

        NeoForge.EVENT_BUS.addListener(CommonSetup::onDeath);
    }

    private static void onASync(final FMLCommonSetupEvent event) {

    }

    private static void networking(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(UpgradeWeaponLevel.TYPE, UpgradeWeaponLevel.STREAM_CODEC, UpgradeWeaponLevel::handle);
        registrar.playToServer(UpgradeWeaponStats.TYPE, UpgradeWeaponStats.STREAM_CODEC, UpgradeWeaponStats::handle);
    }

    private static void onDeath(LivingDeathEvent event) {
        if (event.getSource().getDirectEntity() instanceof BlasterProjectile projectile) {
            Entity owner = projectile.getOwner();
            if (owner != null && owner.isAlive() && owner instanceof Player player) {
                float heal = projectile.getHealOnKill();
                if (heal > 0) {
                    player.heal(heal);
                }
            }
        }
    }
}