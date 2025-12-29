package net.acidicts.powered_tools.block.entity;

import net.acidicts.powered_tools.Powered_tools;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ChargerBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean hasOpened = false;
    private boolean animationSet = false;
    private int tickCounter = 0;
    private static final int ANIMATION_DURATION_TICKS = 40; // Approximately 2 seconds (20 ticks/second)

    public ChargerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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
        Powered_tools.LOGGER.info("registerControllers called - hasOpened: {}, animationSet: {}", hasOpened, animationSet);
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    public void tick() {
        if (world == null || world.isClient) return;

        // Server-side logic to mark as opened when space is available and animation completes
        if (!hasOpened && hasSpaceToOpen()) {
            tickCounter++;
            if (tickCounter >= ANIMATION_DURATION_TICKS) {
                Powered_tools.LOGGER.info("Charger animation should be complete on server side - setting hasOpened to true");
                setHasOpened(true);
            }
        } else if (!hasSpaceToOpen()) {
            // Reset counter if space becomes unavailable
            tickCounter = 0;
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
        Powered_tools.LOGGER.info("setHasOpened called with value: {}", hasOpened);
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
        nbt.putBoolean("HasOpened", hasOpened);
        Powered_tools.LOGGER.info("writeNbt called - Saving hasOpened as: {}", hasOpened);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        hasOpened = nbt.getBoolean("HasOpened");
        Powered_tools.LOGGER.info("readNbt called - hasOpened loaded as: {}", hasOpened);
        // If it was previously opened, mark animation as already set to prevent restart
        if (hasOpened) {
            animationSet = true;
            Powered_tools.LOGGER.info("readNbt: Setting animationSet to true because hasOpened=true");
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        // This method is called to sync data to the client when the chunk loads
        NbtCompound nbt = super.toInitialChunkDataNbt(registryLookup);
        nbt.putBoolean("HasOpened", hasOpened);
        Powered_tools.LOGGER.info("toInitialChunkDataNbt called - Syncing hasOpened: {} to client", hasOpened);
        return nbt;
    }

    @Override
    public net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket toUpdatePacket() {
        // This method is called when the block entity state changes and needs to be synced
        return net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket.create(this);
    }
}

