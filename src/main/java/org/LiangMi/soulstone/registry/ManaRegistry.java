package org.LiangMi.soulstone.registry;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


// 魔法值属性注册类 - 负责注册和管理与魔法值相关的实体属性
public class ManaRegistry {
    // 最大魔法值属性 - 定义实体的最大魔法值上限
    public static final EntityAttribute MANA = register("max_mana",
            (new ClampedEntityAttribute("attribute.name.max_mana",  // 属性名称翻译键
                    (double) 50.0f,  // 默认值：50点魔法值
                    (double) 1.0f,   // 最小值：1点魔法值
                    (double) 100000f) // 最大值：100,000点魔法值
            ).setTracked(true)); // 设置为跟踪，同步到客户端

    // 魔法值回复属性 - 定义实体每秒回复的魔法值数量
    public static final EntityAttribute MANAREGEN = register("mana_regen",
            (new ClampedEntityAttribute("attribute.name.mana_regen", // 属性名称翻译键
                    (double) 0f,   // 默认值：每秒回复0点魔法值
                    (double) 0f,     // 最小值：0点回复（不能为负）
                    (double) 100000f) // 最大值：每秒回复100,000点魔法值
            ).setTracked(true)); // 设置为跟踪，同步到客户端

    // 魔法值消耗属性 - 定义使用魔法时的消耗倍率（百分比）
    public static final EntityAttribute MANACOST = register("mana_cost",
            (new ClampedEntityAttribute("attribute.name.mana_cost", // 属性名称翻译键
                    (double) 100.0f, // 默认值：100%（正常消耗）
                    (double) 1.0f,   // 最小值：1%（最低消耗）
                    (double) 100000f) // 最大值：100,000%（最高消耗）
            ).setTracked(true)); // 设置为跟踪，同步到客户端

    // 私有注册方法 - 将属性注册到游戏注册表中
    private static EntityAttribute register(String id, EntityAttribute attribute) {
        return Registry.register(Registries.ATTRIBUTE, new Identifier("soulstone",id), attribute);
    }
}
