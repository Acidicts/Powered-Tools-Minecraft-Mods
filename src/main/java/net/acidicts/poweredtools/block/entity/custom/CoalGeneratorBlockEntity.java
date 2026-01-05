package net.acidicts.poweredtools.block.entity.custom;

import net.acidicts.poweredtools.block.entity.ImplementedInventory;
import net.acidicts.poweredtools.block.entity.ModBlockEntities;
import net.acidicts.poweredtools.data.Burnables;
import net.acidicts.poweredtools.screen.custom.coal_generator.CoalGeneratorScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class CoalGeneratorBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    private static final int INPUT_SLOT = 0;

    protected final PropertyDelegate propertyDelegate;
    private Item fuelItem;
    private int burnProgress = 0;
    private int maxBurnProgress = 160;
    private boolean isBurning = false;
    private Burnables burnables = new Burnables();

    private static final int ENERGY_TRANSFER_AMOUNT = 320;

    public final SimpleEnergyStorage energyStorage = new SimpleEnergyStorage(128000, ENERGY_TRANSFER_AMOUNT, ENERGY_TRANSFER_AMOUNT) {
        @Override
        protected void onFinalCommit() {
            markDirty();
            getWorld().updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    };

    public CoalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COAL_GENERATOR_BLOCK_ENTITY, pos, state);

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> CoalGeneratorBlockEntity.this.burnProgress;
                    case 1 -> CoalGeneratorBlockEntity.this.maxBurnProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0: CoalGeneratorBlockEntity.this.burnProgress = value;
                    case 1: CoalGeneratorBlockEntity.this.maxBurnProgress = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putLong(("coal_generator_energy"), energyStorage.amount);
        nbt.putInt(("coal_generator_burn_progress"), burnProgress);
        nbt.putInt(("coal_generator_max_burn_progress"), burnProgress);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, inventory, registryLookup);
        energyStorage.amount = nbt.getLong("coal_generator_energy");
        burnProgress = nbt.getInt("coal_generator_burn_progress");
        maxBurnProgress = nbt.getInt("coal_generator_max_burn_progress");
        super.readNbt(nbt, registryLookup);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
        return this.pos;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.poweredtools.coal_generator");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CoalGeneratorScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient()) {
            return;
        }

        if (hasFuelItemInSlot()) {
            if(!isBurningFuel()) {
                startBurning();
            }
        }

        if (isBurningFuel()) {
            if (hasRoomForEnergyTick()) {
                increaseBurnTimer();
                fillUpOnEnergy();
                if (currentFuelDoneBurning()) {
                    resetBurning();
                }
            }
        }

        pushEnergyToNeighbours();
    }

    private void pushEnergyToNeighbours() {
        EnergyStorageUtil.move(this.energyStorage, EnergyStorage.SIDED.find(world, pos.up(), null), Long.MAX_VALUE, null);
        EnergyStorageUtil.move(this.energyStorage, EnergyStorage.SIDED.find(world, pos.down(), null), Long.MAX_VALUE, null);
        EnergyStorageUtil.move(this.energyStorage, EnergyStorage.SIDED.find(world, pos.east(), null), Long.MAX_VALUE, null);
        EnergyStorageUtil.move(this.energyStorage, EnergyStorage.SIDED.find(world, pos.west(), null), Long.MAX_VALUE, null);
        EnergyStorageUtil.move(this.energyStorage, EnergyStorage.SIDED.find(world, pos.north(), null), Long.MAX_VALUE, null);
        EnergyStorageUtil.move(this.energyStorage, EnergyStorage.SIDED.find(world, pos.south(), null), Long.MAX_VALUE, null);
    }

    private void fillUpOnEnergy() {
        if (fuelItem == null) {
            return;
        }

        int energyToAdd = burnables.getEnergyValue(fuelItem, this.maxBurnProgress);
        if (energyToAdd <= 0) {
            return;
        }

        try (Transaction transaction = Transaction.openOuter()){
            energyStorage.insert(energyToAdd, transaction);
            transaction.commit();
        }
    }

    private void resetBurning() {
        isBurning = false;
        fuelItem = null;
        this.burnProgress = 0;
    }

    private boolean currentFuelDoneBurning() {
        return this.burnProgress <= 0;
    }

    private void increaseBurnTimer() {
        burnProgress--;
    }

    private boolean hasRoomForEnergyTick() {
        if (fuelItem == null) {
            return false;
        }

        int energyToAdd = burnables.getEnergyValue(fuelItem, this.maxBurnProgress);
        return energyStorage.amount + energyToAdd <= energyStorage.capacity;
    }

    private void startBurning() {
        fuelItem = this.getStack(INPUT_SLOT).getItem();
        int totalEnergy = burnables.getEnergyValue(fuelItem, 1);
        this.maxBurnProgress = totalEnergy / ENERGY_TRANSFER_AMOUNT;
        if (this.maxBurnProgress == 0) {
            this.maxBurnProgress = 1;
        }
        this.burnProgress = this.maxBurnProgress;
        this.removeStack(INPUT_SLOT, 1);
        isBurning = true;
    }

    private boolean isBurningFuel() {
        return isBurning;
    }

    private boolean hasFuelItemInSlot() {
        return burnables.getEnergyValue(this.getStack(INPUT_SLOT).getItem(), 1) != 0;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
