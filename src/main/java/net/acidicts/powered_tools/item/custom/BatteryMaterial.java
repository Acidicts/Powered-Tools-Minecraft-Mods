package net.acidicts.powered_tools.item.custom;

public interface BatteryMaterial {
    // Max Capacity
    int getCapacity();

    // Charging/Discharging rate - Not in Tool
    int getTransferRate();

    // Tier for potential future use
    String getTier();

    // Number of Charge Cycles
    int getLifespan();

    // Decay Rate per use
    float getDecayRate();
}
