package com.meersalzeis.factoryheart.block.entity;

import com.meersalzeis.factoryheart.FHModMain;

import net.minecraft.core.Direction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;


@EventBusSubscriber(modid = FHModMain.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModBECapabilities {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.WRAPPER_BE.get(),
            (be, side) -> {
                if (side == Direction.UP) return be.topHandler;
                if (side == Direction.DOWN) return be.bottomHandler;
                return be.sideHandler;
            }
        );
    }
}
