package com.iafenvoy.mercury.module.el;

import com.google.common.collect.ImmutableMap;
import com.iafenvoy.mercury.Mercury;
import com.iafenvoy.mercury.config.MercuryConfig;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Map;

public enum ELConfig implements MercuryConfig<Map<Enchantment, Integer>> {
    INSTANCE;
    private static final Map<Enchantment, Integer> DATA = new Object2IntArrayMap<>();

    public int getMaxLevel(Enchantment enchantment) {
        return DATA.getOrDefault(enchantment, enchantment.getMaxLevel());
    }

    @Override
    public void load(Map<Enchantment, Integer> data) {
        DATA.clear();
        DATA.putAll(data);
    }

    @Override
    public Map<Enchantment, Integer> getDefault() {
        return Registries.ENCHANTMENT.stream().<ImmutableMap.Builder<Enchantment, Integer>>reduce(ImmutableMap.builder(), (builder, enchantment) -> builder.put(enchantment, enchantment.getMaxLevel()), (a, b) -> null).build();
    }

    @Override
    public Codec<Map<Enchantment, Integer>> getCodec() {
        return Codec.unboundedMap(Registries.ENCHANTMENT.getCodec(), Codec.INT);
    }

    @Override
    public Identifier getId() {
        return Identifier.of(Mercury.MOD_ID, EnchantmentLimiterModule.MODULE_ID);
    }
}
