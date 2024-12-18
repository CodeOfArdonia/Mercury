package com.iafenvoy.mercury.module.betterop;

import com.iafenvoy.mercury.Mercury;
import com.iafenvoy.mercury.module.MercuryModule;
import net.minecraft.util.Identifier;

public class BetterOpModule implements MercuryModule {
    public static final String MODULE_ID = "better_op";
    public static final Identifier ID = Identifier.of(Mercury.MOD_ID, MODULE_ID);

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean shouldReload() {
        return true;
    }
}
