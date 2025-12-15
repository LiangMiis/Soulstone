package org.LiangMi.soulstone.system;

import net.minecraft.entity.player.PlayerEntity;
import org.LiangMi.soulstone.access.SpellAccess;
import org.LiangMi.soulstone.data.PlayerSpellData;

public class KeyBindsSystem {
    public static void keyBindsManager(PlayerEntity player,String type){
        switch (type){
            case "SpellKey1" ->{
                String spell= keyBindsSpell(player, "SpellKey1");
                SpellSystem.spellKeyManager(player,spell);
            }
            case "SpellKey2" ->{
                String spell= keyBindsSpell(player, "SpellKey2");
                SpellSystem.spellKeyManager(player,spell);
            }
            case "SpellKey3" ->{
                String spell= keyBindsSpell(player, "SpellKey3");
                SpellSystem.spellKeyManager(player,spell);
            }
            case "SpellKey4" ->{
                String spell= keyBindsSpell(player, "SpellKey4");
                SpellSystem.spellKeyManager(player,spell);
            }
        }
    }
    public static String keyBindsSpell(PlayerEntity player,String type){
        PlayerSpellData data = SpellAccess.getPlayerData(player);
        return data.getKeyBindsSpell(type);
    }

}
