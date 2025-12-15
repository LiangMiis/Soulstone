package org.LiangMi.soulstone.client.models;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import org.LiangMi.soulstone.registry.EntityRegistry;
import org.LiangMi.soulstone.client.renderer.Entity.SpellTargetEntityRenderer;

public class EntityModels {
    public static void register(){
        EntityRendererRegistry.register(EntityRegistry.SPELL_TARGET_ENTITY, SpellTargetEntityRenderer::new);
    }
}
