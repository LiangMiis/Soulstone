package org.LiangMi.soulstone.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.LiangMi.soulstone.Soulstone;

public class Group {
    public static Identifier ID = new Identifier(Soulstone.ID, "generic");
    public static RegistryKey<ItemGroup> KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), ID);
    public static ItemGroup SOULSTONE;
    public static void registerGroup(){
        Group.SOULSTONE = FabricItemGroup.builder()
                .icon(() -> new ItemStack(ModItems.ICON))
                .displayName(Text.translatable("itemGroup.soulstone.general"))
                .build();
    }
}
