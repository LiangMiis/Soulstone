package org.LiangMi.soulstone.util;

import net.minecraft.entity.attribute.ClampedEntityAttribute;

public class ManaValue {
    public static final ClampedEntityAttribute MANA = new ClampedEntityAttribute("attribute.name.rpgmana.mana", 0,0,999999);
    public static final ClampedEntityAttribute MANAREGEN = new ClampedEntityAttribute("attribute.name.rpgmana.manaregen", 4,-999999,999999);
    public static final ClampedEntityAttribute MANACOST = new ClampedEntityAttribute("attribute.name.rpgmana.manacost", 100,0,999999);
}
