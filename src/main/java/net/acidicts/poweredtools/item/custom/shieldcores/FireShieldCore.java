package net.acidicts.poweredtools.item.custom.shieldcores;

import net.acidicts.poweredtools.item.custom.ShieldCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class FireShieldCore extends ShieldCore {
    public FireShieldCore(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
    }

    @Override
    public TypedActionResult<ItemStack> coreAbilityUse(World world, PlayerEntity user, Hand hand, ItemStack stack) {
        if (user.isOnFire() && hasEnergy(stack, 100) && user instanceof LivingEntity livingEntity) {
            livingEntity.extinguish();
            consumeEnergy(stack, 100);
        }
        return TypedActionResult.pass(stack);
    }

    @Override
    public boolean canDamageTick(ItemStack stack, Entity entity, DamageSource source) {
        if (entity instanceof LivingEntity livingEntity) {
            // Check for fire damage - consume 50 energy
            if ((source.isOf(DamageTypes.IN_FIRE) || source.isOf(DamageTypes.ON_FIRE) || source.isOf(DamageTypes.LAVA)) && hasEnergy(stack, 50)) {
                consumeEnergy(stack, 50);
                return false;
            }
        }
        return true;
    }
}
