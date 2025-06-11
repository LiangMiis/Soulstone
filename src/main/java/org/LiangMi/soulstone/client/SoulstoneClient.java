package org.LiangMi.soulstone.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.network.ConfigSync;

public class SoulstoneClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ConfigSync.ID, (client, handler, buf, responseSender) -> {
            var config = ConfigSync.read(buf);
            Soulstone.config = config;
        });
    }
}
