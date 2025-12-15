package org.LiangMi.soulstone.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.entity.SpellTargetEntity;

public class EntityRegistry {

    // 实体类型常量声明
    // 使用 public static final 确保实体类型的单例和不可变性

    /**
     * 法术目标实体类型
     * 用于技能系统的目标定位或辅助实体
     */
    public static final EntityType<SpellTargetEntity> SPELL_TARGET_ENTITY;


    // 静态初始化块
    // 在类加载时执行，用于注册所有实体类型
    static {
        /**
         * 注册法术目标实体
         *
         * 参数说明：
         * - Registries.ENTITY_TYPE: 实体类型注册表
         * - new Identifier(SimplySkills.MOD_ID, "custom_entity_1"): 实体唯一标识符（mod_id:实体名称）
         * - FabricEntityTypeBuilder.create(): 创建实体类型构建器
         *   - SpawnGroup.CREATURE: 实体生成组（生物类）
         *   - SpellTargetEntity::new: 实体构造函数引用
         *   - dimensions(EntityDimensions.fixed(0.75f, 0.75f)): 设置实体碰撞箱尺寸（宽0.75，高0.75）
         *   - build(): 构建实体类型实例
         */
        SPELL_TARGET_ENTITY = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(Soulstone.ID, "custom_entity_1"),
                FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, SpellTargetEntity::new)
                        .dimensions(EntityDimensions.fixed(0.75f, 0.75f))
                        .build()
        );
    }


    /**
     * 注册实体方法
     * 在模组初始化时调用，用于输出注册日志
     * 注意：实际的实体注册已在静态初始化块中完成
     * 此方法主要用于提供统一的初始化入口和日志记录
     */
    public static void registerEntities() {
        // 记录实体注册日志
        Soulstone.LOGGER.info("Registering Entities for " + Soulstone.ID);
        // 实体类型已在静态块中注册，此处仅记录日志
        // 如果需要额外的初始化逻辑，可以在此处添加
    }
}
