package net.acidicts.powered_tools.screen;

import net.acidicts.powered_tools.Powered_tools;
import net.acidicts.powered_tools.screen.custom.RecyclerScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModScreenHandlers {
    public static final ScreenHandlerType<RecyclerScreenHandler> RECYCLER_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(Powered_tools.MOD_ID, "recycler_screen_handler"),
                    new ExtendedScreenHandlerType<>(RecyclerScreenHandler::new, BlockPos.PACKET_CODEC));

    public static void registerScreenHandlers() {
        Powered_tools.LOGGER.info("Registering Screen Handlers for " + Powered_tools.MOD_ID);
    }
}
