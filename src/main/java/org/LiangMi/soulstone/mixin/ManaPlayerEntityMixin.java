package org.LiangMi.soulstone.mixin;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.spell_engine.api.spell.Spell;
import org.LiangMi.soulstone.api.ManaInstance;
import org.LiangMi.soulstone.api.ManaInterface;
import org.LiangMi.soulstone.registry.ManaRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

// 将此类混入PlayerEntity类，使其实现魔力接口功能
@Mixin(PlayerEntity.class)
public class ManaPlayerEntityMixin implements ManaInterface {

    // 定义跟踪当前魔力值的数据字段（用于服务端与客户端同步）
    private static final TrackedData<Float> CURRENTMANA;

    // 存储玩家当前生效的魔力效果实例列表
    public List<ManaInstance> manaInstances = new ArrayList<ManaInstance>(List.of());
    // 记录魔力值保持满额的连续游戏刻数
    public int timefull = 0;

    // 静态初始化块：注册魔力值跟踪数据类型
    static {
        // 注册FLOAT类型的跟踪数据，用于同步当前魔力值
        CURRENTMANA = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);
    }

    // 实现接口方法：获取玩家最大魔力值
    @Override
    public double getMaxMana() {
        // 获取当前玩家实体实例
        PlayerEntity player = (PlayerEntity) (Object) this;
        // 基础魔力值
        return player.getAttributeValue(ManaRegistry.MANA);
    }

    // 计算每游戏刻的魔力恢复量
    public double getManaRegen() {
        // 获取当前玩家实体实例
        PlayerEntity player = (PlayerEntity) (Object) this;
        // 将每秒恢复值转换为每刻恢复值（1秒=20游戏刻）
        return (double) player.getAttributeValue(ManaRegistry.MANAREGEN) / 20;
    }

    // 实现接口方法：获取当前所有魔力效果实例
    @Override
    public List<ManaInstance> getManaInstances() {
        return this.manaInstances;
    }

    // 注入到实体tick方法的头部：每游戏刻执行魔力更新逻辑
    @Inject(at = @At("HEAD"), method = "tick")
    public void tickMana(CallbackInfo callbackInfo) {
        // 将当前对象转换为LivingEntity
        LivingEntity living = (LivingEntity) (Object) this;

        // 魔力恢复逻辑：当当前魔力小于最大值时
        if (living.getDataTracker().get(CURRENTMANA) < getMaxMana()) {
            // 增加魔力值（不超过最大值）
            living.getDataTracker().set(CURRENTMANA, (float) Math.min(
                    living.getDataTracker().get(CURRENTMANA) + getManaRegen(),
                    getMaxMana()
            ));
            timefull = 0; // 重置满额计时器
        } else {
            // 确保魔力值精确等于最大值
            living.getDataTracker().set(CURRENTMANA, (float) getMaxMana());
            // 增加满额持续时间计数
            timefull++;
        }

        // 更新所有魔力效果实例
        for (ManaInstance instance : this.getManaInstances()) {
            // 调用每个魔力效果的tick方法
            instance.tick();
        }

        // 清理已结束的魔力效果
        if (!this.getManaInstances().isEmpty()) {
            // 移除持续时间<=0的效果实例
            this.getManaInstances().removeIf(manaInstance -> manaInstance.remainingduration <= 0);
        }
    }

    // 注入到实体数据跟踪器初始化方法的尾部
    @Inject(at = @At("TAIL"), method = "initDataTracker")
    protected void initDataTrackerMana(CallbackInfo callbackInfo) {
        // 将当前对象转换为LivingEntity
        LivingEntity living = (LivingEntity) (Object) this;
        // 注册并初始化魔力值跟踪数据（初始值为0）
        living.getDataTracker().startTracking(CURRENTMANA, 0F);
    }

    // 注入到玩家属性创建方法（在RETURN点）
    @Inject(method = "createPlayerAttributes", at = @At("RETURN"))
    private static void addAttributesextraspellattributes_RETURN(
            final CallbackInfoReturnable<DefaultAttributeContainer.Builder> info
    ) {
        // 向玩家属性系统添加三个自定义属性
        info.getReturnValue().add(ManaRegistry.MANA);      // 最大魔力属性
        info.getReturnValue().add(ManaRegistry.MANAREGEN); // 魔力恢复属性
        info.getReturnValue().add(ManaRegistry.MANACOST);  // 魔力消耗减免属性
    }

    // 记录最后使用的法术（用于连击系统等机制）
    public @Nullable Spell lastSpell;

    // 获取最后使用的法术
    public Spell getLastSpell() {
        return lastSpell;
    }

    // 设置最后使用的法术
    public void setLastSpell(Spell lastSpell) {
        this.lastSpell = lastSpell;
    }

    // 实现接口方法：获取当前魔力值
    @Override
    public double getMana() {
        // 将当前对象转换为LivingEntity
        LivingEntity living = (LivingEntity) (Object) this;
        // 从数据跟踪器获取当前魔力值
        return living.getDataTracker().get(CURRENTMANA);
    }

    // 实现接口方法：消耗/增加魔力
    @Override
    public double spendMana(double toadd) {
        // 将当前对象转换为LivingEntity
        LivingEntity living = (LivingEntity) (Object) this;
        // 创建魔力消耗效果实例（持续80游戏刻）
        if (living instanceof PlayerEntity player) {
            // 添加新的魔力效果实例（负值表示消耗）
            this.getManaInstances().add(new ManaInstance(player, 80, -toadd));
        }
        // 直接更新当前魔力值
        living.getDataTracker().set(CURRENTMANA, (float)(living.getDataTracker().get(CURRENTMANA) + toadd));
        // 返回更新后的魔力值
        return living.getDataTracker().get(CURRENTMANA);
    }

    // 实现接口方法：获取魔力满额持续时间
    @Override
    public int getTimeFull() {
        return timefull;
    }
}