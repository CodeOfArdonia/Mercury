package com.iafenvoy.mercury.mixin;

import com.iafenvoy.mercury.module.MercuryModuleManager;
import com.iafenvoy.mercury.module.banitem.BanItemConfig;
import com.iafenvoy.mercury.module.el.ELConfig;
import com.iafenvoy.mercury.module.el.EnchantmentLimiterModule;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "inventoryTick", at = @At("RETURN"))
    private void checkEnchantment(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (BanItemConfig.INSTANCE.shouldRemove(stack)) {
            stack.setCount(0);
            return;
        }
        if (!MercuryModuleManager.isEnabled(EnchantmentLimiterModule.ID)) return;
        int interval = Math.max(world.getGameRules().getInt(EnchantmentLimiterModule.CHECK_INTERVAL), 1);
        if (entity.age % interval != 0) return;
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
        boolean modified = false;
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            int allowMax = ELConfig.INSTANCE.getMaxLevel(entry.getKey());
            if (allowMax < entry.getValue()) {
                enchantments.put(entry.getKey(), allowMax);
                modified = true;
            }
        }
        if (modified) EnchantmentHelper.set(enchantments, stack);
    }
}
