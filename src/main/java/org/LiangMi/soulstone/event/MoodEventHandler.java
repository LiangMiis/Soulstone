package org.LiangMi.soulstone.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.TypedActionResult;
import org.LiangMi.soulstone.access.MoodAccess;
import org.LiangMi.soulstone.data.PlayerMoodData;
import org.LiangMi.soulstone.system.MoodAttributeSystem;

public class MoodEventHandler {

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            server.execute(() -> {
                // 延迟确保玩家完全加载
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MoodAttributeSystem.updatePlayerAttributes(player);
                System.out.println("玩家加入游戏，属性已初始化: " + player.getName().getString());
            });
        });

        // 每5秒自动恢复心情值并更新属性
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            if (server.getTicks() % 100 == 0) { // 每5秒
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    handleMoodRecovery(player);
                }
            }
        });

        // 每秒检查一次心情值变化并更新属性
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            if (server.getTicks() % 20 == 0) { // 每秒
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    checkAndUpdateMood(player);
                }
            }
        });
    }
    public static void onFoodEaten(ServerPlayerEntity player, ItemStack foodStack) {
        if (foodStack.getItem().isFood()) {
            FoodComponent foodComponent = foodStack.getItem().getFoodComponent();
            if (foodComponent != null) {
                PlayerMoodData moodData = MoodAccess.getPlayerMood(player);
                float oldMood = moodData.getCurrentMood();

                // 根据食物的饥饿恢复值和饱和度计算心情增益
                float moodGain = foodComponent.getHunger() * 1.0f + foodComponent.getSaturationModifier() * 1.5f;
                moodData.addMood(moodGain);
                MoodAccess.setPlayerMood(player, moodData);

                System.out.println("玩家进食完成: " + player.getName().getString());
                System.out.println("食物: " + foodStack.getItem().getName().getString() + ", 心情增益: " + moodGain);
                System.out.println("心情变化: " + oldMood + " -> " + moodData.getCurrentMood());

                // 立即更新属性
                MoodAttributeSystem.updatePlayerAttributes(player);

                if (moodGain > 4.0f) {
                    player.sendMessage(net.minecraft.text.Text.literal("§a美味的食物让你心情变好了！"), true);
                }
            }
        }
    }

    private static void handleMoodRecovery(ServerPlayerEntity player) {
        PlayerMoodData moodData = MoodAccess.getPlayerMood(player);
        float oldMood = moodData.getCurrentMood();
        float oldMultiplier = moodData.getAttributeMultiplier();

        if (moodData.getCurrentMood() < moodData.getMaxMood()) {
            moodData.addMood(2.5f); // 每5秒恢复2.5点心情
            MoodAccess.setPlayerMood(player, moodData);

            float newMultiplier = moodData.getAttributeMultiplier();

            // 只有乘数变化超过阈值时才更新属性
            if (Math.abs(oldMultiplier - newMultiplier) > 0.01f) {
                MoodAttributeSystem.updatePlayerAttributes(player);
            }

            // 心情恢复满时通知
            if (moodData.getCurrentMood() >= moodData.getMaxMood() && oldMood < moodData.getMaxMood()) {
                player.sendMessage(net.minecraft.text.Text.literal("§6你的心情已经完全恢复了！"), true);
            }
        }
    }
    private static void checkAndUpdateMood(ServerPlayerEntity player) {
        // 这个方法确保属性与心情值保持同步
        // 可以添加一些检查逻辑，确保属性正确应用
        PlayerMoodData moodData = MoodAccess.getPlayerMood(player);
        float multiplier = moodData.getAttributeMultiplier();

        // 如果心情值很低且属性没有正确应用，强制更新
        if (multiplier < 0.9f) {
            // 检查当前生命值是否超过了心情值限制的最大生命值
            EntityAttributeInstance healthAttr = player.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MAX_HEALTH);
            if (healthAttr != null) {
                double maxHealth = healthAttr.getValue();
                if (player.getHealth() > maxHealth) {
                    player.setHealth((float) maxHealth);
                }
            }
        }
    }
}
