package net.acidicts.poweredtools.screen.custom.powered_sword;

import net.acidicts.poweredtools.PoweredTools;
import net.acidicts.poweredtools.item.ModItems;
import net.acidicts.poweredtools.item.custom.BatteryItem;
import net.acidicts.poweredtools.item.custom.PoweredSword;
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
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import team.reborn.energy.api.EnergyStorage;

import java.util.HashMap;
import java.util.Map;

public class PoweredSwordScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final ItemStack stack;
    private final PlayerInventory playerInventory;
    private final InventoryChangedListener inventoryChangeListener;

    public PoweredSwordScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, playerInventory.getMainHandStack());
    }

    public PoweredSwordScreenHandler(int syncId, PlayerInventory playerInventory, ItemStack stack) {
        super(ModScreenHandlers.POWERED_SWORD_SCREEN_HANDLER, syncId);
        this.playerInventory = playerInventory;
        this.stack = stack;
        this.inventory = new SimpleInventory(4);
        PropertyDelegate propertyDelegate = new ArrayPropertyDelegate(2);
        this.inventoryChangeListener = this::onInventoryChanged;

        checkSize(inventory, 4);

        this.addSlot(new Slot(inventory, 0, 10, 9){
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(ModItems.SHARPNESS_MODIFIER);
            }
        });
        this.addSlot(new Slot(inventory, 1, 10, 33){
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isIn(ModTags.Items.SwordModifierItems);
            }
        });
        this.addSlot(new Slot(inventory, 2, 10, 57) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isIn(ModTags.Items.SwordModifierItems);
            }
        });
        this.addSlot(new Slot(inventory, 3, 80, 35) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() instanceof BatteryItem;
            }
        });

        if (stack.getItem() instanceof PoweredSword sword) {
            if (sword.isBatteryInstalled(stack)) {
                ItemStack battery = sword.extractBatteryAsItem(stack);
                sword.installBattery(stack, battery, (BatteryItem) battery.getItem());
                inventory.setStack(3, battery);
            }
            if (sword.getSharpnessModifier(stack) > 0) {
                ItemStack modifier = ModItems.SHARPNESS_MODIFIER.getDefaultStack();
                modifier.setCount(sword.getSharpnessModifier(stack));
                inventory.setStack(0, modifier);
            }

            String genericType = sword.getSwordGenericModifierType(stack, 0);
            int genericNum = sword.getSwordGenericModifierNum(stack, 0);
            if (!genericType.isEmpty() && genericNum > 0) {
                Identifier id = Identifier.tryParse(genericType);
                if (id != null) {
                    Item item = Registries.ITEM.get(id);
                    if (item != Items.AIR) {
                        ItemStack modifier = new ItemStack(item, genericNum);
                        inventory.setStack(1, modifier);
                    }
                }
            }

            String genericTypeTwo = sword.getSwordGenericModifierType(stack, 1);
            int genericNumTwo = sword.getSwordGenericModifierNum(stack, 1);
            if (!genericTypeTwo.isEmpty() && genericNumTwo > 0) {
                Identifier id = Identifier.tryParse(genericTypeTwo);
                if (id != null) {
                    Item item = Registries.ITEM.get(id);
                    if (item != Items.AIR) {
                        ItemStack modifier = new ItemStack(item, genericNumTwo);
                        inventory.setStack(2, modifier);
                    }
                }
            }

            PoweredTools.LOGGER.info("Sharpness: {}, Modifier1: {}, Modifier2: {}", sword.getSharpnessModifier(stack), genericNum, genericNumTwo);
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
        if (stack.getItem() instanceof PoweredSword sword) {
            ItemStack sharpnessStack = sender.getStack(0);
            ItemStack modifierStackOne = sender.getStack(1);
            ItemStack modifierStackTwo = sender.getStack(2);
            ItemStack batteryStack = sender.getStack(3);
            if (batteryStack.isEmpty()) {
                if (sword.isBatteryInstalled(stack)) {
                    sword.removeBattery(stack);
                }
            } else if (batteryStack.getItem() instanceof BatteryItem batteryItem) {
                sword.installBattery(stack, batteryStack, batteryItem);
            }

            if (sharpnessStack.isEmpty()) {
                sword.setSharpnessModifier(stack, 0);
            } else if (sharpnessStack.isOf(ModItems.SHARPNESS_MODIFIER)) {
                sword.setSharpnessModifier(stack, sharpnessStack.getCount());
            }

            if (modifierStackOne.isEmpty()) {
                sword.setSwordGenericModifierType(stack, 0, "");
                sword.setSwordGenericModifierAmount(stack, 0, 0);
            } else {
                sword.setSwordGenericModifierType(stack, 0, Registries.ITEM.getId(modifierStackOne.getItem()).toString());
                sword.setSwordGenericModifierAmount(stack, 0, modifierStackOne.getCount());
            }

            if (modifierStackTwo.isEmpty()) {
                sword.setSwordGenericModifierType(stack, 1, "");
                sword.setSwordGenericModifierAmount(stack, 1, 0);
            } else {
                sword.setSwordGenericModifierType(stack, 1, Registries.ITEM.getId(modifierStackTwo.getItem()).toString());
                sword.setSwordGenericModifierAmount(stack, 1, modifierStackTwo.getCount());
            }

            syncEnchantments();
            this.sendContentUpdates();
        }
    }

    private void syncEnchantments() {
        if (stack.getItem() instanceof PoweredSword sword) {
            var registry = playerInventory.player.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT);
            if (registry == null) return;
            ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);

            addEnchantment(builder, registry, Enchantments.SHARPNESS, sword.getSharpnessModifier(stack));

            Map<RegistryKey<Enchantment>, Integer> genericLevels = new HashMap<>();
            collectGenericModifierLevels(genericLevels, sword, sword.getSwordGenericModifierType(stack, 0),
                    sword.getSwordGenericModifierNum(stack, 0));
            collectGenericModifierLevels(genericLevels, sword, sword.getSwordGenericModifierType(stack, 1),
                    sword.getSwordGenericModifierNum(stack, 1));

            for (Map.Entry<RegistryKey<Enchantment>, Integer> entry : genericLevels.entrySet()) {
                addEnchantment(builder, registry, entry.getKey(), entry.getValue());
            }

            stack.set(DataComponentTypes.ENCHANTMENTS, builder.build().withShowInTooltip(false));
            this.sendContentUpdates();
        }
    }

    private void collectGenericModifierLevels(Map<RegistryKey<Enchantment>, Integer> levels, PoweredSword sword,
                                              String genericType, int genericNum) {
        if (!genericType.isEmpty() && genericNum > 0) {
            RegistryKey<Enchantment> enchantKey = sword.getEnchantmentFromGenericType(genericType);
            if (enchantKey != null) {
                levels.merge(enchantKey, genericNum, Math::max);
            }
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
