package com.blackgear.superblaster.common.level.inventory;

import com.blackgear.superblaster.common.level.item.UpgradeableWeapon;
import com.blackgear.superblaster.common.registries.ModBlocks;
import com.blackgear.superblaster.common.registries.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ScrapWorkbenchMenu extends AbstractContainerMenu {
    // UI layout constants-
    private static final int WEAPON_SLOT_X = 98;
    private static final int WEAPON_SLOT_Y = 8;
    private static final int HOTBAR_START_X = 4;
    private static final int HOTBAR_START_Y = 26;
    private static final int HOTBAR_SLOT_SPACING = 18;

    // Slot indices
    public static final int WEAPON_SLOT = 0;
    private static final int HOTBAR_START = 1;
    private static final int HOTBAR_END = 10;

    private final ContainerLevelAccess access;

    public ScrapWorkbenchMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public ScrapWorkbenchMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenuTypes.SCRAP_WORKBENCH.get(), containerId);
        this.access = access;

        this.addSlot(new Slot(inventory, 9, WEAPON_SLOT_X, WEAPON_SLOT_Y));

        // Hotbar slots
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inventory, i, HOTBAR_START_X, HOTBAR_START_Y + i * HOTBAR_SLOT_SPACING));
        }
    }

    public ItemStack getWeaponStack() {
        return this.getSlot(WEAPON_SLOT).getItem();
    }

    public boolean isValidItem(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof UpgradeableWeapon;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot.hasItem()) {
            ItemStack result = slot.getItem();
            stack = result.copy();

            if (slotIndex == WEAPON_SLOT) {
                // Move from weapon slot to hotbar
                if (!this.moveItemStackTo(result, HOTBAR_START, HOTBAR_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex >= HOTBAR_START && slotIndex < HOTBAR_END) {
                // Move from hotbar to weapon slot (if valid)
                if (!this.moveItemStackTo(result, WEAPON_SLOT, HOTBAR_START, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (result.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (result.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, result);
        }

        return stack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        // Return items to the player when the menu is closed
        Slot weaponSlot = this.getSlot(WEAPON_SLOT);
        if (weaponSlot.hasItem()) {
            ItemStack stack = weaponSlot.getItem();

            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }

            weaponSlot.setByPlayer(ItemStack.EMPTY);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.SCRAP_WORKBENCH.get());
    }
}