package org.LiangMi.soulstone.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.util.Identifier;
import net.spell_engine.api.item.trinket.SpellBooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;



@Mixin(SpellBooks.class)
public class SpellBooksMixin {
    @ModifyReturnValue(method = "itemIdFor",at = @At("RETURN"))
    private static Identifier modifyItemId(Identifier original,Identifier poolId){
        return new Identifier(poolId.getNamespace(),poolId.getPath()+"_soul_stone");
    }

}
