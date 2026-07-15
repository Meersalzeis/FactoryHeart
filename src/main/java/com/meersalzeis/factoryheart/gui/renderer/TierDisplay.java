package com.meersalzeis.factoryheart.gui.renderer;

import net.minecraft.network.chat.Component;

public class TierDisplay {

    private int tier;
    private int x;
    private int y;

    public TierDisplay(int newTier) {
        this.tier = newTier;
    }

    public Component getTooltip() {
        return Component.literal("This machine can craft up to tier" + tier);
    }
    
}
