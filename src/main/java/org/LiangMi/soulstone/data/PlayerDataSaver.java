package org.LiangMi.soulstone.data;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import org.LiangMi.soulstone.manager.GameStageManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 玩家数据保存器类
 * 继承自 PersistentState，用于跨游戏会话持久化保存玩家数据
 * 主要用于保存和恢复玩家的游戏阶段（GameStage）数据
 */
public class PlayerDataSaver extends PersistentState {

    /**
     * 玩家数据映射表
     * 键：玩家UUID（唯一标识符）
     * 值：玩家的NBT数据（包含游戏阶段等信息）
     */
    private final Map<UUID, NbtCompound> playersData = new HashMap<>();

    /**
     * 将玩家数据写入NBT以便保存到世界文件
     *
     * @param nbt 要写入的NBT复合标签
     * @return 包含玩家数据的NBT复合标签
     */
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        // 创建一个新的NBT复合标签来存储所有玩家数据
        NbtCompound playersNbt = new NbtCompound();

        // 遍历所有玩家数据，以UUID字符串为键存储到NBT中
        playersData.forEach((uuid, playerNbt) -> {
            playersNbt.put(uuid.toString(), playerNbt);
        });

        // 将玩家数据NBT放入主NBT中
        nbt.put("Players", playersNbt);
        return nbt;
    }

    /**
     * 从NBT数据创建 PlayerDataSaver 实例
     * 用于从世界文件加载已保存的玩家数据
     *
     * @param nbt 包含玩家数据的NBT复合标签
     * @return 从NBT数据恢复的 PlayerDataSaver 实例
     */
    public static PlayerDataSaver createFromNbt(NbtCompound nbt) {
        // 创建新的 PlayerDataSaver 实例
        PlayerDataSaver state = new PlayerDataSaver();

        // 获取玩家数据NBT
        NbtCompound playersNbt = nbt.getCompound("Players");

        // 遍历NBT中的所有键（每个键都是玩家的UUID字符串）
        for (String uuidString : playersNbt.getKeys()) {
            // 将字符串转换回UUID
            UUID uuid = UUID.fromString(uuidString);

            // 将玩家数据放入映射表
            state.playersData.put(uuid, playersNbt.getCompound(uuidString));
        }

        return state;
    }

    /**
     * 获取指定玩家的数据
     *
     * @param uuid 玩家的UUID
     * @return 玩家的NBT数据，如果不存在则返回空NBT
     */
    public NbtCompound getPlayerData(UUID uuid) {
        // 使用getOrDefault确保不会返回null
        return playersData.getOrDefault(uuid, new NbtCompound());
    }

    /**
     * 设置指定玩家的数据
     * 调用后会标记数据为脏（需要保存）
     *
     * @param uuid 玩家的UUID
     * @param data 要设置的玩家数据
     */
    public void setPlayerData(UUID uuid, NbtCompound data) {
        // 将玩家数据放入映射表
        playersData.put(uuid, data);

        // 标记数据为脏，以便Minecraft在下一次保存时机时保存到磁盘
        markDirty();
    }

    /**
     * 获取服务器的PlayerDataSaver状态
     * 如果不存在则创建新的
     *
     * @param server Minecraft服务器实例
     * @return 服务器的PlayerDataSaver状态
     */
    public static PlayerDataSaver getServerState(MinecraftServer server) {
        // 获取主世界的持久状态管理器
        PersistentStateManager persistentStateManager = server.getOverworld().getPersistentStateManager();

        // 获取或创建PlayerDataSaver实例
        return persistentStateManager.getOrCreate(
                PlayerDataSaver::createFromNbt,  // NBT数据解析方法
                PlayerDataSaver::new,           // 创建新实例的方法
                "gamestage_data"                // 在持久状态管理器中的唯一标识符
        );
    }

    /**
     * 注册服务器事件监听器
     * 在玩家加入和离开服务器时自动保存/加载玩家数据
     */
    public static void registerEvents() {
        // 注册玩家加入服务器事件
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            onPlayerJoin(handler.player, server);
        });

        // 注册玩家离开服务器事件
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            onPlayerDisconnect(handler.player, server);
        });
    }

    /**
     * 玩家加入服务器时的处理
     * 从持久状态中加载玩家的游戏阶段数据
     *
     * @param player 加入的玩家
     * @param server 服务器实例
     */
    private static void onPlayerJoin(ServerPlayerEntity player, MinecraftServer server) {
        // 获取服务器状态
        PlayerDataSaver serverState = getServerState(server);

        // 获取玩家的已保存数据
        NbtCompound playerData = serverState.getPlayerData(player.getUuid());

        // 获取玩家的游戏阶段管理器
        GameStageManager.PlayerGameStage stage = GameStageManager.getPlayerStage(player);

        // 如果存在已保存的数据，则从中恢复游戏阶段
        if (playerData != null && !playerData.isEmpty()) {
            stage.readFromNbt(playerData);
        }
    }

    /**
     * 玩家离开服务器时的处理
     * 将玩家的游戏阶段数据保存到持久状态
     *
     * @param player 离开的玩家
     * @param server 服务器实例
     */
    private static void onPlayerDisconnect(ServerPlayerEntity player, MinecraftServer server) {
        // 获取服务器状态
        PlayerDataSaver serverState = getServerState(server);

        // 获取玩家的游戏阶段管理器
        GameStageManager.PlayerGameStage stage = GameStageManager.getPlayerStage(player);

        // 创建新的NBT来存储玩家数据
        NbtCompound playerData = new NbtCompound();

        // 将游戏阶段数据写入NBT
        stage.writeToNbt(playerData);

        // 保存玩家数据到服务器状态
        serverState.setPlayerData(player.getUuid(), playerData);

        // 从内存中移除玩家的游戏阶段数据（可选，防止内存泄漏）
        GameStageManager.removePlayerStage(player.getUuid());
    }
}
