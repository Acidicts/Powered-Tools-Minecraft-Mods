package net.acidicts.poweredtools.item.custom.shieldcores;


import net.acidicts.poweredtools.item.custom.ShieldCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class DamageShieldCore extends ShieldCore {
    public DamageShieldCore(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
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
        if (entity instanceof LivingEntity livingEntity && isActive(stack)) {
            if (hasEnergy(stack, 200)) {
                consumeEnergy(stack, 200);
                return false;
            }
        }
        return true;
    }
}
