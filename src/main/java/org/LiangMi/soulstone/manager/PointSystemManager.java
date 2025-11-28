package org.LiangMi.soulstone.manager;


import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;
import org.LiangMi.soulstone.data.PlayerPointData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
public class PointSystemManager extends PersistentState {
    private final Map<UUID, PlayerPointData> playerPointData = new HashMap<>();

    public static PointSystemManager create() {
        return new PointSystemManager();
    }

    public PlayerPointData getPlayerData(UUID playerId) {
        return playerPointData.computeIfAbsent(playerId, k -> new PlayerPointData());
    }

    public void setPlayerData(UUID playerId, PlayerPointData data) {
        playerPointData.put(playerId, data);
        this.markDirty();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound playersNbt = new NbtCompound();
        for (Map.Entry<UUID, PlayerPointData> entry : playerPointData.entrySet()) {
            playersNbt.put(entry.getKey().toString(), entry.getValue().toNbt());
        }
        nbt.put("playerPointData", playersNbt);
        return nbt;
    }

    public static PointSystemManager fromNbt(NbtCompound nbt) {
        PointSystemManager manager = create();
        if (nbt.contains("playerPointData")) {
            NbtCompound playersNbt = nbt.getCompound("playerPointData");
            for (String key : playersNbt.getKeys()) {
                UUID playerId = UUID.fromString(key);
                PlayerPointData data = PlayerPointData.fromNbt(playersNbt.getCompound(key));
                manager.playerPointData.put(playerId, data);
            }
        }
        return manager;
    }
}
