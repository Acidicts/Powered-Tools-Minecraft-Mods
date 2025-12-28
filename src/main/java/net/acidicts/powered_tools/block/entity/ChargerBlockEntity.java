package net.acidicts.powered_tools.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ChargerBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ChargerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public ChargerBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.CHARGER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    public boolean hasSpaceToOpen() {
        if (world == null) return false;

        // Check all horizontal directions around the charger
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos adjacentPos = pos.offset(direction);
            BlockState adjacentState = world.getBlockState(adjacentPos);

            // If there's a non-air block adjacent, we don't have space
            if (!adjacentState.isAir()) {
                return false;
            }
        }

        // Check the block above (for the lid to open)
        BlockPos abovePos = pos.up();
        BlockState aboveState = world.getBlockState(abovePos);
        if (!aboveState.isAir()) {
            return false;
        }

        return true;
    }

    private PlayState predicate(AnimationState<ChargerBlockEntity> state) {
        if (hasSpaceToOpen()) {
            if (!state.getController().hasAnimationFinished()) {
                state.getController().setAnimation(RawAnimation.begin().then("Open", Animation.LoopType.HOLD_ON_LAST_FRAME));
                return PlayState.CONTINUE;
            } else {
                return PlayState.STOP;
            }
        } else {
            state.getController().forceAnimationReset();
            state.getController().stop();
            return PlayState.STOP;
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}

