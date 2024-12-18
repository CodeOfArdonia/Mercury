package com.iafenvoy.mercury.mixin;

import com.iafenvoy.mercury.module.MercuryModuleManager;
import com.iafenvoy.mercury.module.betterop.BetterOpModule;
import com.iafenvoy.mercury.module.betterop.NewOpCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.command.OpCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OpCommand.class)
public class OpCommandMixin {
    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static void registerOwnOpCommand(CommandDispatcher<ServerCommandSource> dispatcher, CallbackInfo ci) {
        if (MercuryModuleManager.isEnabled(BetterOpModule.ID)) {
            NewOpCommand.register(dispatcher);
            ci.cancel();
        }
    }
}
