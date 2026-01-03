package net.acidicts.poweredtools.block.entity.custom;

import net.acidicts.poweredtools.block.custom.AlloySmelter;
import net.acidicts.poweredtools.block.entity.ImplementedInventory;
import net.acidicts.poweredtools.block.entity.ModBlockEntities;
import net.acidicts.poweredtools.recipe.ModRecipes;
import net.acidicts.poweredtools.recipe.alloy_smelter.AlloySmelterRecipe;
import net.acidicts.poweredtools.recipe.alloy_smelter.AlloySmelterRecipeInput;
import net.acidicts.poweredtools.screen.custom.alloy_smelter.recycler.AlloySmelterScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.Map;
import java.util.Optional;

public class AlloySmelterBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);

    private static final int INPUT_SLOT_0 = 0;
    private static final int INPUT_SLOT_1 = 1;
    private static final int INPUT_SLOT_2 = 2;
    private static final int OUTPUT_SLOT = 3;
    private static final int Energy_ITEM_SLOT = 4;

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 72;
    private final int DEFAULT_MAX_PROGRESS = 72;

    private static final int ENERGY_CRAFTING_AMOUNT = 50;
    private static final int ENERGY_TRANSFER_AMOUNT = 320;

    public final SimpleEnergyStorage energyStorage = new SimpleEnergyStorage(64000, ENERGY_TRANSFER_AMOUNT, ENERGY_TRANSFER_AMOUNT) {
        @Override
        protected void onFinalCommit() {
            markDirty();
            getWorld().updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    };

    public AlloySmelterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ALLOY_SMELTER_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> AlloySmelterBlockEntity.this.progress;
                    case 1 -> AlloySmelterBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0: AlloySmelterBlockEntity.this.progress = value;
                    case 1: AlloySmelterBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        assert this.getWorld() != null;
        Direction localDir = this.getWorld().getBlockState(pos).get(AlloySmelter.FACING);

        if (side == null) {
            return false;
        }

        if (side == Direction.DOWN) {
            return false;
        }

        if (side == Direction.UP) {
            return slot == INPUT_SLOT_1;
        }

        Direction right = localDir.rotateYClockwise();
        Direction left = localDir.rotateYCounterclockwise();

        if (side == right) {
            return slot == INPUT_SLOT_2;
        } else if (side == left) {
            return slot == INPUT_SLOT_0;
        }

        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        assert this.getWorld() != null;
        Direction localDir = this.getWorld().getBlockState(pos).get(AlloySmelter.FACING);

        if (side == Direction.DOWN) {
            return slot == OUTPUT_SLOT;
        }

        return false;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return this.pos;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("gui.poweredtools.recycler");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new AlloySmelterScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("alloyer.progress", progress);
        nbt.putInt("alloyer.max_progress", maxProgress);
        nbt.putLong("alloyer.energy", energyStorage.amount);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, inventory, registryLookup);
        progress = nbt.getInt("alloyer.progress");
        maxProgress = nbt.getInt("alloyer.max_progress");
        energyStorage.amount = nbt.getLong("alloyer.energy");
        super.readNbt(nbt, registryLookup);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (hasRecipe() && canInsertIntoOutputSlot()) {
            increaseCraftingProgress();
            world.setBlockState(pos, state.with(AlloySmelter.LIT, true));

            if (hasCraftingFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            world.setBlockState(pos, state.with(AlloySmelter.LIT, false));
            resetProgress();
        }
        markDirty(world, pos, state);
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = DEFAULT_MAX_PROGRESS;
    }

    private Map<Integer, Boolean> getInputSlotsStatus() {
        return Map.of(
                INPUT_SLOT_0, !this.getStack(INPUT_SLOT_0).isEmpty(),
                INPUT_SLOT_1, !this.getStack(INPUT_SLOT_1).isEmpty(),
                INPUT_SLOT_2, !this.getStack(INPUT_SLOT_2).isEmpty()
        );
    }

    private void craftItem() {
        Optional<RecipeEntry<AlloySmelterRecipe>> recipe = getCurrentRecipe();

        for (Map.Entry<Integer, Boolean> entry : getInputSlotsStatus().entrySet()) {
            if (entry.getValue()) {
                this.removeStack(entry.getKey(), 1);
            }
        }
        this.setStack(OUTPUT_SLOT, new ItemStack(recipe.get().value().output().getItem(),
                this.getStack(OUTPUT_SLOT).getCount() + recipe.get().value().getResult(null).getCount()));
    }

    private boolean hasCraftingFinished() {
        Optional<RecipeEntry<AlloySmelterRecipe>> recipe = getCurrentRecipe();
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        if (hasEnoughEnergy(ENERGY_CRAFTING_AMOUNT)) {
            this.progress++;
            try (Transaction transaction = Transaction.openOuter()) {
                this.energyStorage.extract(ENERGY_CRAFTING_AMOUNT, transaction);
                transaction.commit();
            }
        }
    }

    private boolean canInsertIntoOutputSlot() {
        return this.getStack(OUTPUT_SLOT).isEmpty() ||
                this.getStack(OUTPUT_SLOT).getCount() < this.getStack(OUTPUT_SLOT).getMaxCount();
    }

    private boolean hasRecipe() {
        Optional<RecipeEntry<AlloySmelterRecipe>> recipe = getCurrentRecipe();

        if (recipe.isEmpty()){
            return false;
        }

        ItemStack output = recipe.get().value().getResult(null);

        this.maxProgress = recipe.get().value().getCookingTime();

        return canInsertAmountIntoOutputSlot(output.getCount()) && canInsertItemIntoOutputSlot(output);
    }

    private boolean hasEnoughEnergy(int amount) {
        return this.energyStorage.amount >= amount;
    }

    private Optional<RecipeEntry<AlloySmelterRecipe>> getCurrentRecipe() {
        assert this.getWorld() != null;
        return this.getWorld().getRecipeManager().getFirstMatch(ModRecipes.ALLOYING_TYPE, new AlloySmelterRecipeInput(inventory.get(INPUT_SLOT_0), inventory.get(INPUT_SLOT_1), inventory.get(INPUT_SLOT_2)), this.world);
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return this.getStack(OUTPUT_SLOT).getItem() == output.getItem() || this.getStack(OUTPUT_SLOT).isEmpty();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.getStack(OUTPUT_SLOT).getMaxCount() >= this.getStack(OUTPUT_SLOT).getCount() + count;
    }


    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }
}
