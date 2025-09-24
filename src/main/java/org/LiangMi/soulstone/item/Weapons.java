package org.LiangMi.soulstone.item;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.recipe.Ingredient;
import net.spell_engine.api.item.ItemConfig;
import net.spell_engine.api.item.weapon.StaffItem;
import net.spell_engine.api.item.weapon.Weapon;
import net.spell_power.api.SpellSchools;
import org.LiangMi.soulstone.Soulstone;

import java.util.ArrayList;
import java.util.Map;

public class Weapons {
    public static final ArrayList<Weapon.Entry> entries = new ArrayList<>();
    private static Weapon.Entry entry(String requiredMod, String name, Weapon.CustomMaterial material, Item item, ItemConfig.Weapon defaults) {
        var entry = new Weapon.Entry(Soulstone.ID, name, material, item, defaults, null);
        if (entry.isRequiredModInstalled()) {
            entries.add(entry);
        }
        return entry;
    }
    private static final float orbAttackDamage = 1;
    private static final float orbAttackSpeed = -1F;
    private static final float daggerAttackDamage = 8;
    private static final float daggerAttackSpeed = -1.2F;
    private static final float greatswordAttackDamage = 14;
    private static final float greatswordAttackSpeed = -3.5F;
    private static final float cruciformStaffAttackDamage = 5;
    private static final float cruciformStaffAttackSpeed = -2.8F;
    private static Weapon.Entry Orb(String requiredMod, String name, Weapon.CustomMaterial material) {
        var settings = new Item.Settings();
        var item = new StaffItem(material, settings);
        return entry(requiredMod, name, material, item, new ItemConfig.Weapon(orbAttackDamage, orbAttackSpeed));
    }
    public static final Weapon.Entry lesserAbyssalOrb = Orb(
            "lesser_abyssal_orb",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON,()-> Ingredient.ofItems(Items.STICK)))
            .attribute(ItemConfig.Attribute.bonus(SpellSchools.ARCANE.id, 10));
    public static final Weapon.Entry medianAbyssalOrb = Orb(
            "median_abyssal_orb",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND,()-> Ingredient.ofItems(Items.STICK)))
            .attribute(ItemConfig.Attribute.bonus(SpellSchools.ARCANE.id, 20));
    public static final Weapon.Entry greaterAbyssalOrb = Orb(
            "greater_abyssal_orb",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE,()-> Ingredient.ofItems(Items.STICK)))
            .attribute(ItemConfig.Attribute.bonus(SpellSchools.ARCANE.id, 30));
    private static Weapon.Entry Orb(String name, Weapon.CustomMaterial material) {
        return Orb(null, name, material);
    }
    private static Weapon.Entry Dagger(String requiredMod, String name, Weapon.CustomMaterial material) {
        var settings = new Item.Settings();
        var item = new StaffItem(material, settings);
        return entry(requiredMod, name, material, item, new ItemConfig.Weapon(daggerAttackDamage, daggerAttackSpeed));
    }
    public static final Weapon.Entry lesserAbyssalDagger = Dagger(
            "lesser_abyssal_dagger",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON,()-> Ingredient.ofItems(Items.STICK)))
            .attribute(ItemConfig.Attribute.bonus(SpellSchools.ARCANE.id, 10));
    public static final Weapon.Entry medianAbyssalDagger = Dagger(
            "median_abyssal_dagger",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND,()-> Ingredient.ofItems(Items.STICK)))
            .attribute(ItemConfig.Attribute.bonus(SpellSchools.ARCANE.id, 20));
    public static final Weapon.Entry greaterAbyssalDagger = Dagger(
            "greater_abyssal_dagger",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE,()-> Ingredient.ofItems(Items.STICK)))
            .attribute(ItemConfig.Attribute.bonus(SpellSchools.ARCANE.id, 30));
    private static Weapon.Entry Dagger(String name, Weapon.CustomMaterial material) {
        return Dagger(null, name, material);
    }
    private static Weapon.Entry Greatsword(String requiredMod, String name, Weapon.CustomMaterial material) {
        var settings = new Item.Settings();
        var item = new StaffItem(material, settings);
        return entry(requiredMod, name, material, item, new ItemConfig.Weapon(greatswordAttackDamage, greatswordAttackSpeed));
    }
    public static final Weapon.Entry lesserAbyssalGreatsword = Greatsword(
            "lesser_abyssal_graetsword",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON,()-> Ingredient.ofItems(Items.STICK)))
            .attribute(ItemConfig.Attribute.bonus(SpellSchools.ARCANE.id, 10));
    public static final Weapon.Entry medianAbyssalGreatsword = Greatsword(
            "median_abyssal_graetsword",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND,()-> Ingredient.ofItems(Items.STICK)))
            .attribute(ItemConfig.Attribute.bonus(SpellSchools.ARCANE.id, 20));
    public static final Weapon.Entry greaterAbyssalGreatsword = Greatsword(
            "greater_abyssal_graetsword",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE,()-> Ingredient.ofItems(Items.STICK)))
            .attribute(ItemConfig.Attribute.bonus(SpellSchools.ARCANE.id, 30));
    private static Weapon.Entry Greatsword(String name, Weapon.CustomMaterial material) {
        return Greatsword(null, name, material);
    }
    private static Weapon.Entry CruciformStaff(String requiredMod, String name, Weapon.CustomMaterial material) {
        var settings = new Item.Settings();
        var item = new StaffItem(material, settings);
        return entry(requiredMod, name, material, item, new ItemConfig.Weapon(cruciformStaffAttackDamage, cruciformStaffAttackSpeed));
    }
    public static final Weapon.Entry lesserAbyssalCruciformStaff = CruciformStaff(
            "lesser_abyssal_cruciform_staff",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON,()-> Ingredient.ofItems(Items.STICK)))
            .attribute(ItemConfig.Attribute.bonus(SpellSchools.ARCANE.id, 10));
    public static final Weapon.Entry medianAbyssalCruciformStaff = CruciformStaff(
            "median_abyssal_cruciform_staff",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND,()-> Ingredient.ofItems(Items.STICK)))
            .attribute(ItemConfig.Attribute.bonus(SpellSchools.ARCANE.id, 20));
    public static final Weapon.Entry greaterAbyssalCruciformStaff = CruciformStaff(
            "greater_abyssal_cruciform_staff",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE,()-> Ingredient.ofItems(Items.STICK)))
            .attribute(ItemConfig.Attribute.bonus(SpellSchools.ARCANE.id, 30));
    private static Weapon.Entry CruciformStaff(String name, Weapon.CustomMaterial material) {
        return CruciformStaff(null, name, material);
    }

    public static void register(Map<String, ItemConfig.Weapon> configs) {
        // 注册所有武器条目
        Weapon.register(configs, entries, Group.WEAPON);
    }
}
