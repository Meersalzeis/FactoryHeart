package com.meersalzeis.factoryheart.item.custom;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class VolatileBlazeRod extends Item {

    public VolatileBlazeRod(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
