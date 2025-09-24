package org.LiangMi.soulstone.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.LinkedHashMap;

@Config(name = "server")
public class ServerConfig implements ConfigData {

    public ServerConfig(){}
    public double Stealth_lin = 1.0;
    public double stealth_visibility_multiplier = 0.1;
    public int effects_raw_id_start = 700;
    public boolean spell_book_creation_enabled = true;
    @Comment("在这里添加法术法力值")
    public LinkedHashMap<String, Integer> spells = new LinkedHashMap<String, Integer>() {
        {
            this.put("soulstone:bulwark", 40);
            this.put("soulstone:greathealing", 20);
        }
    };

}
