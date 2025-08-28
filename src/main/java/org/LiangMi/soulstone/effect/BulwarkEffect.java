package org.LiangMi.soulstone.effect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.spell_engine.api.spell.ParticleBatch;
import net.spell_engine.particle.ParticleHelper;
import org.LiangMi.soulstone.Soulstone;

// 堡垒效果（BulwarkEffect）类，继承自状态效果（StatusEffect）
public class BulwarkEffect extends StatusEffect {
    // 构造函数：设置效果类别和颜色
    protected BulwarkEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    // 定义粒子效果批次配置
    private static final ParticleBatch particles = new ParticleBatch(
            Soulstone.ID+":bulwark",          // 粒子效果标识符（使用模组ID + 效果名称）
            ParticleBatch.Shape.PILLAR,       // 粒子形状：柱状
            ParticleBatch.Origin.CENTER,      // 粒子生成原点：实体中心
            null,                             // 无旋转设置
            25,                               // 粒子数量：25个
            0.01F,                            // 最小速度：0.01
            0.2F,                             // 最大速度：0.2
            0                                 // 发射角度：0度（垂直向上）
    );

    /**
     * 触发堡垒效果粒子爆发
     * @param centerEntity 中心实体，粒子将以此实体为中心生成
     */
    public static void pop(Entity centerEntity){
        // 发送粒子批次到客户端，在指定实体位置生成粒子效果
        ParticleHelper.sendBatches(centerEntity, new ParticleBatch[]{particles});
    }
}
