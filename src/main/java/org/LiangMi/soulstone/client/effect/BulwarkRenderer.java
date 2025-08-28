package org.LiangMi.soulstone.client.effect;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.spell_engine.api.render.OrbitingEffectRenderer;
import org.LiangMi.soulstone.Soulstone;

import java.util.List;

// 堡垒效果渲染器类，继承自轨道效果渲染器
public class BulwarkRenderer extends OrbitingEffectRenderer {

    // 基础模型资源标识符
    public static final Identifier modelId_base = new Identifier(Soulstone.ID,"effect/bulwark");

    // 基础渲染层：使用方块图集纹理的实体透明渲染层
    public static final RenderLayer BASE_RENDER_LAYER =
            RenderLayer.getEntityTranslucent(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);

    /**
     * 构造函数：初始化堡垒效果渲染器
     * 配置渲染模型、缩放比例和高度偏移
     */
    public BulwarkRenderer() {
        super(List.of( // 创建模型列表
                        new Model(BASE_RENDER_LAYER, modelId_base)), // 基础模型配置
                1F,    // 缩放比例：1.0（原始大小）
                0.35F  // 高度偏移：0.35（相对于实体中心的位置）
        );
    }
}
