package org.LiangMi.soulstone.spell;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.LiangMi.soulstone.system.SpellSystem;
import org.LiangMi.soulstone.util.HelperMethods;

public class SpellLv1 {
    public static boolean signatureWizardArcaneBolt(PlayerEntity player) {
        boolean success = false;
        Entity target = null;

        int arcaneBoltRange = 120;

        int radius = 3;

        // 获取瞄准的目标实体
        target = HelperMethods.getTargetedEntity(player, arcaneBoltRange);

        // 获取玩家视线指向的方块位置
        BlockPos searchArea = HelperMethods.getBlockLookingAt(player, 512);

        // 如果目标是可攻击的敌对实体或没有目标
        if (((target instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, player)) || target == null) {
            // 施放普通奥术飞弹
            SpellSystem.castSpellEngineIndirectTarget(player,
                    "soulstone:arcane_bolt",
                    radius, target, searchArea);
            success = true;
        }
        return success;
    }
    public static boolean signatureClericSacredOrb(PlayerEntity player) {
        SpellSystem.castSpellEngineDumbFire(player, "soulstone:sacred_orb"); // 施放神圣之球法术
        return true; // 始终返回成功
    }
    public static boolean ghostwalk(PlayerEntity player) {
        SpellSystem.castSpellEngineIndirectTarget(player, "soulstone:ghostwalk", 3, player, null);
        return true;
    }
    public static boolean signatureWizardMeteorShower(PlayerEntity player) {
        Vec3d blockpos = null;
        boolean success = false;

        // 从配置获取技能参数
        int meteoricWrathDuration = 800;
        int meteoricWrathStacks = 10 - 1;
        int meteorShowerRange = 120;

        // 尝试获取瞄准的实体位置
        if (HelperMethods.getTargetedEntity(player, meteorShowerRange) !=null)
            blockpos = HelperMethods.getTargetedEntity(player, meteorShowerRange).getPos();

        // 如果没有瞄准实体，则获取玩家视线指向的位置
        if (blockpos == null)
            blockpos = HelperMethods.getPositionLookingAt(player, meteorShowerRange);

        // 如果找到了有效位置
        if (blockpos != null) {
            int xpos = (int) blockpos.getX();
            int ypos = (int) blockpos.getY();
            int zpos = (int) blockpos.getZ();
            BlockPos searchArea = new BlockPos(xpos, ypos, zpos);

            // 在目标位置周围创建搜索区域
            Box box = HelperMethods.createBoxAtBlock(searchArea, 8);

            // 搜索区域内的所有实体
            for (Entity entities : player.getWorld().getOtherEntities(player, box, EntityPredicates.VALID_LIVING_ENTITY)) {
                if (entities != null) {
                    // 检查是否为可攻击的敌对实体
                    if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, player)) {
                        success = true;


                            // 施放普通版流星
                            SpellSystem.castSpellEngineIndirectTarget(player,
                                    "soulstone:fire_meteor",
                                    8, le, searchArea);
                            break;
                    }
                }
            }
        }
        return success;
    }
}
