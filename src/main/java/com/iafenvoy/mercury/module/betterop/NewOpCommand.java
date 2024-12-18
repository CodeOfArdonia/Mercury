package com.iafenvoy.mercury.module.betterop;

import com.iafenvoy.mercury.mixin.ServerCommandSourceAccessor;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.OperatorList;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Optional;

public class NewOpCommand {
    private static final SimpleCommandExceptionType ALREADY_OPPED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.op.failed"));
    private static final SimpleCommandExceptionType PERMISSION_DENIED = new SimpleCommandExceptionType(Text.literal("Permission Denied"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("op")
                .requires(source -> source.hasPermissionLevel(1))
                .then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile())
                        .suggests((ctx, builder) -> {
                            ServerCommandSource source = ctx.getSource();
                            int level = getPermission(source);
                            PlayerManager playerManager = source.getServer().getPlayerManager();
                            return CommandSource.suggestMatching(playerManager.getPlayerList().stream().filter(player -> player.hasPermissionLevel(level)).map((player) -> player.getGameProfile().getName()), builder);
                        })
                        .then(CommandManager.literal("get")
                                .executes(ctx -> opGet(ctx.getSource(), GameProfileArgumentType.getProfileArgument(ctx, "targets"))))
                        .then(CommandManager.literal("set")
                                .then(CommandManager.argument("level", IntegerArgumentType.integer(1, 4))
                                        .executes(ctx -> opSet(ctx.getSource(), GameProfileArgumentType.getProfileArgument(ctx, "targets"), IntegerArgumentType.getInteger(ctx, "level")))
                                )))
        );
    }

    private static int getPermission(ServerCommandSource source) {
        PlayerManager playerManager = source.getServer().getPlayerManager();
        CommandOutput output = ((ServerCommandSourceAccessor) source).getOutput();
        if (output instanceof MinecraftServer) return 5;
        else if (output instanceof ServerPlayerEntity serverPlayer)
            return Optional.of(serverPlayer).map(x -> playerManager.getOpList().get(x.getGameProfile())).map(OperatorEntry::getPermissionLevel).orElse(0);
        else return 0;
    }

    private static int opSet(ServerCommandSource source, Collection<GameProfile> targets, int level) throws CommandSyntaxException {
        PlayerManager playerManager = source.getServer().getPlayerManager();
        int l = getPermission(source);
        if (l <= level) throw PERMISSION_DENIED.create();
        OperatorList opList = playerManager.getOpList();
        int i = 0;
        for (GameProfile profile : targets) {
            OperatorEntry entry = opList.get(profile);
            if (entry != null && entry.getPermissionLevel() == level) continue;
            opList.add(new OperatorEntry(profile, level, opList.canBypassPlayerLimit(profile)));
            ServerPlayerEntity player = playerManager.getPlayer(profile.getId());
            if (player != null) playerManager.sendCommandTree(player);
            source.sendFeedback(() -> Text.translatable("commands.op.success", targets.iterator().next().getName()), true);
            source.sendFeedback(() -> Text.literal("Now %s's permission is %d".formatted(profile.getName(), level)), true);
            i++;
        }
        if (i == 0) throw ALREADY_OPPED_EXCEPTION.create();
        else return i;
    }

    private static int opGet(ServerCommandSource source, Collection<GameProfile> targets) throws CommandSyntaxException {
        PlayerManager playerManager = source.getServer().getPlayerManager();
        OperatorList opList = playerManager.getOpList();
        int l = getPermission(source);
        int i = 0;
        for (GameProfile profile : targets) {
            OperatorEntry entry = opList.get(profile);
            if (entry == null || entry.getPermissionLevel() > l) continue;
            source.sendFeedback(() -> Text.literal("%s's permission is %d".formatted(profile.getName(), entry.getPermissionLevel())), false);
            i++;
        }
        if (i == 0) throw PERMISSION_DENIED.create();
        else return i;
    }
}
