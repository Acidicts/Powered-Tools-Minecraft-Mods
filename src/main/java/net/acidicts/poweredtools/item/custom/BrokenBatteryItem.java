package net.acidicts.poweredtools.item.custom;

import net.acidicts.poweredtools.PoweredTools;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Map;

public class BrokenBatteryItem extends Item {
    public final BatteryMaterial material;
    public final int max_capacity;
    public final int transfer_rate;
    public final int lifespan;
    public final float decay_rate;
    public final String tier;

    private Map<String, Integer> tier_dict = Map.of(
            "Stone", 0,
            "Iron", 1,
            "Gold", 2,
            "Diamond", 3,
            "Netherite", 4,
            "Diamond_Gold", 5
    );

    public BrokenBatteryItem(BatteryMaterial material, Settings settings) {
        super(settings.maxCount(64));

        this.material = material;
        this.max_capacity = material.getCapacity();
        this.transfer_rate = material.getTransferRate();
        this.lifespan = material.getLifespan();
        this.decay_rate = material.getDecayRate();
        this.tier = material.getTier();
    }

    public int getTierInt() {
        return tier_dict.getOrDefault(this.tier, 0);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            ItemStack stack = user.getStackInHand(hand);
            PoweredTools.LOGGER.info("This battery is broken and cannot be used. Tier: {} ({}).", this.tier, this.getTierInt());
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
