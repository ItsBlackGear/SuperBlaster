package com.blackgear.superblaster.client.level.gui.widgets;

import com.blackgear.superblaster.core.SuperBlaster;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class StatSlider extends AbstractWidget {
    private static final int START_X = 32;
    private static final int STEP_SIZE = 14;
    private static final int MAX_VALUE = 10;
    private static final int HANDLE_WIDTH = 12;
    private static final int HANDLE_HEIGHT = 15;
    private static final int STEP_COUNT = MAX_VALUE + 1; // 0-10 inclusive

    private static final ResourceLocation TEXTURE = SuperBlaster.resource("textures/gui/workbench_ui.png");
    private Component display = Component.empty();
    private int value;
    private final int x;
    private Consumer<Integer> onUpdate = integer -> {};

    public StatSlider(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.x = x;
        this.visible = false;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!this.visible) return;

        int handleX = this.x + 1 + START_X + (value * STEP_SIZE);
        boolean hovering = isMouseOverHandle(mouseX, mouseY, handleX);
        guiGraphics.blit(TEXTURE, handleX, this.getY(), hovering ? 14 : 0, 213, HANDLE_WIDTH, HANDLE_HEIGHT);
        this.isHovered = hovering;
    }

    private boolean isMouseOverHandle(int mouseX, int mouseY, int handleX) {
        return mouseX >= handleX && mouseX <= handleX + HANDLE_WIDTH && mouseY >= this.getY() && mouseY <= this.getY() + HANDLE_HEIGHT;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.visible || !this.active) return false;

        for (int i = 0; i < STEP_COUNT; i++) {
            int stepX = this.x + START_X + (i * STEP_SIZE);
            if (mouseX >= stepX && mouseX < stepX + STEP_SIZE && mouseY >= this.getY() && mouseY <= this.getY() + HANDLE_HEIGHT) {
                this.setValue(i);
                return true;
            }
        }

        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narration) {
        // No OP
    }

    public void setDisplay(Component display) {
        this.display = display;
    }

    public void setOnUpdate(Consumer<Integer> onUpdate) {
        this.onUpdate = onUpdate;
    }

    public Component getStatValue() {
        return Component.literal(String.valueOf(this.value));
    }

    public Component getStatDisplay() {
        return this.display;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        int upgrade = Math.max(0, Math.min(MAX_VALUE, value));
        if (upgrade != this.value) {
            this.value = upgrade;
            this.onUpdate.accept(this.value);
        }
    }
}