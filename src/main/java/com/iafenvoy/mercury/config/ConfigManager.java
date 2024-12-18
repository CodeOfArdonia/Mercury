package com.iafenvoy.mercury.config;

import com.iafenvoy.mercury.Mercury;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;

public enum ConfigManager implements SimpleSynchronousResourceReloadListener {
    INSTANCE;

    public static final List<MercuryConfig<?>> CONFIGS = new LinkedList<>();

    @Override
    public Identifier getFabricId() {
        return Identifier.of(Mercury.MOD_ID, "config");
    }

    @Override
    public void reload(ResourceManager manager) {
        CONFIGS.forEach(MercuryConfig::load);
    }

    public void registerConfig(MercuryConfig<?> config) {
        CONFIGS.add(config);
        config.load();
    }

    static {
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> CONFIGS.forEach(MercuryConfig::unload));
    }
}
