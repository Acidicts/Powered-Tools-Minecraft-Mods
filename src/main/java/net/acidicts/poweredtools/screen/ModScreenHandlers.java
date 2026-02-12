package net.acidicts.poweredtools.screen;

import net.acidicts.poweredtools.PoweredTools;
import net.acidicts.poweredtools.screen.custom.alloy_smelter.AlloySmelterScreenHandler;
import net.acidicts.poweredtools.screen.custom.charger.ChargerScreenHandler;
import net.acidicts.poweredtools.screen.custom.coal_generator.CoalGeneratorScreenHandler;
import net.acidicts.poweredtools.screen.custom.power_pickaxe.PoweredPickaxeScreenHandler;
import net.acidicts.poweredtools.screen.custom.powered_sword.PoweredSwordScreenHandler;
import net.acidicts.poweredtools.screen.custom.recycler.RecyclerScreenHandler;
import net.acidicts.poweredtools.screen.custom.shieldcore.ShieldCoreScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModScreenHandlers {
    public static final ScreenHandlerType<RecyclerScreenHandler> RECYCLER_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(PoweredTools.MOD_ID, "recycler_screen_handler"),
                    new ExtendedScreenHandlerType<>(RecyclerScreenHandler::new, BlockPos.PACKET_CODEC));

    public static final ScreenHandlerType<CoalGeneratorScreenHandler> COAL_GENERATOR_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(PoweredTools.MOD_ID, "coal_generator_screen_handler"),
                    new ExtendedScreenHandlerType<>(CoalGeneratorScreenHandler::new, BlockPos.PACKET_CODEC));

    public static final ScreenHandlerType<AlloySmelterScreenHandler> ALLOY_SMELTER_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(PoweredTools.MOD_ID, "alloy_smelter_screen_handler"),
                    new ExtendedScreenHandlerType<>(AlloySmelterScreenHandler::new, BlockPos.PACKET_CODEC));

    public static final ScreenHandlerType<ChargerScreenHandler> CHARGER_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(PoweredTools.MOD_ID, "charger_screen_handler"),
                    new ExtendedScreenHandlerType<>(ChargerScreenHandler::new, BlockPos.PACKET_CODEC));

    public static final ScreenHandlerType<PoweredPickaxeScreenHandler> POWERED_PICKAXE_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(PoweredTools.MOD_ID, "powered_pickaxe_screen_handler"),
                    new ExtendedScreenHandlerType<>(PoweredPickaxeScreenHandler::new, ItemStack.PACKET_CODEC));

    public static final ScreenHandlerType<PoweredSwordScreenHandler> POWERED_SWORD_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(PoweredTools.MOD_ID, "powered_sword_screen_handler"),
                    new ExtendedScreenHandlerType<>(PoweredSwordScreenHandler::new, ItemStack.PACKET_CODEC));

    public static final ScreenHandlerType<ShieldCoreScreenHandler> SHIELD_CORE_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(PoweredTools.MOD_ID, "shield_core_screen_handler"),
                    new ExtendedScreenHandlerType<>(ShieldCoreScreenHandler::new, ItemStack.PACKET_CODEC));

    public static void registerScreenHandlers() {
        PoweredTools.LOGGER.info("Registering Screen Handlers for " + PoweredTools.MOD_ID);
    }
}
