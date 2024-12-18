package com.iafenvoy.mercury;

import com.iafenvoy.mercury.module.MercuryModuleManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MercuryCommand {
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> dispatcher.register(CommandManager.literal(Mercury.MOD_ID)
                .requires(source -> source.hasPermissionLevel(4))
                .then(CommandManager.literal("modules")
                        .then(CommandManager.literal("enable")
                                .then(CommandManager.argument("module_name", IdentifierArgumentType.identifier())
                                        .suggests((context, builder) -> CommandSource.suggestIdentifiers(MercuryModuleManager.getAllIds(), builder))
                                        .executes(ctx -> {
                                            Identifier id = IdentifierArgumentType.getIdentifier(ctx, "module_name");
                                            ctx.getSource().sendFeedback(() -> Text.literal(MercuryModuleManager.setEnabled(id, true)), true);
                                            return 1;
                                        })))
                        .then(CommandManager.literal("disable")
                                .then(CommandManager.argument("module_name", IdentifierArgumentType.identifier())
                                        .suggests((context, builder) -> CommandSource.suggestIdentifiers(MercuryModuleManager.getAllIds(), builder))
                                        .executes(ctx -> {
                                            Identifier id = IdentifierArgumentType.getIdentifier(ctx, "module_name");
                                            ctx.getSource().sendFeedback(() -> Text.literal(MercuryModuleManager.setEnabled(id, false)), true);
                                            return 1;
                                        })))
                        .then(CommandManager.literal("list")
                                .executes(ctx -> {
                                    ctx.getSource().sendFeedback(() -> Text.literal(String.join(", ", MercuryModuleManager.getAllIds().stream().map(Identifier::toString).toArray(String[]::new))), false);
                                    return 1;
                                }))
                        .then(CommandManager.literal("enabled")
                                .executes(ctx -> {
                                    ctx.getSource().sendFeedback(() -> Text.literal(String.join(", ", MercuryModuleManager.getAllEnabled().stream().map(Identifier::toString).toArray(String[]::new))), false);
                                    return 1;
                                }))
                )));
    }
}
