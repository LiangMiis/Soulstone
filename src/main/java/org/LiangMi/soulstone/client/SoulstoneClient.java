package org.LiangMi.soulstone.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.util.Identifier;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.render.CustomModels;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.client.effect.BulwarkRenderer;
import org.LiangMi.soulstone.client.effect.TemporalShellRenderer;
import org.LiangMi.soulstone.client.gui.BoxScreen;
import org.LiangMi.soulstone.client.input.PointKeybinds;
import org.LiangMi.soulstone.client.input.SpellKeybinds;
import org.LiangMi.soulstone.client.models.EntityModels;
import org.LiangMi.soulstone.effect.Effects;
import org.LiangMi.soulstone.network.ConfigSync;
import org.LiangMi.soulstone.network.c2s.PointClientNetworking;
import org.LiangMi.soulstone.network.c2s.SpellClientNetworking;

import java.util.List;

public class SoulstoneClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ConfigSync.ID,
                (client, handler, buf, responseSender) -> {
            var config = ConfigSync.read(buf);
            Soulstone.config = config;
        });
        CustomModels.registerModelIds(List.of(
                new Identifier(Soulstone.ID,"projectile/elemental_missile"),
                new Identifier(Soulstone.ID,"projectile/elemental_meteor"),
                BulwarkRenderer.modelId_base,
                TemporalShellRenderer.modelId_base
        ));
        CustomModelStatusEffect.register(Effects.BULWARK,new BulwarkRenderer());
        CustomModelStatusEffect.register(Effects.TEMPORALSHELL,new TemporalShellRenderer());

        HandledScreens.register(Soulstone.BOX_SCREEN_HANDLER, BoxScreen::new);
        PointClientNetworking.registerClientReceivers();
        PointKeybinds.register();
        SpellKeybinds.register();
        EntityModels.register();
        SpellClientNetworking.registerClientReceivers();

//        Keybindings.initBindings();
//        Keybindings.register();
    }
}
