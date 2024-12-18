package com.iafenvoy.mercury.module;

import com.iafenvoy.mercury.config.MercuryConfig;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public interface MercuryModule {
    Identifier getId();

    default void load() {
    }

    default void registerConfig(Consumer<MercuryConfig<?>> registry) {
    }

    default void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment, BooleanSupplier enabled) {
    }

    default boolean shouldReload() {
        return false;
    }
}
