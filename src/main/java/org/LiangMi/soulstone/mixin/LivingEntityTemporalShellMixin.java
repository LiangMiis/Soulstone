package org.LiangMi.soulstone.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import org.LiangMi.soulstone.effect.Effects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityTemporalShellMixin {
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void damage_HEAD_Echo_Of_Fate(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
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
        if (entity.hasStatusEffect(Effects.TEMPORALSHELL)) {
            // 取消原版伤害处理
            cir.cancel();
        }
    }
}
