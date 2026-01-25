package net.acidicts.poweredtools.block.entity.custom;

import net.acidicts.poweredtools.block.custom.Recycler;
import net.acidicts.poweredtools.block.entity.ImplementedInventory;
import net.acidicts.poweredtools.block.entity.ModBlockEntities;
import net.acidicts.poweredtools.recipe.ModRecipes;
import net.acidicts.poweredtools.recipe.recycler.RecyclerRecipe;
import net.acidicts.poweredtools.recipe.recycler.RecyclerRecipeInput;
import net.acidicts.poweredtools.screen.custom.recycler.RecyclerScreenHandler;
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

import java.util.Optional;

public class RecyclerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);

    private static final int BATTERY_MATERIAL_SLOT = 0;
    private static final int INPUT_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;
    private static final int Energy_ITEM_SLOT = 3;

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 72;
    private final int DEFAULT_MAX_PROGRESS = 72;

    // Energy constants
    private static final int ENERGY_CRAFTING_AMOUNT = 50;
    private static final int ENERGY_TRANSFER_AMOUNT = 320;

    // Lithium (mB) handling
    private int lithiumMb = 0;
    private static final int MAX_LITHIUM_MB = 10000;
    private static final int LITHIUM_PER_INGOT = 120;
    private static final int LITHIUM_REQUIRED_PER_CRAFT = 300;

    public final SimpleEnergyStorage energyStorage = new SimpleEnergyStorage(64000, ENERGY_TRANSFER_AMOUNT, ENERGY_TRANSFER_AMOUNT) {
        @Override
        protected void onFinalCommit() {
            markDirty();
            getWorld().updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    };

    public RecyclerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RECYCLER_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> RecyclerBlockEntity.this.progress;
                    case 1 -> RecyclerBlockEntity.this.maxProgress;
                    case 2 -> RecyclerBlockEntity.this.lithiumMb;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0: RecyclerBlockEntity.this.progress = value; break;
                    case 1: RecyclerBlockEntity.this.maxProgress = value; break;
                    case 2: RecyclerBlockEntity.this.lithiumMb = value; break;
                }
            }

            @Override
            public int size() {
                return 3;
            }
        };
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        assert this.getWorld() != null;
        Direction localDir = this.getWorld().getBlockState(pos).get(Recycler.FACING);

        if (side == null) {
            return false;
        }

        if (side == Direction.DOWN) {
            return false;
        }

        if (side == Direction.UP) {
            return slot == INPUT_SLOT || slot == BATTERY_MATERIAL_SLOT;
        }

        // For automation on sides, only allow insertion into input slot or battery material slot
        boolean baseCanInsert = switch (localDir) {
            case EAST ->
                    side.rotateYClockwise() == Direction.NORTH && (slot == INPUT_SLOT || slot == BATTERY_MATERIAL_SLOT) ||
                            side.rotateYClockwise() == Direction.WEST && (slot == INPUT_SLOT || slot == BATTERY_MATERIAL_SLOT);
            case SOUTH ->
                    side == Direction.NORTH && (slot == INPUT_SLOT || slot == BATTERY_MATERIAL_SLOT) ||
                            side == Direction.WEST && (slot == INPUT_SLOT || slot == BATTERY_MATERIAL_SLOT);
            case WEST ->
                    side.rotateYCounterclockwise() == Direction.NORTH && (slot == INPUT_SLOT || slot == BATTERY_MATERIAL_SLOT) ||
                            side.rotateYCounterclockwise() == Direction.WEST && (slot == INPUT_SLOT || slot == BATTERY_MATERIAL_SLOT);
            default -> // North
                    side.getOpposite() == Direction.NORTH && (slot == INPUT_SLOT || slot == BATTERY_MATERIAL_SLOT) ||
                            side.getOpposite() == Direction.WEST && (slot == INPUT_SLOT || slot == BATTERY_MATERIAL_SLOT);
        };

        if (!baseCanInsert) return false;

        // Enforce item types for specific slots
        if (slot == BATTERY_MATERIAL_SLOT) {
            // Only allow lithium ingots to be inserted here
            // Use item comparison to ModItems.LITHIUM_INGOT (avoid direct import issues by referencing)
            return stack.isOf(net.acidicts.poweredtools.item.ModItems.LITHIUM_INGOT);
        }

        // Keep existing behavior for other slots
        return slot == INPUT_SLOT;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        assert this.getWorld() != null;
        Direction localDir = this.getWorld().getBlockState(pos).get(Recycler.FACING);

        if (side == Direction.UP) {
            return false;
        }

        if (side == Direction.DOWN) {
            return slot == OUTPUT_SLOT;
        }

        return switch (localDir) {
            case EAST ->
                    side.rotateYClockwise() == Direction.SOUTH && slot == OUTPUT_SLOT ||
                            side.rotateYClockwise() == Direction.EAST && slot == OUTPUT_SLOT;
            case SOUTH ->
                    side == Direction.SOUTH && slot == OUTPUT_SLOT ||
                            side == Direction.EAST && slot == OUTPUT_SLOT;
            case WEST ->
                    side.rotateYCounterclockwise() == Direction.SOUTH && slot == OUTPUT_SLOT ||
                            side.rotateYCounterclockwise() == Direction.EAST && slot == OUTPUT_SLOT;
            default -> // North
                    side.getOpposite() == Direction.SOUTH && slot == OUTPUT_SLOT ||
                            side.getOpposite() == Direction.EAST && slot == OUTPUT_SLOT;
        };
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
        return Text.translatable("block.poweredtools.recycler");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new RecyclerScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("recycler.progress", progress);
        nbt.putInt("recycler.max_progress", maxProgress);
        nbt.putLong("recycler.energy", energyStorage.amount);
        nbt.putInt("recycler.lithium_mb", lithiumMb);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, inventory, registryLookup);
        progress = nbt.getInt("recycler.progress");
        maxProgress = nbt.getInt("recycler.max_progress");
        energyStorage.amount = nbt.getLong("recycler.energy");
        lithiumMb = nbt.contains("recycler.lithium_mb") ? nbt.getInt("recycler.lithium_mb") : 0;
        super.readNbt(nbt, registryLookup);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        // Convert lithium ingots in the BATTERY_MATERIAL_SLOT into internal mB storage if there is space
        ItemStack matStack = this.getStack(BATTERY_MATERIAL_SLOT);
        if (!matStack.isEmpty() && matStack.isOf(net.acidicts.poweredtools.item.ModItems.LITHIUM_INGOT) && this.lithiumMb < MAX_LITHIUM_MB) {
            int spaceForIngot = (MAX_LITHIUM_MB - this.lithiumMb) / LITHIUM_PER_INGOT; // how many full ingots fit
            if (spaceForIngot > 0) {
                int toConsume = Math.min(spaceForIngot, matStack.getCount());
                // remove ingots
                this.removeStack(BATTERY_MATERIAL_SLOT, toConsume);
                this.lithiumMb += toConsume * LITHIUM_PER_INGOT;
            }
        }

        if (hasRecipe() && canInsertIntoOutputSlot()) {
            increaseCraftingProgress();
            world.setBlockState(pos, state.with(Recycler.LIT, true));

            if (hasCraftingFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            world.setBlockState(pos, state.with(Recycler.LIT, false));
            resetProgress();
        }
        markDirty(world, pos, state);
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = DEFAULT_MAX_PROGRESS;
    }

    private void craftItem() {
        Optional<RecipeEntry<RecyclerRecipe>> recipe = getCurrentRecipe();

        if (recipe.isPresent()) {
            this.removeStack(INPUT_SLOT, 1);
            this.setStack(OUTPUT_SLOT, new ItemStack(recipe.get().value().output().getItem(),
                    this.getStack(OUTPUT_SLOT).getCount() + recipe.get().value().getResult(null).getCount()));

            // consume lithium for crafting
            if (this.lithiumMb >= LITHIUM_REQUIRED_PER_CRAFT) {
                this.lithiumMb -= LITHIUM_REQUIRED_PER_CRAFT;
            }
        }
    }

    private boolean hasCraftingFinished() {
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
        Optional<RecipeEntry<RecyclerRecipe>> recipe = getCurrentRecipe();

        if (recipe.isEmpty()){
            return false;
        }

        ItemStack output = recipe.get().value().getResult(null);

        this.maxProgress = recipe.get().value().getCookingTime();

        // require lithium to be available for crafting
        return canInsertAmountIntoOutputSlot(output.getCount()) && canInsertItemIntoOutputSlot(output) && this.lithiumMb >= LITHIUM_REQUIRED_PER_CRAFT;
    }

    private boolean hasEnoughEnergy(int amount) {
        // fixed comparison: check if we have at least `amount` energy
        return this.energyStorage.amount >= amount;
    }

    private Optional<RecipeEntry<RecyclerRecipe>> getCurrentRecipe() {
        return this.getWorld().getRecipeManager().getFirstMatch(ModRecipes.RECYCLER_TYPE, new RecyclerRecipeInput(inventory.get(INPUT_SLOT)), this.world);
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
