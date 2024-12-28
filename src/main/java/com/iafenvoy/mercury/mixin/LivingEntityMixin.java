package com.iafenvoy.mercury.mixin;

import com.iafenvoy.mercury.module.MercuryModuleManager;
import com.iafenvoy.mercury.module.extradamage.ExtraDamageManager;
import com.iafenvoy.mercury.module.extradamage.ExtraDamageModule;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "tick", at = @At("RETURN"))
    private void onLivingTick(CallbackInfo ci) {
        if (!MercuryModuleManager.isEnabled(ExtraDamageModule.ID)) return;
        ExtraDamageManager.apply((LivingEntity) (Object) this);
    }
}
