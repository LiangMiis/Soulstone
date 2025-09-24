package org.LiangMi.soulstone.registry;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;


public class ManaRegistry {
    public static final EntityAttribute MANA = register("max_mana",(new ClampedEntityAttribute("attribute.name.max_health",(double) 50.0f,(double) 1.0f,(double) 100000f)).setTracked(true));
    public static final EntityAttribute MANAREGEN = register("mana_regen",(new ClampedEntityAttribute("attribute.name.mana_regen",(double) 0.3f,(double) 0f,(double) 100000f)).setTracked(true));
    public static final EntityAttribute MANACOST = register("mana_cost",(new ClampedEntityAttribute("attribute.name.mana_cost",(double) 100.0f,(double) 1.0f,(double) 100000f)).setTracked(true));
    public ManaRegistry(){
    }
    private static EntityAttribute register(String id, EntityAttribute attribute) {
        return (EntityAttribute)Registry.register(Registries.ATTRIBUTE, id, attribute);
    }
}
