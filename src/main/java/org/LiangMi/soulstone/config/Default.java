package org.LiangMi.soulstone.config;

import net.spell_engine.api.item.ItemConfig;
import org.LiangMi.soulstone.item.Weapons;

public class Default {
    public static final ItemConfig itemConfig;
    static {
        itemConfig = new ItemConfig();
        for(var weapon: Weapons.entries){
            itemConfig.weapons.put(weapon.name(),weapon.defaults());
        }
    }
}
