package org.LiangMi.soulstone;

import me.shedaniel.autoconfig.AutoConfig;

import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
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
import org.LiangMi.soulstone.command.*;
import org.LiangMi.soulstone.config.Default;
import org.LiangMi.soulstone.config.ServerConfig;
import org.LiangMi.soulstone.config.ServerConfigWrapper;
import org.LiangMi.soulstone.data.PlayerDataSaver;
import org.LiangMi.soulstone.effect.Effects;
import org.LiangMi.soulstone.event.MoodEventHandler;
import org.LiangMi.soulstone.item.*;
import org.LiangMi.soulstone.item.armor.Armors;
import org.LiangMi.soulstone.network.ConfigSync;
import org.LiangMi.soulstone.network.packet.CooldownPacket;
import org.LiangMi.soulstone.network.s2c.SpellServerNetworking;
import org.LiangMi.soulstone.network.s2c.PointServerNetworking;
import org.LiangMi.soulstone.registry.EntityRegistry;
import org.LiangMi.soulstone.system.MoodAttributeSystem;
import org.LiangMi.soulstone.system.PointAttributeSystem;
import org.LiangMi.soulstone.system.PointRewardSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



// Soulstone模组主类 - 实现Fabric模组初始化接口，用于模组核心初始化
public class Soulstone implements ModInitializer {
    // 模组ID常量
    public static final String ID = "soulstone";

    public static final Logger LOGGER = LoggerFactory.getLogger("Soulstone");
    // 获取模组本地化名称
    public static String ModName(){
        return I18n.translate("soulstone.mod_name");
    }

    // 服务器配置实例
    public static ServerConfig config;
    // 配置序列化数据包缓冲区，用于同步配置到客户端
    private static PacketByteBuf configSerialized = PacketByteBufs.create();

    // 物品配置管理器，使用ConfigManager管理物品配置
    public static ConfigManager<ItemConfig> itemConfig = new net.tinyconfig.ConfigManager<>(
            "items", Default.itemConfig)  // 配置文件名和默认配置
            .builder()
            .setDirectory(ID)  // 设置配置存储目录为模组ID
            .sanitize(true)    // 启用配置净化（移除无效条目）
            .build();

    // 注册箱子屏幕处理器类型，用于创建GUI界面
    public static final ScreenHandlerType<BoxScreenHandler> BOX_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER,
                    Identifier.of("soulstone", "box_block"),
                    new ScreenHandlerType<>(BoxScreenHandler::new, FeatureSet.empty()));

    // 模组初始化主方法
    @Override
    public void onInitialize() {
        // 注册自动配置系统，用于管理服务器配置
        AutoConfig.register(ServerConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        // 获取配置实例
        config = AutoConfig.getConfigHolder(ServerConfigWrapper.class).getConfig().server;
        // 序列化配置用于网络同步
        configSerialized = ConfigSync.write(config);

        // 玩家加入服务器时同步配置
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            sender.sendPacket(ConfigSync.ID, configSerialized);
        });


        // 注册武器系统
        Weapons.register(itemConfig.value.weapons);
        // 注册盔甲系统
        Armors.register(itemConfig.value.armor_sets);
        // 初始化模组物品
        ModItems.initialize();
        // 初始化灵魂方块
        SoulBlocks.initialize();
        // 初始化方块实体类型
        SoulBlockEntityType.initialize();
        // 注册灵魂石物品
        StoneItem.register();
        // 初始化饰品系统
        Trinkets.initialize();
        // 注册物品组
        Group.registerGroup();
        // 注册主物品组
        Registry.register(Registries.ITEM_GROUP, Group.MAIN, Group.SOULSTONE);
        // 注册饰品物品组
        Registry.register(Registries.ITEM_GROUP, Group.TRINKET, Group.Trinket);
        // 注册武器物品组
        Registry.register(Registries.ITEM_GROUP, Group.WEAPON, Group.Weapon);
        // 注册盔甲物品组
        Registry.register(Registries.ITEM_GROUP, Group.ARMOR,Group.Armor);
        // 注册状态效果
        Effects.register();
        // 初始化任务命令
        QuestCommand.Init();
        // 注册法力命令
        CommandRegistrationCallback.EVENT.register(ManaCommand::register);
        // 注册心情命令
        CommandRegistrationCallback.EVENT.register(MoodCommand::register);
        // 注册心情属性系统
        MoodAttributeSystem.register();
        // 注册心情事件处理器
        MoodEventHandler.register();
        // 注册点数属性系统
        PointAttributeSystem.register();
        // 注册点数奖励系统
        PointRewardSystem.register();
        // 注册点数测试命令
        CommandRegistrationCallback.EVENT.register(PointTestCommand::register);

        // 服务器启动时注册点数服务器网络接收器
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            PointServerNetworking.registerServerReceivers();
            SpellServerNetworking.initialize();
        });

        // 注册玩家数据保存器事件
        PlayerDataSaver.registerEvents();
        // 注册阶段命令
        CommandRegistrationCallback.EVENT.register(StageCommand::register);
        EntityRegistry.registerEntities();

        CommandRegistrationCallback.EVENT.register(PromotionCommand::register);

    }
}
