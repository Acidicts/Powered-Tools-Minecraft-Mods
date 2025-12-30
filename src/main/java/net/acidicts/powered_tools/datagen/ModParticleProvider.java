package net.acidicts.powered_tools.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.acidicts.powered_tools.Powered_tools;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModParticleProvider implements DataProvider {
    private final DataOutput.PathResolver pathResolver;

    public ModParticleProvider(FabricDataOutput output) {
        this.pathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "particles");
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        futures.add(generateParticle(writer, "electric_spark",
            List.of(Powered_tools.MOD_ID + ":electric_spark")));

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private CompletableFuture<?> generateParticle(DataWriter writer, String name, List<String> textures) {
        JsonObject json = new JsonObject();
        JsonArray texturesArray = new JsonArray();

        for (String texture : textures) {
            texturesArray.add(texture);
        }

        json.add("textures", texturesArray);

        Path path = this.pathResolver.resolveJson(Identifier.of(Powered_tools.MOD_ID, name));
        return DataProvider.writeToPath(writer, json, path);
    }

    @Override
    public String getName() {
        return "Particle Definitions";
    }
}

