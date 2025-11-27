package org.LiangMi.soulstone.item.trinkets;

import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import org.LiangMi.soulstone.effect.Effects;

public class WarriorRoar extends TrinketBass{
    public WarriorRoar(String tooltip) {
        super(tooltip);
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        var health = entity.getHealth();
        var maxHealth = entity.getMaxHealth();
        if(health>=maxHealth/2){
            entity.addStatusEffect(new StatusEffectInstance(Effects.ROCK,1,0,false,false,false));
        }else {
            entity.addStatusEffect(new StatusEffectInstance(Effects.BREAKINGWIND,1,0,false,false,false));
        }

    }
}
