package org.LiangMi.soulstone.mixin;

import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.LiangMi.soulstone.api.WeightInterface;
import org.LiangMi.soulstone.registry.ManaRegistry;
import org.LiangMi.soulstone.registry.WeightRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class WeightPlayerEntityMixin implements WeightInterface{
    private static final TrackedData<Float> CURRENTWEIGHT;
    static {
        CURRENTWEIGHT = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);
    }
    @Override
    public double maxWeight() {
        PlayerEntity player = (PlayerEntity) (Object) this;
        return 50 + player.getAttributeValue(WeightRegistry.WEIGHT);
    }
    public void actualIteamWeight(){
        ItemStack stack = null;
    }
    // 注入到玩家属性创建方法（在RETURN点）
    @Inject(method = "createPlayerAttributes", at = @At("RETURN"))
    private static void addAttributesextraspellattributes_RETURN(
            final CallbackInfoReturnable<DefaultAttributeContainer.Builder> info
    ) {
        // 向玩家属性系统添加三个自定义属性
        info.getReturnValue().add(WeightRegistry.WEIGHT);      // 最大魔力属性
    }

    @Override
    public double playerWeight() {
        return 0;
    }

    @Override
    public double itemWeight() {
        PlayerEntity player = (PlayerEntity) (Object) this;
        return 0;
    }

    @Override
    public double actualWeight() {
        return 0;
    }
}
