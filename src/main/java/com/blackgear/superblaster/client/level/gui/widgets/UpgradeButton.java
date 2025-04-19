package com.blackgear.superblaster.client.level.gui.widgets;

import com.blackgear.superblaster.client.level.gui.ScrapWorkbenchScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UpgradeButton extends Button {
    public UpgradeButton(int x, int y, int width, int height, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
        this.visible = false;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.visible) {
            int uOffset = this.isHovered() ? 14 : 0;
            guiGraphics.blit(ScrapWorkbenchScreen.WORKBENCH_UI, this.getX(), this.getY(), uOffset, 230, 12, 12);
        }
    }
}