package org.LiangMi.soulstone.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.ParticleBatch;
import net.spell_engine.particle.ParticleHelper;
import net.spell_engine.utils.SoundHelper;
import org.LiangMi.soulstone.Soulstone;

// 隐身效果状态效果类
public class StealthEffect extends StatusEffect {
    // 构造函数：设置效果类别和颜色
    protected StealthEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    // 隐身效果结束时的粒子效果配置
    public static final ParticleBatch POP_PARTICLES = new ParticleBatch(
            "spell_engine:smoke_medium",  // 粒子效果资源路径（中型烟雾）
            ParticleBatch.Shape.CIRCLE,   // 粒子形状（圆形扩散）
            ParticleBatch.Origin.FEET,    // 粒子发射原点（脚部位置）
            null,                         // 无额外方向参数
            20,                           // 粒子数量
            0.18F,                        // 水平扩散半径
            0.2F,                         // 垂直扩散范围
            0);                           // 初始速度

    // 隐身效果结束时的音效标识符和事件
    public static final Identifier LEAVE_SOUND_ID = new Identifier(Soulstone.ID, "stealth_leave");
    public static final SoundEvent LEAVE_SOUND = SoundEvent.of(LEAVE_SOUND_ID);

    /**
     * 当隐身效果从实体移除时调用
     * @param entity 受影响的实体
     * @param attributes 实体属性容器
     * @param amplifier 效果放大器等级
     */
    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        // 调用父类方法处理基础移除逻辑
        super.onRemoved(entity, attributes, amplifier);

        // 调试输出（空行）
        System.out.println();

        // 确保只在服务端执行（避免客户端重复执行）
        if (!entity.getWorld().isClient()) {
            // 播放隐身结束音效
            SoundHelper.playSoundEvent(entity.getWorld(), entity, LEAVE_SOUND);

            // 发送隐身结束粒子效果
            ParticleHelper.sendBatches(entity, new ParticleBatch[]{ POP_PARTICLES });
        }
    }
}