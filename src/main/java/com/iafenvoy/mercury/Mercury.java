package com.iafenvoy.mercury;

import com.iafenvoy.mercury.config.ConfigManager;
import com.iafenvoy.mercury.module.MercuryModuleManager;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;

public class Mercury implements ModInitializer {
    public static final String MOD_ID = "mercury";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ConfigManager.INSTANCE);
        MercuryModuleManager.loadAll();
        MercuryCommand.init();
    }
}