package org.LiangMi.soulstone.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.client.input.SpellKeybinds;

public class CooldownPacket {

    /**
     * 数据包标识符
     * 格式：modid:packet_name
     * 用于唯一标识这个数据包类型
     */
    public static final Identifier COOLDOWN_PACKET = new Identifier(Soulstone.ID, "cooldown");

    /**
     * 初始化数据包处理器
     * 注册客户端接收数据包的事件监听器
     */
    public static void init() {
        // 注册全局数据包接收器，当客户端收到指定标识符的数据包时触发
        ClientPlayNetworking.registerGlobalReceiver(COOLDOWN_PACKET, (client, handler, buffer, sender) -> {

            // 从数据包缓冲区读取冷却时间值（以毫秒为单位）
            int cooldown = buffer.readInt();
            // 从数据包缓冲区读取冷却时间类型标识字符串
            String cooldownType = buffer.readString();

            // 切换到客户端主线程执行，确保线程安全
            client.execute(() -> {

                // 根据冷却时间类型将冷却时间值存储到对应的客户端变量中
                if (cooldownType.contains("SpellKey1"))
                    // 存储到签名技能冷却时间变量
                    SpellKeybinds.spellCooldown1 = cooldown;
                else if (cooldownType.contains("ascendancy"))
                    // 存储到进阶技能冷却时间变量
                    SpellKeybinds.spellCooldown2 = cooldown;

                // 调试用：打印冷却时间值（当前被注释掉）
                System.out.println("cooldown is: " +cooldown +"ms");

            });
        });
    }
}
