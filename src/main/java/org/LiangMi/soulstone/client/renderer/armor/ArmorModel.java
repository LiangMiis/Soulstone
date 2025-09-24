package org.LiangMi.soulstone.client.renderer.armor;

import net.minecraft.util.Identifier;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.item.armor.SoulArmor;
import software.bernie.geckolib.model.GeoModel;

public class ArmorModel extends GeoModel<SoulArmor> {
    @Override
    public Identifier getModelResource(SoulArmor soulArmor) {
        return new Identifier(Soulstone.ID,"geo/armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(SoulArmor soulArmor) {
        var Texture = soulArmor.customMaterial.name();
        return new Identifier(Soulstone.ID,"textures/armor/"+ Texture + ".png");
    }

    @Override
    public Identifier getAnimationResource(SoulArmor soulArmor) {
        return null;
    }
}
