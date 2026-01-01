package net.acidicts.poweredtools.block.entity.renderer.charger;

import net.acidicts.poweredtools.PoweredTools;
import net.acidicts.poweredtools.block.entity.custom.ChargerBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ChargerBlockModel extends GeoModel<ChargerBlockEntity> {
    @Override
    public Identifier getModelResource(ChargerBlockEntity animatable) {
        return Identifier.of(PoweredTools.MOD_ID, "geo/charger.geo.json");
    }

    @Override
    public Identifier getTextureResource(ChargerBlockEntity animatable) {
        return Identifier.of(PoweredTools.MOD_ID, "textures/entity/charger.png");
    }

    @Override
    public Identifier getAnimationResource(ChargerBlockEntity animatable) {
        return Identifier.of(PoweredTools.MOD_ID, "animations/charger.animation.json");
    }
}

