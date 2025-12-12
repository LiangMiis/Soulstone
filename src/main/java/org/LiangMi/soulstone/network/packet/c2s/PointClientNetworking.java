package org.LiangMi.soulstone.network.packet.c2s;


import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
    private static final Identifier POINT_DATA_UPDATE = new Identifier(Soulstone.ID, "point_data_update");

    public static void sendAssignPoint(String attribute, int amount) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeString(attribute);
        buf.writeInt(amount);
        ClientPlayNetworking.send(ASSIGN_POINT_PACKET, buf);
    }

    public static void sendResetPoints() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        ClientPlayNetworking.send(RESET_POINTS_PACKET, buf);
    }

    public static void sendOpenScreenRequest() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        ClientPlayNetworking.send(OPEN_SCREEN_REQUEST, buf);
    }

    public static void registerClientReceivers() {
        // 接收打开界面请求
        ClientPlayNetworking.registerGlobalReceiver(OPEN_SCREEN_PACKET,
                (client, handler, buf, responseSender) -> {
                    client.execute(() -> {
                        if (client.player != null) {
                            client.setScreen(new PointScreen(client.player));
                        }
                    });
                });

        // 接收点数数据更新
        ClientPlayNetworking.registerGlobalReceiver(POINT_DATA_UPDATE,
                (client, handler, buf, responseSender) -> {
                    int availablePoints = buf.readInt();
                    int attributeCount = buf.readInt();
                    int gameLv = buf.readInt();
                    java.util.HashMap<String, Integer> assignedPoints = new java.util.HashMap<>();

                    for (int i = 0; i < attributeCount; i++) {
                        String attribute = buf.readString();
                        int points = buf.readInt();
                        assignedPoints.put(attribute, points);
                    }

                    client.execute(() -> {
                        // 更新当前打开的加点界面
                        if (client.currentScreen instanceof PointScreen) {
                            ((PointScreen) client.currentScreen).updateFromNetwork(availablePoints, assignedPoints,gameLv);
                        }
                    });
                });
    }
}