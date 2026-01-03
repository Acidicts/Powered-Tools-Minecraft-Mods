package net.acidicts.poweredtools.recipe;

import com.mojang.serialization.MapCodec;
import net.acidicts.poweredtools.recipe.recycler.RecyclerRecipeClass;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface ModRecipeSerializer<T extends Recipe<?>> {
    RecipeSerializer<RecyclerRecipeClass> RECYCLING = register("recycling", new CookingRecipeSerializer(RecyclerRecipeClass::new, 72));
    RecipeSerializer<RecyclerRecipeClass> ALLOYING = register("recycling", new CookingRecipeSerializer(RecyclerRecipeClass::new, 72));

    MapCodec<T> codec();

    PacketCodec<RegistryByteBuf, T> packetCodec();

    static <S extends net.minecraft.recipe.RecipeSerializer<T>, T extends Recipe<?>> S register(String id, S serializer) {
        return (S)(Registry.register(Registries.RECIPE_SERIALIZER, id, serializer));
    }
}
