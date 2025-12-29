package net.acidicts.powered_tools.block.entity.renderer;

import net.acidicts.powered_tools.block.entity.ChargerBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ChargerBlockRenderer extends GeoBlockRenderer<ChargerBlockEntity> {
    public ChargerBlockRenderer(BlockEntityRendererFactory.Context context) {
        super(new ChargerBlockModel());
    }
}

