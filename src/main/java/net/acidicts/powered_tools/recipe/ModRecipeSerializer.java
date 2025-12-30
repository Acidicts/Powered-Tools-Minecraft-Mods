package net.acidicts.powered_tools.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface ModRecipeSerializer<T extends Recipe<?>> {
    net.minecraft.recipe.RecipeSerializer<RecyclerRecipeClass> RECYCLING = register("recycling", new CookingRecipeSerializer(RecyclerRecipeClass::new, 72));

    MapCodec<T> codec();

    PacketCodec<RegistryByteBuf, T> packetCodec();

    static <S extends net.minecraft.recipe.RecipeSerializer<T>, T extends Recipe<?>> S register(String id, S serializer) {
        return (S)(Registry.register(Registries.RECIPE_SERIALIZER, id, serializer));
    }
}
