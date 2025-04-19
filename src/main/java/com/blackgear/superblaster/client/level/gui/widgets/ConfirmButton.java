package com.blackgear.superblaster.client.level.gui.widgets;

import com.blackgear.superblaster.client.level.gui.ScrapWorkbenchScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConfirmButton extends Button {
    private static final int[] NORMAL_UV = {28, 213};
    private static final int[] HOVER_UV = {28, 230};
    private static final int[] INACTIVE_UV = {83, 213};

    public ConfirmButton(int x, int y, int width, int height, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
        this.visible = false;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!this.visible) return;

        int[] uv = NORMAL_UV;

        if (!this.isActive()) {
            uv = INACTIVE_UV;
        } else if (this.isHovered()) {
            uv = HOVER_UV;
        }

        guiGraphics.blit(ScrapWorkbenchScreen.WORKBENCH_UI, this.getX(), this.getY(), uv[0], uv[1], this.width, this.height);
    }
}