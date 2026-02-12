package net.acidicts.poweredtools.item.custom;

import net.acidicts.poweredtools.screen.custom.powered_sword.PoweredSwordScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class PoweredSword extends SwordItem implements BatteryUsing {
    public PoweredSword(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (hasEnergy(stack, 100)) {
            consumeEnergy(stack, 100);
            super.postDamageEntity(stack, target, attacker);
        }
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
                    return new PoweredSwordScreenHandler(syncId, playerInventory, stack);
                }
            });
            return TypedActionResult.success(stack);
        }
        return super.use(world, user, hand);
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
