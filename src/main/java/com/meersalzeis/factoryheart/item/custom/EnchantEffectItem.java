package com.meersalzeis.factoryheart.item.custom;

import com.meersalzeis.factoryheart.item.FuelItem;

import net.minecraft.world.item.ItemStack;

/**
 * As is, this class requires you to place a json into assets/mod_id/models/item
 * Grants enchanted / foil visual effect on items
 */
public class EnchantEffectItem extends FuelItem {

    public EnchantEffectItem(Properties properties, int burnTime) {
        super(properties, burnTime);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
