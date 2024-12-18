package com.iafenvoy.mercury.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.iafenvoy.mercury.Mercury;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;

public interface MercuryConfig<T> {
    String BASE_PATH = "./config/%s/%s.json";
    Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    void load(T data);

    T getDefault();

    Codec<T> getCodec();

    Identifier getId();

    default String getPath() {
        Identifier id = this.getId();
        return BASE_PATH.formatted(id.getNamespace(), id.getPath());
    }

    default void unload() {
    }

    default void load() {
        try {
            this.load(this.getCodec().parse(JsonOps.INSTANCE, JsonParser.parseReader(new FileReader(this.getPath()))).resultOrPartial(Mercury.LOGGER::error).orElse(this.getDefault()));
        } catch (Exception e) {
            Mercury.LOGGER.error("Failed to load config {} from {}", this.getId(), this.getPath(), e);
            try {
                FileUtils.write(new File(this.getPath()), this.getCodec().encodeStart(JsonOps.INSTANCE, this.getDefault()).resultOrPartial(Mercury.LOGGER::error).map(GSON::toJson).orElse("{}"), StandardCharsets.UTF_8);
            } catch (Exception ex) {
                Mercury.LOGGER.error("Failed to save config {} to {}", this.getId(), this.getPath(), e);
            }
        }
    }
}
