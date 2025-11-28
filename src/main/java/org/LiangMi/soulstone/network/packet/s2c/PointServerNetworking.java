package org.LiangMi.soulstone.network.packet.s2c;


import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import io.netty.buffer.Unpooled;
import org.LiangMi.soulstone.access.PointSystemAccess;
import org.LiangMi.soulstone.data.PlayerPointData;

public class PointServerNetworking {
    private static final Identifier ASSIGN_POINT_PACKET = new Identifier("yourmod", "assign_point");
    private static final Identifier RESET_POINTS_PACKET = new Identifier("yourmod", "reset_points");
    private static final Identifier OPEN_SCREEN_REQUEST = new Identifier("yourmod", "open_screen_request");

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
            // 调用加点系统处理加点
            boolean success = PointSystemAccess.assignPoints(player, attribute, amount);

            if (success) {
                player.sendMessage(Text.literal("§a成功分配 " + amount + " 点到 " + getAttributeDisplayName(attribute)), false);
                // 发送数据更新包回客户端
                sendPointDataUpdate(player);
            } else {
                player.sendMessage(Text.literal("§c加点失败！点数不足或达到最大等级"), false);
            }
        });
    }

    private static void handleResetPoints(MinecraftServer server, ServerPlayerEntity player,
                                          ServerPlayNetworkHandler handler,
                                          PacketByteBuf buf,
                                          PacketSender responseSender) {
        server.execute(() -> {
            // 调用加点系统处理重置
            boolean success = PointSystemAccess.resetPoints(player);

            if (success) {
                player.sendMessage(Text.literal("§a已重置所有加点值！"), false);
                // 发送数据更新包回客户端
                sendPointDataUpdate(player);
            } else {
                player.sendMessage(Text.literal("§c没有加点值可以重置！"), false);
            }
        });
    }

    private static void handleOpenScreenRequest(MinecraftServer server, ServerPlayerEntity player,
                                                ServerPlayNetworkHandler handler,
                                                PacketByteBuf buf,
                                                PacketSender responseSender) {
        server.execute(() -> {
            // 发送打开加点界面的包到客户端
            sendOpenScreenPacket(player);
            // 同时发送当前玩家数据
            sendPointDataUpdate(player);
        });
    }

    private static void sendPointDataUpdate(ServerPlayerEntity player) {
        // 发送玩家数据更新到客户端
        PlayerPointData data = PointSystemAccess.getPlayerData(player);

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(data.getAvailablePoints());

        // 写入已分配的属性点
        java.util.Map<String, Integer> assignedPoints = data.getAllAssignedPoints();
        buf.writeInt(assignedPoints.size());
        for (java.util.Map.Entry<String, Integer> entry : assignedPoints.entrySet()) {
            buf.writeString(entry.getKey());
            buf.writeInt(entry.getValue());
        }

        ServerPlayNetworking.send(player, new Identifier("yourmod", "point_data_update"), buf);
    }

    public static void sendOpenScreenPacket(ServerPlayerEntity player) {
        // 发送打开加点界面的包到客户端
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        ServerPlayNetworking.send(player, new Identifier("yourmod", "open_point_screen"), buf);
    }

    private static String getAttributeDisplayName(String attribute) {
        switch (attribute) {
            case "health": return "生命值";
            case "attack": return "攻击力";
            case "defense": return "防御力";
            case "speed": return "移动速度";
            case "mining_speed": return "挖掘速度";
            case "luck": return "幸运值";
            case "experience": return "经验加成";
            default: return attribute;
        }
    }
}
