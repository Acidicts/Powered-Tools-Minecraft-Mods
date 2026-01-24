package net.acidicts.poweredtools.item.custom;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class BatteryUsingItem extends Item {

    public BatteryUsingItem(Settings settings) {
        super(settings);
    }

    private void initializeBattery(ItemStack stack) {
        if (!hasBattery(stack)) {
            resetToDefaultBattery(stack);
        }
    }

    public boolean hasEnergy(ItemStack stack, int amount) {
        NbtCompound nbt = getBatteryData(stack);
        if (!nbt.getBoolean("battery_installed")) {
            return false;
        }
        int currentCharge = nbt.getInt("battery_charge");
        return currentCharge >= amount;
    }

    public boolean consumeEnergy(ItemStack stack, int amount) {
        NbtCompound nbt = getBatteryData(stack);
        if (!nbt.getBoolean("battery_installed")) {
            return false;
        }
        int currentCharge = nbt.getInt("battery_charge");
        currentCharge = Math.max(0, currentCharge - amount);
        nbt.putInt("battery_charge", currentCharge);
        setBatteryData(stack, nbt);
        return true;
    }

    private void resetToDefaultBattery(ItemStack stack) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("battery_tier", "Stone");
        nbt.putInt("battery_capacity", 100);
        nbt.putInt("battery_transfer_rate", 25);
        nbt.putInt("battery_lifespan", 5);
        nbt.putFloat("battery_decay_rate", 0.04f);
        nbt.putInt("battery_cycles", 0);
        nbt.putInt("battery_charge", 100);
        nbt.putBoolean("battery_installed", true);

        nbt.putInt("pickaxe.modifier.speed", 0);
        nbt.putInt("pickaxe.modifier.fortune", 0);
        nbt.putInt("pickaxe.modifier.silk_touch", 0);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    private boolean hasBattery(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent == null) {
            return false;
        }
        return nbtComponent.copyNbt().contains("battery_tier");
    }

    public NbtCompound getBatteryData(ItemStack stack) {
        initializeBattery(stack);
        NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        return nbtComponent.copyNbt();
    }

    private void setBatteryData(ItemStack stack, NbtCompound nbt) {
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    public int getEnergy(ItemStack stack) {
        NbtCompound nbt = getBatteryData(stack);
        if (!nbt.getBoolean("battery_installed")) {
            return 0;
        }
        return nbt.getInt("battery_charge");
    }

    public void setEnergy(ItemStack stack, int energy) {
        NbtCompound nbt = getBatteryData(stack);
        if (!nbt.getBoolean("battery_installed")) {
            return;
        }
        int maxCapacity = getMaxCapacity(stack);
        energy = Math.max(0, Math.min(energy, maxCapacity));
        nbt.putInt("battery_charge", energy);
        setBatteryData(stack, nbt);
    }

    public int getMaxCapacity(ItemStack stack) {
        NbtCompound nbt = getBatteryData(stack);
        if (!nbt.getBoolean("battery_installed")) {
            return 0;
        }
        int baseCapacity = nbt.getInt("battery_capacity");
        int cycles = nbt.getInt("battery_cycles");
        float decayRate = nbt.getFloat("battery_decay_rate");
        return (int) (baseCapacity * (1.0f - (decayRate * cycles)));
    }

    public boolean isBatteryInstalled(ItemStack stack) {
        NbtCompound nbt = getBatteryData(stack);
        return nbt.getBoolean("battery_installed");
    }

    public void removeBattery(ItemStack stack) {
        NbtCompound nbt = getBatteryData(stack);
        nbt.putBoolean("battery_installed", false);
        setBatteryData(stack, nbt);
    }

    public ItemStack extractBatteryAsItem(ItemStack itemStack) {
        NbtCompound itemNbt = getBatteryData(itemStack);

        String tier = itemNbt.getString("battery_tier");

        Item batteryItemType = getBatteryItemByTier(tier);
        ItemStack batteryStack = new ItemStack(batteryItemType);

        NbtCompound batteryNbt = new NbtCompound();
        batteryNbt.putInt("cycles", itemNbt.getInt("battery_cycles"));
        batteryNbt.putLong("currentCharge", itemNbt.getInt("battery_charge"));
        batteryStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(batteryNbt));

        return batteryStack;
    }

    private Item getBatteryItemByTier(String tier) {
        return switch (tier.toLowerCase()) {
            case "iron" -> net.acidicts.poweredtools.item.ModItems.BATTERY_TIER_1;
            case "gold" -> net.acidicts.poweredtools.item.ModItems.BATTERY_TIER_2;
            case "diamond" -> net.acidicts.poweredtools.item.ModItems.BATTERY_TIER_3;
            case "netherite" -> net.acidicts.poweredtools.item.ModItems.BATTERY_TIER_4;
            case "diamond_gold" -> net.acidicts.poweredtools.item.ModItems.BATTERY_TIER_5;
            default -> net.acidicts.poweredtools.item.ModItems.BATTERY_TIER_0;
        };
    }

    public void installBattery(ItemStack itemStack, ItemStack batteryStack, BatteryItem batteryItem) {
        NbtCompound itemNbt = getBatteryData(itemStack);

        itemNbt.putString("battery_tier", (String) batteryItem.getTier(batteryStack, "string"));
        itemNbt.putInt("battery_capacity", batteryItem.getMaxCapacity(batteryStack));
        itemNbt.putInt("battery_transfer_rate", batteryItem.getTransferRate(batteryStack));
        itemNbt.putInt("battery_lifespan", batteryItem.getBatteryLifespan(batteryStack));
        itemNbt.putFloat("battery_decay_rate", batteryItem.getDecayRate(batteryStack));
        itemNbt.putBoolean("battery_installed", true);

        int cycles = batteryItem.getCycles(batteryStack);
        int charge = batteryItem.getCurrentCharge(batteryStack);

        itemNbt.putInt("battery_cycles", cycles);
        itemNbt.putInt("battery_charge", charge);

        setBatteryData(itemStack, itemNbt);
    }

    public void addEnergy(ItemStack stack, int amount) {
        int currentEnergy = getEnergy(stack);
        setEnergy(stack, currentEnergy + amount);
    }

    public int cyclesLeft(ItemStack stack) {
        NbtCompound nbt = getBatteryData(stack);
        int cycles = nbt.getInt("battery_cycles");
        int lifespan = nbt.getInt("battery_lifespan");
        return lifespan - cycles;
    }

    public int getSpeedModifiers(ItemStack stack) {
        NbtCompound nbt = getBatteryData(stack);
        return nbt.getInt("pickaxe.modifier.speed");
    }

    public void setSpeedModifier(ItemStack stack, int number) {
        NbtCompound nbt = getBatteryData(stack);
        nbt.putInt("pickaxe.modifier.speed", number);
        setBatteryData(stack, nbt);
    }

    public int getFortuneModifiers(ItemStack stack) {
        NbtCompound nbt = getBatteryData(stack);
        return nbt.getInt("pickaxe.modifier.fortune");
    }

    public void setFortuneModifier(ItemStack stack, int number) {
        NbtCompound nbt = getBatteryData(stack);
        nbt.putInt("pickaxe.modifier.fortune", number);
        setBatteryData(stack, nbt);
    }

    public int getSilkTouchModifier(ItemStack stack) {
        NbtCompound nbt = getBatteryData(stack);
        return nbt.getInt("pickaxe.modifier.silk_touch");
    }

    public void setSilkTouchModifier(ItemStack stack, int number) {
        NbtCompound nbt = getBatteryData(stack);
        nbt.putInt("pickaxe.modifier.silk_touch", number);
        setBatteryData(stack, nbt);
    }

    public String getGenericModifierType(ItemStack stack) {
        NbtCompound nbt = getBatteryData(stack);
        return nbt.getString("pickaxe.modifier.modifier.type");
    }

    public int getGenericModifierNum(ItemStack stack) {
        NbtCompound nbt = getBatteryData(stack);
        return nbt.getInt("pickaxe.modifier.modifier.num");
    }

    public void setGenericModifierType(ItemStack stack, String str) {
        NbtCompound nbt = getBatteryData(stack);
        nbt.putString("pickaxe.modifier.modifier.type", str);
        setBatteryData(stack, nbt);
    }

    public void setGenericModifierAmount(ItemStack stack, int num) {
        NbtCompound nbt = getBatteryData(stack);
        nbt.putInt("pickaxe.modifier.modifier.num", num);
        setBatteryData(stack, nbt);
    }

    public void setCycles(ItemStack stack, int cycles) {
        NbtCompound nbt = getBatteryData(stack);
        nbt.putInt("battery_cycles", cycles);
        setBatteryData(stack, nbt);
    }

    public int getCycles(ItemStack stack) {
        NbtCompound nbt = getBatteryData(stack);
        return nbt.getInt("battery_cycles");
    }

    private int getCurrentCharge(ItemStack stack) {
        NbtCompound nbt = getBatteryData(stack);
        return nbt.getInt("battery_charge");
    }

    public void setCurrentCharge(ItemStack stack, int charge) {
        NbtCompound nbt = getBatteryData(stack);
        nbt.putInt("battery_charge", charge);
        setBatteryData(stack, nbt);
    }

    public void recharge(ItemStack stack, int amount) {
        int currentCharge = getCurrentCharge(stack);
        int maxCapacity = getMaxCapacity(stack);
        int newCharge = Math.min(maxCapacity, currentCharge + amount);

        if (cyclesLeft(stack) > 0) {
            int currentEnergy = getEnergy(stack);
            setEnergy(stack, currentEnergy + amount);

            setCycles(stack, getCycles(stack) + 1);
            int newMaxCapacity = getMaxCapacity(stack);
            if (newCharge > newMaxCapacity) {
                setCurrentCharge(stack, newMaxCapacity);
            }
        }
    }

    public RegistryKey<Enchantment> getEnchantmentFromGenericType(String genericType) {
        if (genericType == null || genericType.isEmpty()) return null;
        Identifier id = Identifier.tryParse(genericType);
        if (id == null) return null;

        String path = id.getPath();
        if (path.endsWith("_modifier")) {
            String enchantName = path.substring(0, path.length() - "_modifier".length());
            // Mapping for standard vanilla enchantments provided by mod items
            if (enchantName.equals("silk_touch")) return Enchantments.SILK_TOUCH;
            // For others, we assume minecraft namespace for now as per common vanilla enchants
            return RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of("minecraft", enchantName));
        }
        return null;
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
