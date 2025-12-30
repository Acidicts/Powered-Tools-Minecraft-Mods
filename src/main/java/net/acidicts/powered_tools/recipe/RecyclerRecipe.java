package net.acidicts.powered_tools.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.acidicts.powered_tools.block.ModBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public record RecyclerRecipe(Ingredient inputItem, ItemStack output, int cookingTime) implements Recipe<RecyclerRecipeInput> {
    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> list = DefaultedList.of();
        list.add(this.inputItem);

        return list;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.RECYCLER);
    }

    public int getCookingTime() {
        return cookingTime;
    }

    @Override
    public boolean matches(RecyclerRecipeInput input, World world) {
        if (world.isClient) {
            return false;
        }

        return inputItem.test(input.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(RecyclerRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
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
        return ModRecipes.RECYCLER_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.RECYCLER_TYPE;
    }

    public static class Serializer implements RecipeSerializer<RecyclerRecipe>{

        public static final MapCodec<RecyclerRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(RecyclerRecipe::inputItem),
                ItemStack.CODEC.fieldOf("output").forGetter(RecyclerRecipe::output),
                com.mojang.serialization.Codec.INT.fieldOf("cookingtime").forGetter(RecyclerRecipe::cookingTime)
        ).apply(inst, RecyclerRecipe::new));

        public static final PacketCodec<RegistryByteBuf, RecyclerRecipe> STREAM_CODEC =
                PacketCodec.tuple(
                        Ingredient.PACKET_CODEC, RecyclerRecipe::inputItem,
                        ItemStack.PACKET_CODEC, RecyclerRecipe::output,
                        PacketCodec.ofStatic((buf, value) -> buf.writeInt(value), buf -> buf.readInt()), RecyclerRecipe::cookingTime,
                        RecyclerRecipe::new
                );

        @Override
        public MapCodec<RecyclerRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, RecyclerRecipe> packetCodec() {
            return STREAM_CODEC;
        }
    }
}
