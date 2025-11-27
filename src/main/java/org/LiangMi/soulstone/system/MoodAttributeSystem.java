package org.LiangMi.soulstone.system;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.LiangMi.soulstone.access.MoodAccess;
import org.LiangMi.soulstone.data.PlayerMoodData;

import java.util.UUID;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class MoodAttributeSystem {

    // 使用固定的UUIDs
    private static final UUID MOOD_HEALTH_UUID = UUID.fromString("342608f6-a398-449d-a749-c41ff3c8f2dc");
    private static final UUID MOOD_ATTACK_UUID = UUID.fromString("f7834d77-8105-4396-a227-46a5062ec6fa");
    private static final UUID MOOD_SPEED_UUID = UUID.fromString("6d6e828d-5724-4774-99df-afab56dcfcae");
    private static final UUID MOOD_ARMOR_UUID = UUID.fromString("97d87d9b-0095-476f-93e7-802ec89c0446");
    private static final UUID MOOD_ARMOR_TOUGHNESS_UUID = UUID.fromString("6af9e0b5-797b-4d55-a41a-af85d26ccae4");

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
                // 延迟一点确保玩家完全加载
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updatePlayerAttributes(handler.getPlayer());
            });
        });
    }

    public static void updatePlayerAttributes(ServerPlayerEntity player) {
        if (player == null || player.getWorld().isClient) return;

        float multiplier = MoodAccess.getMoodMultiplier(player);
        PlayerMoodData moodData = MoodAccess.getPlayerMood(player);

        // 调试信息
        System.out.println("更新玩家属性: " + player.getName().getString());
        System.out.println("心情值: " + moodData.getCurrentMood() + "/" + moodData.getMaxMood());
        System.out.println("属性乘数: " + multiplier);

        // 更新所有属性
        updateHealthAttribute(player, multiplier);
        updateAttackDamageAttribute(player, multiplier);
        updateMovementSpeedAttribute(player, multiplier);
        updateArmorAttribute(player, multiplier);
        updateArmorToughnessAttribute(player, multiplier);

        // 强制同步属性到客户端
        player.calculateDimensions();
        player.networkHandler.syncWithPlayerPosition();
    }

    private static void updateHealthAttribute(ServerPlayerEntity player, float multiplier) {
        EntityAttributeInstance attribute = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            // 移除旧的修饰符
            EntityAttributeModifier oldModifier = attribute.getModifier(MOOD_HEALTH_UUID);
            if (oldModifier != null) {
                attribute.removeModifier(MOOD_HEALTH_UUID);
            }

            // 如果心情不满，添加新的修饰符
            if (multiplier < 1.0f) {
                double baseValue = 20.0; // 默认最大生命值
                double reduction = baseValue * (1.0 - multiplier);
                System.out.println("生命值减少: " + reduction);

                attribute.addPersistentModifier(new EntityAttributeModifier(
                        MOOD_HEALTH_UUID,
                        "Mood Health Reduction",
                        -reduction,
                        EntityAttributeModifier.Operation.ADDITION
                ));
            }

            // 确保当前生命值不超过最大生命值
            if (player.getHealth() > attribute.getValue()) {
                player.setHealth((float) attribute.getValue());
            }
        }
    }

    private static void updateAttackDamageAttribute(ServerPlayerEntity player, float multiplier) {
        EntityAttributeInstance attribute = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (attribute != null) {
            // 移除旧的修饰符
            EntityAttributeModifier oldModifier = attribute.getModifier(MOOD_ATTACK_UUID);
            if (oldModifier != null) {
                attribute.removeModifier(MOOD_ATTACK_UUID);
            }

            // 如果心情不满，添加新的修饰符
            if (multiplier < 1.0f) {
                double baseValue = 1.0; // 默认攻击伤害
                double reduction = baseValue * (1.0 - multiplier);
                System.out.println("攻击伤害减少: " + reduction);

                attribute.addPersistentModifier(new EntityAttributeModifier(
                        MOOD_ATTACK_UUID,
                        "Mood Attack Reduction",
                        -reduction,
                        EntityAttributeModifier.Operation.ADDITION
                ));
            }
        }
    }

    private static void updateMovementSpeedAttribute(ServerPlayerEntity player, float multiplier) {
        EntityAttributeInstance attribute = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (attribute != null) {
            // 移除旧的修饰符
            EntityAttributeModifier oldModifier = attribute.getModifier(MOOD_SPEED_UUID);
            if (oldModifier != null) {
                attribute.removeModifier(MOOD_SPEED_UUID);
            }

            // 如果心情不满，添加新的修饰符
            if (multiplier < 1.0f) {
                double baseValue = 0.1; // 默认移动速度
                double reduction = baseValue * (1.0 - multiplier);
                System.out.println("移动速度减少: " + reduction);

                attribute.addPersistentModifier(new EntityAttributeModifier(
                        MOOD_SPEED_UUID,
                        "Mood Speed Reduction",
                        -reduction,
                        EntityAttributeModifier.Operation.ADDITION
                ));
            }
        }
    }

    private static void updateArmorAttribute(ServerPlayerEntity player, float multiplier) {
        EntityAttributeInstance attribute = player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
        if (attribute != null) {
            EntityAttributeModifier oldModifier = attribute.getModifier(MOOD_ARMOR_UUID);
            if (oldModifier != null) {
                attribute.removeModifier(MOOD_ARMOR_UUID);
            }

            if (multiplier < 1.0f) {
                double currentArmor = attribute.getBaseValue();
                double reduction = currentArmor * (1.0 - multiplier);

                attribute.addPersistentModifier(new EntityAttributeModifier(
                        MOOD_ARMOR_UUID,
                        "Mood Armor Reduction",
                        -reduction,
                        EntityAttributeModifier.Operation.ADDITION
                ));
            }
        }
    }

    private static void updateArmorToughnessAttribute(ServerPlayerEntity player, float multiplier) {
        EntityAttributeInstance attribute = player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
        if (attribute != null) {
            EntityAttributeModifier oldModifier = attribute.getModifier(MOOD_ARMOR_TOUGHNESS_UUID);
            if (oldModifier != null) {
                attribute.removeModifier(MOOD_ARMOR_TOUGHNESS_UUID);
            }

            if (multiplier < 1.0f) {
                double currentToughness = attribute.getBaseValue();
                double reduction = currentToughness * (1.0 - multiplier);

                attribute.addPersistentModifier(new EntityAttributeModifier(
                        MOOD_ARMOR_TOUGHNESS_UUID,
                        "Mood Armor Toughness Reduction",
                        -reduction,
                        EntityAttributeModifier.Operation.ADDITION
                ));
            }
        }
    }
}
