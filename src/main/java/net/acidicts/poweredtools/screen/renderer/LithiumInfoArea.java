package net.acidicts.poweredtools.screen.renderer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.text.Text;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;

/*
 *  BluSunrize
 *  Copyright (c) 2021
 *
 *  This code is licensed under "Blu's License of Common Sense" (FORGE VERSION)
 *  Modified for Fabric by: Kaupenjoe
 *  Modified for other utils by: Acidicts
 */

public class LithiumInfoArea {
    private final Rect2i area;
    private final EnergyStorage energy;

    public LithiumInfoArea(int xMin, int yMin)  {
        this(xMin, yMin, null,8,64);
    }

    public LithiumInfoArea(int xMin, int yMin, EnergyStorage energy)  {
        this(xMin, yMin, energy,8,64);
    }

    public LithiumInfoArea(int xMin, int yMin, EnergyStorage energy, int width, int height)  {
        area = new Rect2i(xMin, yMin, width, height);
        this.energy = energy;
    }

    public List<Text> getTooltips() {
        return List.of(Text.literal(energy.getAmount()+" / "+energy.getCapacity()+" E"));
    }

    public void draw(DrawContext context) {
        final int height = area.getHeight();
        int stored = (int)(height*(energy.getAmount()/(float)energy.getCapacity()));
        context.fillGradient(
                area.getX(), area.getY()+(height-stored),
                area.getX() + area.getWidth(), area.getY() +area.getHeight(),
                0x51808080, 0x51808080
        );
    }
}

