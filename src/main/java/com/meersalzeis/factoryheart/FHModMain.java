package com.meersalzeis.factoryheart;

import java.util.Random;

import org.slf4j.Logger;

import com.meersalzeis.factoryheart.block.ModBlocks;
import com.meersalzeis.factoryheart.block.entity.ModBlockEntities;
import com.meersalzeis.factoryheart.hearts.HeartFeeding;
import com.meersalzeis.factoryheart.hearts.HeartNetwork;
import com.meersalzeis.factoryheart.item.ModCreativeModeTabs;
import com.meersalzeis.factoryheart.item.ModItems;
import com.meersalzeis.factoryheart.recipe.ModRecipes;
import com.meersalzeis.factoryheart.gui.ModMenuTypes;
import com.meersalzeis.factoryheart.gui.screens.CrystallizerScreen;
import com.meersalzeis.factoryheart.gui.screens.ExtractorScreen;
import com.meersalzeis.factoryheart.gui.screens.TesterScreen;
import com.meersalzeis.factoryheart.gui.screens.BlazerScreen;
import com.meersalzeis.factoryheart.gui.screens.CondenserScreen;
import com.meersalzeis.factoryheart.gui.screens.WrapperScreen;
import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(FHModMain.MOD_ID)
public class FHModMain {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "factoryheart";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final Random rnd = new Random();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public FHModMain(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        //modEventBus.addListener(this::commonSetup);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);

        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        ModRecipes.register(modEventBus);

        // Tells Neoforge to look at this class here
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    //private void commonSetup(FMLCommonSetupEvent event) {}

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        HeartFeeding.RegisterFuelsAndCoolants();
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }

        @SubscribeEvent
        public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
            // event.registerBlockEntityRenderer(ModBlockEntities.PEDESTAL_BE.get(), PedestalBlockEntityRenderer::new);
            // event.registerBlockEntityRenderer(ModBlockEntities.TANK_BE.get(), TankBlockEntityRenderer::new);
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenuTypes.CRYSTALLIZER_MENU.get(), CrystallizerScreen::new);
            event.register(ModMenuTypes.BLAZER_MENU.get(), BlazerScreen::new);
            event.register(ModMenuTypes.WRAPPER_MENU.get(), WrapperScreen::new);
            event.register(ModMenuTypes.EXTRACTOR_MENU.get(), ExtractorScreen::new);
            event.register(ModMenuTypes.TESTER_MENU.get(), TesterScreen::new);
            event.register(ModMenuTypes.CONDENSER_MENU.get(), CondenserScreen::new);

            // event.register(ModMenuTypes.COAL_GENERATOR_MENU.get(), CoalGeneratorScreen::new);
            // event.register(ModMenuTypes.TANK_MENU.get(), TankScreen::new);
        }
    }
}
