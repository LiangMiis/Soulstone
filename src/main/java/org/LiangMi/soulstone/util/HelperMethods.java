package org.LiangMi.soulstone.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;


import java.util.*;


/**
 * 工具方法类
 * 包含游戏中的各种辅助功能，如碰撞检测、技能检查、粒子效果等
 */
public class HelperMethods {

    /**
     * 检查是否可以攻击目标（友军伤害检查）
     *
     * @param livingEntity 目标生物实体
     * @param player 玩家实体
     * @return 是否可以攻击目标（true表示可以攻击）
     */
    public static boolean checkFriendlyFire(LivingEntity livingEntity, PlayerEntity player) {
        // 空值检查
        if (livingEntity == null || player == null)
            return false;
        // 检查实体黑名单
        if (!checkEntityBlacklist(livingEntity, player))
            return false;
        // 不能攻击自己
        if (livingEntity == player)
            return false;

        // 检查玩家和目标是否在同一队伍
        AbstractTeam playerTeam = player.getScoreboardTeam();
        AbstractTeam entityTeam = livingEntity.getScoreboardTeam();


        // 如果双方在同一队伍，不允许友军伤害
        if (playerTeam != null && entityTeam != null && livingEntity.isTeammate(player)) {
            return false;
        }

        // 如果目标是玩家
        if (livingEntity instanceof PlayerEntity playerEntity) {
            if (playerEntity == player)
                return false;
            return playerEntity.shouldDamagePlayer(player);
        }

        // 如果目标是驯服生物
        if (livingEntity instanceof Tameable tameable) {
            if (tameable.getOwner() != null) {
                if (tameable.getOwner() != player
                        && (tameable.getOwner() instanceof PlayerEntity ownerPlayer)) {
                    return player.shouldDamagePlayer(ownerPlayer);
                }
                return tameable.getOwner() != player;
            }
            return true;
        }

        // 其他情况允许攻击
        return true;
    }

    /**
     * 检查是否为背击
     *
     * @param attacker 攻击者
     * @param target 目标
     * @return 是否为背击
     */
    public static boolean isBehindTarget(LivingEntity attacker, LivingEntity target) {
        // 检查攻击者是否在目标身后32度范围内
        return target.getBodyYaw() < (attacker.getBodyYaw() + 32)
                && target.getBodyYaw() > (attacker.getBodyYaw() - 32);
    }





    /**
     * 检查目标是否为黑名单中的实体类型
     * 可扩展为可配置的黑名单（如果有需求）
     *
     * @param livingEntity 目标生物实体
     * @param player 玩家实体
     * @return 是否为黑名单实体
     */
    public static boolean checkEntityBlacklist(LivingEntity livingEntity, PlayerEntity player) {
        if (livingEntity == null || player == null) {
            return false;
        }
        // 盔甲架和村民不被视为可攻击目标
        return !(livingEntity instanceof ArmorStandEntity)
                && !(livingEntity instanceof VillagerEntity);
    }

    /**
     * 获取物品的攻击伤害值
     *
     * @param stack 物品堆栈
     * @return 攻击伤害值
     */
    public static double getAttackDamage(ItemStack stack){
        return stack.getItem().getAttributeModifiers(EquipmentSlot.MAINHAND)
                .get(EntityAttributes.GENERIC_ATTACK_DAMAGE)
                .stream()
                .mapToDouble(EntityAttributeModifier::getValue)
                .sum();
    }

    /**
     * 以实体为中心创建边界框（高度为半径的1/3）
     *
     * @param entity 实体
     * @param radius 半径
     * @return 边界框
     */
    public static Box createBox(Entity entity, int radius) {
        Box box = new Box(entity.getX() + radius, entity.getY() + (float) radius / 3, entity.getZ() + radius,
                entity.getX() - radius, entity.getY() - (float) radius / 3, entity.getZ() - radius);
        return box;
    }

    /**
     * 以实体为中心创建边界框（完整高度）
     *
     * @param entity 实体
     * @param radius 半径
     * @return 边界框
     */
    public static Box createBoxHeight(Entity entity, int radius) {
        Box box = new Box(entity.getX() + radius, entity.getY() + (float) radius, entity.getZ() + radius,
                entity.getX() - radius, entity.getY() - (float) radius, entity.getZ() - radius);
        return box;
    }

    /**
     * 以方块位置为中心创建边界框
     *
     * @param blockpos 方块位置
     * @param radius 半径
     * @return 边界框
     */
    public static Box createBoxAtBlock(BlockPos blockpos, int radius) {
        Box box = new Box(blockpos.getX() + radius, blockpos.getY() + radius, blockpos.getZ() + radius,
                blockpos.getX() - radius, blockpos.getY() - radius, blockpos.getZ() - radius);
        return box;
    }

    /**
     * 在两个方块位置之间创建边界框
     *
     * @param blockpos 第一个方块位置
     * @param blockpos2 第二个方块位置
     * @param radius 半径
     * @return 边界框
     */
    public static Box createBoxBetween(BlockPos blockpos, BlockPos blockpos2, int radius) {
        Box box = new Box(blockpos.getX() + radius, blockpos.getY() + radius, blockpos.getZ() + radius,
                blockpos2.getX() - radius, blockpos2.getY() - radius, blockpos2.getZ() - radius);
        return box;
    }

    /*
     * getTargetedEntity方法借鉴自ZsoltMolnarrr的CombatSpells
     * https://github.com/ZsoltMolnarrr/CombatSpells/blob/main/common/src/main/java/net/combatspells/utils/TargetHelper.java#L72
     */
    /**
     * 获取玩家瞄准的实体
     *
     * @param user 使用者实体
     * @param range 范围
     * @return 瞄准的实体，如果没有则返回null
     */
    public static Entity getTargetedEntity(Entity user, int range) {
        Vec3d rayCastOrigin = user.getEyePos();
        Vec3d userView = user.getRotationVec(1.0F).normalize().multiply(range);
        Vec3d rayCastEnd = rayCastOrigin.add(userView);
        Box searchBox = user.getBoundingBox().expand(range, range, range);
        EntityHitResult hitResult = ProjectileUtil.raycast(user, rayCastOrigin, rayCastEnd, searchBox,
                (target) -> !target.isSpectator() && target.canHit() && target instanceof LivingEntity, range * range);
        if (hitResult != null) {
            return hitResult.getEntity();
        }
        return null;
    }

    /**
     * 获取玩家视线指向的位置
     *
     * @param player 玩家实体
     * @param range 范围
     * @return 视线指向的位置，如果没有则返回null
     */
    public static Vec3d getPositionLookingAt(PlayerEntity player, int range) {
        HitResult result = player.raycast(range, 0, false);
        if (!(result.getType() == HitResult.Type.BLOCK)) return null;

        BlockHitResult blockResult = (BlockHitResult) result;
        return blockResult.getPos();
    }

    /**
     * 获取玩家视线指向的方块位置
     * 如果没有方块，则寻找相对于范围最远的空气方块
     *
     * @param player 玩家实体
     * @param range 范围
     * @return 方块位置
     */
    public static BlockPos getBlockLookingAt(PlayerEntity player, int range) {
        HitResult result = player.raycast(range, 0, false);
        if (result.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockResult = (BlockHitResult) result;
            return blockResult.getBlockPos();
        }
        return getFirstAirBlockLookingAt(player, range);
    }

    /**
     * 获取玩家视线上的第一个空气方块位置
     *
     * @param player 玩家实体
     * @param range 范围
     * @return 第一个空气方块位置
     */
    public static BlockPos getFirstAirBlockLookingAt(PlayerEntity player, int range) {
        Vec3d start = player.getEyePos();
        Vec3d look = player.getRotationVec(1.0F);
        for (int i = range - 4; i < range; i++) {
            Vec3d step = start.add(look.x * i, look.y * i, look.z * i);
            BlockPos pos = new BlockPos((int) step.x, (int) step.y, (int) step.z);
            if (player.getWorld().isAir(pos)) {
                return pos;
            }
        }
        return null;
    }

    /**
     * 递增状态效果，但不超过最大层数
     *
     * @param livingEntity 生物实体
     * @param statusEffect 状态效果
     * @param duration 持续时间
     * @param amplifier 增加的层数
     * @param amplifierMax 最大层数
     */
    public static void incrementStatusEffect(
            LivingEntity livingEntity,
            StatusEffect statusEffect,
            int duration,
            int amplifier,
            int amplifierMax) {

        if (livingEntity.hasStatusEffect(statusEffect)) {
            int currentAmplifier = livingEntity.getStatusEffect(statusEffect).getAmplifier();

            if (currentAmplifier >= amplifierMax) {
                // 已达到最大层数，只刷新持续时间
                livingEntity.addStatusEffect(new StatusEffectInstance(
                        statusEffect, duration, currentAmplifier, false, false, true));
                return;
            }

            // 增加层数并刷新持续时间
            livingEntity.addStatusEffect(new StatusEffectInstance(
                    statusEffect, duration, currentAmplifier + amplifier, false, false, true));
        } else {
            // 如果没有该效果，则添加新效果
            livingEntity.addStatusEffect(new StatusEffectInstance(
                    statusEffect, duration, amplifier, false, false, true));
        }
    }

    /**
     * 限制状态效果的最大层数
     *
     * @param livingEntity 生物实体
     */
    public static void capStatusEffect(LivingEntity livingEntity) {
        // 各效果的最大层数
        int spellforgedCap = 5;
        int mightCap = 30;
        int marksmanshipCap = 30;

        List<StatusEffectInstance> list = livingEntity.getStatusEffects().stream().toList();
        if (!list.isEmpty()) {
            for (StatusEffectInstance statusEffectInstance : list) {
                StatusEffect statusEffect = statusEffectInstance.getEffectType();

                // 根据效果名称检查并限制层数
                switch (statusEffect.getName().getString()) {
                    case "Spellforged":
                        if (statusEffectInstance.getAmplifier() > spellforgedCap)
                            decrementStatusEffects(livingEntity, statusEffect,
                                    statusEffectInstance.getAmplifier() - spellforgedCap);
                        break;
                    case "Might":
                        if (statusEffectInstance.getAmplifier() > mightCap)
                            decrementStatusEffects(livingEntity, statusEffect,
                                    statusEffectInstance.getAmplifier() - mightCap);
                        break;
                    case "Marksmanship":
                        if (statusEffectInstance.getAmplifier() > marksmanshipCap)
                            decrementStatusEffects(livingEntity, statusEffect,
                                    statusEffectInstance.getAmplifier() - marksmanshipCap);
                        break;
                }
            }
        }
    }

    /**
     * 检查字符串是否包含列表中的任何字符串
     *
     * @param string 要检查的字符串
     * @param stringList 字符串列表
     * @return 是否包含
     */
    public static boolean stringContainsAny(String string, String[] stringList) {
        for (String s : stringList) {
            if (string.contains(s))
                return true;
        }
        return false;
    }

    /**
     * 递减状态效果的层数
     *
     * @param livingEntity 生物实体
     * @param statusEffect 状态效果
     */
    public static void decrementStatusEffect(
            LivingEntity livingEntity,
            StatusEffect statusEffect) {

        if (livingEntity.hasStatusEffect(statusEffect)) {
            int currentAmplifier = livingEntity.getStatusEffect(statusEffect).getAmplifier();
            int currentDuration = livingEntity.getStatusEffect(statusEffect).getDuration();

            if (currentAmplifier < 1) {
                // 层数为0，移除效果
                livingEntity.removeStatusEffect(statusEffect);
                return;
            }

            // 减少一层并保持持续时间
            livingEntity.removeStatusEffect(statusEffect);
            livingEntity.addStatusEffect(new StatusEffectInstance(
                    statusEffect, currentDuration, currentAmplifier - 1, false, false, true));
        }
    }

    /**
     * 递减状态效果的指定层数
     *
     * @param livingEntity 生物实体
     * @param statusEffect 状态效果
     * @param stacksRemoved 要移除的层数
     */
    public static void decrementStatusEffects(
            LivingEntity livingEntity,
            StatusEffect statusEffect,
            int stacksRemoved) {

        if (livingEntity.hasStatusEffect(statusEffect)) {
            int currentAmplifier = livingEntity.getStatusEffect(statusEffect).getAmplifier();
            int currentDuration = livingEntity.getStatusEffect(statusEffect).getDuration();

            if (currentAmplifier < 1) {
                livingEntity.removeStatusEffect(statusEffect);
                return;
            }

            livingEntity.removeStatusEffect(statusEffect);
            livingEntity.addStatusEffect(new StatusEffectInstance(
                    statusEffect, currentDuration, currentAmplifier - stacksRemoved, false, false, true));
        }
    }

    /**
     * 偷取/移除目标的增益/减益效果
     *
     * @param user 使用者
     * @param target 目标
     * @param strip 是否移除目标的效果
     * @param singular 是否只影响一个效果
     * @param debuff 是否影响减益效果（默认影响增益效果）
     * @param cleanse 是否不清除目标效果（仅移除不转移）
     * @return 是否成功执行
     */
    public static boolean buffSteal(
            LivingEntity user,
            LivingEntity target,
            boolean strip,
            boolean singular,
            boolean debuff,
            boolean cleanse) {

        // Strip - 移除状态效果
        // Singular - 每次调用只影响一个状态效果
        // Debuff - 影响非有益状态效果而不是有益状态效果
        // Cleanse - 不将效果转移到使用者（当debuff和strip都为true时，实际上是清除效果）

        List<StatusEffectInstance> list = target.getStatusEffects().stream().toList();
        if (list.isEmpty())
            return false;

        for (StatusEffectInstance statusEffectInstance : list) {
            StatusEffect statusEffect = statusEffectInstance.getEffectType();
            int duration = statusEffectInstance.getDuration();
            int amplifier = statusEffectInstance.getAmplifier();

            // 处理增益效果
            if (statusEffect.isBeneficial() && !debuff) {
                if (user != null && !cleanse)
                    HelperMethods.incrementStatusEffect(user, statusEffect, duration, 1, amplifier);
                if (strip)
                    HelperMethods.decrementStatusEffect(target, statusEffectInstance.getEffectType());
                if (singular)
                    return true;
            }
            // 处理减益效果
            else if (!statusEffect.isBeneficial() && debuff) {
                if (user != null && !cleanse)
                    HelperMethods.incrementStatusEffect(user, statusEffect, duration, 1, amplifier);
                if (strip)
                    HelperMethods.decrementStatusEffect(target, statusEffectInstance.getEffectType());
                if (singular)
                    return true;
            }
        }

        return true;
    }

    /**
     * 在客户端和服务器上生成粒子效果
     *
     * @param world 世界
     * @param particle 粒子效果
     * @param xpos X坐标
     * @param ypos Y坐标
     * @param zpos Z坐标
     * @param xvelocity X轴速度
     * @param yvelocity Y轴速度
     * @param zvelocity Z轴速度
     */
    public static void spawnParticle(World world, ParticleEffect particle, double xpos, double ypos, double zpos,
                                     double xvelocity, double yvelocity, double zvelocity) {

        if (world.isClient) {
            // 客户端生成单个粒子
            world.addParticle(particle, xpos, ypos, zpos, xvelocity, yvelocity, zvelocity);
        } else {
            // 服务器生成粒子
            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(particle, xpos, ypos, zpos, 1, xvelocity, yvelocity, zvelocity, 0);
            }
        }
    }

    /**
     * 在平面上生成粒子
     *
     * @param world 世界
     * @param particle 粒子效果
     * @param blockpos 中心位置
     * @param radius 半径
     * @param xvelocity X轴速度
     * @param yvelocity Y轴速度
     * @param zvelocity Z轴速度
     */
    public static void spawnParticlesPlane(
            World world,
            ParticleEffect particle,
            BlockPos blockpos,
            int radius,
            double xvelocity,
            double yvelocity,
            double zvelocity) {

        double xpos = blockpos.getX() - (radius + 1);
        double ypos = blockpos.getY();
        double zpos = blockpos.getZ() - (radius + 1);

        // 在平面上均匀分布粒子
        for (int i = radius * 2; i > 0; i--) {
            for (int j = radius * 2; j > 0; j--) {
                float choose = (float) (Math.random() * 1);
                HelperMethods.spawnParticle(world, particle, xpos + i + choose,
                        ypos,
                        zpos + j + choose,
                        xvelocity, yvelocity, zvelocity);
            }
        }
    }

    /**
     * 在玩家面前生成粒子
     *
     * @param world 服务器世界
     * @param livingEntity 生物实体
     * @param particle 粒子效果
     * @param distance 距离
     * @param speed 速度
     * @param count 数量
     */
    public static void spawnParticlesInFrontOfPlayer(ServerWorld world, LivingEntity livingEntity, ParticleEffect particle, int distance, double speed, int count) {
        Vec3d lookVec = livingEntity.getRotationVec(1.0F).normalize();
        Vec3d startPosition = livingEntity.getEyePos().add(lookVec.multiply(distance)); // 玩家面前的起始位置

        for (int i = 0; i < count; i++) {
            // 随机偏移以在起始位置周围散布粒子
            double offsetX = (world.random.nextDouble() - 0.5) * 2.0;
            double offsetY = (world.random.nextDouble() - 0.5) * 2.0;
            double offsetZ = (world.random.nextDouble() - 0.5) * 2.0;

            // 计算带偏移的生成位置
            double xPos = startPosition.x + offsetX;
            double yPos = startPosition.y + offsetY;
            double zPos = startPosition.z + offsetZ;

            // 应用速度使粒子沿玩家视线方向移动
            double xVelocity = lookVec.x * speed;
            double yVelocity = lookVec.y * speed;
            double zVelocity = lookVec.z * speed;

            // 生成粒子
            world.spawnParticles(particle, xPos, yPos, zPos, 0, xVelocity, yVelocity, zVelocity, 1.0);
        }
    }






    /**
     * 获取物品堆栈在玩家背包中的槽位
     *
     * @param player 玩家实体
     * @param stack 物品堆栈
     * @return 槽位索引，未找到返回-1
     */
    public static int getSlotWithStack(PlayerEntity player, ItemStack stack) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (ItemStack.areEqual(player.getInventory().getStack(i), stack)) {
                return i;
            }
        }
        return -1; // 未找到返回-1
    }


    /**
     * 打印NBT数据到工具提示
     *
     * @param stack 物品堆栈
     * @param tooltip 工具提示列表
     * @param type 打印类型（category/skill/name）
     */
    public static void printNBT(ItemStack stack, List<Text> tooltip, String type) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null)
            return;

        if (!nbt.isEmpty()) {
            int tempSize = nbt.getSize();
            int skillPrintCount = 0;

            for (int i = 0; i < tempSize; i++) {
                if (!nbt.getString("category" + i).isEmpty()) {
                    if (type.equals("category") && !nbt.getString("category" + i).contains("tree"))
                        tooltip.add(Text.literal("  §6◇ §f" + nbt.getString("category" + i).
                                replace("simplyskills:", "").replace("puffish_skills:prom", "Talent Tree")));
                }
                if (!nbt.getString("skill" + i).isEmpty())
                    skillPrintCount++;
            }

            if (type.equals("skill"))
                tooltip.add(Text.literal("  §b◇ §f" + skillPrintCount));

            if (!nbt.getString("player_name").isEmpty()) {
                String name = nbt.getString("player_name");
                if (type.equals("name"))
                    tooltip.add(Text.literal("§7Bound to: " + name));
            }
        }
    }


    /**
     * 获取玩家的最高属性值
     *
     * @param player 玩家实体
     * @return 最高属性值
     */
    public static double getHighestAttributeValue(PlayerEntity player) {
        double attackDamage = player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        double toughness = SpellPower.getSpellPower(SpellSchools.FROST, player).baseValue();
        double fire = SpellPower.getSpellPower(SpellSchools.FIRE, player).baseValue();
        double arcane = SpellPower.getSpellPower(SpellSchools.ARCANE, player).baseValue();
        double soul = SpellPower.getSpellPower(SpellSchools.SOUL, player).baseValue();
        double healing = SpellPower.getSpellPower(SpellSchools.HEALING, player).baseValue();
        double lightning = SpellPower.getSpellPower(SpellSchools.LIGHTNING, player).baseValue();

        Double[] attributeValues = {attackDamage, toughness, fire, arcane, soul, healing, lightning};

        return Arrays.stream(attributeValues).max(Comparator.naturalOrder()).orElse(Double.MIN_VALUE);
    }


    /**
     * 获取玩家指定属性中的最高值
     *
     * @param player 玩家实体
     * @param attributes 属性数组
     * @return 最高属性值
     */
    public static double getHighestSpecificAttributeValue(PlayerEntity player, EntityAttribute... attributes) {
        double highestValue = Double.MIN_VALUE;

        for (EntityAttribute attribute : attributes) {
            double attributeValue = player.getAttributeValue(attribute);
            if (attributeValue > highestValue) {
                highestValue = attributeValue;
            }
        }

        return highestValue;
    }

    /**
     * 在实体腰部高度之间生成粒子
     *
     * @param world 服务器世界
     * @param particle 粒子效果
     * @param entity1 第一个实体
     * @param entity2 第二个实体
     * @param count 粒子数量
     */
    public static void spawnWaistHeightParticles(ServerWorld world, ParticleEffect particle, Entity entity1, Entity entity2, int count) {
        Vec3d startPos = entity1.getPos().add(0, entity1.getHeight() / 2.0, 0); // 实体1腰部高度
        Vec3d endPos = entity2.getPos().add(0, entity2.getHeight() / 2.0, 0); // 实体2腰部高度
        Vec3d direction = endPos.subtract(startPos);
        double distance = direction.length();
        Vec3d normalizedDirection = direction.normalize();

        for (int i = 0; i < count; i++) {
            double lerpFactor = (double) i / (count - 1);
            Vec3d currentPos = startPos.add(normalizedDirection.multiply(distance * lerpFactor));
            world.spawnParticles(particle,
                    currentPos.x, currentPos.y, currentPos.z,
                    1,
                    0, 0, 0,
                    0.0);
        }
    }

    /**
     * 生成环绕粒子
     *
     * @param world 服务器世界
     * @param center 中心位置
     * @param particleType 粒子类型
     * @param radius 半径
     * @param particleCount 粒子数量
     */
    public static void spawnOrbitParticles(ServerWorld world, Vec3d center, ParticleEffect particleType, double radius, int particleCount) {
        for (int i = 0; i < particleCount; i++) {
            // 计算当前粒子的角度
            double angle = 2 * Math.PI * i / particleCount;

            // 计算轨道上的坐标
            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);
            double y = center.y;

            world.spawnParticles(particleType, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    /**
     * 获取实体到地面的距离
     *
     * @param entity 实体
     * @return 到地面的距离
     */
    public static double getGroundDistance(Entity entity) {
        BlockPos pos = entity.getBlockPos();
        // 向下寻找第一个固体方块
        while (pos.getY() > 0 && !entity.getWorld().getBlockState(pos).isSolidBlock(entity.getWorld(), pos)) {
            pos = pos.down();
        }
        return entity.getY() - pos.getY();
    }

    /**
     * 检查实体是否有有害状态效果
     *
     * @param entity 生物实体
     * @return 是否有有害状态效果
     */
    public static boolean hasHarmfulStatusEffect(LivingEntity entity) {
        for (StatusEffectInstance effectInstance : entity.getStatusEffects()) {
            if (effectInstance.getEffectType().getCategory() == StatusEffectCategory.HARMFUL) {
                return true;
            }
        }
        return false;
    }

    /**
     * 统计实体的有害状态效果数量
     *
     * @param entity 生物实体
     * @return 有害状态效果数量
     */
    public static int countHarmfulStatusEffects(LivingEntity entity) {
        int harmfulEffectCount = 0;
        for (StatusEffectInstance effectInstance : entity.getStatusEffects()) {
            if (effectInstance.getEffectType().getCategory() == StatusEffectCategory.HARMFUL) {
                harmfulEffectCount++;
            }
        }
        return harmfulEffectCount;
    }

    /**
     * 检查实体是否双持武器
     *
     * @param livingEntity 生物实体
     * @return 是否双持武器
     */
    public static boolean isDualWielding(LivingEntity livingEntity) {
        return (livingEntity.getMainHandStack().getItem() instanceof SwordItem || livingEntity.getMainHandStack().getItem() instanceof AxeItem)
                && (livingEntity.getOffHandStack().getItem() instanceof SwordItem || livingEntity.getOffHandStack().getItem() instanceof AxeItem);
    }

    /**
     * 沿实体视线方向生成粒子
     *
     * @param world 服务器世界
     * @param particle 粒子效果
     * @param entity 实体
     * @param count 粒子数量
     * @param distance 距离
     */
    public static void spawnDirectionalParticles(ServerWorld world, ParticleEffect particle, Entity entity, int count, double distance) {
        Vec3d startPos = entity.getPos().add(0, entity.getHeight() / 2.0, 0);

        float pitch = entity.getPitch(1.0F);
        float yaw = entity.getYaw(1.0F);

        double pitchRadians = Math.toRadians(pitch);
        double yawRadians = Math.toRadians(yaw);

        // 计算视线方向
        double xDirection = -Math.sin(yawRadians) * Math.cos(pitchRadians);
        double yDirection = -Math.sin(pitchRadians);
        double zDirection = Math.cos(yawRadians) * Math.cos(pitchRadians);
        Vec3d direction = new Vec3d(xDirection, yDirection, zDirection).normalize();

        for (int i = 0; i < count; i++) {
            double lerpFactor = (double) i / (count - 1);
            Vec3d currentPos = startPos.add(direction.multiply(distance * lerpFactor));
            world.spawnParticles(particle,
                    currentPos.x, currentPos.y, currentPos.z,
                    1,
                    0, 0, 0,
                    0.0);
        }
    }

    /**
     * 沿弹道伤害实体
     *
     * @param world 服务器世界
     * @param sourceEntity 源实体
     * @param playerEntity 玩家实体（用于友好伤害检查）
     * @param distance 距离
     * @param damage 伤害值
     * @param damageSource 伤害来源
     */
    public static void damageEntitiesInTrajectory(ServerWorld world, Entity sourceEntity, PlayerEntity playerEntity, double distance, float damage, DamageSource damageSource) {
        Vec3d startPos = sourceEntity.getPos().add(0, sourceEntity.getHeight() / 2.0, 0);
        float pitch = sourceEntity.getPitch(1.0F);
        float yaw = sourceEntity.getYaw(1.0F);

        double pitchRadians = Math.toRadians(pitch);
        double yawRadians = Math.toRadians(yaw);

        // 计算视线方向
        double xDirection = -Math.sin(yawRadians) * Math.cos(pitchRadians);
        double yDirection = -Math.sin(pitchRadians);
        double zDirection = Math.cos(yawRadians) * Math.cos(pitchRadians);
        Vec3d direction = new Vec3d(xDirection, yDirection, zDirection).normalize();

        Vec3d endPos = startPos.add(direction.multiply(distance));

        // 创建搜索框
        double boxSize = 0.5;
        Box searchBox = new Box(startPos, endPos).expand(boxSize);

        // 检查搜索框内的实体
        for (Entity entity : world.getOtherEntities(sourceEntity, searchBox)) {
            Box entityBox = entity.getBoundingBox().expand(entity.getTargetingMargin());
            if (entityBox.intersects(searchBox)) {
                if ((entity instanceof LivingEntity livingTarget)
                        && checkFriendlyFire(livingTarget, playerEntity)) {
                    livingTarget.damage(damageSource, damage);

                }
            }
        }
    }

    /**
     * 检查OpenPAC模组是否已加载
     *
     * @return 是否已加载
     */
    public static boolean isOpacLoaded() {
        return FabricLoader.getInstance().isModLoaded("openpartiesandclaims");
    }
}
