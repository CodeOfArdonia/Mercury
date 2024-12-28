package com.iafenvoy.mercury.module.extradamage;

import com.iafenvoy.mercury.Mercury;
import com.iafenvoy.mercury.module.MercuryModule;
import net.minecraft.util.Identifier;

public class ExtraDamageModule implements MercuryModule {
    public static final String MODULE_ID = "extra_damage";
    public static final Identifier ID = Identifier.of(Mercury.MOD_ID, MODULE_ID);

    @Override
    public Identifier getId() {
        return ID;
    }
}
