package net.acidicts.poweredtools.block.entity.custom;

import net.acidicts.poweredtools.block.entity.ModBlockEntities;
import net.acidicts.poweredtools.item.custom.BatteryItem;
import net.acidicts.poweredtools.screen.custom.charger.ChargerScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.acidicts.poweredtools.block.entity.ImplementedInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class ChargerBlockEntity extends BlockEntity implements GeoBlockEntity, ExtendedScreenHandlerFactory<BlockPos>, ImplementedInventory {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private boolean hasOpened = false;
    private boolean animationSet = false;
    private int tickCounter = 0;
    private static final int ANIMATION_DURATION_TICKS = 40; // Approximately 2 seconds (20 ticks/second)

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 5;

    int ENERGY_TRANSFER_AMOUNT = 16384;

    public final SimpleEnergyStorage energyStorage = new SimpleEnergyStorage(64000, ENERGY_TRANSFER_AMOUNT, ENERGY_TRANSFER_AMOUNT) {
        @Override
        protected void onFinalCommit() {
            markDirty();
            if (getWorld() != null) {
                getWorld().updateListeners(pos, getCachedState(), getCachedState(), 3);
            }
        }
    };

    public ChargerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                int value = switch (index) {
                    case 0 -> ChargerBlockEntity.this.progress;
                    case 1 -> ChargerBlockEntity.this.maxProgress;
                    case 2 -> (int) (ChargerBlockEntity.this.energyStorage.amount & 0xFFFFL);
                    case 3 -> (int) ((ChargerBlockEntity.this.energyStorage.amount >> 16) & 0xFFFFL);
                    default -> 0;
                };
                if (index == 2 || index == 3) {
                    System.out.println("PropertyDelegate.get(" + index + ") = " + value + " (energy.amount=" + ChargerBlockEntity.this.energyStorage.amount + ")");
                }
                return value;
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> ChargerBlockEntity.this.progress = value;
                    case 1 -> ChargerBlockEntity.this.maxProgress = value;
                    case 2 -> {
                        long current = ChargerBlockEntity.this.energyStorage.amount;
                        // Clear the lower 16 bits and set them to value
                        ChargerBlockEntity.this.energyStorage.amount = (current & ~0xFFFFL) | (value & 0xFFFFL);
                    }
                    case 3 -> {
                        long current = ChargerBlockEntity.this.energyStorage.amount;
                        // Clear bits 16-31 and set them from value
                        ChargerBlockEntity.this.energyStorage.amount = (current & 0xFFFFL) | ((value & 0xFFFFL) << 16);
                    }
                }
            }

            @Override
            public int size() {
                return 4;
            }
        };
    }

    public static ChargerBlockEntity createFromNbt(BlockPos pos, BlockState state, NbtCompound nbt) {
        ChargerBlockEntity entity = new ChargerBlockEntity(pos, state);
        entity.readNbt(nbt, null);
        return entity;
    }

    public ChargerBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.CHARGER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.poweredtools.charger");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ChargerScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return this.pos;
    }

    public void tick() {
        if (world == null || world.isClient) return;

        // Server-side logic to mark as opened when space is available and animation completes
        if (!hasOpened && hasSpaceToOpen()) {
            tickCounter++;
            if (tickCounter >= ANIMATION_DURATION_TICKS) {
                setHasOpened(true);
            }
        } else if (!hasSpaceToOpen()) {
            // Reset counter if space becomes unavailable
            tickCounter = 0;
        }

        checkCharge();
    }

    public void checkCharge() {
        if (inventory.getFirst().isEmpty()) {
            progress = 0;
            return;
        }

        ItemStack itemStack = inventory.getFirst();

        if (itemStack.getItem() instanceof net.acidicts.poweredtools.item.custom.BatteryItem batteryItem) {
            int energyInItem = batteryItem.getCurrentCharge(itemStack);
            if (energyInItem < batteryItem.getMaxCapacity(itemStack)) {

                progress++;
                if (progress >= maxProgress) {
                    chargeItem(itemStack);
                    progress = 0; // Reset progress after a full charge cycle
                }
            }
        }
    }

    public void chargeItem(ItemStack itemStack) {
        if (itemStack.getItem() instanceof BatteryItem batteryItem) {
            int maxReceive = batteryItem.getMaxReceive(itemStack);
            int space = batteryItem.getMaxCapacity(itemStack) - batteryItem.getCurrentCharge(itemStack);
            int toTransfer = Math.min(maxReceive, space);
            toTransfer = Math.min(toTransfer, ENERGY_TRANSFER_AMOUNT);

            if (toTransfer <= 0) return;

            try (Transaction transaction = Transaction.openOuter()) {
                long extracted = energyStorage.extract(toTransfer, transaction);
                if (extracted > 0) {
                    batteryItem.recharge(itemStack, (int) extracted);
                    transaction.commit();
                }
            }
        }
    }

    public boolean hasSpaceToOpen() {
        if (world == null) return false;

        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos adjacentPos = pos.offset(direction);
            BlockState adjacentState = world.getBlockState(adjacentPos);

            if (!adjacentState.isAir()) {
                return false;
            }
        }

        BlockPos abovePos = pos.up();
        BlockState aboveState = world.getBlockState(abovePos);

        if (!aboveState.isAir()) {
            return false;
        }

        return true;
    }

    private PlayState predicate(AnimationState<ChargerBlockEntity> state) {
        if (hasOpened()) {
            state.getController().setAnimation(RawAnimation.begin().then("Opened", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;

        } else {
            if (hasSpaceToOpen()) {
                if (!state.getController().hasAnimationFinished()) {
                    state.getController().setAnimation(RawAnimation.begin().then("Open", Animation.LoopType.HOLD_ON_LAST_FRAME));
                    return PlayState.CONTINUE;
                } else {
                    if (!hasOpened()){
                        setHasOpened(true);
                    }
                    return PlayState.STOP;
                }
            } else {
                state.getController().forceAnimationReset();
                state.getController().stop();
                return PlayState.STOP;
            }
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public boolean hasOpened() {
        return hasOpened;
    }

    public void setHasOpened(boolean hasOpened) {
        this.hasOpened = hasOpened;
        markDirty(); // Mark dirty to ensure NBT is saved to disk
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3); // Sync to client
        }
    }

    public NbtCompound getNbtData() {
        NbtCompound nbt = new NbtCompound();
        if (world != null) {
            writeNbt(nbt, world.getRegistryManager());
        } else {
            writeNbt(nbt, null);
        }
        return nbt;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putBoolean("HasOpened", hasOpened);
        nbt.putInt("Charger.progress", progress);
        nbt.putLong("Charger.energy", energyStorage.amount);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
        hasOpened = nbt.getBoolean("HasOpened");
        progress = nbt.getInt("Charger.progress");
        energyStorage.amount = nbt.getLong("Charger.energy");
        // If it was previously opened, mark animation as already set to prevent restart
        if (hasOpened) {
            animationSet = true;
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        // This method is called to sync data to the client when the chunk loads
        NbtCompound nbt = super.toInitialChunkDataNbt(registryLookup);
        nbt.putBoolean("HasOpened", hasOpened);
        nbt.putLong("Charger.energy", energyStorage.amount);
        return nbt;
    }

    @Override
    public net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket toUpdatePacket() {
        // This method is called when the block entity state changes and needs to be synced
        return net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }
}
