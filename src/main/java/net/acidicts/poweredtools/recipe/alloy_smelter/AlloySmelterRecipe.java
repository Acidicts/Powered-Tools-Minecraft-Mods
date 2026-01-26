package net.acidicts.poweredtools.recipe.alloy_smelter;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.acidicts.poweredtools.block.ModBlocks;
import net.acidicts.poweredtools.recipe.ModRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public record AlloySmelterRecipe(Ingredient input1, Ingredient input2, Ingredient input3, ItemStack output, int cookingTime, int requiredInputs) implements Recipe<AlloySmelterRecipeInput> {
    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> list = DefaultedList.of();
        list.add(this.input1);
        list.add(this.input2);
        list.add(this.input3);

        return list;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.ALLOY_SMELTER);
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public int getRequiredInputs() {
        return requiredInputs;
    }

    @Override
    public boolean matches(AlloySmelterRecipeInput input, World world) {
        if (world.isClient) {
            return false;
        }

        boolean[] slotMatched = new boolean[3];
        boolean[] ingredientMatched = new boolean[3];
        Ingredient[] ingredients = {input1, input2, input3};

        // Try to match each slot with an unmatched ingredient
        for (int slot = 0; slot < 3; slot++) {
            ItemStack stack = input.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }

            for (int ing = 0; ing < 3; ing++) {
                if (!ingredientMatched[ing] && ingredients[ing].test(stack)) {
                    slotMatched[slot] = true;
                    ingredientMatched[ing] = true;
                    break;
                }
            }
        }

        // Count matched ingredients and ensure we have at least the required number
        int matchedCount = 0;
        for (boolean m : ingredientMatched) if (m) matchedCount++;

        return matchedCount >= this.requiredInputs;
    }

    @Override
    public ItemStack craft(AlloySmelterRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ALLOY_SMELTER_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ALLOYING_TYPE;
    }

    public static class Serializer implements RecipeSerializer<AlloySmelterRecipe>{

        public static final MapCodec<AlloySmelterRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("input1").forGetter(AlloySmelterRecipe::input1),
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("input2").forGetter(AlloySmelterRecipe::input2),
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("input3").forGetter(AlloySmelterRecipe::input3),
                ItemStack.CODEC.fieldOf("output").forGetter(AlloySmelterRecipe::output),
                com.mojang.serialization.Codec.INT.fieldOf("cookingtime").forGetter(AlloySmelterRecipe::cookingTime),
                com.mojang.serialization.Codec.INT.fieldOf("requiredInputs").forGetter(AlloySmelterRecipe::requiredInputs)
        ).apply(inst, AlloySmelterRecipe::new));

        public static final PacketCodec<RegistryByteBuf, AlloySmelterRecipe> STREAM_CODEC =
                PacketCodec.tuple(
                        Ingredient.PACKET_CODEC, AlloySmelterRecipe::input1,
                        Ingredient.PACKET_CODEC, AlloySmelterRecipe::input2,
                        Ingredient.PACKET_CODEC, AlloySmelterRecipe::input3,
                        ItemStack.PACKET_CODEC, AlloySmelterRecipe::output,
                        PacketCodec.ofStatic((buf, value) -> buf.writeInt(value), buf -> buf.readInt()), AlloySmelterRecipe::cookingTime,
                        PacketCodec.ofStatic((buf, value) -> buf.writeInt(value), buf -> buf.readInt()), AlloySmelterRecipe::requiredInputs,
                        AlloySmelterRecipe::new
                );

        @Override
        public MapCodec<AlloySmelterRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, AlloySmelterRecipe> packetCodec() {
            return STREAM_CODEC;
        }
    }
}
