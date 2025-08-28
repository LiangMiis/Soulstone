package org.LiangMi.soulstone.mixin;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellRegistry;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.api.SpellcostMixinInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpellRegistry.class)
public class SpellRegistryMixin {
    @Inject(at = @At("TAIL"),method = "loadSpells")
    private static void manaCosts(ResourceManager resourceManager ,CallbackInfo callbackInfo){
        Soulstone.config.spells.entrySet().iterator().forEachRemaining(entry ->{
            Spell spell = SpellRegistry.getSpell(new Identifier(entry.getKey()));
            System.out.println(spell);
            if(spell != null){
                ((SpellcostMixinInterface) spell.cost).setManaCost(entry.getValue());
            }
        });
    }
}
