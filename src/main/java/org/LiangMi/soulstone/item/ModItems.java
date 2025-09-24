package org.LiangMi.soulstone.item;





import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.LiangMi.soulstone.item.trinkets.ChampionsArmlet;
import org.LiangMi.soulstone.item.trinkets.TreasureHuntersBlade;
import org.LiangMi.soulstone.item.trinkets.TrinketBass;

import java.util.ArrayList;
import java.util.List;


public class ModItems {
    public static List<TrinketBass> allTrinket = new ArrayList<>();
    public static final Item ICON = ItemRegister("icon",new Item(new Item.Settings()));
    public static final Item EMPTY_SOUL_STONE = ItemRegister("empty_soul_stone",new Item(new Item.Settings()));

    public static final Item RadiantAbyssPass = ItemRegister("radiant_abyss_pass",new Item(new Item.Settings()));
    public static final Item EternalAbyssPass = ItemRegister("eternal_abyss_pass",new Item(new Item.Settings()));
    public static final Item ChaoticAbyssPass = ItemRegister("chaotic_abyss_pass",new Item(new Item.Settings()));

    public static final Item TreasureHuntersBlade = ItemRegister("treasure_hunter_s_blade",new TreasureHuntersBlade(new Item.Settings()));
    public static final Item ChampionsArmlet = ItemRegister("champions_armlet",new ChampionsArmlet(StatusEffects.DARKNESS,1,"tooltip.champions" ));
    public static Item ItemRegister(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier("soulstone", name), item);
    }
    public static void addItemsToItemGroup(){
        addToItemGroup(Group.MAIN,EMPTY_SOUL_STONE);
        addToItemGroup(Group.MAIN,RadiantAbyssPass);
        addToItemGroup(Group.MAIN,EternalAbyssPass);
        addToItemGroup(Group.MAIN,ChaoticAbyssPass);
        addToItemGroup(Group.TRINKET,TreasureHuntersBlade);
        addToItemGroup(Group.TRINKET,ChampionsArmlet);
    }
    private static void addToItemGroup(RegistryKey<ItemGroup> groupKey, Item item) {
        ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.add(item));
    }

    public static void initialize() {
        addItemsToItemGroup();
    }

}
