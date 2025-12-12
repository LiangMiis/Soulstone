package org.LiangMi.soulstone.network.packet.s2c;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

public class PointServerNetworking {
    private static final Identifier ASSIGN_POINT_PACKET = new Identifier(Soulstone.ID, "assign_point");
    private static final Identifier RESET_POINTS_PACKET = new Identifier(Soulstone.ID, "reset_points");
    private static final Identifier OPEN_SCREEN_REQUEST = new Identifier(Soulstone.ID, "open_screen_request");

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(ASSIGN_POINT_PACKET,
                PointServerNetworking::handleAssignPoint);

        ServerPlayNetworking.registerGlobalReceiver(RESET_POINTS_PACKET,
                PointServerNetworking::handleResetPoints);

        ServerPlayNetworking.registerGlobalReceiver(OPEN_SCREEN_REQUEST,
                PointServerNetworking::handleOpenScreenRequest);
    }

    private static void handleAssignPoint(MinecraftServer server, ServerPlayerEntity player,
                                          ServerPlayNetworkHandler handler,
                                          PacketByteBuf buf,
                                          PacketSender responseSender) {
        String attribute = buf.readString();
        int amount = buf.readInt();

        server.execute(() -> {
            boolean success = PointSystemAccess.assignPoints(player, attribute, amount);

            if (success) {
                // 发送更新后的数据回客户端
                sendPointDataUpdate(player);
            }
        });
    }

    private static void handleResetPoints(MinecraftServer server, ServerPlayerEntity player,
                                          ServerPlayNetworkHandler handler,
                                          PacketByteBuf buf,
                                          PacketSender responseSender) {
        server.execute(() -> {
            boolean success = PointSystemAccess.resetPoints(player);

            if (success) {
                // 发送更新后的数据回客户端
                sendPointDataUpdate(player);
            }
        });
    }

    private static void handleOpenScreenRequest(MinecraftServer server, ServerPlayerEntity player,
                                                ServerPlayNetworkHandler handler,
                                                PacketByteBuf buf,
                                                PacketSender responseSender) {
        server.execute(() -> {
            // 发送当前玩家数据
            sendPointDataUpdate(player);
        });
    }

    public static void sendPointDataUpdate(ServerPlayerEntity player) {
        PlayerPointData data = PointSystemAccess.getPlayerData(player);
        int gameLv = GameStageManager.getCurrentLevel(player);

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(data.getAvailablePoints());

        // 写入已分配的属性点
        java.util.Map<String, Integer> assignedPoints = data.getAllAssignedPoints();
        buf.writeInt(assignedPoints.size());
        buf.writeInt(gameLv);
        for (java.util.Map.Entry<String, Integer> entry : assignedPoints.entrySet()) {
            buf.writeString(entry.getKey());
            buf.writeInt(entry.getValue());
        }


        ServerPlayNetworking.send(player, new Identifier(Soulstone.ID, "point_data_update"), buf);
    }
}
