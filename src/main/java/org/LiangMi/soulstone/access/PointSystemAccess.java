package org.LiangMi.soulstone.access;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.LiangMi.soulstone.data.PlayerPointData;
import org.LiangMi.soulstone.manager.PointSystemManager;
import org.LiangMi.soulstone.system.PointAttributeSystem;

public class PointSystemAccess {
    private static final String DATA_NAME = "point_system";

    public static PointSystemManager getPointManager(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        return persistentStateManager.getOrCreate(
                PointSystemManager::fromNbt,
                PointSystemManager::create,
                DATA_NAME
        );
    }

    public static PlayerPointData getPlayerData(PlayerEntity player) {
        if (player.getWorld().isClient) {
            return new PlayerPointData(); // 客户端返回默认值
        }
        PointSystemManager manager = getPointManager(player.getServer());
        return manager.getPlayerData(player.getUuid());
    }

    public static void setPlayerData(PlayerEntity player, PlayerPointData data) {
        if (player.getWorld().isClient) return;

        PointSystemManager manager = getPointManager(player.getServer());
        manager.setPlayerData(player.getUuid(), data);
    }

    public static void addPoints(PlayerEntity player, int points) {
        PlayerPointData data = getPlayerData(player);
        data.addPoints(points);
        setPlayerData(player, data);
        PointAttributeSystem.updatePlayerAttributes((ServerPlayerEntity) player);
    }

    public static boolean assignPoints(PlayerEntity player, String attribute, int amount) {
        PlayerPointData data = getPlayerData(player);
        boolean success = data.assignPoint(attribute, amount);
        if (success) {
            setPlayerData(player, data);
            PointAttributeSystem.updatePlayerAttributes((ServerPlayerEntity) player);
        }
        return success;
    }

    public static boolean resetPoints(PlayerEntity player) {
        PlayerPointData data = getPlayerData(player);
        boolean success = data.resetPoints();
        if (success) {
            setPlayerData(player, data);
            PointAttributeSystem.updatePlayerAttributes((ServerPlayerEntity) player);
        }
        return success;
    }
}
