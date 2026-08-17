package com.meersalzeis.factoryheart.item.custom;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * As is, this class requires you to place a josn into assets/mod_id/models/item
 * Grants enchanted / foil visual effect on items
 */
public class EnchantEffectItem extends Item {

    public EnchantEffectItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
