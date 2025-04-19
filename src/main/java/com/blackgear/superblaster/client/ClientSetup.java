package com.blackgear.superblaster.client;

import com.blackgear.superblaster.client.level.gui.ScrapWorkbenchScreen;
import com.blackgear.superblaster.client.level.particle.BlastExplosionParticle;
import com.blackgear.superblaster.client.level.renderer.entity.BlasterProjectileRenderer;
import com.blackgear.superblaster.client.registries.ModParticleTypes;
import com.blackgear.superblaster.common.registries.ModBlocks;
import com.blackgear.superblaster.common.registries.ModEntityTypes;
import com.blackgear.superblaster.common.registries.ModMenuTypes;
import net.minecraft.client.particle.CritParticle;
import net.minecraft.client.particle.HugeExplosionParticle;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public class ClientSetup {
    public static void onSync(IEventBus bus) {
        bus.addListener(ClientSetup::onASync);
        bus.addListener(ClientSetup::entityRenderer);
        bus.addListener(ClientSetup::menuScreen);
        bus.addListener(ClientSetup::renderParticle);
    }

    private static void onASync(final FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SCRAP_WORKBENCH.get(), RenderType.cutout());
    }

    private static void entityRenderer(EntityRenderersEvent.RegisterRenderers event) {
         event.registerEntityRenderer(ModEntityTypes.BLASTER_PROJECTILE.get(), BlasterProjectileRenderer::new);
    }

    private static void menuScreen(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.SCRAP_WORKBENCH.value(), ScrapWorkbenchScreen::new);
    }

    private static void renderParticle(RegisterParticleProvidersEvent event) {
        event.registerSpecial(ModParticleTypes.DEFAULT_EXPLOSION_EMITTER.get(), new BlastExplosionParticle.DefaultProvider());
        event.registerSpecial(ModParticleTypes.SUCCESSFUL_EXPLOSION_EMITTER.get(), new BlastExplosionParticle.SuccessfulProvider());
        event.registerSpriteSet(ModParticleTypes.BOOM.get(), HugeExplosionParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.SPARK.get(), HugeExplosionParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.SUPER_BLASTER_TRAIL.get(), CritParticle.Provider::new);
    }
}