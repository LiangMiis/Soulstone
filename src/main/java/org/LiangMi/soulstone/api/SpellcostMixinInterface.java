package org.LiangMi.soulstone.api;

public interface SpellcostMixinInterface {
    void setManaCost(float cost);

    float getManaCost();
    boolean calculateManaCost();
}
