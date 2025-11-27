package org.LiangMi.soulstone.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.item.Group;

public class SoulBlocks {

    public static final Block SoulTable = register("soul_table",new SoulTableBlock(AbstractBlock.Settings.create().strength(4.0f)));

    private static <T extends Block> T register(String path, T block) {
        Registry.register(Registries.BLOCK, Identifier.of(Soulstone.ID, path), block);
        Registry.register(Registries.ITEM, Identifier.of(Soulstone.ID, path), new BlockItem(block, new Item.Settings()));
        return block;
    }

    public static void addItemsToItemGroup(){
        addToItemGroup(Group.MAIN,SoulTable);
    }
    private static void addToItemGroup(RegistryKey<ItemGroup> groupKey, Block block) {
        ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.add(block));
    }

    public static void initialize() {
        addItemsToItemGroup();
    }
}
