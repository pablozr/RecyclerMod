package com.recycler.recyclermod.client.screen;

import com.recycler.recyclermod.content.menu.RecyclerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RecyclerScreen extends AbstractContainerScreen<RecyclerMenu> {
    private static final ResourceLocation BG = new ResourceLocation("recycler","textures/gui/base1.png");
    private static final ResourceLocation VANILLA = new ResourceLocation("minecraft","textures/gui/container/generic_54.png");
    private static final int SLOT_U = 7, SLOT_V = 17;

    public RecyclerScreen(RecyclerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelX = 8;  this.titleLabelY = 6;
        this.inventoryLabelX = 8; this.inventoryLabelY = this.imageHeight - 96 + 2;
    }

    private void drawArrowBackground(GuiGraphics g, int centerX, int topY,
                                     int shaftH, int headH, int thickness, int bgColor) {
        int left = centerX - (thickness / 2);
        int right = left + Math.max(1, thickness);
        if (shaftH > 0) g.fill(left, topY, right, topY + shaftH, bgColor);
        int baseY = topY + shaftH;
        for (int i = 0; i < headH; i++) {
            int w = thickness + 2*i;
            int x0 = centerX - w/2;
            int x1 = x0 + w;
            g.fill(x0, baseY + i, x1, baseY + i + 1, bgColor);
        }
    }

    private void drawArrowFillMasked(GuiGraphics g, int centerX, int topY,
                                     int shaftH, int headH, double progress01,
                                     int thickness, int fillColor) {
        int fullH = Math.max(0, shaftH) + Math.max(0, headH);
        int filled = (int)Math.round(Math.max(0.0, Math.min(1.0, progress01)) * fullH);
        if (filled <= 0) return;

        int shaftFilled = Math.min(filled, Math.max(0, shaftH));
        if (shaftFilled > 0) {
            int left = centerX - (thickness / 2);
            int right = left + Math.max(1, thickness);
            g.fill(left, topY, right, topY + shaftFilled, fillColor);
        }

        int remain = filled - shaftFilled;
        if (remain > 0) {
            int baseY = topY + shaftH;
            int headLines = Math.min(remain, Math.max(0, headH));
            for (int i = 0; i < headLines; i++) {
                int w = thickness + 2*i;
                int x0 = centerX - w/2;
                int x1 = x0 + w;
                g.fill(x0, baseY + i, x1, baseY + i + 1, fillColor);
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics g, float pt, int mx, int my) {
        g.blit(BG, leftPos, topPos, 0, 0, imageWidth, imageHeight, 176, 166);

        int[] idxInputs  = { RecyclerMenu.IN0, RecyclerMenu.IN1, RecyclerMenu.IN2, RecyclerMenu.IN3, RecyclerMenu.IN4 };
        int[] idxOutputs = { RecyclerMenu.OUT0, RecyclerMenu.OUT1, RecyclerMenu.OUT2, RecyclerMenu.OUT3, RecyclerMenu.OUT4 };

        java.util.function.IntConsumer blitSlotFrame = (slotIdx) -> {
            var s = this.menu.slots.get(slotIdx);
            int sx = leftPos + s.x - 1;
            int sy = topPos + s.y - 1;
            g.blit(VANILLA, sx, sy, SLOT_U, SLOT_V, 18, 18, 256, 256);
        };
        for (int i : idxInputs)  blitSlotFrame.accept(i);
        for (int i : idxOutputs) blitSlotFrame.accept(i);

        final int BAR_W = 4, BAR_H = 40, PAD = 1, MARGIN_RIGHT = 6, OFFSET_Y = 18;
        int x0 = leftPos + imageWidth - MARGIN_RIGHT - BAR_W - 2*PAD;
        int y0 = topPos  + OFFSET_Y;
        int border = 0xFF2A2A2A, bg = 0xFF111111, fill = 0xFF2ECC71;
        g.fill(x0, y0, x0 + BAR_W + 2*PAD, y0 + BAR_H + 2*PAD, border);
        int innerX0 = x0 + PAD, innerY0 = y0 + PAD;
        g.fill(innerX0, innerY0, innerX0 + BAR_W, innerY0 + BAR_H, bg);
        int eFill = this.menu.getEnergyScaled(BAR_H);
        if (eFill > 0) {
            int fy0 = innerY0 + (BAR_H - eFill);
            g.fill(innerX0, fy0, innerX0 + BAR_W, innerY0 + BAR_H, fill);
        }

        final int ARW_THICK = 3;
        final int ARW_HEAD  = 6;
        final int BG_COL    = 0xFF3A3A3A;
        final int FILL_COL  = 0xFFFFFFFF;

        for (int i = 0; i < 5; i++) {
            var inSlot  = this.menu.slots.get(idxInputs[i]);
            var outSlot = this.menu.slots.get(idxOutputs[i]);
            int centerX = leftPos + inSlot.x + 8;
            int topY       = topPos + inSlot.y + 18 + 1;
            int bottomY    = topPos + outSlot.y - 3;
            int fullHeight = Math.max(ARW_HEAD + 1, bottomY - topY);
            int shaftH     = Math.max(0, fullHeight - ARW_HEAD);
            drawArrowBackground(g, centerX, topY, shaftH, ARW_HEAD, ARW_THICK, BG_COL);
            int prog = this.menu.getProgress(i);
            int max  = this.menu.getMaxProgress();
            double p = max > 0 ? Math.min(1.0, Math.max(0.0, prog / (double)max)) : 0.0;
            drawArrowFillMasked(g, centerX, topY, shaftH, ARW_HEAD, p, ARW_THICK, FILL_COL);
        }
        final int INV_Y = 84, HOTBAR_Y = 142;
        for (int r = 0; r < 3; ++r)
            for (int c = 0; c < 9; ++c)
                g.blit(VANILLA, leftPos + 8 + c*18 - 1, topPos + INV_Y + r*18 - 1, SLOT_U, SLOT_V, 18, 18, 256, 256);
        for (int c = 0; c < 9; ++c)
            g.blit(VANILLA, leftPos + 8 + c*18 - 1, topPos + HOTBAR_Y - 1, SLOT_U, SLOT_V, 18, 18, 256, 256);
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        super.render(g, mx, my, pt);
        renderTooltip(g, mx, my);

        final int BAR_W = 4, BAR_H = 40, PAD = 1, MARGIN_RIGHT = 6, OFFSET_Y = 18;
        if (isHovering(imageWidth - MARGIN_RIGHT - BAR_W - 2*PAD, OFFSET_Y, BAR_W + 2*PAD, BAR_H + 2*PAD, mx, my)) {
            int e = menu.getEnergyStored(), m = menu.getEnergyCapacity();
            g.renderTooltip(this.font, Component.literal(e + " / " + m + " FE"), mx, my);
        }
    }
}
