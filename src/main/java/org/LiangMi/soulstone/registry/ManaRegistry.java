package org.LiangMi.soulstone.registry;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static org.LiangMi.soulstone.Soulstone.ID;

public class ManaRegistry {
    public static final ClampedEntityAttribute MANA = new ClampedEntityAttribute("attribute.name.soulstone.mana", 0,0,999999);
    public static final ClampedEntityAttribute MANAREGEN = new ClampedEntityAttribute("attribute.name.soulstone.manaregen", 4,-999999,999999);
    public static final ClampedEntityAttribute MANACOST = new ClampedEntityAttribute("attribute.name.soulstone.manacost", 100,0,999999);
    static {
        Registry.register(Registries.ATTRIBUTE,new Identifier(ID,"mana"),MANA);
        Registry.register(Registries.ATTRIBUTE,new Identifier(ID,"manaregen"),MANAREGEN);
        Registry.register(Registries.ATTRIBUTE,new Identifier(ID,"manacost"),MANACOST);
    }
}
