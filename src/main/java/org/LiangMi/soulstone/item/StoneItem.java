package org.LiangMi.soulstone.item;

import net.minecraft.util.Identifier;
import net.spell_engine.api.item.trinket.SpellBooks;
import org.LiangMi.soulstone.Soulstone;

import java.util.List;

public class StoneItem {
    public static void register(){
        var stones = List.of("pastor","warrior","mage","con_artist","ranger");
        for(var name:stones){
            SpellBooks.createAndRegister(new Identifier(Soulstone.ID,name),Group.KEY);
        }
    }
}
