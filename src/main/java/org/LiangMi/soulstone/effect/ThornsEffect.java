package org.LiangMi.soulstone.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

public class ThornsEffect extends StatusEffect {
    protected ThornsEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }
    // 这个方法在每个 tick 都会调用，以检查是否应应用药水效果
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // 在我们的例子中，为了确保每一 tick 药水效果都会被应用，我们只要这个方法返回 true 就行了。
        return true;
    }
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity) {
            if(entity.getMovementSpeed()>0.1f){
                entity.damage(entity.getDamageSources().magic(), 8.0F);
            }
        }
    }
}
