package org.LiangMi.soulstone.item.trinkets;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import org.LiangMi.soulstone.client.glint.GlintRenderTypes;

import java.util.UUID;

public class ChampionsArmlet extends TrinketBass {

    private final StatusEffect effect;  // 戒指提供的状态效果
    private final int amplifier;        // 状态效果的等级/放大器

    public ChampionsArmlet(StatusEffect effect,int amplifier, String tooltip) {
        super(tooltip);
        this.effect = effect;
        this.amplifier = amplifier;
    }


    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        var modifiers = super.getModifiers(stack, slot, entity, uuid);
        modifiers.put(EntityAttributes.GENERIC_MOVEMENT_SPEED,new EntityAttributeModifier(uuid,"soulstone:champions_armlet",-0.2,EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
        modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE,new EntityAttributeModifier(uuid,"soulstone:champions_armlet",5,EntityAttributeModifier.Operation.ADDITION));
        return modifiers;
    }
    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            StatusEffectInstance effectInstance = new StatusEffectInstance(effect, 10, amplifier, false, false, false);
            entity.addStatusEffect(effectInstance);
    }
}
