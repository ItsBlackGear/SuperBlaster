package com.blackgear.superblaster.core;

import com.blackgear.superblaster.client.ClientSetup;
import com.blackgear.superblaster.client.registries.ModCreativeTabs;
import com.blackgear.superblaster.client.registries.ModParticleTypes;
import com.blackgear.superblaster.common.CommonSetup;
import com.blackgear.superblaster.common.registries.*;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(SuperBlaster.MODID)
public class SuperBlaster {
    public static final String MODID = "superblaster";
    private static final Logger LOGGER = LogUtils.getLogger();

    public SuperBlaster(IEventBus bus, ModContainer container) {
        bootstrap(bus);

        ModDataComponents.COMPONENTS.register(bus);
        ModItems.ITEMS.register(bus);
        ModBlocks.BLOCKS.register(bus);
        ModParticleTypes.PARTICLES.register(bus);
        ModEntityTypes.ENTITY_TYPES.register(bus);
        ModCreativeTabs.CREATIVE_TABS.register(bus);
        ModMenuTypes.MENUS.register(bus);
    }

    private static void bootstrap(IEventBus bus) {
        if (FMLEnvironment.dist.isClient()) {
            ClientSetup.onSync(bus);
        }

        CommonSetup.onSync(bus);
    }

    public static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}