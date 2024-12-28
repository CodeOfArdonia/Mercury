package com.iafenvoy.mercury.module.extradamage;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

public class ExtraDamageManager {
    private static final List<DamageProcessor> CONSUMERS = new LinkedList<>();
    private static final TagKey<Block> STONECUTTERS = c("stonecutters");
    private static final TagKey<Block> TORCHES = c("torches");

    private static TagKey<Block> c(String id) {
        return TagKey.of(RegistryKeys.BLOCK, Identifier.of("c", id));
    }

    public static void apply(LivingEntity living) {
        CONSUMERS.forEach(x -> x.process(living, living.getWorld(), living.getDamageSources()));
    }

    public static void add(DamageProcessor consumer) {
        CONSUMERS.add(consumer);
    }

    static {
        add((living, world, damageSources) -> {
            BlockPos pos = living.getBlockPos();
            if (world.getBlockState(pos).isIn(STONECUTTERS) || world.getBlockState(pos.down()).isIn(STONECUTTERS))
                living.damage(damageSources.generic(), 1);
        });
        add((living, world, damageSources) -> {
            BlockPos pos = living.getBlockPos();
            if (world.getBlockState(pos).isIn(TORCHES) || world.getBlockState(pos.up()).isIn(TORCHES)) {
                living.damage(damageSources.inFire(), 1);
                living.setFireTicks(1);
            }
        });
    }

    @FunctionalInterface
    public interface DamageProcessor {
        void process(LivingEntity living, World world, DamageSources damageSources);
    }
}
