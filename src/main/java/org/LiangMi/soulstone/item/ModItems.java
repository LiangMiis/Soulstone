package org.LiangMi.soulstone.item;





import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;



public class ModItems {
    public static final Item ICON = ItemRegister("icon",new Item(new Item.Settings()));
    public static final Item EMPTY_SOUL_STONE = ItemRegister("empty_soul_stone",new Item(new Item.Settings()));

    public static final Item RadiantAbyssPass = ItemRegister("radiant_abyss_pass",new Item(new Item.Settings()));
    public static final Item EternalAbyssPass = ItemRegister("eternal_abyss_pass",new Item(new Item.Settings()));
    public static final Item ChaoticAbyssPass = ItemRegister("chaotic_abyss_pass",new Item(new Item.Settings()));
    public static Item ItemRegister(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier("soulstone", name), item);
    }
    public static void addItemsToItemGroup(){
        addToItemGroup(Group.KEY,EMPTY_SOUL_STONE);
        addToItemGroup(Group.KEY,RadiantAbyssPass);
        addToItemGroup(Group.KEY,EternalAbyssPass);
        addToItemGroup(Group.KEY,ChaoticAbyssPass);
    }
    private static void addToItemGroup(RegistryKey<ItemGroup> groupKey, Item item) {
        ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.add(item));
    }

    public static void initialize() {
        addItemsToItemGroup();
    }

}
