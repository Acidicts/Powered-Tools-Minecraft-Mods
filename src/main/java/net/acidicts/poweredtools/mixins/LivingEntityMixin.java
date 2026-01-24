package net.acidicts.poweredtools.mixins;

import net.acidicts.poweredtools.item.custom.ShieldCore;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        // Check main hand for ShieldCore
        ItemStack mainHandStack = entity.getMainHandStack();
        if (mainHandStack.getItem() instanceof ShieldCore shieldCore) {
            if (!shieldCore.canDamageTick(mainHandStack, entity, source)) {
                cir.setReturnValue(false);
                return;
            }
        }

        // Check off hand for ShieldCore
        ItemStack offHandStack = entity.getOffHandStack();
        if (offHandStack.getItem() instanceof ShieldCore shieldCore) {
            if (!shieldCore.canDamageTick(offHandStack, entity, source)) {
                cir.setReturnValue(false);
                return;
            }
        }
    }
}
