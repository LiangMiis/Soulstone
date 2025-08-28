package org.LiangMi.soulstone.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.spell_engine.spellbinding.SpellBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpellBinding.class)
public class SpellBindingMixin {
    @Redirect(
            method = "offersFor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"
            )
    )
    private static Item redirectGetItem(ItemStack stack) {
        Item item = stack.getItem();

        // 扩展条件：允许书、成书和书与笔
        if (item == Items.BOOK || item == Items.WRITTEN_BOOK || item == Items.WRITABLE_BOOK) {
            return Items.BOOK; // 返回 BOOK 使原条件成立
        }

        return item; // 其他物品保持原样
    }
}
