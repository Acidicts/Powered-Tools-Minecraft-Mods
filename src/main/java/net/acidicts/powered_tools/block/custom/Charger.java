package net.acidicts.powered_tools.block.custom;

import com.mojang.serialization.MapCodec;
import net.acidicts.powered_tools.block.entity.ChargerBlockEntity;
import net.acidicts.powered_tools.item.ModItems;
import net.acidicts.powered_tools.item.custom.BatteryItem;
import net.acidicts.powered_tools.item.custom.Powered_Pickaxe;
import net.acidicts.powered_tools.tags.ModTags;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class Charger extends BlockWithEntity {
    public static final MapCodec<Charger> CODEC = createCodec(Charger::new);
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public Charger(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<net.minecraft.block.Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChargerBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                  WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        // Trigger block entity update when neighbors change
        if (!world.isClient()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ChargerBlockEntity chargerEntity) {
                // Mark the block entity as needing a sync to update the animation
                chargerEntity.markDirty();
            }
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockRenderView renderView, BlockPos pos, Direction side, @Nullable BlockState sourceState, @Nullable BlockPos sourcePos) {
        return super.getAppearance(state, renderView, pos, side, sourceState, sourcePos);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ChargerBlockEntity) {
            if (!((ChargerBlockEntity) blockEntity).hasSpaceToOpen()) {
                return ActionResult.FAIL;
            }
        }
        if (!world.isClient && player.getMainHandStack().isIn(ModTags.Items.chargeable)) {
            var stack = player.getMainHandStack();

            if (stack.getItem() instanceof Powered_Pickaxe poweredPickaxe) {
                int maxCapacity = poweredPickaxe.getMaxCapacity(stack);
                int currentCharge = poweredPickaxe.getEnergy(stack);
                int rechargeAmount = maxCapacity - currentCharge;

                if (rechargeAmount > 0) {
                    poweredPickaxe.setCycles(stack, poweredPickaxe.getCycles(stack) + 1);
                    poweredPickaxe.setEnergy(stack, maxCapacity);
                    player.sendMessage(net.minecraft.text.Text.literal("Pickaxe charged!").formatted(net.minecraft.util.Formatting.GREEN), true);
                    return ActionResult.SUCCESS;
                } else {
                    player.sendMessage(net.minecraft.text.Text.literal("Pickaxe is already fully charged!").formatted(net.minecraft.util.Formatting.YELLOW), true);
                    return ActionResult.SUCCESS;
                }
            }
            // Check if it's a BatteryItem
            else if (stack.getItem() instanceof BatteryItem batteryItem) {
                int maxCapacity = batteryItem.getMaxCapacity(stack);
                int currentCharge = batteryItem.getCurrentCharge(stack);
                int rechargeAmount = maxCapacity - currentCharge;

                if (batteryItem.isBroken(stack)) {
                    int slot = player.getInventory().getSlotWithStack(stack);
                    player.getInventory().removeStack(slot);
                    Item brokenBattery = ModItems.getBrokenBatteryByTier(batteryItem.tier);
                    player.getInventory().insertStack(brokenBattery.getDefaultStack());
                }

                if (rechargeAmount > 0) {
                    batteryItem.recharge(stack, rechargeAmount);
                    return ActionResult.SUCCESS;
                }
            }
        }


        return ActionResult.PASS;
    }
}
