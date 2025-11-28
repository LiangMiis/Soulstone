package org.LiangMi.soulstone.config;

import java.util.HashMap;
import java.util.Map;
public class AttributeConfig {
    private static final Map<String, AttributeDefinition> ATTRIBUTES = new HashMap<>();

    static {
        // 定义所有可加点的属性
        registerAttribute("health", "最大生命值", 2.0, 0.5, 100, "每点增加1颗心");
        registerAttribute("attack", "攻击伤害", 1.0, 0.5, 50, "每点增加1点攻击伤害");
        registerAttribute("defense", "防御力", 1.0, 0.5, 50, "每点增加1点护甲值");
        registerAttribute("speed", "移动速度", 0.01, 0.002, 30, "每点增加1%移动速度");
        registerAttribute("mining_speed", "挖掘速度", 0.1, 0.02, 50, "每点增加10%挖掘速度");
        registerAttribute("luck", "幸运值", 0.05, 0.01, 20, "每点增加5%幸运值");
        registerAttribute("experience", "经验加成", 0.1, 0.02, 30, "每点增加10%经验获取");
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

        public String getDisplayValue(int points) {
            double value = calculateValue(points);
            if (id.equals("speed") || id.equals("luck") || id.equals("experience")) {
                return String.format("%.1f%%", value * 100);
            }
            return String.format("%.1f", value);
        }
    }
}
