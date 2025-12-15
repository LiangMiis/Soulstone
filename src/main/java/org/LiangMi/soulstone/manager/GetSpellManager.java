package org.LiangMi.soulstone.manager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.LiangMi.soulstone.access.PointSystemAccess;
import org.LiangMi.soulstone.access.SpellAccess;
import org.LiangMi.soulstone.data.PlayerPointData;
import org.LiangMi.soulstone.data.PlayerSpellData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GetSpellManager {
    public static void upGetSpell(ServerPlayerEntity player){
        int lv = GameStageManager.getNextLevel(GameStageManager.getCurrentLevel(player));
        int lv2 = GameStageManager.getPreviousLevel(GameStageManager.getCurrentLevel(player));
        GameStageManager.setCurrentLevel(player,lv);
        getUnlockSpell(player,lv2);
    }
    public static void getUnlockSpell(ServerPlayerEntity player, int level) {
        PlayerPointData pointData = PointSystemAccess.getPlayerData(player);
        Map<String, Integer> pointMap = pointData.getAllAssignedPoints();

        for (Map.Entry<String, Integer> entry : pointMap.entrySet()) {
            Integer points=pointMap.get(entry.getKey());
            if (level == points) {
                String spell = unlockSpell2(entry.getKey(), level);
                if (!Objects.equals(spell, "none")) {
                    SpellAccess.addSpell(player,spell);
                }
            } else if (level / 2 <= points) {
                String spell = unlockSpell1(entry.getKey(), level);
                if (!Objects.equals(spell, "none")) {
                    SpellAccess.addSpell(player,spell);
                }
            }
        }
    }
    public static String unlockSpell2(String id,int lv){
        String Spell="none";
        if (lv==20){
            if(Objects.equals(id, "health")){

            }if(Objects.equals(id, "attack")){

            }if(Objects.equals(id, "defense")){

            }if(Objects.equals(id, "mana")){

            }if(Objects.equals(id, "arcane")){
                Spell = "arcane_bolt";
            }if(Objects.equals(id, "fire")){
                Spell = "fire_meteor";
            }if(Objects.equals(id, "frost")){

            }if(Objects.equals(id, "healing")){
                Spell = "sacred_orb";
            }if(Objects.equals(id, "lightning")){

            }if(Objects.equals(id, "soul")){
                Spell = "ghostwalk";
            }
        }if (lv==30){
            if(Objects.equals(id, "health")){

            }if(Objects.equals(id, "attack")){

            }if(Objects.equals(id, "defense")){

            }if(Objects.equals(id, "mana")){

            }if(Objects.equals(id, "arcane")){

            }if(Objects.equals(id, "fire")){

            }if(Objects.equals(id, "frost")){

            }if(Objects.equals(id, "healing")){

            }if(Objects.equals(id, "lightning")){

            }if(Objects.equals(id, "soul")){

            }
        }if (lv==40){
            if(Objects.equals(id, "health")){

            }if(Objects.equals(id, "attack")){

            }if(Objects.equals(id, "defense")){

            }if(Objects.equals(id, "mana")){

            }if(Objects.equals(id, "arcane")){

            }if(Objects.equals(id, "fire")){

            }if(Objects.equals(id, "frost")){

            }if(Objects.equals(id, "healing")){

            }if(Objects.equals(id, "lightning")){

            }if(Objects.equals(id, "soul")){

            }
        }if (lv==50){
            if(Objects.equals(id, "health")){

            }if(Objects.equals(id, "attack")){

            }if(Objects.equals(id, "defense")){

            }if(Objects.equals(id, "mana")){

            }if(Objects.equals(id, "arcane")){

            }if(Objects.equals(id, "fire")){

            }if(Objects.equals(id, "frost")){

            }if(Objects.equals(id, "healing")){

            }if(Objects.equals(id, "lightning")){

            }if(Objects.equals(id, "soul")){

            }
        }
        return Spell;
    }
    public static String unlockSpell1(String id,int lv){
        String Spell="none";
        if (lv==20){
            if(Objects.equals(id, "health")){

            }if(Objects.equals(id, "attack")){

            }if(Objects.equals(id, "defense")){

            }if(Objects.equals(id, "mana")){

            }if(Objects.equals(id, "arcane")){
                Spell = "arcane_bolt";
            }if(Objects.equals(id, "fire")){
                Spell = "fire_meteor";
            }if(Objects.equals(id, "frost")){

            }if(Objects.equals(id, "healing")){
                Spell = "sacred_orb";
            }if(Objects.equals(id, "lightning")){

            }if(Objects.equals(id, "soul")){
                Spell = "ghostwalk";
            }
        }if (lv==30){
            if(Objects.equals(id, "health")){

            }if(Objects.equals(id, "attack")){

            }if(Objects.equals(id, "defense")){

            }if(Objects.equals(id, "mana")){

            }if(Objects.equals(id, "arcane")){

            }if(Objects.equals(id, "fire")){

            }if(Objects.equals(id, "frost")){

            }if(Objects.equals(id, "healing")){

            }if(Objects.equals(id, "lightning")){

            }if(Objects.equals(id, "soul")){

            }
        }if (lv==40){
            if(Objects.equals(id, "health")){

            }if(Objects.equals(id, "attack")){

            }if(Objects.equals(id, "defense")){

            }if(Objects.equals(id, "mana")){

            }if(Objects.equals(id, "arcane")){

            }if(Objects.equals(id, "fire")){

            }if(Objects.equals(id, "frost")){

            }if(Objects.equals(id, "healing")){

            }if(Objects.equals(id, "lightning")){

            }if(Objects.equals(id, "soul")){

            }
        }if (lv==50){
            if(Objects.equals(id, "health")){

            }if(Objects.equals(id, "attack")){

            }if(Objects.equals(id, "defense")){

            }if(Objects.equals(id, "mana")){

            }if(Objects.equals(id, "arcane")){

            }if(Objects.equals(id, "fire")){

            }if(Objects.equals(id, "frost")){

            }if(Objects.equals(id, "healing")){

            }if(Objects.equals(id, "lightning")){

            }if(Objects.equals(id, "soul")){

            }
        }
        return Spell;
    }

}
