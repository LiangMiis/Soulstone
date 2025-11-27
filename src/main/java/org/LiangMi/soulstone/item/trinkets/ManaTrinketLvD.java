package org.LiangMi.soulstone.item.trinkets;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import org.LiangMi.soulstone.registry.ManaRegistry;

import java.util.UUID;

public class ManaTrinketLvD extends TrinketBass{
    public ManaTrinketLvD(String tooltip) {
        super(tooltip);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        var modifier = super.getModifiers(stack, slot, entity, uuid);
        modifier.put(ManaRegistry.MANA,new EntityAttributeModifier("ManaTT1",30, EntityAttributeModifier.Operation.ADDITION));
        modifier.put(ManaRegistry.MANAREGEN,new EntityAttributeModifier("ManaTT1",0.2, EntityAttributeModifier.Operation.ADDITION));
        return modifier;
    }
}
