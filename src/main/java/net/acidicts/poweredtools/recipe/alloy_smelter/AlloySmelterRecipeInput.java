package net.acidicts.poweredtools.recipe.alloy_smelter;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public record AlloySmelterRecipeInput(ItemStack input1, ItemStack input2, ItemStack input3) implements RecipeInput {
    @Override
    public ItemStack getStackInSlot(int slot) {
        return switch (slot) {
            case 0 -> input1;
            case 1 -> input2;
            case 2 -> input3;
            default -> throw new IllegalArgumentException("Recipe does not contain slot " + slot);
        };
    }

    @Override
    public int getSize() {
        return 3;
    }
}
