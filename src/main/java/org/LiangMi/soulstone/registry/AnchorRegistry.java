package org.LiangMi.soulstone.registry;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class AnchorRegistry {
    public static final EntityAttribute Anchor = register("anchor",
            (new ClampedEntityAttribute("attribute.name.anchor", // 属性名称翻译键
                    100.0f, // 默认值：100%（正常消耗）
                    1.0f,   // 最小值：1%（最低消耗）
                    100000f) // 最大值：100,000%（最高消耗）
            ).setTracked(true)); // 设置为跟踪，同步到客户端

    private static EntityAttribute register(String id, EntityAttribute attribute) {
        return Registry.register(Registries.ATTRIBUTE, id, attribute);
    }
}
