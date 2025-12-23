package net.acidicts.powered_tools.item.custom;


import com.google.common.base.Suppliers;
import net.minecraft.block.Block;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.TagKey;

import java.util.function.Supplier;

public enum ModBatteryMaterials implements BatteryMaterial {
    Stone(100, 25, "Stone", 5, 0.04f),
    Iron(500, 50, "Iron", 25, 0.02f),
    Gold(1000, 100, "Gold", 50, 0.015f),
    Diamond(5000, 250, "Diamond", 200, 0.01f),
    Netherite(10000, 500, "Netherite", 500, 0.005f),
    Diamond_Gold(20000, 1000, "Diamond_Gold", 1000, 0.002f);

    private final int capacity;
    private final int transferRate;
    private final String tier;
    private final int lifespan;
    private final float decayRate;
    ModBatteryMaterials
            (
            int capacity,
            int transferRate,
            String tier,
            int lifespan,
            float decayRate
    ) {
        this.capacity = capacity;
        this.transferRate = transferRate;
        this.tier = tier;
        this.lifespan = lifespan;
        this.decayRate = decayRate;
    }
    @Override
    public int getCapacity() {
        return this.capacity;
    }

    @Override
    public int getTransferRate() {
        return this.transferRate;
    }

    @Override
    public String getTier() {
        return this.tier;
    }

    @Override
    public int getLifespan() {
        return this.lifespan;
    }

    @Override
    public float getDecayRate() {
        return this.decayRate;
    }
}
