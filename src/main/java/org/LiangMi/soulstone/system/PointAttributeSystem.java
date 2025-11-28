package org.LiangMi.soulstone.system;


import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import org.LiangMi.soulstone.access.PointSystemAccess;
import org.LiangMi.soulstone.config.AttributeConfig;
import org.LiangMi.soulstone.data.PlayerPointData;

import java.util.UUID;
public class PointAttributeSystem {

    // 为每个属性创建唯一的UUID
    private static final UUID POINT_HEALTH_UUID = UUID.fromString("de8fcae6-9d24-43d4-8624-de5ec07d8f28");
    private static final UUID POINT_ATTACK_UUID = UUID.fromString("07aa15d3-cccd-4b5f-846e-2f8280b069c8");
    private static final UUID POINT_DEFENSE_UUID = UUID.fromString("511d42d8-505c-4cb6-a175-707f55413e1a");
    private static final UUID POINT_SPEED_UUID = UUID.fromString("fc6ddab3-72ec-4d1f-821b-cdad1317018d");

    public static void register() {
        // 玩家切换世界时更新属性
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            updatePlayerAttributes(player);
        });

        // 玩家复活时更新属性
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            updatePlayerAttributes(newPlayer);
        });

        // 玩家加入游戏时更新属性
        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            server.execute(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updatePlayerAttributes(handler.getPlayer());
            });
        });
    }

    public static void updatePlayerAttributes(ServerPlayerEntity player) {
        if (player == null || player.getWorld().isClient) return;

        PlayerPointData pointData = PointSystemAccess.getPlayerData(player);

        // 更新所有属性
        updateHealthAttribute(player, pointData);
        updateAttackAttribute(player, pointData);
        updateDefenseAttribute(player, pointData);
        updateSpeedAttribute(player, pointData);
        updateMiningSpeed(player, pointData); // 自定义属性需要特殊处理
        updateLuck(player, pointData); // 自定义属性需要特殊处理

        System.out.println("玩家属性已更新: " + player.getName().getString());
    }

    private static void updateHealthAttribute(ServerPlayerEntity player, PlayerPointData pointData) {
        EntityAttributeInstance attribute = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            // 移除旧的修饰符
            attribute.removeModifier(POINT_HEALTH_UUID);

            int points = pointData.getAssignedPoints("health");
            if (points > 0) {
                double bonus = AttributeConfig.getAttribute("health").calculateValue(points);

                attribute.addPersistentModifier(new EntityAttributeModifier(
                        POINT_HEALTH_UUID,
                        "Point Health Bonus",
                        bonus,
                        EntityAttributeModifier.Operation.ADDITION
                ));

                // 确保当前生命值不超过最大生命值
                double maxHealth = attribute.getValue();
                if (player.getHealth() > maxHealth) {
                    player.setHealth((float) maxHealth);
                }
            }
        }
    }

    private static void updateAttackAttribute(ServerPlayerEntity player, PlayerPointData pointData) {
        EntityAttributeInstance attribute = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (attribute != null) {
            attribute.removeModifier(POINT_ATTACK_UUID);

            int points = pointData.getAssignedPoints("attack");
            if (points > 0) {
                double bonus = AttributeConfig.getAttribute("attack").calculateValue(points);

                attribute.addPersistentModifier(new EntityAttributeModifier(
                        POINT_ATTACK_UUID,
                        "Point Attack Bonus",
                        bonus,
                        EntityAttributeModifier.Operation.ADDITION
                ));
            }
        }
    }

    private static void updateDefenseAttribute(ServerPlayerEntity player, PlayerPointData pointData) {
        EntityAttributeInstance attribute = player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
        if (attribute != null) {
            attribute.removeModifier(POINT_DEFENSE_UUID);

            int points = pointData.getAssignedPoints("defense");
            if (points > 0) {
                double bonus = AttributeConfig.getAttribute("defense").calculateValue(points);

                attribute.addPersistentModifier(new EntityAttributeModifier(
                        POINT_DEFENSE_UUID,
                        "Point Defense Bonus",
                        bonus,
                        EntityAttributeModifier.Operation.ADDITION
                ));
            }
        }
    }

    private static void updateSpeedAttribute(ServerPlayerEntity player, PlayerPointData pointData) {
        EntityAttributeInstance attribute = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (attribute != null) {
            attribute.removeModifier(POINT_SPEED_UUID);

            int points = pointData.getAssignedPoints("speed");
            if (points > 0) {
                double bonus = AttributeConfig.getAttribute("speed").calculateValue(points);

                attribute.addPersistentModifier(new EntityAttributeModifier(
                        POINT_SPEED_UUID,
                        "Point Speed Bonus",
                        bonus,
                        EntityAttributeModifier.Operation.ADDITION
                ));
            }
        }
    }

    // 自定义属性：挖掘速度（通过修改效率附魔级别实现）
    private static void updateMiningSpeed(ServerPlayerEntity player, PlayerPointData pointData) {
        int points = pointData.getAssignedPoints("mining_speed");
        // 这个属性需要特殊处理，可以通过监听方块破坏事件来实现
        // 这里只是占位符实现
        if (points > 0) {
            double multiplier = AttributeConfig.getAttribute("mining_speed").calculateValue(points);
            // 实际实现需要在挖掘事件中应用这个倍率
        }
    }

    // 自定义属性：幸运值（影响战利品掉落）
    private static void updateLuck(ServerPlayerEntity player, PlayerPointData pointData) {
        int points = pointData.getAssignedPoints("luck");
        // 幸运值需要特殊处理，可以通过监听战利品表生成事件来实现
        // 这里只是占位符实现
        if (points > 0) {
            double bonus = AttributeConfig.getAttribute("luck").calculateValue(points);
            // 实际实现需要在战利品生成事件中应用这个加成
        }
    }
}
