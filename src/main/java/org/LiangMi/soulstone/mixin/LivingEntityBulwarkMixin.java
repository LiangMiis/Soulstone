package org.LiangMi.soulstone.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.tag.DamageTypeTags;
import org.LiangMi.soulstone.effect.BulwarkEffect;
import org.LiangMi.soulstone.effect.Effects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityBulwarkMixin {

    // 使用@ModifyVariable注解修改伤害值的方案（已被注释掉）
    // 当设置`argsOnly = true`时，ModifyVariable会根据返回值类型推断我们要修改哪个参数
    //
    // @ModifyVariable(method = "modifyAppliedDamage", at = @At("HEAD"), argsOnly = true)
    // private float modifyAppliedDamage_DivineProtection(float amount, DamageSource source) {
    //     LivingEntity entity = (LivingEntity) (Object) this;
    //     // 检查伤害来源是否绕过抗性或无敌，或者实体没有神圣保护效果
    //     if (source.isIn(DamageTypeTags.BYPASSES_RESISTANCE)
    //             || source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)
    //             || !entity.hasStatusEffect(Effects.DIVINE_PROTECTION)) {
    //         return amount;
    //     }
    //     // 应用伤害乘数
    //     return amount * DivineProtectionStatusEffect.multiplier;
    // }

    // 使用@Inject注解在damage方法的头部注入自定义逻辑
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void damage_HEAD_Bulwark(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        // 获取当前实体实例
        LivingEntity entity = (LivingEntity) (Object) this;
        // 获取攻击者
        Entity attacker = source.getAttacker();

        // 条件检查：如果伤害来源绕过抗性或无敌，或者没有攻击者，或者伤害值<=0，或者在客户端，则直接返回
        if (source.isIn(DamageTypeTags.BYPASSES_RESISTANCE)
                || source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)
                || attacker == null
                || amount <= 0
                || entity.getWorld().isClient()) {
            return;
        }

        // 检查实体是否有神圣保护效果
        if (entity.hasStatusEffect(Effects.BULWARK)) {
            // 取消原版伤害处理
            cir.cancel();
            // 获取神圣保护效果实例
            var instance = entity.getStatusEffect(Effects.BULWARK);
            if (instance != null) {
                // 移除当前的效果实例
                entity.removeStatusEffect(Effects.BULWARK);
                // 如果效果等级大于0，添加一个等级降低的新实例
                if (instance.getAmplifier() > 0) {
                    entity.addStatusEffect(
                            new StatusEffectInstance(Effects.BULWARK,
                                    instance.getDuration(),          // 持续时间不变
                                    instance.getAmplifier() - 1,     // 等级减1
                                    instance.isAmbient(),            // 保持环境效果属性
                                    instance.shouldShowParticles(),  // 保持粒子显示属性
                                    instance.shouldShowIcon())       // 保持图标显示属性
                    );
                }
            }
            // 触发神圣保护效果的特殊处理（可能是视觉或声音效果）
            BulwarkEffect.pop(entity);

            // 以下是原版击退逻辑的复制（已被注释掉）
            // double d = attacker.getX() - entity.getX();
            // double e;
            // for(e = attacker.getZ() - entity.getZ(); d * d + e * e < 1.0E-4; e = (Math.random() - Math.random()) * 0.01) {
            //     d = (Math.random() - Math.random()) * 0.01;
            // }
            // System.out.println("ASD Knockback: " + d + ", " + e);
            // entity.takeKnockback(0.4, d, e);
        }
    }

    // 另一个使用@ModifyVariable的方案（已被注释掉）
    // @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    // private float damage_DivineProtection(float amount, DamageSource source) {
    //     LivingEntity entity = (LivingEntity) (Object) this;
    //     Entity attacker = source.getAttacker();
    //     // 条件检查
    //     if (source.isIn(DamageTypeTags.BYPASSES_RESISTANCE)
    //             || source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)
    //             || attacker == null
    //             || entity.getWorld().isClient()) {
    //         return amount;
    //     }
    //
    //     // 检查是否有神圣保护效果
    //     if (entity.hasStatusEffect(Effects.DIVINE_PROTECTION)) {
    //         var instance = entity.getStatusEffect(Effects.DIVINE_PROTECTION);
    //         if (instance != null) {
    //             // 移除当前效果
    //             entity.removeStatusEffect(Effects.DIVINE_PROTECTION);
    //             // 如果等级大于0，添加等级降低的新效果
    //             if (instance.getAmplifier() > 0) {
    //                 entity.addStatusEffect(
    //                         new StatusEffectInstance(Effects.DIVINE_PROTECTION,
    //                                 instance.getDuration(),
    //                                 instance.getAmplifier() - 1,
    //                                 instance.isAmbient(),
    //                                 instance.shouldShowParticles(),
    //                                 instance.shouldShowIcon())
    //                 );
    //             }
    //         }
    //         // 播放音效
    //         SoundHelper.playSoundEvent(entity.getWorld(), entity, SoundHelper.divineProtectionImpact);
    //         // 返回0伤害
    //         return 0;
    //     }
    //
    //     return amount;
    // }
}
