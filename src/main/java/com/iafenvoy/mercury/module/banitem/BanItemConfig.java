package com.iafenvoy.mercury.module.banitem;

import com.iafenvoy.mercury.Mercury;
import com.iafenvoy.mercury.config.MercuryConfig;
import com.iafenvoy.mercury.module.MercuryModuleManager;
import com.mojang.serialization.Codec;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;

public enum BanItemConfig implements MercuryConfig<List<Item>> {
    INSTANCE;
    private static final List<Item> DATA = new LinkedList<>();
    private static final TagKey<Item> BAN_ITEM = TagKey.of(RegistryKeys.ITEM, Identifier.of(Mercury.MOD_ID, "banned_item"));

    public boolean shouldRemove(ItemStack stack) {
        return MercuryModuleManager.isEnabled(BanItemModule.ID) && (DATA.contains(stack.getItem()) || stack.isIn(BAN_ITEM));
    }

    @Override
    public void load(List<Item> data) {
        DATA.clear();
        DATA.addAll(data);
    }

    @Override
    public List<Item> getDefault() {
        return List.of();
    }

    @Override
    public Codec<List<Item>> getCodec() {
        return Registries.ITEM.getCodec().listOf();
    }

    @Override
    public Identifier getId() {
        return BanItemModule.ID;
    }
}
