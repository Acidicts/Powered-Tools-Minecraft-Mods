package net.acidicts.powered_tools.block.custom;

import net.acidicts.powered_tools.item.ModItems;
import net.acidicts.powered_tools.item.custom.BatteryItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class Charger extends Block {
    public Charger(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockRenderView renderView, BlockPos pos, Direction side, @Nullable BlockState sourceState, @Nullable BlockPos sourcePos) {
        return super.getAppearance(state, renderView, pos, side, sourceState, sourcePos);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient && player.getMainHandStack().getItem() instanceof BatteryItem batteryItem) {
            var stack = player.getMainHandStack();

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

        return ActionResult.PASS;
    }
}
