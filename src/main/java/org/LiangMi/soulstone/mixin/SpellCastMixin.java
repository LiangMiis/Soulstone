package org.LiangMi.soulstone.mixin;

import com.google.common.base.Suppliers;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spell_engine.SpellEngineMod;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellCastSyncHelper;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellRegistry;
import net.spell_engine.internals.WorldScheduler;
import net.spell_engine.internals.casting.SpellCast;
import net.spell_engine.internals.casting.SpellCasterEntity;
import net.spell_engine.particle.ParticleHelper;
import net.spell_engine.utils.AnimationHelper;
import net.spell_engine.utils.SoundHelper;
import net.spell_power.api.SpellPower;
import net.spell_power.api.enchantment.SpellPowerEnchanting;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.api.ManaInterface;
import org.LiangMi.soulstone.api.SpellcostMixinInterface;
import org.LiangMi.soulstone.item.ModItems;
import org.LiangMi.soulstone.registry.ManaRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.spell_engine.internals.SpellHelper.*;

/**
 * 此类混入(SpellHelper.class)，用于修改法术施放逻辑，引入基于魔力的施法系统
 */
@Mixin(SpellHelper.class)
public class SpellCastMixin {

    /**
     * 计算法术的魔力消耗（自定义实现）
     *
     * @param player  施法玩家
     * @param spell   法术对象
     * @param action  施法动作类型（CHANNEL/RELEASE）
     * @param progress 施法进度（0.0-1.0）
     * @return 计算后的魔力消耗值
     */
    private static float getManaCost(PlayerEntity player, Spell spell, SpellCast.Action action, float progress) {
        // 根据施法动作确定通道乘数
        float channelMultiplier = 1.0F;
        switch (action) {
            case CHANNEL:
                channelMultiplier = channelValueMultiplier(spell); // 获取通道法术的乘数
                break;
            case RELEASE:
                if (isChanneled(spell)) {
                    channelMultiplier = 1.0F; // 通道法术释放时使用完整值
                } else {
                    // 非通道法术需达到100%进度才消耗法力
                    channelMultiplier = progress >= 1.0F ? 1.0F : 0.0F;
                }
        }

        // 创建法术影响上下文（包含通道乘数等参数）
        ImpactContext context = new ImpactContext(channelMultiplier, 1.0F, (Vec3d) null,
                SpellPower.getSpellPower(spell.school, player), impactTargetingMode(spell));

        // 计算法术伤害系数（仅当法术需要计算魔力消耗时）
        float coeff = 0;
        int proj = 1; // 投射物数量基数
        if (spell.impact != null && ((SpellcostMixinInterface) spell.cost).calculateManaCost()) {
            // 遍历法术效果计算总伤害系数
            for (Spell.Impact impact : spell.impact) {
                if (impact.action != null && impact.action.damage != null) {
                    coeff += impact.action.damage.spell_power_coefficient;
                }
            }
            // 计算平均伤害系数
            if (spell.impact != null && spell.impact.length > 0) {
                coeff /= spell.impact.length;
            }
            // 计算额外投射物数量
            if (spell.release != null && spell.release.target != null && spell.release.target.projectile != null) {
                proj += spell.release.target.projectile.launch_properties.extra_launch_count;
            }
        }

        // 计算魔法消耗倍率（受附魔和属性影响）
        float mult = (float) (
                        player.getAttributeValue(ManaRegistry.MANACOST) * 0.01F // 魔力消耗属性加成
        );

        // 返回最终魔力消耗值
        return mult * context.total() *
                (((SpellcostMixinInterface) spell.cost).calculateManaCost() ?
                        Math.max(20, 40 * coeff * proj) : // 动态计算的法力消耗
                        ((SpellcostMixinInterface) spell.cost).getManaCost() // 预设的法力消耗
                );
    }

    /**
     * 正则匹配工具方法
     *
     * @param subject      待匹配字符串
     * @param nullableRegex 可为空的正则表达式
     * @return 是否匹配成功
     */
    private static boolean matches(String subject, String nullableRegex) {
        if (subject == null) return false;
        if (nullableRegex == null || nullableRegex.isEmpty()) return false;

        Pattern pattern = Pattern.compile(nullableRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(subject);
        return matcher.find();
    }


    /**
     * 注入法术施放方法（HEAD位置）
     * 实现魔力消耗机制
     */
    @Inject(at = @At("HEAD"), method = "performSpell", cancellable = true)
    private static void performSpell(World world, PlayerEntity player, Identifier spellId, List<Entity> targets,
                                     SpellCast.Action action, float progress, CallbackInfo info) {
        Spell spell = SpellRegistry.getSpell(spellId);

        // 只处理非黑名单法术且玩家有魔力接口的法术
        if (!matches(spellId.toString(), Soulstone.config.blacklist_spell_casting_regex) &&
                player instanceof ManaInterface manaInterface &&
                spell.release != null &&
                spell.cost != null &&
                spell.cost.item_id != null &&
                !spell.cost.item_id.contains("arrow")) {

            SpellCast.Attempt attempt = attemptCasting(player, player.getMainHandStack(), spellId);

            if (attempt.isSuccess()) {
                progress = Math.max(Math.min(progress, 1.0F), 0.0F); // 钳制进度值
                float channelMultiplier = 1.0F;
                boolean shouldPerformImpact = true;

                // 确定通道乘数（同getManaCost逻辑）
                switch (action) {
                    case CHANNEL:
                        channelMultiplier = channelValueMultiplier(spell);
                        break;
                    case RELEASE:
                        if (isChanneled(spell)) {
                            channelMultiplier = 1.0F;
                        } else {
                            channelMultiplier = progress >= 1.0F ? 1.0F : 0.0F;
                        }
                        SpellCastSyncHelper.clearCasting(player); // 清除施法状态
                }

                float finalProgress = progress;
                // 检查是否使用魔力作为弹药
                if (ammoForSpell(player, spell, player.getMainHandStack()).ammo() != null &&
                        ammoForSpell(player, spell, player.getMainHandStack()).ammo().getItem() == ModItems.MANA &&
                        player instanceof SpellCasterEntity casterEntity) {

                    // 延迟1tick执行魔力消耗和冷却
                    ((WorldScheduler) world).schedule(1, () -> {
                        // 仅释放动作且不在冷却中时设置冷却
                        if (!casterEntity.getCooldownManager().isCoolingDown(spellId) && action == SpellCast.Action.RELEASE) {
                            casterEntity.getCooldownManager().set(spellId, 2); // 短冷却
                        }
                        // 消耗魔力（负值表示扣除）
                        manaInterface.spendMana(-getManaCost(player, spell, action, finalProgress));
                    });
                }
            } else {
                // 法术尝试失败时的处理（原版消耗机制）
                Identifier id;
                boolean needsArrow = false;
                if (spell.cost != null) {
                    id = new Identifier(spell.cost.item_id);
                    if (spell.cost.item_id != null) {
                        needsArrow = id.getPath().contains("arrow"); // 检查是否需要箭矢
                    }
                }

                // 如果弹药已满足或是箭矢法术，直接返回
                if (SpellHelper.ammoForSpell(player, spell, player.getMainHandStack()).satisfied() || needsArrow) {
                    return;
                }

                // 执行原版法术消耗逻辑
                Supplier<Collection<ServerPlayerEntity>> trackingPlayers = Suppliers.memoize(() -> PlayerLookup.tracking(player));
                ItemStack itemStack = player.getMainHandStack();

                // 播放法术效果
                ParticleHelper.sendBatches(player, spell.release.particles);
                SoundHelper.playSound(world, player, spell.release.sound);
                float castingSpeed = ((SpellCasterEntity) player).getCurrentCastingSpeed();

                // 发送动画
                AnimationHelper.sendAnimation(player, (Collection) trackingPlayers.get(),
                        SpellCast.Animation.RELEASE, spell.release.animation, castingSpeed);

                // 应用冷却和消耗
                imposeCooldown(player, spellId, spell, 1.0F);
                player.addExhaustion(spell.cost.exhaust * SpellEngineMod.config.spell_cost_exhaust_multiplier);

                // 消耗物品耐久
                if (SpellEngineMod.config.spell_cost_durability_allowed && spell.cost.durability > 0) {
                    itemStack.damage(spell.cost.durability, player, (playerObj) -> {
                        playerObj.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
                        playerObj.sendEquipmentBreakStatus(EquipmentSlot.OFFHAND);
                    });
                }

                // 移除状态效果（如果存在）
                if (spell.cost.effect_id != null) {
                    StatusEffect effect = (StatusEffect) Registries.STATUS_EFFECT.get(new Identifier(spell.cost.effect_id));
                    player.removeStatusEffect(effect);
                }

                SpellCastSyncHelper.clearCasting(player); // 清除施法状态
                info.cancel(); // 取消原版施法逻辑
            }
        }
    }
}