package org.LiangMi.soulstone.item.armor;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.spell_engine.api.item.ItemConfig;
import net.spell_engine.api.item.armor.Armor;
import net.spell_power.api.SpellSchools;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.item.Group;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Armors {
    private static final Supplier<Ingredient> WOOL_INGREDIENTS = () -> { return Ingredient.ofItems(
            Items.GOLD_INGOT);
    };
    public static Armor.CustomMaterial matreil = new Armor.CustomMaterial(
            "somsd",
            10,
            9,
            SoulArmor.equipSound,
            WOOL_INGREDIENTS
    );
    public static final ArrayList<Armor.Entry> entries = new ArrayList<>();

    // 创建盔甲条目的辅助方法
    private static Armor.Entry create(Armor.CustomMaterial material, ItemConfig.ArmorSet defaults) {
        return new Armor.Entry(material, null, defaults);
    }
    public static float robeSpellPower = 0.20F;
    public static final Armor.Set somsd =
            create(
                    matreil,
                    ItemConfig.ArmorSet.with(
                            new ItemConfig.ArmorSet.Piece(10)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellSchools.ARCANE.id, robeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellSchools.FIRE.id, robeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellSchools.HEALING.id, robeSpellPower)
                                    )),
                            new ItemConfig.ArmorSet.Piece(14)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellSchools.ARCANE.id, robeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellSchools.FIRE.id, robeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellSchools.HEALING.id, robeSpellPower)
                                    )),
                            new ItemConfig.ArmorSet.Piece(12)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellSchools.ARCANE.id, robeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellSchools.FIRE.id, robeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellSchools.HEALING.id, robeSpellPower)
                                    )),
                            new ItemConfig.ArmorSet.Piece(10)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellSchools.ARCANE.id, robeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellSchools.FIRE.id, robeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellSchools.HEALING.id, robeSpellPower)
                                    ))
                    )
            ).bundle(matreil -> new Armor.Set(Soulstone.ID,
                    new SoulArmor(matreil, ArmorItem.Type.HELMET,new Item.Settings()),
                    new SoulArmor(matreil, ArmorItem.Type.CHESTPLATE,new Item.Settings()),
                    new SoulArmor(matreil, ArmorItem.Type.LEGGINGS,new Item.Settings()),
                    new SoulArmor(matreil, ArmorItem.Type.BOOTS,new Item.Settings())
                    ))
                    .put(entries)
                    .armorSet();
    public static void register(Map<String, ItemConfig.ArmorSet> configs) {
        Armor.register(configs, entries, Group.ARMOR);
    }
}
