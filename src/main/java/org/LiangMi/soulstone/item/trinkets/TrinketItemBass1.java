package org.LiangMi.soulstone.item.trinkets;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class TrinketItemBass1 extends TrinketBass{
    public final EntityAttribute Modifier;
    public final String Name;
    public final double Num;
    public final EntityAttributeModifier.Operation Operation;


    public TrinketItemBass1(String tooltip,EntityAttribute modifier,String name,double num,EntityAttributeModifier.Operation operation) {
        super(tooltip);
        this.Modifier = modifier;
        this.Name = name;
        this.Num = num;
        this.Operation = operation;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        var modifiers = super.getModifiers(stack, slot, entity, uuid);
        modifiers.put(Modifier,new EntityAttributeModifier(uuid,Name,Num,Operation));
        return modifiers;
    }
}
