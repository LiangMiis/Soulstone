package org.LiangMi.soulstone.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.LiangMi.soulstone.access.MoodAccess;
import org.LiangMi.soulstone.data.PlayerMoodData;
import org.LiangMi.soulstone.debug.MoodDebug;
import org.LiangMi.soulstone.system.MoodAttributeSystem;

public class MoodCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {

        dispatcher.register(CommandManager.literal("mood")
                .executes(context -> getMood(context))
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("value", FloatArgumentType.floatArg(0, 100))
                                .executes(context -> setMood(context, FloatArgumentType.getFloat(context, "value")))
                        )
                )
                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("amount", FloatArgumentType.floatArg())
                                .executes(context -> addMood(context, FloatArgumentType.getFloat(context, "amount")))
                        )
                )
                .then(CommandManager.literal("info")
                        .executes(context -> getMoodInfo(context))
                )
                .then(CommandManager.literal("debug")
                        .executes(context -> debugMood(context))
                )
                .then(CommandManager.literal("update")
                        .executes(context -> forceUpdateAttributes(context))
                )
        );
    }

    private static int getMood(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        PlayerMoodData moodData = MoodAccess.getPlayerMood(player);
        float percentage = moodData.getMoodPercentage() * 100;
        player.sendMessage(Text.literal(String.format("当前心情: %.1f/%.1f (%.1f%%)",
                moodData.getCurrentMood(), moodData.getMaxMood(), percentage)), false);
        player.sendMessage(Text.literal(String.format("属性乘数: %.2f", moodData.getAttributeMultiplier())), false);
        return 1;
    }

    private static int setMood(CommandContext<ServerCommandSource> context, float value) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        PlayerMoodData moodData = MoodAccess.getPlayerMood(player);
        moodData.setMood(value);
        MoodAccess.setPlayerMood(player, moodData);
        MoodAttributeSystem.updatePlayerAttributes(player);

        player.sendMessage(Text.literal("心情值已设置为: " + value), false);
        return 1;
    }

    private static int addMood(CommandContext<ServerCommandSource> context, float amount) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        PlayerMoodData moodData = MoodAccess.getPlayerMood(player);
        if (amount > 0) {
            moodData.addMood(amount);
        } else {
            moodData.removeMood(-amount);
        }
        MoodAccess.setPlayerMood(player, moodData);
        MoodAttributeSystem.updatePlayerAttributes(player);

        player.sendMessage(Text.literal("心情值已" + (amount > 0 ? "增加" : "减少") + ": " + Math.abs(amount)), false);
        return 1;
    }

    private static int getMoodInfo(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        PlayerMoodData moodData = MoodAccess.getPlayerMood(player);
        float multiplier = moodData.getAttributeMultiplier();

        player.sendMessage(Text.literal("=== 心情系统信息 ==="), false);
        player.sendMessage(Text.literal(String.format("当前心情: %.1f/%.1f",
                moodData.getCurrentMood(), moodData.getMaxMood())), false);
        player.sendMessage(Text.literal(String.format("属性乘数: %.2f", multiplier)), false);
        player.sendMessage(Text.literal("影响属性: 生命值, 攻击力, 移动速度"), false);
        player.sendMessage(Text.literal("恢复方式: 吃食物, 时间流逝"), false);

        return 1;
    }
    private static int debugMood(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        MoodDebug.debugAttributes(player);
        return 1;
    }

    private static int forceUpdateAttributes(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        MoodAttributeSystem.updatePlayerAttributes(player);
        player.sendMessage(Text.literal("已强制更新属性"), false);
        return 1;
    }
}
