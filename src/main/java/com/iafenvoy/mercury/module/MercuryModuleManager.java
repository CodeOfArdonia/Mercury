package com.iafenvoy.mercury.module;

import com.iafenvoy.mercury.Mercury;
import com.iafenvoy.mercury.config.ConfigManager;
import com.iafenvoy.mercury.config.MercuryConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MercuryModuleManager {
    private static final Map<Identifier, MercuryModule> MODULES = new HashMap<>();
    private static final List<Identifier> ENABLED = new LinkedList<>();

    public static void loadAll() {
        ConfigManager.INSTANCE.registerConfig(StatesConfig.INSTANCE);
        for (MercuryModule module : FabricLoader.getInstance().getEntrypoints("mercury-module", MercuryModule.class)) {
            if (MODULES.put(module.getId(), module) != null)
                throw new RuntimeException("Duplicate module id: " + module.getId());
            module.registerConfig(ConfigManager.INSTANCE::registerConfig);
            module.load();
        }
        Mercury.LOGGER.info("Loaded {} modules: {}", MODULES.size(), String.join(", ", MODULES.keySet().stream().map(Identifier::toString).toArray(String[]::new)));
        CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> {
            for (Map.Entry<Identifier, MercuryModule> entry : MODULES.entrySet())
                entry.getValue().registerCommand(dispatcher, access, environment, () -> isEnabled(entry.getKey()));
        });
    }

    public static Set<Identifier> getAllIds() {
        return MODULES.keySet();
    }

    public static String setEnabled(Identifier id, boolean enable) {
        MercuryModule module = MODULES.get(id);
        if (module == null) return "Cannot find this module.";
        if (enable) {
            if (ENABLED.contains(id)) return "Module %s is already enabled.".formatted(id.toString());
            else {
                ENABLED.add(id);
                if (module.shouldReload())
                    return "Module %s enabled. Need to reload before take effect.".formatted(id.toString());
                return "Module %s enabled.".formatted(id.toString());
            }
        } else {
            if (ENABLED.contains(id)) {
                ENABLED.remove(id);
                if (module.shouldReload())
                    return "Module %s disabled. Need to reload before take effect.".formatted(id.toString());
                return "Module %s disabled.".formatted(id.toString());
            } else return "Module %s is already disabled.".formatted(id.toString());
        }
    }

    public static boolean isEnabled(Identifier id) {
        return ENABLED.contains(id);
    }

    public static List<Identifier> getAllEnabled() {
        return List.copyOf(ENABLED);
    }

    private enum StatesConfig implements MercuryConfig<List<Identifier>> {
        INSTANCE;

        @Override
        public void load(List<Identifier> data) {
            ENABLED.clear();
            ENABLED.addAll(data);
        }

        @Override
        public List<Identifier> getDefault() {
            return List.of();
        }

        @Override
        public Codec<List<Identifier>> getCodec() {
            return Identifier.CODEC.listOf();
        }

        @Override
        public Identifier getId() {
            return Identifier.of(Mercury.MOD_ID, "enabled_modules");
        }

        @Override
        public void unload() {
            try {
                FileUtils.write(new File(this.getPath()), this.getCodec().encodeStart(JsonOps.INSTANCE, ENABLED).resultOrPartial(Mercury.LOGGER::error).map(GSON::toJson).orElseThrow(), StandardCharsets.UTF_8);
            } catch (Exception e) {
                Mercury.LOGGER.error("Failed to save config {} to {}", this.getId(), this.getPath(), e);
            }
        }
    }
}
