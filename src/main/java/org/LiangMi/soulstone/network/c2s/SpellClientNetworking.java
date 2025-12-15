package org.LiangMi.soulstone.network.c2s;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.client.screen.PointScreen;
import org.LiangMi.soulstone.client.screen.SpellGuiScreen;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellClientNetworking {
    private static final Identifier OPEN_SCREEN_REQUEST = new Identifier(Soulstone.ID, "open_spell_screen_request");
    private static final Identifier OPEN_SCREEN_PACKET = new Identifier(Soulstone.ID, "open_spell_screen");
    private static final Identifier SPELL_DATA_UPDATE = new Identifier(Soulstone.ID, "spell_data_update");
    private static final Identifier KEYBINDS_DATA_PACKET = new Identifier(Soulstone.ID,"keybinds_data_packet") ;
    public static void sendKeyBindsSpell(String key, String spell) {
        // 创建数据包缓冲区
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        // 写入属性名称和点数数量
        buf.writeString(key);
        buf.writeString(spell);

        // 发送数据包到服务器
        ClientPlayNetworking.send(KEYBINDS_DATA_PACKET, buf);
    }
    public static void sendOpenScreenRequest() {
        // 创建数据包缓冲区（无额外数据）
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        // 发送数据包到服务器
        ClientPlayNetworking.send(OPEN_SCREEN_REQUEST, buf);
    }
    public static void registerClientReceivers() {
        // 1. 接收打开点数界面请求（服务器->客户端）
        ClientPlayNetworking.registerGlobalReceiver(OPEN_SCREEN_PACKET,
                (client, handler, buf, responseSender) -> {
                    // 在主游戏线程中执行UI操作
                    client.execute(() -> {
                        // 检查玩家是否存在
                        if (client.player != null) {
                            // 打开点数分配界面
                            client.setScreen(new SpellGuiScreen());
                        }
                    });
                });

        // 2. 接收点数数据更新（服务器->客户端）
        ClientPlayNetworking.registerGlobalReceiver(SPELL_DATA_UPDATE,
                (client, handler, buf, responseSender) -> {
                    // 从数据包缓冲区读取数据
                    int spell = buf.readInt();
                    int keyBinds = buf.readInt();// 属性数量

                    // 创建映射表存储属性分配情况
                    List<String> spellList = new ArrayList<>();
                    Map<String,String> keyBindsMap = new HashMap<>();

                    for (int i = 0; i < spell; i++) {
                        String string = buf.readString();  // 属性名称
                        spellList.add(i,string);
                    }
                    for (int i = 0; i < keyBinds;i++){
                        String key = buf.readString();
                        String string = buf.readString();
                        keyBindsMap.put(key,string);
                    }

                    // 在主游戏线程中更新UI
                    client.execute(() -> {
                        // 如果当前屏幕是点数分配界面，则更新界面数据
                        if (client.currentScreen instanceof SpellGuiScreen) {
                            ((SpellGuiScreen) client.currentScreen).updateFromNetwork(
                                    spellList,keyBindsMap
                            );
                        }
                    });
                });
    }
}
