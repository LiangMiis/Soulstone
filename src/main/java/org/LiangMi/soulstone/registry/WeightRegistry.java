package org.LiangMi.soulstone.registry;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


import static org.LiangMi.soulstone.Soulstone.ID;

public class WeightRegistry {
    public static final ClampedEntityAttribute WEIGHT = new ClampedEntityAttribute("attrubute.name.soulstone.weight",0,0,1000);
    static {
        Registry.register(Registries.ATTRIBUTE,new Identifier(ID,"weight"),WEIGHT);
    }
}
