package net.acidicts.poweredtools.block.entity.renderer.charger;

import net.acidicts.poweredtools.block.entity.custom.ChargerBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ChargerBlockRenderer extends GeoBlockRenderer<ChargerBlockEntity> {
    public ChargerBlockRenderer(BlockEntityRendererFactory.Context context) {
        super(new ChargerBlockModel());
    }
}

