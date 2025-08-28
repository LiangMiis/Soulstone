package org.LiangMi.soulstone.registry;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import static org.LiangMi.soulstone.Soulstone.ID;

public class AnchorRegistry {
    public static final ClampedEntityAttribute ANCHOR = new ClampedEntityAttribute("attribute.name.soulstone.anchor",0,0,999999);


    static {
        Registry.register(Registries.ATTRIBUTE,new Identifier(ID,"anchor"),ANCHOR);
    }
}
