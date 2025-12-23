package net.acidicts.powered_tools.item.custom;

import net.acidicts.powered_tools.item.ModItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Map;

public class BatteryItem extends Item {
    public final BatteryMaterial material;
    public final int max_capacity;
    public final int transfer_rate;
    public final int lifespan;
    public final float decay_rate;
    public final String tier;

    private final Map<String, Integer> tier_dict = Map.of(
            "Stone", 0,
            "Iron", 1,
            "Gold", 2,
            "Diamond", 3,
            "Netherite", 4,
            "Diamond_Gold", 5
    );

    public BatteryItem(BatteryMaterial material, Settings settings) {
        super(settings.maxCount(1));

        this.material = material;
        this.max_capacity = material.getCapacity();
        this.transfer_rate = material.getTransferRate();
        this.lifespan = material.getLifespan();
        this.decay_rate = material.getDecayRate();
        this.tier = material.getTier();
    }

    public int getTierInt() {
        return tier_dict.getOrDefault(this.tier, 0);
    }

    public boolean isBroken(ItemStack stack) {
        return getCycles(stack) >= this.lifespan;
    }

    private void initializeData(ItemStack stack) {
        if (!hasData(stack)) {
            NbtCompound nbt = new NbtCompound();
            nbt.putInt("cycles", 0);
            nbt.putInt("current_charge", 0);
            stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
        }
    }

    private boolean hasData(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent == null) {
            return false;
        }
        NbtCompound nbt = nbtComponent.copyNbt();
        return nbt.contains("cycles") && nbt.contains("current_charge");
    }

    public int getCycles(ItemStack stack) {
        initializeData(stack);
        NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        return nbtComponent.copyNbt().getInt("cycles");
    }

    public void setCycles(ItemStack stack, int cycles) {
        initializeData(stack);
        NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = nbtComponent.copyNbt();
        nbt.putInt("cycles", Math.max(0, Math.min(cycles, this.lifespan)));
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    public int getMaxCapacity(ItemStack stack) {
        int cycles = getCycles(stack);
        return (int) (this.max_capacity * (1.0f - (this.decay_rate * cycles)));
    }

    public int getCurrentCharge(ItemStack stack) {
        initializeData(stack);
        NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        return nbtComponent.copyNbt().getInt("current_charge");
    }

    public void setCurrentCharge(ItemStack stack, int charge) {
        initializeData(stack);
        int maxCapacity = getMaxCapacity(stack);
        charge = Math.max(0, Math.min(charge, maxCapacity));

        NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = nbtComponent.copyNbt();
        nbt.putInt("current_charge", charge);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    public void useCharge(ItemStack stack, int amount) {
        int currentCharge = getCurrentCharge(stack);
        setCurrentCharge(stack, currentCharge - amount);
    }

    public void recharge(ItemStack stack, int amount) {
        int currentCharge = getCurrentCharge(stack);
        int maxCapacity = getMaxCapacity(stack);
        int newCharge = Math.min(maxCapacity, currentCharge + amount);

        setCurrentCharge(stack, newCharge);

        if (newCharge >= maxCapacity && currentCharge < maxCapacity) {
            int cycles = getCycles(stack);
            if (cycles < this.lifespan) {
                setCycles(stack, cycles + 1);
                int newMaxCapacity = getMaxCapacity(stack);
                if (newCharge > newMaxCapacity) {
                    setCurrentCharge(stack, newMaxCapacity);
                }
            }
        }
    }

    public int discharge(ItemStack stack, int amount) {
        int currentCharge = getCurrentCharge(stack);
        int dechargedAmount = Math.min(currentCharge, amount);
        setCurrentCharge(stack, currentCharge - dechargedAmount);
        return dechargedAmount;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, net.minecraft.item.tooltip.TooltipType type) {
        int currentCharge = getCurrentCharge(stack);
        int maxCapacity = getMaxCapacity(stack);
        int cycles = getCycles(stack);
        int remainingCycles = this.lifespan - cycles;

        tooltip.add(Text.literal("Charge: " + currentCharge + " / " + maxCapacity).formatted(Formatting.GREEN));
        tooltip.add(Text.literal("Transfer Rate: " + this.transfer_rate + " per tick").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Remaining Cycles: " + remainingCycles).formatted(
            remainingCycles > this.lifespan * 0.5 ? Formatting.GREEN :
            remainingCycles > this.lifespan * 0.25 ? Formatting.YELLOW : Formatting.RED
        ));
        tooltip.add(Text.literal("Tier: " + this.tier).formatted(Formatting.GOLD));
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        int currentCharge = getCurrentCharge(stack);
        int maxCapacity = getMaxCapacity(stack);
        if (maxCapacity == 0) return 0;
        return Math.round(13.0F * currentCharge / maxCapacity);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        int currentCharge = getCurrentCharge(stack);
        int maxCapacity = getMaxCapacity(stack);
        if (maxCapacity == 0) return 0xFF0000;

        float ratio = (float) currentCharge / maxCapacity;

        if (ratio > 0.5F) {
            return 0x00FF00;
        } else if (ratio > 0.25F) {
            return 0xFFFF00;
        } else {
            return 0xFF0000;
        }
    }
}
