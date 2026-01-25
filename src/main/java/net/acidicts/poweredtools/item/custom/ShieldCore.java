package net.acidicts.poweredtools.item.custom;

import net.acidicts.poweredtools.screen.custom.shieldcore.ShieldCoreScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public abstract class ShieldCore extends BatteryUsingItem {
    public ShieldCore(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return false;
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
                    return new ShieldCoreScreenHandler(syncId, playerInventory, stack);
                }
            });
            return TypedActionResult.success(stack);
        } else if (hand==Hand.MAIN_HAND) {
            return coreAbilityUse(world, user, hand, stack);
        }
        return super.use(world, user, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
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
        }
    }

    public TypedActionResult<ItemStack> coreAbilityUse(World world, PlayerEntity user, Hand hand, ItemStack stack) {
        return TypedActionResult.pass(stack);
    }

    public boolean canDamageTick(ItemStack stack, Entity entity, DamageSource source) {
        return true;
    }

    public boolean isActive(ItemStack stack) {
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData != null) {
            return customData.copyNbt().getBoolean("IsActive");
        }
        return false;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        if (isActive(stack)) {
            return true;
        }
        return super.hasGlint(stack);
    }
}
