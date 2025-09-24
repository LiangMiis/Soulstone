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
    // 装备时触发的方法 - 添加状态效果
    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        // 如果实体已经有该状态效果，则不重复添加
        if(entity.hasStatusEffect(effect)) return;
        // 创建无限持续时间、指定等级的状态效果实例
        // 参数说明：效果类型，持续时间(Integer.MAX_VALUE表示无限)，等级，环境粒子显示，图标显示，在HUD上显示
        StatusEffectInstance effectInstance = new StatusEffectInstance(effect, Integer.MAX_VALUE, amplifier, false, false, false);
        entity.addStatusEffect(effectInstance);
    }
    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.tick(stack, slot, entity); // 调用父类的tick方法
        // 如果实体有指定的状态效果，则移除它
        if(!entity.hasStatusEffect(effect)) {
            StatusEffectInstance effectInstance = new StatusEffectInstance(effect, Integer.MAX_VALUE, amplifier, false, false, false);
            entity.addStatusEffect(effectInstance);
        }
    }

    // 卸下时触发的方法 - 移除状态效果
    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        entity.removeStatusEffect(effect);
    }
}
