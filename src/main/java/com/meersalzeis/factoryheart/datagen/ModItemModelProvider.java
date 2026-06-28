package com.meersalzeis.factoryheart.datagen;

import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.block.ModBlocks;
import com.meersalzeis.factoryheart.item.ModItems;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Objects;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, FHModMain.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        basicItem(ModItems.ABZERO_MATTER.get());
        basicItem(ModItems.ACTIVATED_BLAZE_ROD.get());
        basicItem(ModItems.BLAZEAR_FUEL_ROD.get());
        basicItem(ModItems.BLAZEAR_PELLET.get());
        basicItem(ModItems.CONTAINED_DEEPCOOL_CRYSTAL.get());
        basicItem(ModItems.COPPER_WIRE.get());
        basicItem(ModItems.DEEPCOOL_CRYSTAL.get());
        basicItem(ModItems.EMPTY_CAN.get());
        basicItem(ModItems.ENERGY_DRINK.get());
        basicItem(ModItems.ENERGY_POWDER.get());
        basicItem(ModItems.FACTORY_PASTE.get());
        basicItem(ModItems.FACTORY_TOOTH.get());
        basicItem(ModItems.INFERNO_MATTER.get());
        basicItem(ModItems.METAL_DISC.get());
        basicItem(ModItems.METAL_SHEET.get());
        basicItem(ModItems.SODA_POWDER.get());

        basicItem(ModItems.CONTAINMENT_SPOOL.get());
        basicItem(ModItems.CONTAINMENT_FIELD_PROJECTOR.get());
        basicItem(ModItems.CONTAINMENT_CASING.get());

        customItemTriple(ModItems.SMART_SHARD.getRegisteredName(), modLoc("item/smart_shard"));
        customItemTriple(ModItems.SMART_PART.getRegisteredName(), modLoc("item/smart_part"));
        customItemTriple(ModItems.SMART_HEART.getRegisteredName(), modLoc("item/smart_heart"));
        customItemTriple(ModItems.HARD_PART.getRegisteredName(), modLoc("item/hard_part"));
        
        copiedTextureItem(ModItems.VOLATILE_BLAZE_ROD.get(), ModItems.ACTIVATED_BLAZE_ROD.get());

        customBlockItem(ModBlocks.FACTORY_MAW);
    }

    public ItemModelBuilder copiedTextureItem(ResourceLocation item, String assetspace ,ResourceLocation donor) {
        return getBuilder(item.getPath())
            .parent(new ModelFile.UncheckedModelFile(assetspace + ":item/" + donor.getPath())); }

    public ItemModelBuilder copiedTextureItem(Item item, Item donor) {
        return getBuilder(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)).getPath())
            .parent(new ModelFile.UncheckedModelFile(
                    Objects.requireNonNull(
                            BuiltInRegistries.ITEM.getKey(donor)).getNamespace()
                            + ":item/"
                            + Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(donor)).getPath()
            ));
    }

    private ItemModelBuilder customBlockItem(DeferredBlock<Block> block) {
        return getBuilder(block.getId().getPath()).parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID,
                "block/" + block.getId().getPath())));
    }

    private void customItemTriple(String name, ResourceLocation baseTexture) {
        generateLayeredItem(name, baseTexture);
        generateLayeredItem(name + "_faulty", baseTexture, modLoc("item/overlay_faulty"));
        generateLayeredItem(name + "_unidentified", baseTexture, modLoc("item/overlay_unidentified"));
    }

    private ItemModelBuilder generateLayeredItem(String name, ResourceLocation... textures) {
        ItemModelBuilder builder = withExistingParent(name, mcLoc("item/generated"));

        for (int i = 0; i < textures.length; i++) {
            builder.texture("layer" + i, textures[i]);
        }
        return builder;
    }
}
