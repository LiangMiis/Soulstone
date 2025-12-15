package org.LiangMi.soulstone.network.s2c;


import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import net.minecraft.util.Identifier;
import io.netty.buffer.Unpooled;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.access.PointSystemAccess;
import org.LiangMi.soulstone.data.PlayerPointData;
import org.LiangMi.soulstone.manager.GameStageManager;

/**
 * 点数系统服务器端网络通信处理器
 * 负责处理客户端发送的点数分配、重置和界面请求等网络包
 * 注意：所有网络操作必须在服务器线程上执行
 */
public class PointServerNetworking {

    // ==================== 网络包标识符定义 ====================

    /** 分配点数包标识符 - 客户端向服务器请求分配属性点 */
    private static final Identifier ASSIGN_POINT_PACKET = new Identifier(Soulstone.ID, "assign_point");

    /** 重置点数包标识符 - 客户端向服务器请求重置所有属性点 */
    private static final Identifier RESET_POINTS_PACKET = new Identifier(Soulstone.ID, "reset_points");

    /** 打开界面请求包标识符 - 客户端请求打开点数分配界面 */
    private static final Identifier OPEN_SCREEN_REQUEST = new Identifier(Soulstone.ID, "open_point_screen_request");

    // ==================== 公共方法 ====================

    /**
     * 注册所有服务器端的网络包接收器
     * 该方法应在服务器启动时调用一次
     */
    public static void registerServerReceivers() {
        // 注册点数分配包处理器
        ServerPlayNetworking.registerGlobalReceiver(ASSIGN_POINT_PACKET,
                PointServerNetworking::handleAssignPoint);

        // 注册重置点数包处理器
        ServerPlayNetworking.registerGlobalReceiver(RESET_POINTS_PACKET,
                PointServerNetworking::handleResetPoints);

        // 注册打开界面请求包处理器
        ServerPlayNetworking.registerGlobalReceiver(OPEN_SCREEN_REQUEST,
                PointServerNetworking::handleOpenScreenRequest);
    }

    // ==================== 网络包处理器 ====================

    /**
     * 处理分配点数请求
     * 客户端发送：属性名称和要分配的点数
     * 服务器响应：更新玩家数据并发送更新回客户端
     *
     * @param server Minecraft服务器实例
     * @param player 发送请求的玩家
     * @param handler 网络处理器
     * @param buf 包含请求数据的缓冲区
     * @param responseSender 用于发送响应的发送器
     */
    private static void handleAssignPoint(MinecraftServer server, ServerPlayerEntity player,
                                          ServerPlayNetworkHandler handler,
                                          PacketByteBuf buf,
                                          PacketSender responseSender) {
        // 从缓冲区读取数据
        String attribute = buf.readString();  // 属性名称（如：strength, agility等）
        int amount = buf.readInt();           // 要分配的点数

        // 在服务器线程上执行实际操作（网络包处理可能在网络线程）
        server.execute(() -> {
            // 调用点数系统分配点数
            boolean success = PointSystemAccess.assignPoints(player, attribute, amount);

            if (success) {
                // 分配成功后，发送更新后的数据回客户端以同步状态
                sendPointDataUpdate(player);
            }
            // 注意：如果失败，可以选择发送错误消息给客户端
        });
    }

    /**
     * 处理重置点数请求
     * 客户端发送：重置请求（无附加数据）
     * 服务器响应：重置玩家所有属性点并发送更新回客户端
     *
     * @param server Minecraft服务器实例
     * @param player 发送请求的玩家
     * @param handler 网络处理器
     * @param buf 包含请求数据的缓冲区
     * @param responseSender 用于发送响应的发送器
     */
    private static void handleResetPoints(MinecraftServer server, ServerPlayerEntity player,
                                          ServerPlayNetworkHandler handler,
                                          PacketByteBuf buf,
                                          PacketSender responseSender) {
        // 在服务器线程上执行实际操作
        server.execute(() -> {
            // 调用点数系统重置所有点数
            boolean success = PointSystemAccess.resetPoints(player);

            if (success) {
                // 重置成功后，发送更新后的数据回客户端
                sendPointDataUpdate(player);
            }
        });
    }

    /**
     * 处理打开界面请求
     * 客户端发送：打开点数分配界面的请求
     * 服务器响应：发送当前玩家的点数数据给客户端
     *
     * @param server Minecraft服务器实例
     * @param player 发送请求的玩家
     * @param handler 网络处理器
     * @param buf 包含请求数据的缓冲区
     * @param responseSender 用于发送响应的发送器
     */
    private static void handleOpenScreenRequest(MinecraftServer server, ServerPlayerEntity player,
                                                ServerPlayNetworkHandler handler,
                                                PacketByteBuf buf,
                                                PacketSender responseSender) {
        // 在服务器线程上执行
        server.execute(() -> {
            // 当客户端请求打开界面时，发送当前玩家的点数数据
            sendPointDataUpdate(player);
        });
    }

    // ==================== 数据发送方法 ====================

    /**
     * 发送点数数据更新到客户端
     * 包含：可用点数、当前游戏等级和所有已分配的点数
     *
     * @param player 要发送数据的目标玩家
     */
    public static void sendPointDataUpdate(ServerPlayerEntity player) {
        // 获取玩家的点数数据和游戏等级
        PlayerPointData data = PointSystemAccess.getPlayerData(player);
        int gameLv = GameStageManager.getCurrentLevel(player);

        // 创建网络数据缓冲区
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        // 写入可用点数
        buf.writeInt(data.getAvailablePoints());

        // 写入已分配的属性点信息
        java.util.Map<String, Integer> assignedPoints = data.getAllAssignedPoints();

        // 先写入映射的大小，以便客户端知道要读取多少个条目
        buf.writeInt(assignedPoints.size());

        // 写入玩家的游戏等级
        buf.writeInt(gameLv);

        // 写入每个属性的分配点数
        for (java.util.Map.Entry<String, Integer> entry : assignedPoints.entrySet()) {
            buf.writeString(entry.getKey());    // 属性名称
            buf.writeInt(entry.getValue());     // 已分配的点数
        }

        // 发送数据包到客户端
        ServerPlayNetworking.send(player, new Identifier(Soulstone.ID, "point_data_update"), buf);
    }
}