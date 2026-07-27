package com.meersalzeis.factoryheart.compat.Jade;

import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.block.ModBlocks;

import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadePluginRegistering implements IWailaPlugin {

  public static ResourceLocation getUid() {
    return ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID, "jade_blocks");
  }

  @Override
  public void register(IWailaCommonRegistration registration) {
    //TODO register data providers
  }

  @Override
  public void registerClient(IWailaClientRegistration registration) {
    registration.registerBlockComponent(FHJadeComponentProvider.INSTANCE, ModBlocks.FACTORY_HEART.get().getClass());
    registration.registerBlockComponent(FHJadeComponentProvider.INSTANCE, ModBlocks.BLAZER.get().getClass());
    registration.registerBlockComponent(FHJadeComponentProvider.INSTANCE, ModBlocks.WRAPPER.get().getClass());
    registration.registerBlockComponent(FHJadeComponentProvider.INSTANCE, ModBlocks.EXTRACTOR.get().getClass());
    registration.registerBlockComponent(FHJadeComponentProvider.INSTANCE, ModBlocks.TESTER.get().getClass());
    registration.registerBlockComponent(FHJadeComponentProvider.INSTANCE, ModBlocks.CONDENSER.get().getClass());
  }
}