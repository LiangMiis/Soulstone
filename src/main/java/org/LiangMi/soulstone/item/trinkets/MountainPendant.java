package org.LiangMi.soulstone.item.trinkets;

import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.spell_engine.particle.ParticleHelper;
import net.spell_engine.utils.SoundHelper;
import org.LiangMi.soulstone.api.AttackerTargetInterface;
import org.LiangMi.soulstone.effect.Effects;
import org.spongepowered.asm.mixin.Unique;

public class MountainPendant extends TrinketBass{
    public MountainPendant(String tooltip) {
        super(tooltip);
    }
    @Unique
    public int dev;

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        dev = 0;
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        Entity attacker = ((AttackerTargetInterface) entity).getAttacker(entity);
        if (attacker != null){
            dev++;
            if(dev>=3){
                dev = 0;
                var maxHealth = entity.getMaxHealth();
                var health = entity.getHealth();
                var setHealth = maxHealth * 0.1f;
                entity.setHealth(health+setHealth);
            }
        }
    }
}
