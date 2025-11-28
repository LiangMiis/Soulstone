package org.LiangMi.soulstone.system;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.advancement.Advancement;
import net.minecraft.server.network.ServerPlayerEntity;
import org.LiangMi.soulstone.access.PointSystemAccess;

public class PointRewardSystem {

    public static void register() {
        // 玩家升级时奖励加点值
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            if (!alive) return;

            int oldLevel = oldPlayer.experienceLevel;
            int newLevel = newPlayer.experienceLevel;

            if (newLevel > oldLevel) {
                int levelUps = newLevel - oldLevel;
                int pointsReward = levelUps * 2; // 每升一级奖励2点

                PointSystemAccess.addPoints(newPlayer, pointsReward);
                newPlayer.sendMessage(net.minecraft.text.Text.literal(
                        "§6升级奖励! 获得 " + pointsReward + " 点加点值!"
                ), false);
            }
        });

        // 玩家达成成就时奖励加点值
        net.fabricmc.fabric.api.event.player.UseItemCallback.EVENT.register((player, world, hand) -> {
            // 这里可以监听特定物品使用来模拟成就
            return net.minecraft.util.TypedActionResult.pass(net.minecraft.item.ItemStack.EMPTY);
        });

        // 每游戏日奖励加点值
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            if (server.getOverworld().getTimeOfDay() % 24000 == 0) { // 每天一次
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    PointSystemAccess.addPoints(player, 1);
                    player.sendMessage(net.minecraft.text.Text.literal("§a每日奖励! 获得 1 点加点值!"), false);
                }
            }
        });
    }

    // 通过命令或其他方式直接奖励加点值
    public static void rewardPoints(ServerPlayerEntity player, int points, String reason) {
        PointSystemAccess.addPoints(player, points);
        player.sendMessage(net.minecraft.text.Text.literal(
                "§6" + reason + "! 获得 " + points + " 点加点值!"
        ), false);
    }
}
