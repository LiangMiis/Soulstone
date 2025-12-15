package org.LiangMi.soulstone.entity;

import com.google.common.base.Suppliers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import java.util.function.Supplier;

/**
 * 法术目标实体类
 * 用于技能系统中的目标定位或标记，通常作为临时实体使用
 * 特点：无重力、不可见、自动销毁
 */
public class SpellTargetEntity extends Entity {

    /**
     * 构造函数
     *
     * @param type 实体类型
     * @param world 所在世界
     */
    public SpellTargetEntity(EntityType<?> type, World world) {
        super(type, world); // 调用父类Entity的构造函数
    }

    /**
     * 实体类型供应商
     * 使用Suppliers.memoize()确保单例，延迟初始化实体类型
     *
     * SpawnGroup.MISC: 生成组为杂项，表示这是一个功能性实体而非生物
     */
    public static final Supplier<EntityType<SpellTargetEntity>> TYPE = Suppliers.memoize(() ->
            EntityType.Builder.create(SpellTargetEntity::new, SpawnGroup.MISC).build("spell_target_entity"));

    /**
     * 实体生命周期（以游戏刻为单位）
     * 120刻 = 6秒（1秒=20刻）
     */
    public static int lifetime = 120;

    /**
     * 基础实体更新逻辑
     * 每游戏刻调用一次，处理实体的基本行为
     */
    @Override
    public void baseTick() {
        // 确保实体无重力
        this.setNoGravity(true);

        // 检查实体存在时间，超过生命周期则销毁
        if (this.age > lifetime)
            this.discard(); // 从世界中移除实体
    }

    /**
     * 检查实体是否受重力影响
     *
     * @return 是否无重力（true表示无重力）
     */
    @Override
    public boolean hasNoGravity() {
        return true; // 始终无重力
    }

    /**
     * 检查实体是否应该渲染（基于摄像机位置）
     *
     * @param cameraX 摄像机X坐标
     * @param cameraY 摄像机Y坐标
     * @param cameraZ 摄像机Z坐标
     * @return 是否渲染实体（false表示不渲染）
     */
    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return false; // 始终不渲染，实体不可见
    }

    /**
     * 检查实体是否应该渲染（基于距离）
     *
     * @param distance 到摄像机的距离
     * @return 是否渲染实体（false表示不渲染）
     */
    @Override
    public boolean shouldRender(double distance) {
        return false; // 始终不渲染，实体不可见
    }

    /**
     * 初始化数据追踪器
     * 用于同步实体状态数据到客户端
     * 此实体没有需要同步的数据，所以留空
     */
    @Override
    protected void initDataTracker() {
        // 此实体不需要数据同步
    }

    /**
     * 从NBT数据读取自定义数据
     * 用于从保存的数据恢复实体状态
     *
     * @param nbt NBT数据复合标签
     */
    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        // 此实体没有需要保存的自定义数据
    }

    /**
     * 将自定义数据写入NBT
     * 用于保存实体状态到世界数据
     *
     * @param nbt NBT数据复合标签
     */
    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        // 此实体没有需要保存的自定义数据
    }
}
