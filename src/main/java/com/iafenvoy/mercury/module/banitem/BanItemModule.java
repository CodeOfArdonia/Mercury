package com.iafenvoy.mercury.module.banitem;

import com.iafenvoy.mercury.Mercury;
import com.iafenvoy.mercury.config.MercuryConfig;
import com.iafenvoy.mercury.module.MercuryModule;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class BanItemModule implements MercuryModule {
    public static final String MODULE_ID = "ban_item";
    public static final Identifier ID = Identifier.of(Mercury.MOD_ID, MODULE_ID);

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void registerConfig(Consumer<MercuryConfig<?>> registry) {
        registry.accept(BanItemConfig.INSTANCE);
    }
}
