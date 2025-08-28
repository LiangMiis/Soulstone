package org.LiangMi.soulstone.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import org.LiangMi.soulstone.api.AnchorInstance;
import org.LiangMi.soulstone.api.AnchorInterface;
import org.LiangMi.soulstone.api.ManaInstance;
import org.LiangMi.soulstone.registry.AnchorRegistry;
import org.LiangMi.soulstone.registry.ManaRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
@Mixin(PlayerEntity.class)
public class AnchorPlayerEntityMixin implements AnchorInterface {
    private static final TrackedData<Float> CURRENTANTHOR;
    public List<AnchorInstance> anchorInstances = new ArrayList<AnchorInstance>(List.of());
    static {
        // 注册FLOAT类型的跟踪数据，用于同步当前魔力值
        CURRENTANTHOR = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);
    }
    @Override
    public double getAnchor() {
        // 将当前对象转换为LivingEntity
        LivingEntity living = (LivingEntity) (Object) this;
        // 从数据跟踪器获取当前心锚值
        return living.getDataTracker().get(CURRENTANTHOR);
    }

    @Override
    public double getMaxAnchor() {
        PlayerEntity player = (PlayerEntity) (Object) this;
        return 100 + player.getAttributeValue(AnchorRegistry.ANCHOR);
    }
    // 注入到实体tick方法的头部：每游戏刻执行魔力更新逻辑
    @Inject(at = @At("HEAD"), method = "tick")
    public void tickMana(CallbackInfo callbackInfo) {
        // 将当前对象转换为LivingEntity
        LivingEntity living = (LivingEntity) (Object) this;



    }
    // 注入到实体数据跟踪器初始化方法的尾部
    @Inject(at = @At("TAIL"), method = "initDataTracker")
    protected void initDataTrackerAnchor(CallbackInfo callbackInfo) {
        // 将当前对象转换为LivingEntity
        LivingEntity living = (LivingEntity) (Object) this;
        // 注册并初始化魔力值跟踪数据（初始值为0）
        living.getDataTracker().startTracking(CURRENTANTHOR, 0F);
    }
    // 注入到玩家属性创建方法（在RETURN点）
    @Inject(method = "createPlayerAttributes", at = @At("RETURN"))
    private static void addAttributesextraspellattributes_RETURN(
            final CallbackInfoReturnable<DefaultAttributeContainer.Builder> info
    ) {
        // 向玩家属性系统添加三个自定义属性
        info.getReturnValue().add(AnchorRegistry.ANCHOR);

    }
}
