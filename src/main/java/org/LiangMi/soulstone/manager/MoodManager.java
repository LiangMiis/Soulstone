package org.LiangMi.soulstone.manager;


import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;
import org.LiangMi.soulstone.data.PlayerMoodData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
public class MoodManager extends PersistentState {
    private final Map<UUID, PlayerMoodData> playerMoodData = new HashMap<>();

    public static MoodManager create() {
        return new MoodManager();
    }

    public PlayerMoodData getPlayerMood(UUID playerId) {
        return playerMoodData.computeIfAbsent(playerId, k -> new PlayerMoodData());
    }

    public void setPlayerMood(UUID playerId, PlayerMoodData moodData) {
        playerMoodData.put(playerId, moodData);
        this.markDirty();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound moodNbt = new NbtCompound();
        for (Map.Entry<UUID, PlayerMoodData> entry : playerMoodData.entrySet()) {
            moodNbt.put(entry.getKey().toString(), entry.getValue().toNbt());
        }
        nbt.put("playerMoodData", moodNbt);
        return nbt;
    }

    public static MoodManager fromNbt(NbtCompound nbt) {
        MoodManager manager = create();
        if (nbt.contains("playerMoodData")) {
            NbtCompound moodNbt = nbt.getCompound("playerMoodData");
            for (String key : moodNbt.getKeys()) {
                UUID playerId = UUID.fromString(key);
                PlayerMoodData moodData = PlayerMoodData.fromNbt(moodNbt.getCompound(key));
                manager.playerMoodData.put(playerId, moodData);
            }
        }
        return manager;
    }
}
