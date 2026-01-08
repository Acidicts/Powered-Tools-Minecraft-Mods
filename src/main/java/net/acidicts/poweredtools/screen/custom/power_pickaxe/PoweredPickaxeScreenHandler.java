package net.acidicts.poweredtools.screen.custom.power_pickaxe;

import net.acidicts.poweredtools.PoweredTools;
import net.acidicts.poweredtools.item.ModItems;
import net.acidicts.poweredtools.item.custom.BatteryItem;
import net.acidicts.poweredtools.item.custom.Powered_Pickaxe;
import net.acidicts.poweredtools.screen.ModScreenHandlers;
import net.acidicts.poweredtools.tags.ModTags;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.inventory.InventoryChangedListener;
import team.reborn.energy.api.EnergyStorage;

public class PoweredPickaxeScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final ItemStack stack;
    private final PlayerInventory playerInventory;
    private final InventoryChangedListener inventoryChangeListener;

    public PoweredPickaxeScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, playerInventory.getMainHandStack());
    }

    public PoweredPickaxeScreenHandler(int syncId, PlayerInventory playerInventory, ItemStack stack) {
        super(ModScreenHandlers.POWERED_PICKAXE_SCREEN_HANDLER, syncId);
        this.playerInventory = playerInventory;
        this.stack = stack;
        this.inventory = new SimpleInventory(4);
        PropertyDelegate propertyDelegate = new ArrayPropertyDelegate(2);
        this.inventoryChangeListener = this::onInventoryChanged;

        checkSize(inventory, 4);

        this.addSlot(new Slot(inventory, 0, 10, 9){
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(ModItems.EFFICIENCY_MODIFIER);
            }
        });
        this.addSlot(new Slot(inventory, 1, 10, 33){
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(ModItems.FORTUNE_MODIFIER);
            }
        });
        this.addSlot(new Slot(inventory, 2, 10, 57) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(ModItems.SILK_TOUCH_MODIFIER);
            }
        });
        this.addSlot(new Slot(inventory, 3, 80, 35) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() instanceof BatteryItem;
            }
        });

        if (stack.getItem() instanceof Powered_Pickaxe pickaxe) {
            if (pickaxe.isBatteryInstalled(stack)) {
                ItemStack battery = pickaxe.extractBatteryAsItem(stack);
                pickaxe.installBattery(stack, battery, (BatteryItem) battery.getItem());
                inventory.setStack(3, battery);
            }
            if (pickaxe.getSpeedModifiers(stack) > 0) {
                ItemStack modifier = ModItems.EFFICIENCY_MODIFIER.getDefaultStack();
                modifier.setCount(pickaxe.getSpeedModifiers(stack));
                inventory.setStack(0, modifier);
            }
            if (pickaxe.getFortuneModifiers(stack) > 0) {
                ItemStack modifier = ModItems.FORTUNE_MODIFIER.getDefaultStack();
                modifier.setCount(pickaxe.getFortuneModifiers(stack));
                inventory.setStack(1, modifier);
            }
            if (pickaxe.getSilkTouchModifier(stack) > 0) {
                ItemStack modifier = ModItems.SILK_TOUCH_MODIFIER.getDefaultStack();
                modifier.setCount(pickaxe.getSilkTouchModifier(stack));
                inventory.setStack(2, modifier);
            }
            PoweredTools.LOGGER.info("{} {} {}", pickaxe.getSpeedModifiers(stack), pickaxe.getFortuneModifiers(stack), pickaxe.getSilkTouchModifier(stack));
            syncEnchantments();
        }

        if (inventory instanceof SimpleInventory simpleInventory) {
            simpleInventory.addListener(inventoryChangeListener);
        }

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addProperties(propertyDelegate);
    }

    private void onInventoryChanged(Inventory sender) {
        if (stack.getItem() instanceof Powered_Pickaxe pickaxe) {
            ItemStack efficiencyStack = sender.getStack(0);
            ItemStack fortuneStack = sender.getStack(1);
            ItemStack modifierStack = sender.getStack(2);
            ItemStack batteryStack = sender.getStack(3);
            if (batteryStack.isEmpty()) {
                if (pickaxe.isBatteryInstalled(stack)) {
                    pickaxe.removeBattery(stack);
                }
            } else if (batteryStack.getItem() instanceof BatteryItem batteryItem) {
                pickaxe.installBattery(stack, batteryStack, batteryItem);
            }

            if (efficiencyStack.isEmpty()) {
                pickaxe.setSpeedModifier(stack, 0);
            } else if (efficiencyStack.isOf(ModItems.EFFICIENCY_MODIFIER)) {
                pickaxe.setSpeedModifier(stack, efficiencyStack.getCount());
            }
            if (fortuneStack.isEmpty()) {
                pickaxe.setFortuneModifier(stack, 0);
            } else if (fortuneStack.isOf(ModItems.FORTUNE_MODIFIER)) {
                pickaxe.setFortuneModifier(stack, fortuneStack.getCount());
            }
            if (modifierStack.isEmpty()) {
                pickaxe.setSilkTouchModifier(stack, 0);
            } else if (modifierStack.isOf(ModItems.SILK_TOUCH_MODIFIER)) {
                pickaxe.setSilkTouchModifier(stack, modifierStack.getCount());
                pickaxe.setModifierType(stack, modifierStack.getItem().toString());
            } else {
                pickaxe.setSilkTouchModifier(stack, 0);
            }

            syncEnchantments();
            this.sendContentUpdates();
        }
    }

    private void syncEnchantments() {
        if (stack.getItem() instanceof Powered_Pickaxe pickaxe) {
            var registry = playerInventory.player.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT);
            if (registry == null) return;
            ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);

            addEnchantment(builder, registry, Enchantments.EFFICIENCY, pickaxe.getSpeedModifiers(stack));
            addEnchantment(builder, registry, Enchantments.FORTUNE, pickaxe.getFortuneModifiers(stack));
            addEnchantment(builder, registry, Enchantments.SILK_TOUCH, pickaxe.getSilkTouchModifier(stack));

            stack.set(DataComponentTypes.ENCHANTMENTS, builder.build().withShowInTooltip(false));
            this.sendContentUpdates();
        }
    }

    private void addEnchantment(ItemEnchantmentsComponent.Builder builder, Registry<Enchantment> registry, RegistryKey<Enchantment> key, int level) {
        if (level > 0) {
            registry.getEntry(key).ifPresent(entry -> builder.set(entry, level));
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
