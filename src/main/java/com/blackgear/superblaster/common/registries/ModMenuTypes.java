package com.blackgear.superblaster.common.registries;

import com.blackgear.superblaster.common.level.inventory.ScrapWorkbenchMenu;
import com.blackgear.superblaster.core.SuperBlaster;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, SuperBlaster.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<ScrapWorkbenchMenu>> SCRAP_WORKBENCH = register("scrap_workbench", ScrapWorkbenchMenu::new);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> register(String key, MenuType.MenuSupplier<T> factory) {
        return MENUS.register(key, () -> new MenuType<>(factory, FeatureFlags.VANILLA_SET));
    }
}