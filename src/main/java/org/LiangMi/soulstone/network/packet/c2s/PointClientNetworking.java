package org.LiangMi.soulstone.network.packet.c2s;


import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.client.screen.PointScreen;


public class PointClientNetworking {
    private static final Identifier ASSIGN_POINT_PACKET = new Identifier(Soulstone.ID, "assign_point");
    private static final Identifier RESET_POINTS_PACKET = new Identifier(Soulstone.ID, "reset_points");
    private static final Identifier OPEN_SCREEN_PACKET = new Identifier(Soulstone.ID, "open_point_screen");
    private static final Identifier OPEN_SCREEN_REQUEST = new Identifier(Soulstone.ID, "open_screen_request");

    public static void sendAssignPoint(String attribute, int amount) {
        // 创建 PacketByteBuf 并写入数据
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeString(attribute);
        buf.writeInt(amount);

        // 发送加点请求到服务器
        ClientPlayNetworking.send(ASSIGN_POINT_PACKET, buf);
    }

    public static void sendResetPoints() {
        // 发送重置请求到服务器
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        ClientPlayNetworking.send(RESET_POINTS_PACKET, buf);
    }

    public static void sendOpenScreenRequest() {
        // 发送打开界面请求到服务器
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        ClientPlayNetworking.send(OPEN_SCREEN_REQUEST, buf);
    }

    public static void registerClientReceivers() {
        // 注册从服务器接收数据的包
        ClientPlayNetworking.registerGlobalReceiver(OPEN_SCREEN_PACKET,
                (client, handler, buf, responseSender) -> {
                    // 当服务器要求打开加点界面时
                    client.execute(() -> {
                        if (client.player != null) {
                            client.setScreen(new PointScreen(client.player));
                        }
                    });
                });

        // 注册接收玩家数据更新的包
        ClientPlayNetworking.registerGlobalReceiver(new Identifier("soulstone", "point_data_update"),
                (client, handler, buf, responseSender) -> {
                    int availablePoints = buf.readInt();
                    int attributeCount = buf.readInt();
                    java.util.Map<String, Integer> assignedPoints = new java.util.HashMap<>();

                    for (int i = 0; i < attributeCount; i++) {
                        String attribute = buf.readString();
                        int points = buf.readInt();
                        assignedPoints.put(attribute, points);
                    }

                    client.execute(() -> {
                        // 更新当前打开的加点界面
                        if (client.currentScreen instanceof PointScreen) {
                            ((PointScreen) client.currentScreen).updateFromNetwork(availablePoints, assignedPoints);
                        }
                    });
                });
    }
}