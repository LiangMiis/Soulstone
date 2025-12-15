package org.LiangMi.soulstone.system;


import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellPowerMechanics;
import net.spell_power.api.SpellSchools;
import org.LiangMi.soulstone.access.PointSystemAccess;
import org.LiangMi.soulstone.config.AttributeConfig;
import org.LiangMi.soulstone.data.PlayerPointData;
import org.LiangMi.soulstone.registry.ManaRegistry;

import java.util.UUID;
/**
 * 点数属性系统类
 * 管理玩家通过点数系统分配点数后属性的实际应用和更新
 * 通过属性修饰符动态调整玩家属性，支持物理属性和法术属性
 */
public class PointAttributeSystem {

    // 为每个属性创建唯一的UUID，用于标识和管理属性修饰符
    // 这些UUID确保每个属性修饰符都有唯一标识，避免冲突
    private static final UUID POINT_HEALTH_UUID = UUID.fromString("de8fcae6-9d24-43d4-8624-de5ec07d8f28");
    private static final UUID POINT_ATTACK_UUID = UUID.fromString("07aa15d3-cccd-4b5f-846e-2f8280b069c8");
    private static final UUID POINT_DEFENSE_UUID = UUID.fromString("511d42d8-505c-4cb6-a175-707f55413e1a");
    private static final UUID POINT_SPEED_UUID = UUID.fromString("fc6ddab3-72ec-4d1f-821b-cdad1317018d");
    private static final UUID POINT_MANA_UUID = UUID.fromString("eaf1340c-8fa4-489f-b8f4-668ddfa417ff");
    private static final UUID POINT_ARCANE_UUID = UUID.fromString("84c75274-d0c3-4c57-a010-34fdda7893b1");
    private static final UUID POINT_FIRE_UUID = UUID.fromString("93cb8d93-5718-4b44-83c5-45610c7cf2c0");
    private static final UUID POINT_FROST_UUID = UUID.fromString("bbf5afbf-8882-4762-8ecf-f6caa3883605");
    private static final UUID POINT_HEALING_UUID = UUID.fromString("02b20d77-efc5-470d-b51d-7c2379fa1086");
    private static final UUID POINT_LIGHTNING_UUID = UUID.fromString("352a3c4c-eb93-4cc8-83ff-a5ca3ea4fb1d");
    private static final UUID POINT_SOUL_UUID = UUID.fromString("3c87b311-8ab9-4d10-b2a4-5aca3cb9edb8");
    private static final UUID POINT_CRITICAL_CHANCE_UUID = UUID.fromString("93b9285d-73f4-4c84-bf85-f2cfe8654e4b");
    private static final UUID POINT_CRITICAL_DAMAGE_UUID = UUID.fromString("8c1f3f65-27b9-4b37-ae69-e11a4eef5036");
    private static final UUID POINT_HASTE_UUID = UUID.fromString("15a07389-bac0-4719-899b-6a3fcd9ba889");

    /**
     * 注册事件监听器
     * 在以下情况下更新玩家属性：
     * 1. 玩家切换世界时
     * 2. 玩家复活时
     * 3. 玩家加入游戏时
     */
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
            // 延迟500毫秒执行，确保玩家完全加载
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

    /**
     * 更新玩家所有属性的主方法
     * 根据玩家的点数分配情况，更新所有相关属性
     *
     * @param player 服务器玩家实体
     */
    public static void updatePlayerAttributes(ServerPlayerEntity player) {
        // 空值和客户端检查
        if (player == null || player.getWorld().isClient) return;

        // 获取玩家的点数数据
        PlayerPointData pointData = PointSystemAccess.getPlayerData(player);

        // 更新所有属性（物理属性和法术属性）
        updateHealthAttribute(player, pointData);
        updateAttackAttribute(player, pointData);
        updateDefenseAttribute(player, pointData);
        updateManaAttribute(player, pointData);
        updateArcaneAttribute(player, pointData);
        updateFireAttribute(player, pointData);
        updateFrostAttribute(player, pointData);
        updateHealingAttribute(player, pointData);
        updateLightningAttribute(player, pointData);
        updateSoulAttribute(player, pointData);

        // 调试输出（生产环境可移除）
        System.out.println("玩家属性已更新: " + player.getName().getString());
    }

    /**
     * 更新生命值属性（最大生命值）
     *
     * @param player 服务器玩家实体
     * @param pointData 玩家点数数据
     */
    private static void updateHealthAttribute(ServerPlayerEntity player, PlayerPointData pointData) {
        // 获取玩家的最大生命值属性实例
        EntityAttributeInstance attribute = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            // 移除旧的修饰符（确保不会叠加多个修饰符）
            attribute.removeModifier(POINT_HEALTH_UUID);

            // 获取分配到生命值属性的点数
            int points = pointData.getAssignedPoints("health");
            if (points > 0) {
                // 从配置中获取每点增加的数值
                double bonus = AttributeConfig.getAttribute("health").calculateValue(points);

                // 添加新的属性修饰符（加法操作）
                attribute.addPersistentModifier(new EntityAttributeModifier(
                        POINT_HEALTH_UUID,
                        "Point Health Bonus",  // 修饰符名称
                        bonus,
                        EntityAttributeModifier.Operation.ADDITION  // 加法操作
                ));

                // 确保当前生命值不超过新的最大生命值
                double maxHealth = attribute.getValue();
                if (player.getHealth() > maxHealth) {
                    player.setHealth((float) maxHealth);
                }
            }
        }
    }

    /**
     * 更新攻击伤害属性
     *
     * @param player 服务器玩家实体
     * @param pointData 玩家点数数据
     */
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

    /**
     * 更新防御（护甲）属性
     *
     * @param player 服务器玩家实体
     * @param pointData 玩家点数数据
     */
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


    /**
     * 更新法力值属性
     *
     * @param player 服务器玩家实体
     * @param pointData 玩家点数数据
     */
    private static void updateManaAttribute(ServerPlayerEntity player, PlayerPointData pointData) {
        EntityAttributeInstance attribute = player.getAttributeInstance(ManaRegistry.MANA);
        if (attribute != null) {
            attribute.removeModifier(POINT_MANA_UUID);

            int points = pointData.getAssignedPoints("mana");
            if (points > 0) {
                double bonus = AttributeConfig.getAttribute("mana").calculateValue(points);

                attribute.addPersistentModifier(new EntityAttributeModifier(
                        POINT_MANA_UUID,
                        "Point Mana Bonus",
                        bonus,
                        EntityAttributeModifier.Operation.ADDITION
                ));
            }
        }
    }

    /**
     * 更新奥术法术强度属性
     *
     * @param player 服务器玩家实体
     * @param pointData 玩家点数数据
     */
    private static void updateArcaneAttribute(ServerPlayerEntity player, PlayerPointData pointData) {
        EntityAttributeInstance attribute = player.getAttributeInstance(SpellSchools.ARCANE.attribute);
        if (attribute != null) {
            attribute.removeModifier(POINT_ARCANE_UUID);

            int points = pointData.getAssignedPoints("arcane");
            if (points > 0) {
                double bonus = AttributeConfig.getAttribute("arcane").calculateValue(points);

                attribute.addPersistentModifier(new EntityAttributeModifier(
                        POINT_ARCANE_UUID,
                        "Point Arcane Bonus",
                        bonus,
                        EntityAttributeModifier.Operation.ADDITION
                ));
            }
        }
    }

    /**
     * 更新火焰法术强度属性
     *
     * @param player 服务器玩家实体
     * @param pointData 玩家点数数据
     */
    private static void updateFireAttribute(ServerPlayerEntity player, PlayerPointData pointData) {
        EntityAttributeInstance attribute = player.getAttributeInstance(SpellSchools.FIRE.attribute);
        if (attribute != null) {
            attribute.removeModifier(POINT_FIRE_UUID);

            int points = pointData.getAssignedPoints("fire");
            if (points > 0) {
                double bonus = AttributeConfig.getAttribute("fire").calculateValue(points);

                attribute.addPersistentModifier(new EntityAttributeModifier(
                        POINT_FIRE_UUID,
                        "Point fire Bonus",
                        bonus,
                        EntityAttributeModifier.Operation.ADDITION
                ));
            }
        }
    }

    /**
     * 更新冰霜法术强度属性
     *
     * @param player 服务器玩家实体
     * @param pointData 玩家点数数据
     */
    private static void updateFrostAttribute(ServerPlayerEntity player, PlayerPointData pointData) {
        EntityAttributeInstance attribute = player.getAttributeInstance(SpellSchools.FROST.attribute);
        if (attribute != null) {
            attribute.removeModifier(POINT_FROST_UUID);

            int points = pointData.getAssignedPoints("frost");
            if (points > 0) {
                double bonus = AttributeConfig.getAttribute("frost").calculateValue(points);

                attribute.addPersistentModifier(new EntityAttributeModifier(
                        POINT_FROST_UUID,
                        "Point Frost Bonus",
                        bonus,
                        EntityAttributeModifier.Operation.ADDITION
                ));
            }
        }
    }

    /**
     * 更新治疗法术强度属性
     *
     * @param player 服务器玩家实体
     * @param pointData 玩家点数数据
     */
    private static void updateHealingAttribute(ServerPlayerEntity player, PlayerPointData pointData) {
        EntityAttributeInstance attribute = player.getAttributeInstance(SpellSchools.HEALING.attribute);
        if (attribute != null) {
            attribute.removeModifier(POINT_HEALING_UUID);

            int points = pointData.getAssignedPoints("healing");
            if (points > 0) {
                double bonus = AttributeConfig.getAttribute("healing").calculateValue(points);

                attribute.addPersistentModifier(new EntityAttributeModifier(
                        POINT_HEALING_UUID,
                        "Point Healing Bonus",
                        bonus,
                        EntityAttributeModifier.Operation.ADDITION
                ));
            }
        }
    }

    /**
     * 更新闪电法术强度属性
     *
     * @param player 服务器玩家实体
     * @param pointData 玩家点数数据
     */
    private static void updateLightningAttribute(ServerPlayerEntity player, PlayerPointData pointData) {
        EntityAttributeInstance attribute = player.getAttributeInstance(SpellSchools.LIGHTNING.attribute);
        if (attribute != null) {
            attribute.removeModifier(POINT_LIGHTNING_UUID);

            int points = pointData.getAssignedPoints("lightning");
            if (points > 0) {
                double bonus = AttributeConfig.getAttribute("lightning").calculateValue(points);

                attribute.addPersistentModifier(new EntityAttributeModifier(
                        POINT_LIGHTNING_UUID,
                        "Point Lightning Bonus",
                        bonus,
                        EntityAttributeModifier.Operation.ADDITION
                ));
            }
        }
    }

    /**
     * 更新灵魂法术强度属性
     *
     * @param player 服务器玩家实体
     * @param pointData 玩家点数数据
     */
    private static void updateSoulAttribute(ServerPlayerEntity player, PlayerPointData pointData) {
        EntityAttributeInstance attribute = player.getAttributeInstance(SpellSchools.SOUL.attribute);
        if (attribute != null) {
            attribute.removeModifier(POINT_SOUL_UUID);

            int points = pointData.getAssignedPoints("soul");
            if (points > 0) {
                double bonus = AttributeConfig.getAttribute("soul").calculateValue(points);

                attribute.addPersistentModifier(new EntityAttributeModifier(
                        POINT_SOUL_UUID,
                        "Point Soul Bonus",
                        bonus,
                        EntityAttributeModifier.Operation.ADDITION
                ));
            }
        }
    }
}