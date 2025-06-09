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
import org.LiangMi.soulstone.util.ManaValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.spell_engine.internals.SpellHelper.*;

@Mixin(SpellHelper.class)
public class SpellCastMixin {
    /**
     * 计算法术的Mana消耗
     *
     * @param player   施法玩家
     * @param spell    目标法术
     * @param action   施法动作类型
     * @param progress 施法进度
     * @return 计算后的Mana消耗值
     */
    private static float getManaCost(PlayerEntity player, Spell spell, SpellCast.Action action, float progress) {
        // 根据施法动作计算通道倍率
        float channelMultiplier = 1.0F;
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
        }

        // 创建法术影响上下文
        ImpactContext context = new ImpactContext(
                channelMultiplier,
                1.0F,
                (Vec3d) null,
                SpellPower.getSpellPower(spell.school, player),
                impactTargetingMode(spell)
        );

        // 计算法术强度系数
        float coeff = 0;
        int proj = 1;
        if (spell.impact != null && ((SpellcostMixinInterface) spell.cost).calculateManaCost()) {
            // 累加所有伤害效果的法术强度系数
            for (Spell.Impact impact : spell.impact) {
                if (impact.action != null && impact.action.damage != null) {
                    coeff += impact.action.damage.spell_power_coefficient;
                }
            }
            // 计算平均系数
            if (spell.impact != null && spell.impact.length > 0) {
                coeff /= spell.impact.length;
            }
            // 计算额外投射物数量
            if (spell.release != null && spell.release.target != null && spell.release.target.projectile != null) {
                proj += spell.release.target.projectile.launch_properties.extra_launch_count;
            }
        }

        // 计算Mana消耗倍率（基于附魔和玩家属性）
        float mult = (float) (
                        player.getAttributeValue(ManaValue.MANACOST) * 0.01F
        );

        // 返回最终Mana消耗
        return mult * context.total() * (
                ((SpellcostMixinInterface) spell.cost).calculateManaCost() ?
                        (float) Math.max(20, 40 * coeff * proj) :  // 动态计算
                        ((SpellcostMixinInterface) spell.cost).getManaCost()  // 固定值
        );
    }
    /**
     * 正则匹配工具方法
     *
     * @param subject     匹配目标字符串
     * @param nullableRegex 可空正则表达式
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
     * 注入原版performSpell方法，实现Mana消耗机制
     *
     * @param world    游戏世界
     * @param player   施法玩家
     * @param spellId  法术ID
     * @param targets  法术目标列表
     * @param action   施法动作
     * @param progress 施法进度
     * @param info     回调信息（可取消原方法）
     */
    @Inject(at = @At("HEAD"), method = "performSpell", cancellable = true)
    private static void performSpell(World world, PlayerEntity player, Identifier spellId, List<Entity> targets, SpellCast.Action action, float progress, CallbackInfo info) {
        Spell spell = SpellRegistry.getSpell(spellId);

        // 检查是否启用Mana系统且法术不在黑名单中
        if (!matches(spellId.toString(), Soulstone.config.blacklist_spell_casting_regex) &&
                player instanceof ManaInterface manaInterface &&
                spell.release != null &&
                spell.cost != null &&
                spell.cost.item_id != null &&
                !spell.cost.item_id.contains("arrow")) {

            SpellCast.Attempt attempt = attemptCasting(player, player.getMainHandStack(), spellId);
            if (attempt.isSuccess()) {
                progress = Math.max(Math.min(progress, 1.0F), 0.0F);
                float channelMultiplier = 1.0F;
                boolean shouldPerformImpact = true;

                // 根据施法动作设置通道倍率
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
                        SpellCastSyncHelper.clearCasting(player);  // 清除施法状态
                }

            } else {
                // 处理非Mana施法情况（原版消耗机制）
                Identifier id;
                boolean needsArrow = false;
                if (spell.cost != null) {
                    id = new Identifier(spell.cost.item_id);
                    if (spell.cost.item_id != null)
                        needsArrow = id.getPath().contains("arrow");
                }

                // 如果弹药已满足或是箭矢则跳过
                if (!SpellHelper.ammoForSpell(player, spell, player.getMainHandStack()).satisfied() && !needsArrow) {// 原版消耗机制执行流程
                    Supplier<Collection<ServerPlayerEntity>> trackingPlayers = Suppliers.memoize(() -> PlayerLookup.tracking(player));
                    ItemStack itemStack = player.getMainHandStack();// 播放法术效果
                    ParticleHelper.sendBatches(player, spell.release.particles);
                    SoundHelper.playSound(world, player, spell.release.sound);
                    float castingSpeed = ((SpellCasterEntity) player).getCurrentCastingSpeed();
                    AnimationHelper.sendAnimation(player, (Collection) trackingPlayers.get(), SpellCast.Animation.RELEASE, spell.release.animation, castingSpeed);// 应用消耗效果
                    imposeCooldown(player, spellId, spell, 1.0F);  // 设置冷却
                    player.addExhaustion(spell.cost.exhaust * SpellEngineMod.config.spell_cost_exhaust_multiplier);  // 增加疲惫值
// 消耗物品耐久
                    if (SpellEngineMod.config.spell_cost_durability_allowed && spell.cost.durability > 0) {
                        itemStack.damage(spell.cost.durability, player, (playerObj) -> {
                            playerObj.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
                            playerObj.sendEquipmentBreakStatus(EquipmentSlot.OFFHAND);
                        });
                    }// 移除状态效果
                    if (spell.cost.effect_id != null) {
                        StatusEffect effect = (StatusEffect) Registries.STATUS_EFFECT.get(new Identifier(spell.cost.effect_id));
                        player.removeStatusEffect(effect);
                    }
                    SpellCastSyncHelper.clearCasting(player);  // 清除施法状态
                    info.cancel();  // 取消原方法执行
                }

            }
        }
    }
}
