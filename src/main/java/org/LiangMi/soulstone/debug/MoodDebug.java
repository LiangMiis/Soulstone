package org.LiangMi.soulstone.debug;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.LiangMi.soulstone.access.MoodAccess;
import org.LiangMi.soulstone.data.PlayerMoodData;

public class MoodDebug {

    public static void debugAttributes(ServerPlayerEntity player) {
        if (player == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("=== 属性调试 ===\n");

        // 心情数据
        PlayerMoodData moodData = MoodAccess.getPlayerMood(player);
        sb.append(String.format("心情值: %.1f/%.1f\n", moodData.getCurrentMood(), moodData.getMaxMood()));
        sb.append(String.format("心情百分比: %.1f%%\n", moodData.getMoodPercentage() * 100));
        sb.append(String.format("属性乘数: %.2f\n", moodData.getAttributeMultiplier()));

        // 属性值
        sb.append(String.format("最大生命值: %.1f\n", player.getMaxHealth()));
        sb.append(String.format("当前生命值: %.1f\n", player.getHealth()));
        sb.append(String.format("攻击伤害: %.1f\n", player.getAttributeValue(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE)));
        sb.append(String.format("移动速度: %.3f\n", player.getAttributeValue(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MOVEMENT_SPEED)));

        player.sendMessage(Text.literal(sb.toString()), false);
    }
}
