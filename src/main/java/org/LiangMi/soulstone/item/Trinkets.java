package org.LiangMi.soulstone.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.LiangMi.soulstone.item.trinkets.*;
import org.LiangMi.soulstone.registry.ManaRegistry;

public class Trinkets {

    public static final Item ChampionsArmlet = ItemRegister("champions_armlet",new ChampionsArmlet(StatusEffects.DARKNESS,1,"tooltip.champions_armlet" ));

    public static final Item TreasureHuntersBlade = ItemRegister("treasure_hunter_s_blade",new TrinketItemBass1("tooltip.treasure_hunter_s_blade", EntityAttributes.GENERIC_ATTACK_DAMAGE,"treasure_hunter_s_blade",1, EntityAttributeModifier.Operation.ADDITION));
    public static final Item HealthRing = ItemRegister("health_ring",new TrinketItemBass1("tooltip.health_ring",EntityAttributes.GENERIC_MAX_HEALTH,"health_ring",2, EntityAttributeModifier.Operation.ADDITION));
    public static final Item ManaRing = ItemRegister("mana_ring",new TrinketItemBass1("tooltip.mana_ring", ManaRegistry.MANA,"mana_ring",0.1, EntityAttributeModifier.Operation.MULTIPLY_BASE));
    public static final Item ArmorRing = ItemRegister("armor_ring",new TrinketItemBass1("tooltip.armor_ring",EntityAttributes.GENERIC_ARMOR,"armor_ring",0.1, EntityAttributeModifier.Operation.MULTIPLY_BASE));
    public static final Item LuckRing = ItemRegister("luck_ring",new TrinketItemBass1("tooltip.luck_ring",EntityAttributes.GENERIC_LUCK,"luck_ring",0.1, EntityAttributeModifier.Operation.MULTIPLY_BASE));
    public static final Item AttackSpeedRing = ItemRegister("attack_speed_ring",new TrinketItemBass1("tooltip.attack_speed_ring",EntityAttributes.GENERIC_ATTACK_SPEED,"attack_speed_ring",0.1, EntityAttributeModifier.Operation.MULTIPLY_BASE));
    public static final Item SpeedRing = ItemRegister("speed_ring",new TrinketItemBass1("tooltip.speed_ring",EntityAttributes.GENERIC_MOVEMENT_SPEED,"speed_ring",0.1, EntityAttributeModifier.Operation.MULTIPLY_BASE));
    public static final Item ArmorToughnessRing = ItemRegister("armor_toughness_ring",new TrinketItemBass1("tooltip.armor_toughness_ring",EntityAttributes.GENERIC_ARMOR_TOUGHNESS,"armor_toughness_ring",0.1, EntityAttributeModifier.Operation.MULTIPLY_BASE));
    public static final Item AttackKnockBackRing = ItemRegister("attack_knock_back_ring",new TrinketItemBass1("tooltip.attack_knock_back_ring",EntityAttributes.GENERIC_ATTACK_KNOCKBACK,"attack_knock_back_ring",0.1, EntityAttributeModifier.Operation.MULTIPLY_BASE));
    public static final Item FlyingSpeedRing = ItemRegister("flying_speed_ring",new TrinketItemBass1("tooltip.flying_speed_ring",EntityAttributes.GENERIC_FLYING_SPEED,"flying_speed_ring",0.1, EntityAttributeModifier.Operation.MULTIPLY_BASE));
    public static final Item KnockBackResistanceRing = ItemRegister("knock_back_resistance_ring",new TrinketItemBass1("tooltip.knock_back_resistance_ring_ring",EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,"knock_back_resistance_ring",0.1, EntityAttributeModifier.Operation.MULTIPLY_BASE));

    public static final Item ManaTrinketLvD = ItemRegister("mana_trinket_lv_d",new ManaTrinketLvD("tooltip.mana_trinket_lv_d"));

    public static final Item CrimsonAuthority = ItemRegister("crimson_authority",new CrimsonAuthority("tooltip.crimson_authority"));
    public static final Item PhilosophersStone = ItemRegister("philosopher_s_stone",new PhilosophersStone("tooltip.philosopher_s_stone"));
    public static final Item WarriorRoar = ItemRegister("warrior_roar",new WarriorRoar("tooltip.warrior_roar"));
    public static final Item MountainPendant = ItemRegister("mountain_pendant",new MountainPendant("tooltip.mountain_pendant"));
    public static final Item ImmortalMonitor = ItemRegister("immortal_monitor",new ImmortalMonitor("tooltip.immortal_monitor"));
    public static Item ItemRegister(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier("soulstone", name), item);
    }
    public static void addItemsToItemGroup(){
        addToItemGroup(TreasureHuntersBlade);
        addToItemGroup(ChampionsArmlet);
        addToItemGroup(HealthRing);
        addToItemGroup(CrimsonAuthority);
        addToItemGroup(PhilosophersStone);
        addToItemGroup(WarriorRoar);
        addToItemGroup(MountainPendant);
        addToItemGroup(ImmortalMonitor);
        addToItemGroup(ManaRing);
        addToItemGroup(ArmorRing);
        addToItemGroup(LuckRing);
        addToItemGroup(AttackSpeedRing);
        addToItemGroup(SpeedRing);
        addToItemGroup(ArmorToughnessRing);
        addToItemGroup(AttackKnockBackRing);
        addToItemGroup(FlyingSpeedRing);
        addToItemGroup(KnockBackResistanceRing);
        addToItemGroup(ManaTrinketLvD);
    }
    private static void addToItemGroup(Item item) {
        ItemGroupEvents.modifyEntriesEvent(Group.TRINKET).register(entries -> entries.add(item));
    }

    public static void initialize() {
        addItemsToItemGroup();
    }
}
