package net.acidicts.poweredtools.item.custom;

import net.acidicts.poweredtools.item.ModToolMaterials;
import net.acidicts.poweredtools.screen.custom.power_pickaxe.PoweredPickaxeScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class Powered_Pickaxe extends PickaxeItem {

    private static final int ENERGY_PER_USE = 10;

    public Powered_Pickaxe(ToolMaterial material, Settings settings) {
        super(material, settings.maxCount(1));
    }

    private void initializeBattery(ItemStack stack) {
        if (!hasBattery(stack)) {
            resetToDefaultBattery(stack);
        }
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

    private NbtCompound getBatteryData(ItemStack stack) {
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

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (hand == Hand.MAIN_HAND && !world.isClient() && user.isSneaking()) {
            user.openHandledScreen(new ExtendedScreenHandlerFactory<ItemStack>() {
                @Override
                public ItemStack getScreenOpeningData(ServerPlayerEntity player) {
                    return stack;
                }

                @Override
                public Text getDisplayName() {
                    return stack.getName();
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                    return new PoweredPickaxeScreenHandler(syncId, playerInventory, stack);
                }
            });
            return TypedActionResult.success(stack);
        }
        return super.use(world, user, hand);
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

    public ItemStack extractBatteryAsItem(ItemStack pickaxeStack) {
        NbtCompound pickaxeNbt = getBatteryData(pickaxeStack);

        String tier = pickaxeNbt.getString("battery_tier");

        Item batteryItemType = getBatteryItemByTier(tier);
        ItemStack batteryStack = new ItemStack(batteryItemType);

        NbtCompound batteryNbt = new NbtCompound();
        batteryNbt.putInt("cycles", pickaxeNbt.getInt("battery_cycles"));
        batteryNbt.putLong("currentCharge", pickaxeNbt.getInt("battery_charge"));
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

    public void installBattery(ItemStack pickaxeStack, ItemStack batteryStack, BatteryItem batteryItem) {
        NbtCompound pickaxeNbt = getBatteryData(pickaxeStack);

        pickaxeNbt.putString("battery_tier", (String) batteryItem.getTier(batteryStack, "string"));
        pickaxeNbt.putInt("battery_capacity", batteryItem.getMaxCapacity(batteryStack));
        pickaxeNbt.putInt("battery_transfer_rate", batteryItem.getTransferRate(batteryStack));
        pickaxeNbt.putInt("battery_lifespan", batteryItem.getBatteryLifespan(batteryStack));
        pickaxeNbt.putFloat("battery_decay_rate", batteryItem.getDecayRate(batteryStack));
        pickaxeNbt.putBoolean("battery_installed", true);

        int cycles = batteryItem.getCycles(batteryStack);
        int charge = batteryItem.getCurrentCharge(batteryStack);

        pickaxeNbt.putInt("battery_cycles", cycles);
        pickaxeNbt.putInt("battery_charge", charge);

        setBatteryData(pickaxeStack, pickaxeNbt);
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

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient && state.getHardness(world, pos) != 0.0F) {
            int currentEnergy = getEnergy(stack);
            if (currentEnergy >= ENERGY_PER_USE) {
                setEnergy(stack, currentEnergy - ENERGY_PER_USE);
            }
        }
        return false;
    }

    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        if (getEnergy(stack) < ENERGY_PER_USE) {
            return 0.0F;
        }
        return super.getMiningSpeed(stack, state);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        ItemStack stack = miner.getMainHandStack();
        return getEnergy(stack) >= ENERGY_PER_USE && super.canMine(state, world, pos, miner);
    }

    @SuppressWarnings("unused")
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

    public void setCycles(ItemStack stack, int cycles){
        NbtCompound nbt = getBatteryData(stack);
        nbt.putInt("battery_cycles", cycles);
        setBatteryData(stack, nbt);
    }

    public int getCycles(ItemStack stack){
        NbtCompound nbt = getBatteryData(stack);
        return nbt.getInt("battery_cycles");
    }

    private int getCurrentCharge(ItemStack stack){
        NbtCompound nbt = getBatteryData(stack);
        return nbt.getInt("battery_charge");
    }

    public void setCurrentCharge(ItemStack stack, int charge){
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

            setCycles(stack, getCycles(stack)+ 1);
            int newMaxCapacity = getMaxCapacity(stack);
            if (newCharge > newMaxCapacity) {
                setCurrentCharge(stack, newMaxCapacity);
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        NbtCompound nbt = getBatteryData(stack);

        if (nbt.getBoolean("battery_installed")) {
            int energy = getEnergy(stack);
            int maxCapacity = getMaxCapacity(stack);
            String tier = nbt.getString("battery_tier");
            int cycles = nbt.getInt("battery_cycles");
            int lifespan = nbt.getInt("battery_lifespan");
            int remainingCycles = lifespan - cycles;

            tooltip.add(Text.literal("Energy: " + energy + " / " + maxCapacity)
                    .formatted(energy > maxCapacity * 0.5 ? Formatting.GREEN :
                            energy > maxCapacity * 0.25 ? Formatting.YELLOW : Formatting.RED));
            tooltip.add(Text.literal("Battery Tier: " + tier).formatted(Formatting.GOLD));
            tooltip.add(Text.literal("Remaining Battery Cycles: " + remainingCycles).formatted(
                    remainingCycles > lifespan * 0.5 ? Formatting.GREEN :
                            remainingCycles > lifespan * 0.25 ? Formatting.YELLOW : Formatting.RED
            ));
            tooltip.add(Text.literal("Sneak + right-click to open GUI").formatted(Formatting.GRAY).formatted(Formatting.ITALIC));
        } else {
            tooltip.add(Text.literal("No battery installed").formatted(Formatting.RED));
            tooltip.add(Text.literal("Sneak + right-click to open GUI").formatted(Formatting.GRAY).formatted(Formatting.ITALIC));
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
}
