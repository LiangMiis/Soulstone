package org.LiangMi.soulstone.item.trinkets;

import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Unique;

public class ImmortalMonitor extends TrinketBass{
    public ImmortalMonitor(String tooltip) {
        super(tooltip);
    }
    @Unique
    public int Cooldown=0;

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {

        var maxHealth = entity.getMaxHealth();
        var health = entity.getHealth();
        var lHealth = maxHealth * 0.3;
        Cooldown++;
        if(health<= lHealth){
            if(Cooldown>=1800){
                Cooldown=0;
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION,120,5,false,false,false));
            }else {
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION,1,2,false,false,false));
            }
        }else {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION,1,2,false,false,false));
        }
    }
}
