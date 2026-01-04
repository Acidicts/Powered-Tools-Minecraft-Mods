package net.acidicts.poweredtools.screen.custom.power_pickaxe;

import net.acidicts.poweredtools.item.custom.BatteryItem;
import net.acidicts.poweredtools.item.custom.Powered_Pickaxe;
import net.acidicts.poweredtools.screen.ModScreenHandlers;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import team.reborn.energy.api.EnergyStorage;

public class PoweredPickaxeScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    private final ItemStack stack;

    public PoweredPickaxeScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, playerInventory.getMainHandStack());
    }

    public PoweredPickaxeScreenHandler(int syncId, PlayerInventory playerInventory, ItemStack stack) {
        super(ModScreenHandlers.POWERED_PICKAXE_SCREEN_HANDLER, syncId);
        this.stack = stack;
        this.inventory = new SimpleInventory(4);
        this.propertyDelegate = new ArrayPropertyDelegate(2);

        checkSize(inventory, 4);

        this.addSlot(new Slot(inventory, 0, 10, 9));
        this.addSlot(new Slot(inventory, 1, 10, 33));
        this.addSlot(new Slot(inventory, 2, 10, 57));
        this.addSlot(new Slot(inventory, 3, 80, 35));

        if (stack.getItem() instanceof Powered_Pickaxe pickaxe) {
            if (pickaxe.isBatteryInstalled(stack)) {
                ItemStack battery = pickaxe.extractBatteryAsItem(stack);
                pickaxe.installBattery(stack, battery, (BatteryItem) battery.getItem());
                inventory.setStack(3, battery);
            }
        }

        if (inventory instanceof SimpleInventory simpleInventory) {
            simpleInventory.addListener(this::onInventoryChanged);
        }

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addProperties(propertyDelegate);
    }

    private void onInventoryChanged(Inventory sender) {
        if (stack.getItem() instanceof Powered_Pickaxe pickaxe) {
            ItemStack batteryStack = sender.getStack(3);
            if (batteryStack.isEmpty()) {
                if (pickaxe.isBatteryInstalled(stack)) {
                    pickaxe.removeBattery(stack);
                }
            } else if (batteryStack.getItem() instanceof BatteryItem batteryItem) {
                pickaxe.installBattery(stack, batteryStack, batteryItem);
            }
        }
    }

    public EnergyStorage getEnergyStorage() {
        return new EnergyStorage() {
            @Override
            public long getAmount() {
                ItemStack battery = inventory.getStack(3);
                if (!battery.isEmpty() && battery.getItem() instanceof BatteryItem batteryItem) {
                    return batteryItem.getCurrentCharge(battery);
                }
                return 0;
            }

            @Override
            public long getCapacity() {
                ItemStack battery = inventory.getStack(3);
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
        if (slot != null && slot.hasStack()) {
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
            simpleInventory.removeListener(this::onInventoryChanged);
        }
        // Clear the battery slot so it doesn't get dropped (it's saved in the pickaxe)
        inventory.setStack(3, ItemStack.EMPTY);

        super.onClosed(player);
        dropInventory(player, inventory);
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
