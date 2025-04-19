package com.blackgear.superblaster.client.level.gui;

import com.blackgear.superblaster.client.level.gui.widgets.ConfirmButton;
import com.blackgear.superblaster.client.level.gui.widgets.StatSlider;
import com.blackgear.superblaster.client.level.gui.widgets.UpgradeButton;
import com.blackgear.superblaster.common.level.inventory.ScrapWorkbenchMenu;
import com.blackgear.superblaster.common.level.item.StatHolder;
import com.blackgear.superblaster.common.level.item.UpgradeableWeapon;
import com.blackgear.superblaster.common.registries.ModDataComponents;
import com.blackgear.superblaster.core.SuperBlaster;
import com.blackgear.superblaster.core.network.UpgradeWeaponLevel;
import com.blackgear.superblaster.core.network.UpgradeWeaponStats;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class ScrapWorkbenchScreen extends AbstractContainerScreen<ScrapWorkbenchMenu> {
    // UI dimensions
    private static final int SLIDER_WIDTH = 100;
    private static final int SLIDER_HEIGHT = 15;
    private static final int MAX_WEAPON_LEVEL = 4;

    // Level badge display
    private static final int LEVEL_BADGE_SIZE = 8;
    private static final int LEVEL_BADGE_SPACING = 4;

    // UI Colors
    private static final int COLOR_RED = 0xFF0000;
    private static final int COLOR_GREEN = 0x55FF55;
    private static final int COLOR_YELLOW = 0xFFFF55;
    private static final int COLOR_WHITE = 0xFFFFFF;

    public static final ResourceLocation WORKBENCH_UI = SuperBlaster.resource("textures/gui/workbench_ui.png");
    private UpgradeButton upgradeButton;
    private ConfirmButton confirmButton;
    private final StatSlider[] sliders = new StatSlider[4];

    public ScrapWorkbenchScreen(ScrapWorkbenchMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 187;
        this.imageHeight = 211;
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth - 25) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        initButtons();
        initSliders();
    }

    private void initButtons() {
        this.upgradeButton = new UpgradeButton(
            this.leftPos + 120,
            this.topPos + 10,
            12, 12,
            button -> this.upgradeWeapon()
        );

        this.confirmButton = new ConfirmButton(
            this.leftPos + 79,
            this.topPos + 189,
            54, 16,
            button -> this.confirmChanges()
        );

        this.addRenderableWidget(this.upgradeButton);
        this.addRenderableWidget(this.confirmButton);
    }

    private void initSliders() {
        for (int index = 0; index < this.sliders.length; index++) {
            this.sliders[index] = new StatSlider(
                this.leftPos - 7,
                this.topPos + 60 + (36 * index),
                SLIDER_WIDTH,
                SLIDER_HEIGHT
            );
            this.addRenderableWidget(this.sliders[index]);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // No OP
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        guiGraphics.blit(WORKBENCH_UI, x, y, 0, 0, this.imageWidth, this.imageHeight);

        this.renderLevelBadges(guiGraphics, x, y);
        this.renderStatDisplay(guiGraphics);
        this.renderTotalStats(guiGraphics);
    }

    private void renderLevelBadges(GuiGraphics guiGraphics, int x, int y) {
        ItemStack stack = this.menu.getWeaponStack();
        if (!this.menu.isValidItem(stack)) {
            return;
        }

        int level = stack.getOrDefault(ModDataComponents.WEAPON_LEVEL, 0);
        int startY = y + 30;
        int startX = x + 78;
        int badges = Math.min(level, 4) + 1;
        boolean isMaxedOut = badges == 5;

        for (int i = 0; i < badges; i++) {
            int iconX = startX + (i * (LEVEL_BADGE_SIZE + LEVEL_BADGE_SPACING));
            int iconOffset = isMaxedOut ? 9 : 0;
            guiGraphics.blit(WORKBENCH_UI, iconX, startY, iconOffset, 243, LEVEL_BADGE_SIZE, LEVEL_BADGE_SIZE);
        }
    }

    private void renderStatDisplay(GuiGraphics guiGraphics) {
        int nameXCoord = this.leftPos + 92;
        int valueXCoord = this.leftPos + 164;

        for (StatSlider slider : sliders) {
            if (slider.visible) {
                Component statName = slider.getStatDisplay();
                Component valueText = slider.getStatValue();

                int nameWidth = this.font.width(statName);
                int valueWidth = this.font.width(valueText);

                guiGraphics.drawString(this.font, statName, nameXCoord - (nameWidth / 2), slider.getY() - 12, COLOR_WHITE);
                guiGraphics.drawString(this.font, valueText, valueXCoord - (valueWidth / 2), slider.getY() - 12, COLOR_WHITE);
            }
        }
    }

    private void renderTotalStats(GuiGraphics guiGraphics) {
        ItemStack stack = this.menu.getWeaponStack();
        if (!this.menu.isValidItem(stack)) {
            return;
        }

        int totalStats = calculateTotalStats();
        int statCap = calculateStatCap(stack);

        String statText = totalStats + "/" + statCap;
        int textColor = totalStats > statCap
            ? COLOR_RED
            : totalStats == statCap
                ? COLOR_GREEN
                : COLOR_YELLOW;

        int textX = this.leftPos + 157 - (this.font.width(statText) / 2);
        int textY = this.confirmButton.getY() + 4;

        guiGraphics.drawString(this.font, Component.literal(statText), textX, textY, textColor);
    }

    private void upgradeWeapon() {
        ItemStack stack = this.menu.getWeaponStack();
        if (this.menu.isValidItem(stack)) {
            PacketDistributor.sendToServer(new UpgradeWeaponLevel(this.menu.containerId));
        }
    }

    private void confirmChanges() {
        ItemStack stack = this.menu.getWeaponStack();
        if (!this.menu.isValidItem(stack)) {
            return;
        }

        int totalStats = this.calculateTotalStats();
        int statCap = this.calculateStatCap(stack);

        if (totalStats <= statCap) {
            PacketDistributor.sendToServer(
                new UpgradeWeaponStats(
                    this.menu.containerId,
                    sliders[0].getValue(),
                    sliders[1].getValue(),
                    sliders[2].getValue(),
                    sliders[3].getValue()
                )
            );
        }
    }

    private int calculateStatCap(ItemStack stack) {
        int level = stack.getOrDefault(ModDataComponents.WEAPON_LEVEL, 0);
        return 10 + level * 5;
    }

    private int calculateTotalStats() {
        return sliders[0].getValue() + sliders[1].getValue() + sliders[2].getValue() + sliders[3].getValue();
    }

    private boolean hasUpgradeMaterial() {
        if (this.minecraft == null || this.minecraft.player == null) {
            return false;
        }

        return this.minecraft.player.getInventory().contains(new ItemStack(Items.DIAMOND));
    }

    private void updateSliderConfigs(StatHolder[] stats) {
        for (int i = 0; i < stats.length; i++) {
            StatHolder stat = stats[i];
            sliders[i].setDisplay(stat.display());
            sliders[i].setOnUpdate(value -> this.updateComponent(stat.data(), value));
        }
    }

    private void updateSlidersFromItem(ItemStack stack, StatHolder[] stats) {
        for (int i = 0; i < this.sliders.length; i++) {
            int value = stack.getOrDefault(stats[i].data(), 0);
            this.sliders[i].setValue(value);
        }
    }

    private void updateComponent(Supplier<DataComponentType<Integer>> component, int value) {
        ItemStack stack = this.menu.getWeaponStack();
        if (this.menu.isValidItem(stack)) {
            stack.set(component, value);
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        ItemStack stack = this.menu.getWeaponStack();
        boolean isValid = this.menu.isValidItem(stack);
        boolean canUpgrade = false;

        if (isValid && stack.getItem() instanceof UpgradeableWeapon weapon) {
            StatHolder[] stats = { weapon.getFirstStat(), weapon.getSecondStat(), weapon.getThirdStat(), weapon.getForthStat() };

            updateSliderConfigs(stats);
            updateSlidersFromItem(stack, stats);

            int level = stack.getOrDefault(ModDataComponents.WEAPON_LEVEL, 0);
            canUpgrade = level < MAX_WEAPON_LEVEL && hasUpgradeMaterial();

            int totalStats = this.calculateTotalStats();
            int statCap = this.calculateStatCap(stack);
            this.confirmButton.active = totalStats == statCap;
        }

        this.upgradeButton.visible = canUpgrade;
        this.confirmButton.visible = isValid;

        for (StatSlider slider : sliders) {
            slider.visible = isValid;
        }
    }
}