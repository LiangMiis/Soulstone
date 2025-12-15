package org.LiangMi.soulstone.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

public class LifeImprint extends StatusEffect {

    protected LifeImprint(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // 每20游戏刻（1秒）执行一次治疗效果
        // Minecraft中20刻 = 1秒
        int interval = 20;
        return duration % interval == 0;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        // 每秒钟回复3点血量
        if (entity.getHealth() < entity.getMaxHealth()) {
            entity.heal(3.0f);
        }
    }
}
