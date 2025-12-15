package org.LiangMi.soulstone.manager;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;
import org.LiangMi.soulstone.data.PlayerPointData;
import org.LiangMi.soulstone.data.PlayerSpellData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpellSystemManager extends PersistentState {
    private final Map<UUID, PlayerSpellData> playerSpellData = new HashMap<>();
    public static SpellSystemManager create(){return new SpellSystemManager();}

    public PlayerSpellData getPlayerData(UUID playerId) {
        return playerSpellData.computeIfAbsent(playerId, k -> new PlayerSpellData());
    }

    public void setPlayerData(UUID playerId, PlayerSpellData data) {
        playerSpellData.put(playerId, data);
        this.markDirty();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound playersNbt = new NbtCompound();
        for (Map.Entry<UUID, PlayerSpellData> entry : playerSpellData.entrySet()) {
            playersNbt.put(entry.getKey().toString(), entry.getValue().toNbt());
        }
        nbt.put("playerSpellData", playersNbt);
        return nbt;
    }

    public static SpellSystemManager fromNbt(NbtCompound nbt) {
        SpellSystemManager manager = create();
        if (nbt.contains("playerSpellData")) {
            NbtCompound playersNbt = nbt.getCompound("playerSpellData");
            for (String key : playersNbt.getKeys()) {
                UUID playerId = UUID.fromString(key);
                PlayerSpellData data = PlayerSpellData.fromNbt(playersNbt.getCompound(key));
                manager.playerSpellData.put(playerId, data);
            }
        }
        return manager;
    }
}
