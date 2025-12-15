package org.LiangMi.soulstone.network.s2c;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.access.PointSystemAccess;
import org.LiangMi.soulstone.access.SpellAccess;
import org.LiangMi.soulstone.data.PlayerPointData;
import org.LiangMi.soulstone.data.PlayerSpellData;
import org.LiangMi.soulstone.manager.GameStageManager;
import org.LiangMi.soulstone.system.KeyBindsSystem;

import java.util.List;
import java.util.Map;

public class SpellServerNetworking {
    private static final Identifier SPELL_KEY_PACKET = new Identifier(Soulstone.ID, "spell");

    private static final Identifier OPEN_SCREEN_REQUEST = new Identifier(Soulstone.ID, "open_spell_screen_request");
    private static final Identifier KEYBINDS_DATA_PACKET = new Identifier(Soulstone.ID,"keybinds_data_packet") ;

    public static void initialize(){
        ServerPlayNetworking.registerGlobalReceiver(SPELL_KEY_PACKET,
                SpellServerNetworking::setSpellKeyPacket);
        ServerPlayNetworking.registerGlobalReceiver(KEYBINDS_DATA_PACKET,
                SpellServerNetworking::addKeyBindsPacket);
        ServerPlayNetworking.registerGlobalReceiver(OPEN_SCREEN_REQUEST,
                SpellServerNetworking::handleOpenScreenRequest);

    }
    private static void setSpellKeyPacket(MinecraftServer server, ServerPlayerEntity player,
                                                ServerPlayNetworkHandler handler,
                                                PacketByteBuf buf,
                                                PacketSender responseSender) {
        String type = buf.readString();
        server.execute(()->{
            KeyBindsSystem.keyBindsManager(player,type);
        });

    }
    private static void addKeyBindsPacket(MinecraftServer server, ServerPlayerEntity player,
                                       ServerPlayNetworkHandler handler,
                                       PacketByteBuf buf,
                                       PacketSender responseSender){
        String key = buf.readString();
        String spell = buf.readString();
        server.execute(() -> {
            // 调用点数系统分配点数
            boolean success = SpellAccess.addKeyBindsSpell(player,key,spell);

            if (success) {
                // 分配成功后，发送更新后的数据回客户端以同步状态
                sendSpellDataUpdate(player);
            }
            // 注意：如果失败，可以选择发送错误消息给客户端
            System.out.print("添加法术错误");
        });
    }
    private static void handleOpenScreenRequest(MinecraftServer server, ServerPlayerEntity player,
                                                ServerPlayNetworkHandler handler,
                                                PacketByteBuf buf,
                                                PacketSender responseSender) {
        // 在服务器线程上执行
        server.execute(() -> {
            // 当客户端请求打开界面时，发送当前玩家的点数数据
            sendSpellDataUpdate(player);
        });
    }
    /**
     * 发送点数数据更新到客户端
     * 包含：可用点数、当前游戏等级和所有已分配的点数
     *
     * @param player 要发送数据的目标玩家
     */
    public static void sendSpellDataUpdate(ServerPlayerEntity player) {
        // 获取玩家的点数数据和游戏等级
        PlayerSpellData data = SpellAccess.getPlayerData(player);
        // 创建网络数据缓冲区
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());


        // 写入已分配的属性点信息
        List<String> spell = data.getSpell();
        Map<String,String>  keyBinds = data.getKeyBindsSpell();

        // 先写入映射的大小，以便客户端知道要读取多少个条目
        buf.writeInt(spell.size());
        buf.writeInt(keyBinds.size());

        // 写入每个属性的分配点数
        for (String entry : spell) {
            buf.writeString(entry);    // 属性名称
        }
        // 写入每个属性的分配点数
        for (java.util.Map.Entry<String, String> entry : keyBinds.entrySet()) {
            buf.writeString(entry.getKey());    // 属性名称
            buf.writeString(entry.getValue());     // 已分配的点数
        }

        // 发送数据包到客户端
        ServerPlayNetworking.send(player, new Identifier(Soulstone.ID, "spell_data_update"), buf);
    }

}
