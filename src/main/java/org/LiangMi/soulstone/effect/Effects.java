package org.LiangMi.soulstone.effect;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spell_engine.api.effect.RemoveOnHit;
import net.spell_engine.api.effect.Synchronized;
import net.spell_engine.api.event.CombatEvents;
import org.LiangMi.soulstone.Soulstone;

// 效果管理类：定义和注册所有自定义状态效果
public class Effects {
    // 定义隐身效果：有益效果，灰色
    public static final StatusEffect STEALTH = new StealthEffect(StatusEffectCategory.BENEFICIAL, 0xAAAAAA);
    // 定义堡垒效果：有益效果，浅蓝色
    public static StatusEffect BULWARK = new BulwarkEffect(StatusEffectCategory.BENEFICIAL, 0x66ccff);
    // 定义时空壳效果：有益效果，淡黄色
    public static StatusEffect TEMPORALSHELL = new TemporalShellEffect(StatusEffectCategory.BENEFICIAL, 0xf6f6a3);
    public static StatusEffect CRIMSONMADNESS = new CrimsonMadnessEffect(StatusEffectCategory.BENEFICIAL, 0x5b0808);
    public static StatusEffect THORNS = new ThornsEffect(StatusEffectCategory.BENEFICIAL,0x000000);

    /**
     * 注册所有状态效果并配置其属性
     */
    public static void register() {
        // 配置隐身效果在被击中时移除
        RemoveOnHit.configure(STEALTH, true);

        // 从配置中获取参数
        var config = Soulstone.config;

        // 为隐身效果添加移动速度加成
        STEALTH.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                "112f3133-8a44-11ed-a1eb-0242ac320003", // 唯一UUID
                config.Stealth_lin, // 速度倍率（从配置读取）
                EntityAttributeModifier.Operation.MULTIPLY_BASE); // 基础乘法操作
        // 注册战斗事件：当实体攻击时移除隐身效果
        CombatEvents.ENTITY_ATTACK.register((args) -> {
            var attacker = args.attacker();
            if (attacker.hasStatusEffect(STEALTH)) {
                attacker.removeStatusEffect(STEALTH);
            }
        });
        // 定义"消失"法术的标识符
        var vanishId = new Identifier(Soulstone.ID, "vanish");

        // 注册物品使用事件：使用物品时移除隐身效果
        CombatEvents.ITEM_USE.register((args) -> {
            var user = args.user();
            if (user.hasStatusEffect(STEALTH)) {
                user.removeStatusEffect(STEALTH);
            }
        });

        CRIMSONMADNESS.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                "CDDA466F-1598-475D-A9DC-8644DDE0B779",
                1.0f,
                EntityAttributeModifier.Operation.MULTIPLY_BASE);
        CRIMSONMADNESS.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED,
                "CDDA466F-1598-475D-A9DC-8644DDE0B779",
                1.0f,
                EntityAttributeModifier.Operation.MULTIPLY_BASE);



        // 配置时空壳效果的同步属性
        Synchronized.configure(TEMPORALSHELL,true);

        // 配置堡垒效果的同步属性
        Synchronized.configure(BULWARK, true);

        Synchronized.configure(CRIMSONMADNESS,true);
        Synchronized.configure(THORNS,true);

        // 注册所有状态效果到游戏注册表
        int rawId = config.effects_raw_id_start; // 从配置获取起始ID
        Registry.register(Registries.STATUS_EFFECT, rawId++, new Identifier(Soulstone.ID, "stealth").toString(), STEALTH);
        Registry.register(Registries.STATUS_EFFECT, rawId++, new Identifier(Soulstone.ID, "bulwark").toString(), BULWARK);
        Registry.register(Registries.STATUS_EFFECT, rawId++, new Identifier(Soulstone.ID, "temporal_shell").toString(), TEMPORALSHELL);
        Registry.register(Registries.STATUS_EFFECT, rawId++, new Identifier(Soulstone.ID, "crimson_madness").toString(), CRIMSONMADNESS);
        Registry.register(Registries.STATUS_EFFECT, rawId++, new Identifier(Soulstone.ID, "thorns").toString(), THORNS);

    }
}
