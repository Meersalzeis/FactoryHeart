package com.meersalzeis.factoryheart.gui;

import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.gui.menus.CrystallizerMenu;
import com.meersalzeis.factoryheart.gui.menus.ExtractorMenu;
import com.meersalzeis.factoryheart.gui.menus.TesterMenu;
import com.meersalzeis.factoryheart.gui.menus.WrapperMenu;
import com.meersalzeis.factoryheart.gui.menus.BlazerMenu;
import com.meersalzeis.factoryheart.gui.menus.CondenserMenu;

// import com.meersalzeis.factoryheart.screen.CoalGeneratorMenu;
// import com.meersalzeis.factoryheart.screen.CrystallizerMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
        public static final DeferredRegister<MenuType<?>> MENUS =
                DeferredRegister.create(Registries.MENU, FHModMain.MOD_ID);

        public static final DeferredHolder<MenuType<?>, MenuType<CrystallizerMenu>> CRYSTALLIZER_MENU =
                registerMenuType("crystallizer_menu", CrystallizerMenu::new);

        public static final DeferredHolder<MenuType<?>, MenuType<BlazerMenu>> BLAZER_MENU =
                registerMenuType("blazer_menu", BlazerMenu::new);

        public static final DeferredHolder<MenuType<?>, MenuType<WrapperMenu>> WRAPPER_MENU =
                registerMenuType("wrapper_menu", WrapperMenu::new);
    
        public static final DeferredHolder<MenuType<?>, MenuType<ExtractorMenu>> EXTRACTOR_MENU =
                registerMenuType("extractor_menu", ExtractorMenu::new);

        public static final DeferredHolder<MenuType<?>, MenuType<TesterMenu>> TESTER_MENU =
                registerMenuType("tester_menu", TesterMenu::new);

        public static final DeferredHolder<MenuType<?>, MenuType<CondenserMenu>> CONDENSER_MENU =
                registerMenuType("condenser_menu", CondenserMenu::new);

//     public static final DeferredHolder<MenuType<?>, MenuType<TankMenu>> TANK_MENU =
//             registerMenuType("tank_menu", TankMenu::new);

    private static <T extends AbstractContainerMenu>DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(
            String name,
            IContainerFactory<T> factory)
        {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
