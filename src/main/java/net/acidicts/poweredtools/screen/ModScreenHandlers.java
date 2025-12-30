package net.acidicts.poweredtools.screen;

import net.acidicts.poweredtools.PoweredTools;
import net.acidicts.poweredtools.screen.custom.RecyclerScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModScreenHandlers {
    public static final ScreenHandlerType<RecyclerScreenHandler> RECYCLER_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(PoweredTools.MOD_ID, "recycler_screen_handler"),
                    new ExtendedScreenHandlerType<>(RecyclerScreenHandler::new, BlockPos.PACKET_CODEC));

    public static void registerScreenHandlers() {
        PoweredTools.LOGGER.info("Registering Screen Handlers for " + PoweredTools.MOD_ID);
    }
}
