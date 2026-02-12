package net.acidicts.poweredtools.item.custom;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class BatteryUsingItem extends Item implements BatteryUsing {

    public BatteryUsingItem(Settings settings) {
        super(settings);
    }


    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        NbtCompound nbt = getBatteryData(stack);
        if (nbt.getBoolean("battery_installed")) {
            int energy = getEnergy(stack);
            int maxCapacity = getMaxCapacity(stack);
            if (maxCapacity == 0) return 0;
            return Math.round(13.0F * energy / maxCapacity);
        }
        return (int) 13.0F;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        NbtCompound nbt = getBatteryData(stack);
        if (nbt.getBoolean("battery_installed")) {
            int energy = getEnergy(stack);
            int maxCapacity = getMaxCapacity(stack);
            if (maxCapacity == 0) return 0xFF0000;

            float ratio = (float) energy / maxCapacity;

            if (ratio > 0.5F) {
                return 0x00FFFF;
            } else if (ratio > 0.25F) {
                return 0xFFFF00;
            } else {
                return 0xFF0000;
            }
        }
        return 0xFF0000;
    }
}
