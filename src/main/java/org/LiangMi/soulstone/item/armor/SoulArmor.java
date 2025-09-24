package org.LiangMi.soulstone.item.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.spell_engine.api.item.ConfigurableAttributes;
import net.spell_engine.api.item.armor.Armor;
import net.spell_engine.api.item.weapon.Weapon;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.client.renderer.armor.ArmorRenderer;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

// 定义一个名为SoulArmor的公共类，继承自ArmorItem，并实现GeoItem和ConfigurableAttributes接口
public class SoulArmor extends ArmorItem implements GeoItem, ConfigurableAttributes {
    // 声明一个静态不可变的Identifier（标识符）常量，用于装备音效的资源路径（使用Soulstone模组ID）
    public static final Identifier equipSoundId = new Identifier(Soulstone.ID, "soulstone_robes_equip");
    // 声明一个静态不可变的SoundEvent（音效事件）常量，使用上面的标识符创建
    public static final SoundEvent equipSound = SoundEvent.of(equipSoundId);

    // 声明一个final实例变量，存储自定义护甲材料
    public final Armor.CustomMaterial customMaterial;

    // 构造函数，接收材料类型、护甲类型和设置参数
    public SoulArmor(Armor.CustomMaterial material, Type type, Settings settings) {
        // 调用父类ArmorItem的构造函数
        super(material, type, settings);
        // 初始化实例变量customMaterial
        this.customMaterial = material;
    }

    // 声明一个私有变量，用于存储属性修改器的多重映射
    private Multimap<EntityAttribute, EntityAttributeModifier> attributes;

    // 实现ConfigurableAttributes接口的方法，用于设置属性修改器
    @Override
    public void setAttributes(Multimap<EntityAttribute, EntityAttributeModifier> attributes) {
        // 使用ImmutableMultimap.Builder构建不可变的属性映射
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        // 注释掉的代码：将父类的属性修改器添加到构建器（当前被禁用）
        // builder.putAll(super.getAttributeModifiers(this.slot));
        // 将传入的属性修改器添加到构建器
        builder.putAll(attributes);
        // 构建不可变的多重映射并赋值给实例变量
        this.attributes = builder.build();
    }

    // 重写获取属性修改器的方法
    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        // 如果属性映射为空，则调用父类方法
        if (attributes == null) {
            return super.getAttributeModifiers(slot);
        }
        // 如果传入的装备槽位匹配当前护甲类型的槽位，返回自定义属性，否则返回父类属性
        return slot == this.type.getEquipmentSlot() ? this.attributes : super.getAttributeModifiers(slot);
    }

    // MARK: GeoItem - 以下部分实现GeoItem接口

    // 创建可动画实例缓存，用于Geo渲染系统（使用GeckoLibUtil而非AzureLibUtil）
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    // 创建渲染提供者的供应商（Supplier），使用GeoItem的makeRenderer方法
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    // 实现GeoItem接口的方法，创建渲染器
    @Override
    public void createRenderer(Consumer<Object> consumer) {
        // 接受一个渲染提供者
        consumer.accept(new RenderProvider() {
            // 声明GeoArmorRenderer变量
            private GeoArmorRenderer<?> renderer;

            // 获取人形护甲模型的方法
            @Override
            public BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, BipedEntityModel<LivingEntity> original) {
                // 如果渲染器尚未初始化，则创建新的ArmorRenderer（注意：此处使用的是ArmorRenderer而非WizardArmorRenderer）
                if (this.renderer == null) {
                    this.renderer = new ArmorRenderer();
                }
                // 准备渲染器进行渲染
                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                // 返回渲染器
                return this.renderer;
            }
        });
    }

    // 实现GeoItem接口的方法，获取渲染提供者
    @Override
    public Supplier<Object> getRenderProvider() {
        return renderProvider;
    }

    // 实现GeoItem接口的方法，注册动画控制器（当前为空实现）
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    // 实现GeoItem接口的方法，获取可动画实例缓存
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
