package org.LiangMi.soulstone.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.spell_engine.api.effect.Synchronized;
import org.LiangMi.soulstone.effect.Effects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 抽象类：用于渲染具有隐身效果的生物实体
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRenderStealthMixin<T extends Entity> extends EntityRenderer<T> {
    // 构造函数：接收实体渲染器上下文
    protected LivingEntityRenderStealthMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    // 获取本地玩家实体（客户端玩家）
    @Unique
    private ClientPlayerEntity localPlayer() {
        return MinecraftClient.getInstance().player;
    }

    // 检查实体是否具有隐身效果
    @Unique
    private boolean hasStealthEffect(LivingEntity entity) {
        // 通过同步状态效果提供者接口检查效果（直接检查可能因线程或tick顺序问题失效）
        var effects = ((Synchronized.Provider)entity).SpellEngine_syncedStatusEffects();
        for (var effect : effects) {
            if (effect.effect() == Effects.STEALTH) {
                return true;
            }
        }
        return false;
    }

    // 检查实体对本地玩家是否可见
    @Unique
    private boolean visibleForLocalPlayer(LivingEntity entity) {
        return entity == localPlayer() || !entity.isInvisibleTo(localPlayer());
    }

    /**
     * 注入到渲染层获取方法头部：修改隐身实体的渲染层
     * @param entity 被渲染的实体
     * @param showBody 是否显示身体
     * @param translucent 是否半透明
     * @param showOutline 是否显示轮廓
     * @param cir 回调信息，用于返回渲染层和取消原方法
     */
    @Inject(method = "getRenderLayer", at = @At("HEAD"), cancellable = true)
    private void getRenderLayer_HEAD_Stealth(LivingEntity entity, boolean showBody, boolean translucent, boolean showOutline, CallbackInfoReturnable<RenderLayer> cir) {
        // 如果实体有隐身效果且对本地玩家可见
        if (hasStealthEffect(entity) && visibleForLocalPlayer(entity)) {
            // 获取实体纹理
            Identifier identifier = ((EntityRenderer<T>)this).getTexture((T)entity);
            // 使用半透明剔除渲染层
            var layer = RenderLayer.getItemEntityTranslucentCull(identifier);
            // 设置返回值并取消原方法执行
            cir.setReturnValue(layer);
            cir.cancel();
        }
    }

    /**
     * 包装模型渲染操作：调整隐身实体的透明度
     * @param instance 实体模型实例
     * @param matrixStack 矩阵栈
     * @param vertexConsumer 顶点消费者
     * @param light 光照等级
     * @param overlayIndex 覆盖层索引
     * @param red 红色分量
     * @param green 绿色分量
     * @param blue 蓝色分量
     * @param alpha 透明度分量
     * @param original 原始方法操作
     * @param entity 被渲染的实体
     * @param f 参数1
     * @param g 参数2
     * @param contextMatrixStack 上下文矩阵栈
     * @param contextVertexConsumerProvider 上下文顶点消费者提供者
     * @param contextLight 上下文光照等级
     */
    @WrapOperation(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V")
    )
    private void modelRender_WRAP_STEALTH(
            // Mixin参数
            EntityModel instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlayIndex, float red, float green, float blue, float alpha, Operation<Void> original,
            // 上下文参数
            LivingEntity entity, float f, float g, MatrixStack contextMatrixStack, VertexConsumerProvider contextVertexConsumerProvider, int contextLight
    ) {
        // 如果实体有隐身效果且对本地玩家可见
        if (hasStealthEffect(entity) && visibleForLocalPlayer(entity)) {
            // 使用低透明度渲染（0.15F = 15%不透明度）
            original.call(instance, matrixStack, vertexConsumer, light, overlayIndex, red, green, blue, 0.25F);
        } else {
            // 正常渲染
            original.call(instance, matrixStack, vertexConsumer, light, overlayIndex, red, green, blue, alpha);
        }
    }

    /**
     * 条件包装特性渲染器：控制隐身实体的特性渲染
     * @param instance 特性渲染器实例
     * @param matrixStack 矩阵栈
     * @param vertexConsumerProvider 顶点消费者提供者
     * @param i 光照等级
     * @param t 实体
     * @param a 参数1
     * @param b 参数2
     * @param c 参数3
     * @param d 参数4
     * @param e 参数5
     * @param f 参数6
     * @return 是否渲染该特性
     */
    @WrapWithCondition(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/FeatureRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/Entity;FFFFFF)V")
    )
    private boolean featureRenderer_WRAP_CONDITION_STEALTH(FeatureRenderer instance, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T t, float a, float b, float c, float d, float e, float f) {
        var entity = (LivingEntity) t;
        // 如果实体有隐身效果
        if (hasStealthEffect(entity)) {
            // 如果对本地玩家可见
            if (visibleForLocalPlayer(entity)) {
                // 只渲染手持物品特性（保持物品可见）
                return instance instanceof HeldItemFeatureRenderer<?, ?>;
            } else {
                // 完全隐藏其他特性
                return false;
            }
        }
        // 没有隐身效果，正常渲染所有特性
        return true;
    }
}
