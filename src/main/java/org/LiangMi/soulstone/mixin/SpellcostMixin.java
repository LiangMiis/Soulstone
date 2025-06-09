package org.LiangMi.soulstone.mixin;

import net.spell_engine.api.spell.Spell;
import org.LiangMi.soulstone.api.SpellcostMixinInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Spell.Cost.class)
public class SpellcostMixin implements SpellcostMixinInterface {
    @Unique
    float rpgmana = -1;

    public void setManaCost(float cost) {
        rpgmana = cost;
    }

    @Override
    public float getManaCost() {
        return rpgmana == -1 ? 0 : rpgmana;
    }

    @Override
    public boolean calculateManaCost() {
        return rpgmana == -1;
    }
}
