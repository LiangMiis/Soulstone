package org.LiangMi.soulstone.access;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.LiangMi.soulstone.data.PlayerMoodData;
import org.LiangMi.soulstone.manager.MoodManager;

public class MoodAccess {
    private static final String DATA_NAME = "mood_manager";

    public static MoodManager getMoodManager(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        return persistentStateManager.getOrCreate(
                MoodManager::fromNbt,
                MoodManager::create,
                DATA_NAME
        );
    }

    public static PlayerMoodData getPlayerMood(PlayerEntity player) {
        if (player.getWorld().isClient) {
            return new PlayerMoodData(); // 客户端返回默认值
        }
        MoodManager manager = getMoodManager(player.getServer());
        return manager.getPlayerMood(player.getUuid());
    }

    public static void setPlayerMood(PlayerEntity player, PlayerMoodData moodData) {
        if (player.getWorld().isClient) return;

        MoodManager manager = getMoodManager(player.getServer());
        manager.setPlayerMood(player.getUuid(), moodData);
    }

    public static float getMoodMultiplier(PlayerEntity player) {
        return getPlayerMood(player).getAttributeMultiplier();
    }
}
