package org.LiangMi.soulstone.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.LinkedHashMap;

@Config(name = "server")
public class ServerConfig implements ConfigData {

    public ServerConfig(){}
    @Comment("Maximum Mana Modifier (Default: 1)")
    public  float mana = 1;
    @Comment("Additional Base Mana (Default: 0)")
    public  float basemana = 0;

    @Comment("Mana Regen Modifier (Default: 1)")
    public  float manaregen = 1;
    @Comment("Inspiration Bonus (Default: 10%)")
    public  float inspiration = 10;
    @Comment("Lucidity Bonus (Default: 5%)")
    public  float lucidity = 5;
    @Comment("Manafused Bonus (Default: 10)")
    public  float manafuse =  10;
    @Comment("ManaStabilized Bonus (Default: 5%)")
    public  float manastabilized = 5;
    @Comment("Resplendent Bonus (Default: 10%)")
    public  float resplendent = 10;
    @Comment("Do not apply Mana Costs (and therefore Mana Compatibility) to spells matching this regex.")
    public String blacklist_spell_casting_regex = "";

    @Comment("Apply mana costs to select spells (Format: 'spellid': 40)")
    public LinkedHashMap<String, Integer> spells = new LinkedHashMap<String, Integer>() {
        {
            this.put("examplemod:examplespell", 40);
            this.put("wizards:arcane_beam", 60);
            this.put("wizards:arcane_blast", 40);
            this.put("wizards:arcane_blink", 60);
            this.put("wizards:arcane_bolt", 20);
            this.put("wizards:arcane_missile", 30);
            this.put("wizards:fire_breath", 40);
            this.put("wizards:fire_meteor", 60);
            this.put("wizards:fire_scorch", 20);
            this.put("wizards:fire_wall", 80);
            this.put("wizards:fireball", 30);
            this.put("wizards:frost_blizzard", 60);
            this.put("wizards:frost_nova", 30);
            this.put("wizards:frost_shard", 20);
            this.put("wizards:frost_shield", 70);
            this.put("wizards:frostbolt", 30);
            this.put("paladins:barrier", 80);
            this.put("paladins:battle_banner", 80);
            this.put("paladins:circle_of_healing", 70);
            this.put("paladins:divine_protection", 60);
            this.put("paladins:flash_heal", 30);
            this.put("paladins:heal", 20);
            this.put("paladins:holy_beam", 30);
            this.put("paladins:holy_shock", 30);
            this.put("paladins:judgement", 60);
            this.put("druids:galvanic_rite", 30);
            this.put("druids:lightningblast", 30);
            this.put("druids:maelstromtotem", 50);
            this.put("druids:soulfirebastion", 40);
            this.put("druids:soulflare", 40);
            this.put("druids:spiritbolts", 30);
            this.put("druids:stormcall", 80);
            this.put("druids:voltaic_burst", 20);
        }
    };

}
