package org.LiangMi.soulstone;

import me.shedaniel.autoconfig.AutoConfig;

import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.spell_engine.api.item.ItemConfig;
import net.tinyconfig.ConfigManager;
import org.LiangMi.soulstone.block.SoulBlocks;
import org.LiangMi.soulstone.block.entity.SoulBlockEntityType;
import org.LiangMi.soulstone.client.gui.BoxScreenHandler;
import org.LiangMi.soulstone.command.MoodCommand;
import org.LiangMi.soulstone.command.QuestCommand;
import org.LiangMi.soulstone.config.Default;
import org.LiangMi.soulstone.config.ServerConfig;
import org.LiangMi.soulstone.config.ServerConfigWrapper;
import org.LiangMi.soulstone.debug.MoodDebug;
import org.LiangMi.soulstone.effect.Effects;
import org.LiangMi.soulstone.entity.BloodEyeEntity;
import org.LiangMi.soulstone.event.MoodEventHandler;
import org.LiangMi.soulstone.item.*;
import org.LiangMi.soulstone.item.armor.Armors;
import org.LiangMi.soulstone.network.ConfigSync;
import org.LiangMi.soulstone.system.MoodAttributeSystem;

import static org.LiangMi.soulstone.registry.EntityRegistry.BLOOD_EYE_ENTITY_ENTITY_TYPE;


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

    public static final ScreenHandlerType<BoxScreenHandler> BOX_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, Identifier.of("soulstone", "box_block"), new ScreenHandlerType<>(BoxScreenHandler::new, FeatureSet.empty()));
    @Override
    public void onInitialize() {
        AutoConfig.register(ServerConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        config = AutoConfig.getConfigHolder(ServerConfigWrapper.class).getConfig().server;
        configSerialized = ConfigSync.write(config);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            sender.sendPacket(ConfigSync.ID, configSerialized);
        });

        FabricDefaultAttributeRegistry.register(BLOOD_EYE_ENTITY_ENTITY_TYPE,BloodEyeEntity.createLivingAttributes());

        Weapons.register(itemConfig.value.weapons);
        Armors.register(itemConfig.value.armor_sets);
        ModItems.initialize();
        SoulBlocks.initialize();
        SoulBlockEntityType.initialize();
        StoneItem.register();
        Trinkets.initialize();
        Group.registerGroup();
        Registry.register(Registries.ITEM_GROUP, Group.MAIN, Group.SOULSTONE);
        Registry.register(Registries.ITEM_GROUP, Group.TRINKET, Group.Trinket);
        Registry.register(Registries.ITEM_GROUP, Group.WEAPON, Group.Weapon);
        Registry.register(Registries.ITEM_GROUP, Group.ARMOR,Group.Armor);
        Effects.register();
        QuestCommand.Init();
        CommandRegistrationCallback.EVENT.register(MoodCommand::register);
        MoodAttributeSystem.register();
        MoodEventHandler.register();

    }

}
