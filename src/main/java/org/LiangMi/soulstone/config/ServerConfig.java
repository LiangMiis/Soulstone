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
            this.put("soulstone:mage/elemental_galaxy", 200);
            this.put("soulstone:con_artist/shadow_strike", 40);
            this.put("soulstone:con_artist/shadow_empowerment", 80);
            this.put("soulstone:con_artist/shadow_blessing", 120);
            this.put("soulstone:con_artist/shadow_stun", 200);
            this.put("soulstone:mage/elemental_missile", 40);
            this.put("soulstone:mage/elemental_amplification", 80);
            this.put("soulstone:mage/elemental_beam", 120);
            this.put("soulstone:pastor/holy_surge", 40);
            this.put("soulstone:pastor/echo_of_fate", 80);
            this.put("soulstone:pastor/sacred_radiance", 120);
            this.put("soulstone:pastor/temporal_shell", 200);
            this.put("soulstone:warrior/bulwark", 40);
            this.put("soulstone:warrior/rallying_cry", 80);
            this.put("soulstone:warrior/unyielding_counter", 120);
            this.put("soulstone:warrior/mystic_monolith", 200);


        }
    };

}
