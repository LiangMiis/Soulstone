package org.LiangMi.soulstone.item.trinkets;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class SurvivalsCurse extends TrinketItem {
    public final StatusEffect eff; // 戒指提供的状态效果
    public SurvivalsCurse(StatusEffect effect) {
        super(new Settings().maxCount(1));
        this.eff = effect;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        var modifiers = super.getModifiers(stack, slot, entity, uuid);
        modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE,new EntityAttributeModifier(uuid,"soulstone:treasure_hunters_blade",2,EntityAttributeModifier.Operation.ADDITION));
        return modifiers;
    }

}
