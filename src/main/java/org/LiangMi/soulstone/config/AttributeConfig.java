package org.LiangMi.soulstone.config;

import java.util.HashMap;
import java.util.Map;
public class AttributeConfig {
    private static final Map<String, AttributeDefinition> ATTRIBUTES = new HashMap<>();

    static {
        // 定义所有可加点的属性
        registerAttribute("health", "最大生命值", 2.0, 1, 100, "每点增加1颗心");
        registerAttribute("attack", "攻击伤害", 1.0, 0.5, 50, "每点增加1点攻击伤害");
        registerAttribute("defense", "防御力", 1.0, 0.5, 50, "每点增加1点护甲值");
        registerAttribute("speed", "移动速度", 0.01, 0.002, 30, "每点增加1%移动速度");
        registerAttribute("mana", "以太", 0, 1, 100, "每点增加1点以太");
        registerAttribute("arcane", "奥秘", 0, 1, 100, "每点增加1点奥秘");
        registerAttribute("fire", "火焰", 0, 1, 100, "每点增加1点火焰");
        registerAttribute("frost", "寒冰", 0, 1, 100, "每点增加1点寒冰");
        registerAttribute("healing", "治愈", 0, 1, 100, "每点增加1点治愈");
        registerAttribute("lightning", "雷电", 0, 1, 100, "每点增加1点雷电");
        registerAttribute("soul", "灵魂", 0, 1, 100, "每点增加1点灵魂");
        registerAttribute("critical_chance","法术暴击概率",0,1,100,"每点增加1点暴击概率");
        registerAttribute("critical_damage","法术暴击伤害",0,1,100,"每点增加1点暴击伤害");
        registerAttribute("haste","施法速度",0,1,100,"每点增加1点施法速度");
    }

    public static void registerAttribute(String id, String name, double baseValue, double increment, int maxLevel, String description) {
        ATTRIBUTES.put(id, new AttributeDefinition(id, name, baseValue, increment, maxLevel, description));
    }

    public static AttributeDefinition getAttribute(String id) {
        return ATTRIBUTES.get(id);
    }

    public static Map<String, AttributeDefinition> getAllAttributes() {
        return new HashMap<>(ATTRIBUTES);
    }

    public static class AttributeDefinition {
        public final String id;
        public final String name;
        public final double baseValue;
        public final double increment;
        public final int maxLevel;
        public final String description;

        public AttributeDefinition(String id, String name, double baseValue, double increment, int maxLevel, String description) {
            this.id = id;
            this.name = name;
            this.baseValue = baseValue;
            this.increment = increment;
            this.maxLevel = maxLevel;
            this.description = description;
        }

        public double calculateValue(int points) {
            return baseValue + (increment * points);
        }
    }
}
