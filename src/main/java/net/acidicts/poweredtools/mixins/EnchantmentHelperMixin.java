package net.acidicts.poweredtools.mixins;

import net.acidicts.poweredtools.item.custom.Powered_Pickaxe;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(method = "getLevel", at = @At("RETURN"), cancellable = true)
    private static void getLevel(RegistryEntry<Enchantment> enchantment, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (stack.getItem() instanceof Powered_Pickaxe pickaxe) {
            int level = cir.getReturnValue();
            if (enchantment.matchesKey(Enchantments.FORTUNE)) {
                cir.setReturnValue(level + pickaxe.getFortuneModifiers(stack));
            } else if (enchantment.matchesKey(Enchantments.EFFICIENCY)) {
                cir.setReturnValue(Math.max(level, pickaxe.getSpeedModifiers(stack)));
            }

            String genericType = pickaxe.getGenericModifierType(stack);
            int genericNum = pickaxe.getGenericModifierNum(stack);
            if (genericType != null && !genericType.isEmpty() && genericNum > 0) {
                 RegistryKey<Enchantment> enchantKey = pickaxe.getEnchantmentFromGenericType(genericType);
                 if (enchantKey != null && enchantment.matchesKey(enchantKey)) {
                     cir.setReturnValue(Math.max(level, genericNum));
                 }
            }
        }
    }
}
