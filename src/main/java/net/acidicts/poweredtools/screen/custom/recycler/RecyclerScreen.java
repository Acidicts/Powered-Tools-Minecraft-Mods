package net.acidicts.poweredtools.screen.custom.recycler;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.acidicts.poweredtools.PoweredTools;
import net.acidicts.poweredtools.screen.renderer.EnergyInfoArea;
import net.acidicts.poweredtools.util.MouseUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class RecyclerScreen extends HandledScreen<RecyclerScreenHandler> {
    private static final Identifier GUI_TEXTURE =
            Identifier.of(PoweredTools.MOD_ID, "textures/gui/recycler/recycler_gui.png");
    private static final Identifier ARROW_TEXTURE =
            Identifier.of(PoweredTools.MOD_ID, "textures/gui/arrow_progress.png");

    private EnergyInfoArea energyInfoArea;


    public RecyclerScreen(RecyclerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        titleY = 1000;
        playerInventoryTitleY = 1000;

        assignEnergyInfoArea();
    }

    private void assignEnergyInfoArea() {
        energyInfoArea = new EnergyInfoArea(((width - backgroundWidth) / 2) + 155,
                ((height - backgroundHeight) / 2 ) + 9, handler.blockEntity.energyStorage, 10, 48);
    }

    private void renderEnergyAreaTooltips(DrawContext context, int pMouseX, int pMouseY, int x, int y) {
        if(isMouseAboveArea(pMouseX, pMouseY, x, y, 156, 9, 8, 48)) {
            context.drawTooltip(Screens.getTextRenderer(this), energyInfoArea.getTooltips(),
                    Optional.empty(), pMouseX - x, pMouseY - y);
        }
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        renderEnergyAreaTooltips(context, mouseX, mouseY, x, y);

        // Lithium tooltip (bar area at offset 144,9 with size 8x48) - same vertical position as energy area
        if(isMouseAboveArea(mouseX, mouseY, x, y, 144, 9, 8, 48)) {
            String lithiumText = handler.getLithiumMb() + " / " + handler.getMaxLithiumMb() + " mB";
            context.drawTooltip(Screens.getTextRenderer(this), java.util.List.of(net.minecraft.text.Text.of(lithiumText)), Optional.empty(), mouseX - x, mouseY - y);
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(GUI_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        energyInfoArea.draw(context);
        renderProgressArrow(context, x, y);

        // draw lithium bar background from GUI texture or separate texture
        // We'll use an external small texture (8x48) that contains the bar fill on the left (or draw colored rect)
        // For simplicity, draw a colored rect representing lithium fill.
        int barX = x + 8;
        int barY = y + 7;
        int barWidth = 16;
        int barHeight = 50;

        int lithium = handler.getLithiumMb();
        int lithiumMax = handler.getMaxLithiumMb();

        // calculate filled pixels (bottom-up)
        int filled = 0;
        if (lithiumMax > 0) {
            filled = Math.round(((float) lithium / (float) lithiumMax) * barHeight);
            if (filled < 0) filled = 0;
            if (filled > barHeight) filled = barHeight;
        }

        // draw bar background (dark gray)
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF2A2A2A);
        // draw fill (cyan-like color) from bottom up
        context.fill(barX, barY + (barHeight - filled), barX + barWidth, barY + barHeight, 0xFF3DD1E0);
    }

    private void renderProgressArrow(DrawContext context, int x, int y) {
        if(handler.isCrafting()) {
            context.drawTexture(ARROW_TEXTURE, x + 73, y + 35, 0, 0,
                    handler.getScaledArrowProgress(), 16, 24, 16);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }
}