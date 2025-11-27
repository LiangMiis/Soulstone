package org.LiangMi.soulstone.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.LiangMi.soulstone.api.AttackerTargetInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LivingEntity.class)
public class DamageAttackerMixin implements AttackerTargetInterface {

    @Inject(method = "damage", at = @At("HEAD"))
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        // 只在服务端检测
        if (((LivingEntity)(Object)this).getWorld().isClient()) return;

        Entity attacker = source.getAttacker();
        if (attacker == null) return;

        LivingEntity target = (LivingEntity) (Object) this;

        // 关键检测1：玩家攻击
        if (attacker instanceof PlayerEntity player) {
            this.target = target;
            this.attacker = attacker;
        }

        // 关键检测2：玩家被攻击
        if (target instanceof PlayerEntity player) {
            this.target = target;
            this.attacker = attacker;
        }
    }
    @Unique
    private Entity attacker;
    @Unique
    private LivingEntity target;


    @Override
    public Entity getAttacker(LivingEntity entity) {
        if (entity == target) {
            Entity result = attacker;
            // 同时清理双方引用
            this.attacker = null;
            this.target = null;
            return result;
        }
        return null;
    }

    @Override
    public LivingEntity getTarget(Entity entity) {
        if (entity == attacker) {
            LivingEntity result = target;
            // 同时清理双方引用
            this.attacker = null;
            this.target = null;
            return result;
        }
        return null;
    }
}
