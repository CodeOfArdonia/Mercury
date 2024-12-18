package com.iafenvoy.mercury.module.tour;

import com.iafenvoy.mercury.Mercury;
import com.iafenvoy.mercury.module.MercuryModule;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.util.function.BooleanSupplier;

public class TourModule implements MercuryModule {
    public static final String MODULE_ID = "tour";
    public static final Identifier ID = Identifier.of(Mercury.MOD_ID, MODULE_ID);

    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment, BooleanSupplier enabled) {
        if (enabled.getAsBoolean())
            dispatcher.register(CommandManager.literal(MODULE_ID)
                    .requires(ServerCommandSource::isExecutedByPlayer)
                    .requires(source -> enabled.getAsBoolean())
                    .executes(ctx -> {
                        ServerCommandSource source = ctx.getSource();
                        ServerPlayerEntity player = source.getPlayerOrThrow();
                        TourWorldData data = TourWorldData.get(source.getWorld());
                        if (player.isSpectator()) {
                            Pair<Vec3d, GameMode> tourData = data.getAndRemove(player.getUuid());
                            if (tourData == null) return 0;
                            Vec3d pos = tourData.getLeft();
                            player.teleport(pos.x, pos.y, pos.z);
                            player.changeGameMode(tourData.getRight());
                        } else {
                            data.set(player.getUuid(), player.getPos(), player.interactionManager.getGameMode());
                            player.changeGameMode(GameMode.SPECTATOR);
                        }
                        return 1;
                    }));
    }

    @Override
    public boolean shouldReload() {
        return true;
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
