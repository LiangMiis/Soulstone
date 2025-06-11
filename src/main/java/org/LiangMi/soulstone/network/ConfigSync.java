package org.LiangMi.soulstone.network;

import com.google.gson.Gson;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.config.ServerConfig;

public class ConfigSync {
    /**
     * 配置同步网络包处理工具
     * 功能：实现服务端配置的序列化与反序列化，用于服务端到客户端的配置同步
     */
    public static Identifier ID = new Identifier(Soulstone.ID, "config_sync"); // 网络包标识符

    /**
     * 将服务端配置对象序列化为网络字节缓冲
     *
     * @param config 服务端配置对象
     * @return 包含配置数据的网络字节缓冲
     */
    public static PacketByteBuf write(ServerConfig config) {
        // 创建Gson实例用于JSON序列化
        var gson = new Gson();

        // 将配置对象转换为JSON字符串
        var json = gson.toJson(config);

        // 创建网络数据缓冲区
        var buffer = PacketByteBufs.create();

        // 将JSON字符串写入缓冲区
        buffer.writeString(json);

        return buffer;
    }

    /**
     * 从网络字节缓冲反序列化为配置对象
     *
     * @param buffer 包含配置数据的网络字节缓冲
     * @return 反序列化得到的服务端配置对象
     */
    public static ServerConfig read(PacketByteBuf buffer) {
        // 创建Gson实例用于JSON反序列化
        var gson = new Gson();

        // 从缓冲区读取JSON字符串
        var json = buffer.readString();

        // 将JSON字符串转换为配置对象
        var config = gson.fromJson(json, ServerConfig.class);

        return config;
    }
}
