package org.LiangMi.soulstone.item.weapons.common;


import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class Scythe extends SwordItem {
    private final float attackDamage;
    public Scythe(ToolMaterial toolMaterial, int attackDamage, float attackSpeed , Settings settings){
        super(toolMaterial, attackDamage, attackSpeed, settings);
        this.attackDamage = (float) attackDamage + toolMaterial.getAttackDamage();
    }
    @Override
    public float getAttackDamage(){return this.attackDamage;}

}
