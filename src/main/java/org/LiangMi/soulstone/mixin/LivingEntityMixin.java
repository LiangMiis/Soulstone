package org.LiangMi.soulstone.mixin;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
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

// 将此类混入(PlayerEntity.class)中，使其具备ManaInterface接口的能力
@Mixin(PlayerEntity.class)
public class LivingEntityMixin implements ManaInterface {

    // 声明用于同步的魔力值跟踪数据（服务端与客户端同步）
    private static final TrackedData<Float> CURRENTMANA;

    // 存储玩家的魔力效果实例（如持续消耗/恢复效果）
    public List<ManaInstance> manaInstances = new ArrayList<ManaInstance>(List.of());
    // 记录魔力值保持满额的连续tick数
    public int timefull = 0;

    // 静态初始化块：注册魔力值跟踪数据
    static {
        CURRENTMANA = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);
    }

    // 实现接口方法：获取最大魔力值（基础值50 + 属性加成）
    @Override
    public double getMaxMana() {
        PlayerEntity player = (PlayerEntity) (Object) this;
        return 50 + player.getAttributeValue(ManaRegistry.MANA);
    }

    // 计算每tick的魔力恢复量（属性值除以20，将每秒恢复转换为每tick恢复）
    public double getManaRegen() {
        PlayerEntity living = (PlayerEntity) (Object) this;
        return living.getAttributeValue(ManaRegistry.MANAREGEN) / 20;
    }

    // 获取当前所有魔力效果实例
    @Override
    public List<ManaInstance> getManaInstances() {
        return this.manaInstances;
    }

    // 注入到实体的tick方法头部：每tick更新魔力逻辑
    @Inject(at = @At("HEAD"), method = "tick")
    public void tickMana(CallbackInfo callbackInfo) {
        LivingEntity living = (LivingEntity) (Object) this;

        // 魔力恢复逻辑
        if (living.getDataTracker().get(CURRENTMANA) < getMaxMana()) {
            // 当前魔力未满：增加魔力（不超过最大值）
            living.getDataTracker().set(CURRENTMANA, (float) Math.min(
                    living.getDataTracker().get(CURRENTMANA) + getManaRegen(),
                    getMaxMana()
            ));
            timefull = 0; // 重置满额计时器
        } else {
            // 魔力已满：确保精确等于最大值并递增计时器
            living.getDataTracker().set(CURRENTMANA, (float) getMaxMana());
            timefull++;
        }

        // 更新所有魔力效果实例
        for (ManaInstance instance : this.getManaInstances()) {
            instance.tick(); // 执行每个实例的tick逻辑
        }

        // 清理已结束的效果
        if (!this.getManaInstances().isEmpty()) {
            this.getManaInstances().removeIf(manaInstance -> manaInstance.remainingduration <= 0);
        }
    }

    // 注入到实体数据跟踪器初始化方法：注册魔力值同步数据
    @Inject(at = @At("TAIL"), method = "initDataTracker")
    protected void initDataTrackerMana(CallbackInfo callbackInfo) {
        LivingEntity living = (LivingEntity) (Object) this;
        living.getDataTracker().startTracking(CURRENTMANA, 0F); // 初始值0
    }

    // 注入到玩家属性创建方法：添加自定义魔力属性
    @Inject(method = "createPlayerAttributes", at = @At("RETURN"))
    private static void addAttributesextraspellattributes_RETURN(
            final CallbackInfoReturnable<DefaultAttributeContainer.Builder> info
    ) {
        // 注册三个自定义属性
        info.getReturnValue().add(ManaRegistry.MANA);       // 最大魔力
        info.getReturnValue().add(ManaRegistry.MANAREGEN); // 魔力恢复
        info.getReturnValue().add(ManaRegistry.MANACOST);  // 魔力消耗减免
    }

    // 记录最后使用的法术（用于连击系统等）
    public @Nullable Spell lastSpell;

    public Spell getLastSpell() {
        return lastSpell;
    }

    public void setLastSpell(Spell lastSpell) {
        this.lastSpell = lastSpell;
    }

    // 实现接口方法：获取当前魔力值
    @Override
    public double getMana() {
        LivingEntity living = (LivingEntity) (Object) this;
        return living.getDataTracker().get(CURRENTMANA);
    }

    // 消耗魔力接口实现
    @Override
    public double spendMana(double toadd) {
        LivingEntity living = (LivingEntity) (Object) this;
        // 创建魔力消耗效果实例（持续80ticks）
        if (living instanceof PlayerEntity player) {
            this.getManaInstances().add(new ManaInstance(player, 80, -toadd));
        }
        // 直接扣除魔力（toadd应为负值）
        living.getDataTracker().set(CURRENTMANA, (float)(living.getDataTracker().get(CURRENTMANA) + toadd));
        return living.getDataTracker().get(CURRENTMANA);
    }

    // 获取魔力满额持续时间
    @Override
    public int getTimeFull() {
        return timefull;
    }
}