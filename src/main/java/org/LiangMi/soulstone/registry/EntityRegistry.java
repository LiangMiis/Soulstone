package org.LiangMi.soulstone.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.LiangMi.soulstone.entity.BloodEyeEntity;

public class EntityRegistry {
    public static final EntityType<BloodEyeEntity> BLOOD_EYE_ENTITY_ENTITY_TYPE = registerMob("blood_eye",BloodEyeEntity::new,0.5F,0.5F);


    public static <T extends MobEntity> EntityType registerMob(String name, EntityType.EntityFactory<T> entity, float width, float height) {
        return (EntityType) Registry.register(Registries.ENTITY_TYPE, new Identifier("soulstone", name), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, entity).dimensions(EntityDimensions.changing(width, height)).build());
    }
}
