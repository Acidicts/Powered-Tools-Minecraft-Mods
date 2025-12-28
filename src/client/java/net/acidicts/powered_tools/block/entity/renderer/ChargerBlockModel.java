package net.acidicts.powered_tools.block.entity.renderer;

import net.acidicts.powered_tools.Powered_tools;
import net.acidicts.powered_tools.block.entity.ChargerBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ChargerBlockModel extends GeoModel<ChargerBlockEntity> {
    @Override
    public Identifier getModelResource(ChargerBlockEntity animatable) {
        return Identifier.of(Powered_tools.MOD_ID, "geo/charger.geo.json");
    }

    @Override
    public Identifier getTextureResource(ChargerBlockEntity animatable) {
        return Identifier.of(Powered_tools.MOD_ID, "textures/entity/charger.png");
    }

    @Override
    public Identifier getAnimationResource(ChargerBlockEntity animatable) {
        return Identifier.of(Powered_tools.MOD_ID, "animations/charger.animation.json");
    }
}

