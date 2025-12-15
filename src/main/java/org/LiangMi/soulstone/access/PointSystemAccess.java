package org.LiangMi.soulstone.access;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.LiangMi.soulstone.data.PlayerPointData;
import org.LiangMi.soulstone.manager.PointSystemManager;
import org.LiangMi.soulstone.system.PointAttributeSystem;

/**
 * 点数系统访问类
 * 提供对玩家点数系统的统一访问接口，处理客户端和服务器的数据同步
 * 管理玩家的点数获取、分配、重置以及属性更新
 */
public class PointSystemAccess {

    /**
     * 持久化数据的唯一标识符
     * 用于在持久状态管理器中识别点数系统数据
     */
    private static final String DATA_NAME = "point_system";

    /**
     * 获取服务器的点数系统管理器
     * 如果不存在则创建新的管理器实例
     *
     * @param server Minecraft服务器实例
     * @return 点数系统管理器
     */
    public static PointSystemManager getPointManager(MinecraftServer server) {
        // 获取主世界的持久状态管理器
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        // 获取或创建点数系统管理器实例
        return persistentStateManager.getOrCreate(
                PointSystemManager::fromNbt,  // 从NBT数据反序列化的方法引用
                PointSystemManager::create,   // 创建新实例的方法引用
                DATA_NAME                     // 数据标识符
        );
    }

    /**
     * 获取玩家的点数数据
     * 客户端返回默认值，服务器返回持久化数据
     *
     * @param player 玩家实体
     * @return 玩家的点数数据（客户端返回空数据）
     */
    public static PlayerPointData getPlayerData(PlayerEntity player) {
        // 客户端检查：客户端不处理持久化数据
        if (player.getWorld().isClient) {
            return new PlayerPointData(); // 客户端返回新的默认数据实例
        }

        // 服务器端：从管理器获取玩家数据
        PointSystemManager manager = getPointManager(player.getServer());
        return manager.getPlayerData(player.getUuid());
    }

    /**
     * 设置玩家的点数数据（仅限服务器端）
     *
     * @param player 玩家实体
     * @param data 要设置的点数数据
     */
    public static void setPlayerData(PlayerEntity player, PlayerPointData data) {
        // 客户端不执行任何操作
        if (player.getWorld().isClient) return;

        // 服务器端：更新玩家数据到管理器
        PointSystemManager manager = getPointManager(player.getServer());
        manager.setPlayerData(player.getUuid(), data);
    }

    /**
     * 为玩家添加点数并更新属性
     *
     * @param player 玩家实体
     * @param points 要添加的点数
     */
    public static void addPoints(PlayerEntity player, int points) {
        // 获取玩家当前点数数据
        PlayerPointData data = getPlayerData(player);

        // 添加点数
        data.addPoints(points);

        // 保存更新后的数据
        setPlayerData(player, data);

        // 更新玩家的属性（基于点数分配）
        PointAttributeSystem.updatePlayerAttributes((ServerPlayerEntity) player);
    }

    /**
     * 分配点数到指定属性
     *
     * @param player 玩家实体
     * @param attribute 属性名称
     * @param amount 分配的点数
     * @return 分配是否成功
     */
    public static boolean assignPoints(PlayerEntity player, String attribute, int amount) {
        // 获取玩家当前点数数据
        PlayerPointData data = getPlayerData(player);

        // 尝试分配点数
        boolean success = data.assignPoint(attribute, amount);

        // 如果分配成功，保存数据并更新属性
        if (success) {
            setPlayerData(player, data);
            PointAttributeSystem.updatePlayerAttributes((ServerPlayerEntity) player);
        }

        return success;
    }

    /**
     * 重置玩家已分配的点数
     *
     * @param player 玩家实体
     * @return 重置是否成功
     */
    public static boolean resetPoints(PlayerEntity player) {
        // 获取玩家当前点数数据
        PlayerPointData data = getPlayerData(player);

        // 尝试重置点数
        boolean success = data.resetPoints();

        // 如果重置成功，保存数据并更新属性
        if (success) {
            setPlayerData(player, data);
            PointAttributeSystem.updatePlayerAttributes((ServerPlayerEntity) player);
        }

        return success;
    }
}
