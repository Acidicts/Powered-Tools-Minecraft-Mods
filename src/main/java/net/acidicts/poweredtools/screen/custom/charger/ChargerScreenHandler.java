package net.acidicts.poweredtools.screen.custom.charger;

import net.acidicts.poweredtools.block.entity.custom.ChargerBlockEntity;
import net.acidicts.poweredtools.item.custom.BatteryItem;
import net.acidicts.poweredtools.screen.ModScreenHandlers;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import team.reborn.energy.api.EnergyStorage;

public class ChargerScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    public final ChargerBlockEntity blockEntity;

    public ChargerScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, playerInventory.player.getWorld().getBlockEntity(pos),
                new ArrayPropertyDelegate(4));
    }

    public ChargerScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlers.CHARGER_SCREEN_HANDLER, syncId);
        checkSize(((Inventory) blockEntity), 1);
        this.inventory = (Inventory)blockEntity;
        this.blockEntity = (ChargerBlockEntity) blockEntity;
        this.propertyDelegate = arrayPropertyDelegate;

        this.addSlot(new Slot(inventory, 0, 80, 35){
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() instanceof BatteryItem;
            }
        });

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addProperties(arrayPropertyDelegate);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public int getEnergy() {
        int lower = this.propertyDelegate.get(2) & 0xFFFF;
        int upper = this.propertyDelegate.get(3) & 0xFFFF;
        long combined = ((long)upper << 16) | (long)lower;
        // Debug logging
        System.out.println("ChargerScreenHandler.getEnergy() - lower: " + lower + ", upper: " + upper + ", combined: " + combined);
        // clamp to int range because screens expect int values; energy storage max is 64000 so safe
        return (int)combined;
    }

    public EnergyStorage getEnergyStorage() {
        return new EnergyStorage() {
            @Override
            public long getAmount() {
                return getEnergy();
            }

            @Override
            public long getCapacity() {
                return 64000;
            }

            @Override
            public boolean supportsInsertion() {
                return false;
            }

            @Override
            public long insert(long maxAmount, TransactionContext transaction) {
                return 0;
            }

            @Override
            public boolean supportsExtraction() {
                return false;
            }

            @Override
            public long extract(long maxAmount, TransactionContext transaction) {
                return 0;
            }
        };
    }
}