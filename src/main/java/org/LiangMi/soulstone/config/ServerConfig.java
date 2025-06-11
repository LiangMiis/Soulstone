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
            this.put("soulstone:stonelom", 40);
            this.put("soulstone:greathealing", 40);
        }
    };

}
