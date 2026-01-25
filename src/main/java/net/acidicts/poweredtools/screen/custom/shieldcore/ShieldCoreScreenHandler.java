package net.acidicts.poweredtools.screen.custom.shieldcore;

import net.acidicts.poweredtools.item.custom.BatteryItem;
import net.acidicts.poweredtools.item.custom.ShieldCore;
import net.acidicts.poweredtools.screen.ModScreenHandlers;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import team.reborn.energy.api.EnergyStorage;

public class ShieldCoreScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final ItemStack stack;
    private final PlayerInventory playerInventory;
    private final InventoryChangedListener inventoryChangeListener;

    public ShieldCoreScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, playerInventory.getMainHandStack());
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public ShieldCoreScreenHandler(int syncId, PlayerInventory playerInventory, ItemStack stack) {
        super(ModScreenHandlers.SHIELD_CORE_SCREEN_HANDLER, syncId);
        this.playerInventory = playerInventory;
        this.stack = stack;
        this.inventory = new SimpleInventory(1);
        PropertyDelegate propertyDelegate = new ArrayPropertyDelegate(2);
        this.inventoryChangeListener = this::onInventoryChanged;

        checkSize(inventory, 1);
        this.addSlot(new Slot(inventory, 0, 80, 35) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() instanceof BatteryItem;
            }
        });

        if (stack.getItem() instanceof ShieldCore shieldcore) {
            if (shieldcore.isBatteryInstalled(stack)) {
                ItemStack battery = shieldcore.extractBatteryAsItem(stack);
                shieldcore.installBattery(stack, battery, (BatteryItem) battery.getItem());
                inventory.setStack(0, battery);
            }
        }

        if (inventory instanceof SimpleInventory simpleInventory) {
            simpleInventory.addListener(inventoryChangeListener);
        }

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addProperties(propertyDelegate);
    }

    private void onInventoryChanged(Inventory sender) {
        if (stack.getItem() instanceof ShieldCore shieldcore) {
            ItemStack batteryStack = sender.getStack(0);
            if (batteryStack.isEmpty()) {
                if (shieldcore.isBatteryInstalled(stack)) {
                    shieldcore.removeBattery(stack);
                }
            } else if (batteryStack.getItem() instanceof BatteryItem batteryItem) {
                shieldcore.installBattery(stack, batteryStack, batteryItem);
            }

            this.sendContentUpdates();
        }
    }

    public EnergyStorage getEnergyStorage() {
        return new EnergyStorage() {
            @Override
            public long getAmount() {
                ItemStack battery = inventory.getStack(0);
                if (!battery.isEmpty() && battery.getItem() instanceof BatteryItem batteryItem) {
                    return batteryItem.getCurrentCharge(battery);
                }
                return 0;
            }

            @Override
            public long getCapacity() {
                ItemStack battery = inventory.getStack(0);
                if (!battery.isEmpty() && battery.getItem() instanceof BatteryItem batteryItem) {
                    return batteryItem.getMaxCapacity(battery);
                }
                return 0;
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
        return true;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        if (inventory instanceof SimpleInventory simpleInventory) {
            simpleInventory.removeListener(inventoryChangeListener);
        }

        super.onClosed(player);
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
}
