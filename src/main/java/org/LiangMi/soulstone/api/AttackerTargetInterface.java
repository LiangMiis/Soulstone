package org.LiangMi.soulstone.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public interface AttackerTargetInterface {
    Entity getAttacker(LivingEntity entity);

    LivingEntity getTarget(Entity entity);
}
