package com.blackgear.superblaster.common.registries;

import com.blackgear.superblaster.common.level.item.BlasterItem;
import com.blackgear.superblaster.core.SuperBlaster;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SuperBlaster.MODID);

    public static final DeferredItem<Item> BLASTER = ITEMS.register(
        "blaster",
        () -> new BlasterItem(
            new Item.Properties()
                .stacksTo(1)
                .component(ModDataComponents.WEAPON_LEVEL, 0)
                .component(ModDataComponents.PROJECTILE_DAMAGE, 0)
                .component(ModDataComponents.EXPLOSION_SIZE, 0)
                .component(ModDataComponents.HEAL_ON_KILL, 0)
                .component(ModDataComponents.HOMING_SPEED, 0)
        )
    );

    public static final DeferredItem<BlockItem> SCRAP_WORKBENCH = ITEMS.registerSimpleBlockItem(ModBlocks.SCRAP_WORKBENCH);
}