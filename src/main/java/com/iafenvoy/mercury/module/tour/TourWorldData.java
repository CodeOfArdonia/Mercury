package com.iafenvoy.mercury.module.tour;

import com.iafenvoy.mercury.Mercury;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TourWorldData extends PersistentState {
    private static final Codec<Map<UUID, TourData>> CODEC = Codec.unboundedMap(Uuids.CODEC, RecordCodecBuilder.create(i -> i.group(
            Vec3d.CODEC.fieldOf("pos").forGetter(TourData::pos),
            StringIdentifiable.createCodec(GameMode::values).fieldOf("prev").forGetter(TourData::prev)
    ).apply(i, TourData::new)));
    private final Map<UUID, TourData> data;

    public TourWorldData() {
        this(Map.of());
    }

    private TourWorldData(Map<UUID, TourData> data) {
        this.data = new HashMap<>(data);
    }

    public static TourWorldData fromNbt(NbtCompound nbt) {
        return new TourWorldData(CODEC.parse(NbtOps.INSTANCE, nbt.get("data")).resultOrPartial(Mercury.LOGGER::error).orElseGet(Map::of));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.put("data", CODEC.encodeStart(NbtOps.INSTANCE, this.data).resultOrPartial(Mercury.LOGGER::error).orElse(new NbtCompound()));
        return nbt;
    }

    public static TourWorldData get(ServerWorld world) {
        PersistentStateManager storage = world.getPersistentStateManager();
        return storage.getOrCreate(TourWorldData::fromNbt, TourWorldData::new, Mercury.MOD_ID + "_tour");
    }

    @Nullable
    public Pair<Vec3d, GameMode> getAndRemove(UUID uuid) {
        TourData d = this.data.remove(uuid);
        return d == null ? null : new Pair<>(d.pos, d.prev);
    }

    public void set(UUID uuid, Vec3d pos, GameMode prev) {
        this.data.put(uuid, new TourData(pos, prev));
    }

    private record TourData(Vec3d pos, GameMode prev) {
    }
}
