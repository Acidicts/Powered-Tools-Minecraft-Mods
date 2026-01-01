package net.acidicts.poweredtools.screen.custom.coal_generator;

import com.mojang.blaze3d.systems.RenderSystem;
import net.acidicts.poweredtools.PoweredTools;
import net.acidicts.poweredtools.screen.renderer.EnergyInfoArea;
import net.acidicts.poweredtools.util.MouseUtil;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.Optional;

public class CoalGeneratorScreen extends HandledScreen<CoalGeneratorScreenHandler> {
    private static final Identifier GUI_TEXTURE =
            Identifier.of(PoweredTools.MOD_ID, "textures/gui/coal_generator/coal_generator_gui.png");
    private static final Identifier LIT_PROGRESS_TEXTURE = Identifier.of("container/furnace/lit_progress");
    private EnergyInfoArea energyInfoArea;

    public CoalGeneratorScreen(CoalGeneratorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        // Center title
        titleX = (width - backgroundWidth) / 2;

        assignEnergyInfoArea();
    }

    private void assignEnergyInfoArea() {
        energyInfoArea = new EnergyInfoArea(((width - backgroundWidth) / 2) + 156,
                ((height - backgroundHeight) / 2 ) + 11, handler.blockEntity.energyStorage);
    }

    private void renderEnergyAreaTooltips(DrawContext context, int pMouseX, int pMouseY, int x, int y) {
        if(isMouseAboveArea(pMouseX, pMouseY, x, y, 156, 11, 8, 64)) {
            context.drawTooltip(Screens.getTextRenderer(this), energyInfoArea.getTooltips(),
                    Optional.empty(), pMouseX - x, pMouseY - y);
        }
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        renderEnergyAreaTooltips(context, mouseX, mouseY, x, y);
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
        renderBurnProgress(context, x, y);
    }

    private void renderBurnProgress(DrawContext context, int x, int y) {
        if(handler.isBurning()) {
            int l = MathHelper.ceil(this.handler.getFuelProgress() * 13.0f) + 1;
            context.drawGuiTexture(LIT_PROGRESS_TEXTURE, 14, 14, 0, 14 - l, x + 80, y + 18 + 14 - l, 14, l);
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