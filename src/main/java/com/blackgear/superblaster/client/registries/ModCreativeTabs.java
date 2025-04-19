package com.blackgear.superblaster.client.registries;

import com.blackgear.superblaster.common.registries.ModItems;
import com.blackgear.superblaster.core.SuperBlaster;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SuperBlaster.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SUPER_BLASTER = CREATIVE_TABS.register(
        "super_blaster",
        () -> CreativeModeTab.builder()
            .title(Component.literal("Super Blaster"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ModItems.BLASTER.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.SCRAP_WORKBENCH);
                output.accept(ModItems.BLASTER);
            })
            .build()
    );
}
