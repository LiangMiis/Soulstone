package org.LiangMi.soulstone.network.c2s;


import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.client.screen.PointScreen;


public class PointClientNetworking {

    // ========== 数据包标识符定义 ==========
    // 每个数据包类型都有唯一的标识符，用于区分不同的网络消息

    /**
     * 分配点数数据包标识符
     */
    private static final Identifier ASSIGN_POINT_PACKET = new Identifier(Soulstone.ID, "assign_point");

    /**
     * 重置点数数据包标识符
     */
    private static final Identifier RESET_POINTS_PACKET = new Identifier(Soulstone.ID, "reset_points");

    /**
     * 打开点数界面数据包标识符（服务器->客户端）
     */
    private static final Identifier OPEN_SCREEN_PACKET = new Identifier(Soulstone.ID, "open_point_screen");

    /**
     * 请求打开点数界面数据包标识符（客户端->服务器）
     */
    private static final Identifier OPEN_SCREEN_REQUEST = new Identifier(Soulstone.ID, "open_point_screen_request");

    /**
     * 点数数据更新数据包标识符（服务器->客户端）
     */
    private static final Identifier POINT_DATA_UPDATE = new Identifier(Soulstone.ID, "point_data_update");

    // ========== 数据包发送方法 ==========
    // 客户端主动向服务器发送请求的方法

    /**
     * 发送分配点数请求到服务器
     *
     * @param attribute 要分配的属性名称（如"strength", "health"等）
     * @param amount    分配的点数数量
     */
    public static void sendAssignPoint(String attribute, int amount) {
        // 创建数据包缓冲区
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        // 写入属性名称和点数数量
        buf.writeString(attribute);
        buf.writeInt(amount);

        // 发送数据包到服务器
        ClientPlayNetworking.send(ASSIGN_POINT_PACKET, buf);
    }

    /**
     * 发送重置点数请求到服务器
     * 重置玩家所有已分配的点数，恢复为未分配状态
     */
    public static void sendResetPoints() {
        // 创建数据包缓冲区（无额外数据）
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        // 发送数据包到服务器
        ClientPlayNetworking.send(RESET_POINTS_PACKET, buf);
    }

    /**
     * 发送打开点数界面请求到服务器
     * 请求服务器授权并打开点数分配界面
     */
    public static void sendOpenScreenRequest() {
        // 创建数据包缓冲区（无额外数据）
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        // 发送数据包到服务器
        ClientPlayNetworking.send(OPEN_SCREEN_REQUEST, buf);
    }

    // ========== 数据包接收器注册 ==========

    /**
     * 注册客户端数据包接收器
     * 处理从服务器发送到客户端的网络消息
     */
    public static void registerClientReceivers() {
        // 1. 接收打开点数界面请求（服务器->客户端）
        ClientPlayNetworking.registerGlobalReceiver(OPEN_SCREEN_PACKET,
                (client, handler, buf, responseSender) -> {
                    // 在主游戏线程中执行UI操作
                    client.execute(() -> {
                        // 检查玩家是否存在
                        if (client.player != null) {
                            // 打开点数分配界面
                            client.setScreen(new PointScreen(client.player));
                        }
                    });
                });

        // 2. 接收点数数据更新（服务器->客户端）
        ClientPlayNetworking.registerGlobalReceiver(POINT_DATA_UPDATE,
                (client, handler, buf, responseSender) -> {
                    // 从数据包缓冲区读取数据
                    int availablePoints = buf.readInt();            // 可用的未分配点数
                    int attributeCount = buf.readInt();             // 属性数量
                    int gameLv = buf.readInt();                     // 游戏等级

                    // 创建映射表存储属性分配情况
                    java.util.HashMap<String, Integer> assignedPoints = new java.util.HashMap<>();

                    // 读取每个属性的分配点数
                    for (int i = 0; i < attributeCount; i++) {
                        String attribute = buf.readString();  // 属性名称
                        int points = buf.readInt();           // 该属性已分配的点数
                        assignedPoints.put(attribute, points);
                    }

                    // 在主游戏线程中更新UI
                    client.execute(() -> {
                        // 如果当前屏幕是点数分配界面，则更新界面数据
                        if (client.currentScreen instanceof PointScreen) {
                            ((PointScreen) client.currentScreen).updateFromNetwork(
                                    availablePoints, assignedPoints, gameLv
                            );
                        }
                    });
                });
    }
}