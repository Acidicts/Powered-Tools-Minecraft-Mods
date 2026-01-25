package net.acidicts.poweredtools.screen.custom.shieldcore;

import com.mojang.blaze3d.systems.RenderSystem;
import net.acidicts.poweredtools.PoweredTools;
import net.acidicts.poweredtools.item.custom.ShieldCore;
import net.acidicts.poweredtools.networking.ModMessages;
import net.acidicts.poweredtools.screen.renderer.EnergyInfoArea;
import net.acidicts.poweredtools.util.MouseUtil;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ShieldCoreScreen extends HandledScreen<ShieldCoreScreenHandler> {
    private static final Identifier GUI_TEXTURE =
            Identifier.of(PoweredTools.MOD_ID, "textures/gui/charger/charger_gui.png");
    private EnergyInfoArea energyInfoArea;

    public ShieldCoreScreen(ShieldCoreScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    // Add this field to your ShieldCoreScreen class
    private ButtonWidget toggleButton;

    @Override
    protected void init() {
        super.init();
        titleX = (width - backgroundWidth) / 2;
        assignEnergyInfoArea();

        toggleButton = ButtonWidget.builder(getToggleText(), button -> {
            net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new ModMessages.TogglePayload());
            updateButtonText();
        }).dimensions(x + 5, y + 60, 90, 20).build();

        this.addDrawableChild(toggleButton);
    }

    private Text getToggleText() {
        boolean active = handler.getStack().getItem() instanceof ShieldCore core && core.isActive(handler.getStack());
        return active ? Text.literal("Toggle to Off") : Text.literal("Toggle to On");
    }

    private void updateButtonText() {
        if (toggleButton != null) {
            toggleButton.setMessage(getToggleText());
        }
    }

    private void assignEnergyInfoArea() {
        energyInfoArea = new EnergyInfoArea(((width - backgroundWidth) / 2) + 156,
                ((height - backgroundHeight) / 2 ) + 11, handler.getEnergyStorage());
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

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        updateButtonText();
    }
}