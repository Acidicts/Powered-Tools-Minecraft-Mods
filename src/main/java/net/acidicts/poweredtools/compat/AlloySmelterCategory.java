package net.acidicts.poweredtools.compat;


import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.acidicts.poweredtools.PoweredTools;
import net.acidicts.poweredtools.block.ModBlocks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;

public class AlloySmelterCategory implements DisplayCategory<BasicDisplay> {
    public static final Identifier TEXTURE = Identifier.of(PoweredTools.MOD_ID, "textures/gui/alloy_smelter/alloy_smelter_gui.png");
    public static final CategoryIdentifier<AlloySmelterDisplay> ALLOY_SMELTER =
            CategoryIdentifier.of(PoweredTools.MOD_ID, "alloy_smelter");

    @Override
    public CategoryIdentifier<? extends BasicDisplay> getCategoryIdentifier() {
        return ALLOY_SMELTER;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("rei.poweredtools.alloy_smelter");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.ALLOY_SMELTER.asItem().getDefaultStack());
    }

    @Override
    public List<Widget> setupDisplay(BasicDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 87, bounds.getCenterY() - 35);
        List<Widget> widgets = new LinkedList<>();

        widgets.add(Widgets.createTexturedWidget(TEXTURE, new Rectangle(startPoint.x, startPoint.y, 175, 82)));

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 20, startPoint.y + 12))
                .entries(display.getInputEntries().get(0)).markInput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 20, startPoint.y + 32))
                .entries(display.getInputEntries().get(1)).markInput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 20, startPoint.y + 52))
                .entries(display.getInputEntries().get(2)).markInput());

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 120, startPoint.y + 33))
                .entries(display.getOutputEntries().get(0)).markOutput());

        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 90;
    }
}