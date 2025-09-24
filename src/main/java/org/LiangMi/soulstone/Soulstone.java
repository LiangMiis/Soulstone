package org.LiangMi.soulstone;

import me.shedaniel.autoconfig.AutoConfig;

import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.spell_engine.api.item.ItemConfig;
import net.tinyconfig.ConfigManager;
import org.LiangMi.soulstone.config.Default;
import org.LiangMi.soulstone.config.ServerConfig;
import org.LiangMi.soulstone.config.ServerConfigWrapper;
import org.LiangMi.soulstone.effect.Effects;
import org.LiangMi.soulstone.item.Group;
import org.LiangMi.soulstone.item.ModItems;
import org.LiangMi.soulstone.item.StoneItem;
import org.LiangMi.soulstone.item.Weapons;
import org.LiangMi.soulstone.item.armor.Armors;
import org.LiangMi.soulstone.item.armor.SoulArmor;
import org.LiangMi.soulstone.network.ConfigSync;


public class Soulstone implements ModInitializer {
    public static final String ID = "soulstone";
    public static String ModName(){
        return I18n.translate("soulstone.mod_name");
    }
    public static ServerConfig config;
    private static PacketByteBuf configSerialized = PacketByteBufs.create();
    public static ConfigManager<ItemConfig> itemConfig = new net.tinyconfig.ConfigManager<>(
            "items", Default.itemConfig)
            .builder()
            .setDirectory(ID)
            .sanitize(true)
            .build();
    @Override
    public void onInitialize() {
        AutoConfig.register(ServerConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        config = AutoConfig.getConfigHolder(ServerConfigWrapper.class).getConfig().server;
        configSerialized = ConfigSync.write(config);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            sender.sendPacket(ConfigSync.ID, configSerialized);
        });

        Weapons.register(itemConfig.value.weapons);
        Armors.register(itemConfig.value.armor_sets);
        ModItems.initialize();
        StoneItem.register();
        Group.registerGroup();
        Registry.register(Registries.ITEM_GROUP, Group.MAIN, Group.SOULSTONE);
        Registry.register(Registries.ITEM_GROUP, Group.TRINKET, Group.Trinket);
        Registry.register(Registries.ITEM_GROUP, Group.WEAPON, Group.Weapon);
        Registry.register(Registries.ITEM_GROUP, Group.ARMOR,Group.Armor);
        Effects.register();
    }

}
