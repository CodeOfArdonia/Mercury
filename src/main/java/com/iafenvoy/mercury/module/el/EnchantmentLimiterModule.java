package com.iafenvoy.mercury.module.el;

import com.iafenvoy.mercury.Mercury;
import com.iafenvoy.mercury.config.MercuryConfig;
import com.iafenvoy.mercury.module.MercuryModule;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

import java.util.function.Consumer;

public final class EnchantmentLimiterModule implements MercuryModule {
    public static final String MODULE_ID = "enchantment_limiter";
    public static final Identifier ID = Identifier.of(Mercury.MOD_ID, MODULE_ID);
    public static final GameRules.Key<GameRules.IntRule> CHECK_INTERVAL = GameRuleRegistry.register("%s:interval".formatted(MODULE_ID), GameRules.Category.UPDATES, GameRuleFactory.createIntRule(20));

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void registerConfig(Consumer<MercuryConfig<?>> registry) {
        registry.accept(ELConfig.INSTANCE);
    }
}
