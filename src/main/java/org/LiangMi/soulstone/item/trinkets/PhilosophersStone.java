package org.LiangMi.soulstone.item.trinkets;


import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.spell_power.api.SpellSchools;
import org.LiangMi.soulstone.item.Weapons;
import org.LiangMi.soulstone.registry.ManaRegistry;

import java.util.UUID;

public class PhilosophersStone extends TrinketBass{
    public PhilosophersStone(String tooltip) {
        super(tooltip);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        var modifiers = super.getModifiers(stack, slot, entity, uuid);
        modifiers.put(ManaRegistry.MANA,new EntityAttributeModifier(uuid,"philosopher_s_stone_mana",0.2, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
        modifiers.put(ManaRegistry.MANAREGEN,new EntityAttributeModifier(uuid,"philosopher_s_stone_mana",0.2, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
        modifiers.put(SpellSchools.HEALING.attribute, new EntityAttributeModifier(uuid,"philosopher_s_stone_healing",0.1, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
        return modifiers;
    }
}
