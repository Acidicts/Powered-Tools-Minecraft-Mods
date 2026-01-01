package net.acidicts.poweredtools.item.custom;

import net.acidicts.poweredtools.PoweredTools;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.List;
import java.util.Map;

public class BatteryItem extends Item {

    private final BatteryMaterial material;

    private final Map<String, Integer> tier_dict = Map.of(
            "Stone", 0,
            "Iron", 1,
            "Gold", 2,
            "Diamond", 3,
            "Netherite", 4,
            "Diamond_Gold", 5
    );

    public BatteryItem(BatteryMaterial material, Settings settings) {
        super(settings.maxCount(1).component(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT));

        this.material = material;
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        initializeBatteryNbt(stack);
        return stack;
    }

    private void initializeBatteryNbt(ItemStack stack) {
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();

        if (!nbt.contains("tier")) {
            nbt.putString("tier", material.getTier());
        }
        if (!nbt.contains("cycles")) {
            nbt.putInt("cycles", 0);
        }
        if (!nbt.contains("currentCharge")) {
            nbt.putLong("currentCharge", 0L);
        }

        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    private Map<String, Number> getBatteryTierProperties(String tier) {
        return switch (tier) {
            case "Stone" -> Map.of(
                    "capacity", 100,
                    "transferRate", 25,
                    "lifespan", 5,
                    "tierLevel", 0,
                    "decayRate", 0.04f
            );
            case "Iron" -> Map.of(
                    "capacity", 500,
                    "transferRate", 50,
                    "lifespan", 25,
                    "tierLevel", 1,
                    "decayRate", 0.02f
            );
            case "Gold" -> Map.of(
                    "capacity", 1000,
                    "transferRate", 100,
                    "lifespan", 50,
                    "tierLevel", 2,
                    "decayRate", 0.015f
            );
            case "Diamond" -> Map.of(
                    "capacity", 5000,
                    "transferRate", 250,
                    "lifespan", 200,
                    "tierLevel", 3,
                    "decayRate", 0.01f
            );
            case "Netherite" -> Map.of(
                    "capacity", 10000,
                    "transferRate", 500,
                    "lifespan", 500,
                    "tierLevel", 4,
                    "decayRate", 0.005f
            );
            case "Diamond_Gold" -> Map.of(
                    "capacity", 20000,
                    "transferRate", 1000,
                    "lifespan", 1000,
                    "tierLevel", 5,
                    "decayRate", 0.002f
            );
            default -> Map.of(
                    "capacity", 0,
                    "transferRate", 0,
                    "lifespan", 0,
                    "tierLevel", 0,
                    "decayRate", 0f
            );
        };
    }

    public boolean isBroken(ItemStack stack) {
        return getCycles(stack) >= getBatteryLifespan(stack);
    }

    public int getBatteryLifespan(ItemStack stack) {
        return (int) getBatteryTierProperties((String) getTier(stack, "string")).get("lifespan");
    }

    public Object getTier(ItemStack stack, String DataType) {
        switch (DataType) {
            case "int" -> {
                String str = readNbt(stack, "tier", "string").toString();
                if (str == null || str.isEmpty()) {
                    str = material.getTier();
                }
                return tier_dict.getOrDefault(str, 0);
            }
            case "string" -> {
                Object result = readNbt(stack, "tier", "string");
                if (result == null || result.toString().isEmpty()) {
                    return material.getTier();
                }
                return result;
            }
            default -> {
                return null;
            }
        }
    }

    public Object readNbt(ItemStack stack, String key, String dataType) {
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();

        switch (dataType) {
            case "int" -> {
                return nbt.getInt(key);
            }
            case "float" -> {
                return nbt.getFloat(key);
            }
            case "string" -> {
                return nbt.getString(key);
            }
            case "long" -> {
                return nbt.getLong(key);
            }
            case "double" -> {
                return nbt.getDouble(key);
            }
            case "boolean" -> {
                return nbt.getBoolean(key);
            }
            default -> {
                return null;
            }
        }
    }

    public void writeNbt(ItemStack stack, String key, String dataType, Object value) {
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();

        switch (dataType) {
            case "int" -> nbt.putInt(key, ((Number) value).intValue());
            case "float" -> nbt.putFloat(key, ((Number) value).floatValue());
            case "string" -> nbt.putString(key, (String) value);
            case "long" -> nbt.putLong(key, ((Number) value).longValue());
            case "double" -> nbt.putDouble(key, ((Number) value).doubleValue());
            case "boolean" -> nbt.putBoolean(key, (Boolean) value);
        }

        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        if (!nbt.contains("tier") || !nbt.contains("cycles")) {
            initializeBatteryNbt(stack);
        }
        correctCharge(stack);

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    public int getTransferRate(ItemStack stack) {
        return (int) getBatteryTierProperties((String) getTier(stack, "string")).get("transferRate");
    }

    private void setCycles(ItemStack stack, int i) {
        writeNbt(stack, "cycles", "int", i);
    }

    public int recharge(ItemStack stack, int amount) {
        int accepted = getMaxCapacity(stack) - getCurrentCharge(stack);
        if (accepted >= amount) {
            accepted = amount;
        }
        int denied = Math.max(amount - accepted, 0);
        PoweredTools.LOGGER.info(String.valueOf(accepted));

        SimpleEnergyStorage energyStorage = new SimpleEnergyStorage(getMaxCapacity(stack), getTransferRate(stack), getTransferRate(stack));
        energyStorage.amount = getCurrentCharge(stack);

        try (Transaction transaction = Transaction.openOuter()) {
            energyStorage.insert(accepted, transaction);
            transaction.commit();
        }
        setCurrentCharge(stack, Math.toIntExact(energyStorage.getAmount()));

        if (energyStorage.amount == getMaxCapacity(stack)) {
            setCycles(stack, getCycles(stack) + 1);
        }

        return denied;
    }

    public int discharge(ItemStack stack, int amount) {
        int available = getCurrentCharge(stack);
        if (available >= amount) {
            available = amount;
        }
        int denied = Math.max(amount - available, 0);

        SimpleEnergyStorage energyStorage = new SimpleEnergyStorage(getMaxCapacity(stack), getTransferRate(stack), getTransferRate(stack));
        energyStorage.amount = getCurrentCharge(stack);

        try (Transaction transaction = Transaction.openOuter()) {
            energyStorage.extract(available, transaction);
            transaction.commit();
        }

        setCurrentCharge(stack, Math.toIntExact(energyStorage.getAmount()));

        if (energyStorage.amount >= getMaxCapacity(stack)) {
            setCycles(stack, getCycles(stack) + 1);
        }

        return denied;
    }

    private void correctCharge(ItemStack stack) {
        int maxCapacity = getMaxCapacity(stack);
        if (getCurrentCharge(stack) > maxCapacity) {
            setCurrentCharge(stack, maxCapacity);
        }
    }

    public int getCycles(ItemStack stack) {
        return (int) readNbt(stack, "cycles", "int");
    }

    public int getCurrentCharge(ItemStack stack) {
        return Math.toIntExact((long) readNbt(stack, "currentCharge", "long"));
    }

    public void setCurrentCharge(ItemStack stack, int charge) {
        int modded_charge = Math.max(0, Math.min(charge, getMaxCapacity(stack)));
        writeNbt(stack, "currentCharge", "long", modded_charge);
    }

    public int getMaxCapacity(ItemStack stack) {
        int max_cap = (int) getBatteryTierProperties((String) getTier(stack, "string")).get("capacity");
        float degradation = (float) getBatteryTierProperties((String) getTier(stack, "string")).get("decayRate");

        return (int) (max_cap * (1.0f - (degradation * getCycles(stack))));
    }

    public float getDecayRate(ItemStack stack) {
        return (float) getBatteryTierProperties((String) getTier(stack, "string")).get("decayRate");
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, net.minecraft.item.tooltip.TooltipType type) {
        Map<String, Number> properties = getBatteryTierProperties((String) getTier(stack, "string"));

        int currentCharge = getCurrentCharge(stack);
        int maxCapacity = getMaxCapacity(stack);
        int cycles = getCycles(stack);
        int lifespan = getBatteryLifespan(stack);

        int remainingCycles = lifespan - cycles;

        tooltip.add(Text.literal("Charge: " + currentCharge + " / " + maxCapacity).formatted(Formatting.GREEN));
        tooltip.add(Text.literal("Transfer Rate: " + properties.get("transferRate") + " per tick").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Remaining Cycles: " + remainingCycles).formatted(
            remainingCycles > lifespan * 0.5 ? Formatting.GREEN :
            remainingCycles > lifespan * 0.25 ? Formatting.YELLOW : Formatting.RED
        ));
        tooltip.add(Text.literal("Tier: " + (String) getTier(stack, "string")).formatted(Formatting.GOLD));
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
