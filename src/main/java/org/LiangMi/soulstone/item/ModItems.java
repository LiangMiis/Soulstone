package org.LiangMi.soulstone.item;





import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.LiangMi.soulstone.item.weapons.common.Scythe;



public class ModItems {
    public static final Item SCYTHE = ItemRegister("scythe",new Scythe(ToolMaterials.DIAMOND,4,-2.4F, new Item.Settings()));
    public static final Item MANA = ItemRegister("mana", new Item(new FabricItemSettings()));


    public static Item ItemRegister(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier("soulstone", name), item);
    }
    public static void addItemsToItemGroup(){
        addToItemGroup(Group.KEY,SCYTHE);
    }
    private static void addToItemGroup(RegistryKey<ItemGroup> groupKey, Item item) {
        ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.add(item));
    }
    public static void initialize() {
        addItemsToItemGroup();
    }


}
