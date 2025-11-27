package org.LiangMi.soulstone.item.trinkets;

import dev.emi.trinkets.api.SlotReference;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import org.LiangMi.soulstone.api.AttackerTargetInterface;
import org.LiangMi.soulstone.effect.Effects;
import org.spongepowered.asm.mixin.Unique;

public class CrimsonAuthority extends TrinketBass{
    public CrimsonAuthority(String tooltip) {
        super(tooltip);
    }


    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        StatusEffectInstance effectInstance = new StatusEffectInstance(Effects.CRIMSONMADNESS,20,0,false,false,false);
        Entity attacker = ((AttackerTargetInterface) entity).getAttacker(entity);
        if (attacker != null){
            entity.addStatusEffect(effectInstance);
        }
    }
}


