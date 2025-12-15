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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * 状态效果管理类
 * 定义、配置和注册所有自定义的状态效果（StatusEffect）
 * 提供灵活的注册机制和效果属性配置系统
 */
public class Effects {

    // ========== 效果定义部分 ==========
    // 使用 public static final 声明所有自定义状态效果实例

    /**
     * 隐身效果
     * 使玩家进入隐身状态，在攻击或使用物品时移除
     */
    public static final StatusEffect STEALTH = new StealthEffect(StatusEffectCategory.BENEFICIAL, 0xAAAAAA);

    /**
     * 堡垒效果
     * 提供防御性增益，同步到客户端
     */
    public static StatusEffect BULWARK = new BulwarkEffect(StatusEffectCategory.BENEFICIAL, 0x66ccff);

    /**
     * 时光护盾效果
     * 提供时间相关的防护能力，同步到客户端
     */
    public static StatusEffect TEMPORALSHELL = new TemporalShellEffect(StatusEffectCategory.BENEFICIAL, 0xf6f6a3);

    /**
     * 猩红疯狂效果
     * 提供攻击速度和移动速度加成，同步到客户端
     */
    public static StatusEffect CRIMSONMADNESS = new CrimsonMadnessEffect(StatusEffectCategory.BENEFICIAL, 0x5b0808);

    /**
     * 荆棘效果
     * 反弹伤害给攻击者，同步到客户端
     */
    public static StatusEffect THORNS = new ThornsEffect(StatusEffectCategory.BENEFICIAL, 0x000000);

    /**
     * 岩石效果
     * 大幅增加护甲但降低移动速度，同步到客户端
     */
    public static StatusEffect ROCK = new RockEffect(StatusEffectCategory.BENEFICIAL, 0x000000);

    /**
     * 破风效果
     * 增加移动速度和攻击伤害，同步到客户端
     */
    public static StatusEffect BREAKINGWIND = new BreakingWindEffect(StatusEffectCategory.BENEFICIAL, 0x000000);
    public static StatusEffect LIFEIMPRINT = new LifeImprint(StatusEffectCategory.BENEFICIAL, 0x000000);

    // ========== 效果注册器内部类 ==========

    /**
     * 效果注册器内部类
     * 提供集中管理和注册状态效果的机制
     * 使用LinkedHashMap保持注册顺序，便于调试
     */
    private static class EffectRegistrar {
        /**
         * 效果映射表
         * 键：效果标识符（Identifier）
         * 值：效果条目（包含效果实例和配置器）
         */
        private final Map<Identifier, EffectEntry> effects = new LinkedHashMap<>();

        /**
         * 添加效果到注册器（使用Identifier）
         *
         * @param id 效果唯一标识符
         * @param effect 状态效果实例
         * @param configurator 效果配置器（可为null）
         */
        public void add(Identifier id, StatusEffect effect, Consumer<StatusEffect> configurator) {
            effects.put(id, new EffectEntry(effect, configurator));
        }

        /**
         * 添加效果到注册器（使用Identifier，无配置器）
         */
        public void add(Identifier id, StatusEffect effect) {
            add(id, effect, null);
        }

        /**
         * 添加效果到注册器（使用字符串ID）
         */
        public void add(String id, StatusEffect effect, Consumer<StatusEffect> configurator) {
            add(new Identifier(Soulstone.ID, id), effect, configurator);
        }

        /**
         * 添加效果到注册器（使用字符串ID，无配置器）
         */
        public void add(String id, StatusEffect effect) {
            add(new Identifier(Soulstone.ID, id), effect, null);
        }

        /**
         * 注册所有已添加的效果到游戏注册表
         */
        public void registerAll() {
            effects.forEach((id, entry) -> {
                // 如果配置器存在，应用效果配置
                if (entry.configurator != null) {
                    entry.configurator.accept(entry.effect);
                }

                // 注册效果到游戏注册表
                Registry.register(Registries.STATUS_EFFECT, id, entry.effect);
            });
        }

        /**
         * 效果条目内部类
         * 封装效果实例及其配置器
         */
        private static class EffectEntry {
            final StatusEffect effect;                    // 状态效果实例
            final Consumer<StatusEffect> configurator;    // 效果配置器（可选）

            EffectEntry(StatusEffect effect, Consumer<StatusEffect> configurator) {
                this.effect = effect;
                this.configurator = configurator;
            }
        }
    }

    // ========== 效果配置工具类 ==========

    /**
     * 效果配置工具类
     * 提供快速配置状态效果属性的静态方法
     * 支持添加属性修饰符、同步设置等
     */
    public static class EffectConfig {

        /**
         * 为效果添加移动速度修饰符
         *
         * @param effect 目标状态效果
         * @param multiplier 移动速度倍率（1.0 = 100%）
         */
        public static void withSpeedModifier(StatusEffect effect, float multiplier) {
            effect.addAttributeModifier(
                    EntityAttributes.GENERIC_MOVEMENT_SPEED,
                    generateUUID("speed"),
                    multiplier,
                    EntityAttributeModifier.Operation.MULTIPLY_BASE  // 基础值乘法
            );
        }

        /**
         * 为效果添加攻击速度修饰符
         */
        public static void withAttackSpeedModifier(StatusEffect effect, float multiplier) {
            effect.addAttributeModifier(
                    EntityAttributes.GENERIC_ATTACK_SPEED,
                    generateUUID("attack_speed"),
                    multiplier,
                    EntityAttributeModifier.Operation.MULTIPLY_BASE
            );
        }

        /**
         * 为效果添加攻击伤害修饰符
         */
        public static void withAttackDamageModifier(StatusEffect effect, float multiplier) {
            effect.addAttributeModifier(
                    EntityAttributes.GENERIC_ATTACK_DAMAGE,
                    generateUUID("attack_damage"),
                    multiplier,
                    EntityAttributeModifier.Operation.MULTIPLY_BASE
            );
        }

        /**
         * 为效果添加护甲值修饰符
         *
         * @param effect 目标状态效果
         * @param amount 护甲增加值
         */
        public static void withArmorModifier(StatusEffect effect, float amount) {
            effect.addAttributeModifier(
                    EntityAttributes.GENERIC_ARMOR,
                    generateUUID("armor"),
                    amount,
                    EntityAttributeModifier.Operation.ADDITION  // 加法操作
            );
        }

        /**
         * 设置效果为客户端-服务器同步
         *
         * @param effect 目标状态效果
         */
        public static void withSynchronized(StatusEffect effect) {
            Synchronized.configure(effect, true);
        }

        /**
         * 设置效果在击中目标时移除
         *
         * @param effect 目标状态效果
         */
        public static void withRemoveOnHit(StatusEffect effect) {
            RemoveOnHit.configure(effect, true);
        }

        /**
         * 生成基于类型的UUID字符串
         * 用于确保每个属性修饰符有唯一标识符
         *
         * @param type 类型标识符
         * @return 生成的UUID字符串
         */
        private static String generateUUID(String type) {
            // 生成随机UUID，确保唯一性
            return java.util.UUID.randomUUID().toString();
        }
    }

    // ========== 主注册方法 ==========

    /**
     * 注册所有状态效果并配置其属性
     * 这是模组初始化的主要入口点
     */
    public static void register() {
        // 获取配置实例（从Soulstone配置类）
        var config = Soulstone.config;

        // 创建效果注册器实例
        EffectRegistrar registrar = new EffectRegistrar();

        // 注册并配置所有自定义状态效果

        // 1. 隐身效果
        registrar.add("stealth", STEALTH, effect -> {
            EffectConfig.withRemoveOnHit(effect);  // 击中时移除
            EffectConfig.withSpeedModifier(effect, (float) config.Stealth_lin);  // 配置中的移动速度加成
        });

        // 2. 堡垒效果
        registrar.add("bulwark", BULWARK, EffectConfig::withSynchronized);

        // 3. 时光护盾效果
        registrar.add("temporal_shell", TEMPORALSHELL, EffectConfig::withSynchronized);

        // 4. 猩红疯狂效果
        registrar.add("crimson_madness", CRIMSONMADNESS, effect -> {
            EffectConfig.withSynchronized(effect);                    // 同步到客户端
            EffectConfig.withSpeedModifier(effect, 1.0f);             // 100%移动速度加成
            EffectConfig.withAttackSpeedModifier(effect, 1.0f);       // 100%攻击速度加成
        });

        // 5. 荆棘效果
        registrar.add("thorns", THORNS, EffectConfig::withSynchronized);

        // 6. 岩石效果
        registrar.add("rock", ROCK, effect -> {
            EffectConfig.withSynchronized(effect);                    // 同步到客户端
            EffectConfig.withArmorModifier(effect, 20.0f);            // 增加20点护甲
            // 添加移动速度惩罚（-20%）
            effect.addAttributeModifier(
                    EntityAttributes.GENERIC_MOVEMENT_SPEED,
                    "5AA3C7E4-5966-4B38-B324-021FD20304EE",  // 固定UUID（硬编码）
                    -0.2f,
                    EntityAttributeModifier.Operation.MULTIPLY_BASE
            );
        });

        // 7. 破风效果
        registrar.add("breaking_wind", BREAKINGWIND, effect -> {
            EffectConfig.withSynchronized(effect);                    // 同步到客户端
            EffectConfig.withSpeedModifier(effect, 0.2f);             // 20%移动速度加成
            EffectConfig.withAttackDamageModifier(effect, 0.2f);      // 20%攻击伤害加成
        });
        registrar.add("life_imprint", LIFEIMPRINT, effect -> {
            EffectConfig.withSynchronized(effect);                    // 同步到客户端
        });

        // 执行所有效果的注册
        registrar.registerAll();

        // ========== 事件监听器注册 ==========

        /**
         * 注册战斗事件监听器
         * 当玩家攻击时，如果处于隐身状态，则移除隐身效果
         */
        CombatEvents.ENTITY_ATTACK.register((args) -> {
            var attacker = args.attacker();
            if (attacker.hasStatusEffect(STEALTH)) {
                attacker.removeStatusEffect(STEALTH);
            }
        });

        /**
         * 注册物品使用事件监听器
         * 当玩家使用物品时，如果处于隐身状态，则移除隐身效果
         */
        CombatEvents.ITEM_USE.register((args) -> {
            var user = args.user();
            if (user.hasStatusEffect(STEALTH)) {
                user.removeStatusEffect(STEALTH);
            }
        });
    }

    // ========== 辅助方法 ==========

    /**
     * 批量注册效果的快捷方法
     *
     * @param effects 效果映射表（键：效果ID字符串，值：状态效果实例）
     */
    public static void registerEffects(Map<String, StatusEffect> effects) {
        EffectRegistrar registrar = new EffectRegistrar();
        effects.forEach((id, effect) -> registrar.add(id, effect));
        registrar.registerAll();
    }

    /**
     * 按顺序注册效果（保持注册顺序）
     * 注意：新版本Minecraft通常不需要rawId，但此方法提供了顺序控制
     *
     * @param effects 效果映射表
     * @param startRawId 起始rawId（已废弃，保留参数以兼容旧代码）
     */
    public static void registerEffectsInOrder(Map<String, StatusEffect> effects, int startRawId) {
        // 使用LinkedHashMap自动保持顺序
        EffectRegistrar registrar = new EffectRegistrar();
        effects.forEach((id, effect) -> registrar.add(id, effect));
        registrar.registerAll();
    }
}