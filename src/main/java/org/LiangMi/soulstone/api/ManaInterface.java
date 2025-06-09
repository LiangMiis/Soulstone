package org.LiangMi.soulstone.api;

import net.spell_engine.api.spell.Spell;

import java.util.List;

public interface ManaInterface {
    double getMana();
    double getMaxMana();
    double getManaRegen();
    double spendMana(double toadd);
    int getTimeFull();
    List<ManaInstance> getManaInstances();
    Spell getLastSpell();
    void setLastSpell(Spell spell);
}
