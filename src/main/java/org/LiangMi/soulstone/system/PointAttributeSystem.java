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
public class PointAttributeSystem {

    // 为每个属性创建唯一的UUID
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
        updateManaAttribute(player,pointData);
        updateArcaneAttribute(player,pointData);
        updateFireAttribute(player,pointData);
        updateFrostAttribute(player,pointData);
        updateHealingAttribute(player,pointData);
        updateLightningAttribute(player,pointData);
        updateSoulAttribute(player,pointData);
        updateCriticalChanceAttribute(player,pointData);
        updateCriticalDamageAttribute(player,pointData);
        updateHasteAttribute(player,pointData);

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
    private static void updateCriticalChanceAttribute(ServerPlayerEntity player, PlayerPointData pointData) {
        EntityAttributeInstance attribute = player.getAttributeInstance(SpellPowerMechanics.CRITICAL_CHANCE.attribute);
        if (attribute != null) {
            attribute.removeModifier(POINT_CRITICAL_CHANCE_UUID);

            int points = pointData.getAssignedPoints("critical_chance");
            if (points > 0) {
                double bonus = AttributeConfig.getAttribute("critical_chance").calculateValue(points);

                attribute.addPersistentModifier(new EntityAttributeModifier(
                        POINT_CRITICAL_CHANCE_UUID,
                        "Point Critical Chance Bonus",
                        bonus,
                        EntityAttributeModifier.Operation.ADDITION
                ));
            }
        }
    }
    private static void updateCriticalDamageAttribute(ServerPlayerEntity player, PlayerPointData pointData) {
        EntityAttributeInstance attribute = player.getAttributeInstance(SpellPowerMechanics.CRITICAL_DAMAGE.attribute);
        if (attribute != null) {
            attribute.removeModifier(POINT_CRITICAL_DAMAGE_UUID);

            int points = pointData.getAssignedPoints("critical_damage");
            if (points > 0) {
                double bonus = AttributeConfig.getAttribute("critical_damage").calculateValue(points);

                attribute.addPersistentModifier(new EntityAttributeModifier(
                        POINT_CRITICAL_DAMAGE_UUID,
                        "Point Critical Damage Bonus",
                        bonus,
                        EntityAttributeModifier.Operation.ADDITION
                ));
            }
        }
    }
    private static void updateHasteAttribute(ServerPlayerEntity player, PlayerPointData pointData) {
        EntityAttributeInstance attribute = player.getAttributeInstance(SpellPowerMechanics.HASTE.attribute);
        if (attribute != null) {
            attribute.removeModifier(POINT_HASTE_UUID);

            int points = pointData.getAssignedPoints("haste");
            if (points > 0) {
                double bonus = AttributeConfig.getAttribute("haste").calculateValue(points);

                attribute.addPersistentModifier(new EntityAttributeModifier(
                        POINT_HASTE_UUID,
                        "Point Haste Bonus",
                        bonus,
                        EntityAttributeModifier.Operation.ADDITION
                ));
            }
        }
    }

}
