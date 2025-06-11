package org.LiangMi.soulstone;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.LiangMi.soulstone.config.ServerConfig;
import org.LiangMi.soulstone.config.ServerConfigWrapper;
import org.LiangMi.soulstone.item.Group;
import org.LiangMi.soulstone.item.ModItems;
import org.LiangMi.soulstone.item.StoneItem;
import org.LiangMi.soulstone.network.ConfigSync;


public class Soulstone implements ModInitializer {
    public static final String ID = "soulstone";
    public static ServerConfig config;
    private static PacketByteBuf configSerialized = PacketByteBufs.create();
    @Override
    public void onInitialize() {
        AutoConfig.register(ServerConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        config = AutoConfig.getConfigHolder(ServerConfigWrapper.class).getConfig().server;
        configSerialized = ConfigSync.write(config);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            sender.sendPacket(ConfigSync.ID, configSerialized);
        });
        ModItems.initialize();
        StoneItem.register();
        Group.registerGroup();
        Registry.register(Registries.ITEM_GROUP, Group.KEY, Group.SOULSTONE);
    }

}
