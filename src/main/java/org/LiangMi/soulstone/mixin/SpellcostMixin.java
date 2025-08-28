package org.LiangMi.soulstone.mixin;

import net.spell_engine.api.spell.Spell;
import org.LiangMi.soulstone.api.SpellcostMixinInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Spell.Cost.class)
public class SpellcostMixin implements SpellcostMixinInterface {
    @Unique
    float Mana = -1;

    public void setManaCost(float cost) {
        Mana = cost;
    }

    @Override
    public float getManaCost() {
        return Mana == -1 ? 0 : Mana;
    }

    @Override
    public boolean calculateManaCost() {
        return Mana == -1;
    }
}
