package org.LiangMi.soulstone.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.LiangMi.soulstone.Soulstone;

// 物品组（创造模式物品栏分组）管理类
public class Group {
    // 定义主物品组的标识符
    public static Identifier ID = new Identifier(Soulstone.ID, "generic");
    // 定义饰品物品组的标识符
    public static Identifier ID2 = new Identifier(Soulstone.ID, "trinket");
    public static Identifier ID3 = new Identifier(Soulstone.ID, "weapon");
    public static Identifier ID4 = new Identifier(Soulstone.ID, "armor");
    // 注册表键，用于主物品组
    public static RegistryKey<ItemGroup> MAIN = RegistryKey.of(Registries.ITEM_GROUP.getKey(), ID);
    // 注册表键，用于饰品物品组
    public static RegistryKey<ItemGroup> TRINKET = RegistryKey.of(Registries.ITEM_GROUP.getKey(), ID2);
    public static RegistryKey<ItemGroup> WEAPON = RegistryKey.of(Registries.ITEM_GROUP.getKey(), ID3);
    public static RegistryKey<ItemGroup> ARMOR = RegistryKey.of(Registries.ITEM_GROUP.getKey(), ID4);
    // 主物品组实例
    public static ItemGroup SOULSTONE;
    // 饰品物品组实例
    public static ItemGroup Trinket;
    public static ItemGroup Weapon;
    public static ItemGroup Armor;

    // 注册物品组的方法
    public static void registerGroup(){
        // 构建主物品组
        Group.SOULSTONE = FabricItemGroup.builder()
                .icon(() -> new ItemStack(ModItems.ICON)) // 设置物品组图标
                .displayName(Text.translatable("itemGroup.soulstone.general")) // 设置显示名称
                .build();
        // 构建饰品物品组
        Group.Trinket = FabricItemGroup.builder()
                .icon(()-> new ItemStack(ModItems.TreasureHuntersBlade)) // 设置物品组图标
                .displayName(Text.translatable("itemGroup.soulstone.trinket")) // 设置显示名称
                .build();
        Group.Weapon = FabricItemGroup.builder()
                .icon(()-> new ItemStack(Weapons.greaterAbyssalGreatsword.item())) // 设置物品组图标
                .displayName(Text.translatable("itemGroup.soulstone.weapon")) // 设置显示名称
                .build();
        Group.Armor = FabricItemGroup.builder()
                .icon(()-> new ItemStack(Weapons.greaterAbyssalGreatsword.item())) // 设置物品组图标
                .displayName(Text.translatable("itemGroup.soulstone.armor")) // 设置显示名称
                .build();
    }
}
