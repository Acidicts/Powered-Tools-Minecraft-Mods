package net.acidicts.poweredtools.networking;

import net.acidicts.poweredtools.PoweredTools;
import net.acidicts.poweredtools.item.custom.ShieldCore;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec; // Use PacketCodec
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class ModMessages {
    public static final Identifier TOGGLE_CORE_ID = Identifier.of(PoweredTools.MOD_ID, "toggle_core");

    public record TogglePayload() implements CustomPayload {
        public static final Id<TogglePayload> ID = new Id<>(TOGGLE_CORE_ID);

        // In build.3 mappings, StreamCodec is mapped as PacketCodec
        public static final PacketCodec<RegistryByteBuf, TogglePayload> CODEC =
                PacketCodec.unit(new TogglePayload());

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    public static void registerServerPackets() {
        PayloadTypeRegistry.playC2S().register(TogglePayload.ID, TogglePayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(TogglePayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                ItemStack stack = context.player().getMainHandStack();
                if (stack.getItem() instanceof ShieldCore) {
                    NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
                    NbtCompound nbt = component.copyNbt();

                    boolean currentState = nbt.getBoolean("IsActive");
                    nbt.putBoolean("IsActive", !currentState);

                    stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
                }
            });
        });
    }
}