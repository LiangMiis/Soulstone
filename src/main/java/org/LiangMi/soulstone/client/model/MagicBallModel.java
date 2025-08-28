package org.LiangMi.soulstone.client.model;

import net.minecraft.util.Identifier;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.item.weapons.MagicaBall;
import software.bernie.geckolib.model.GeoModel;

public class MagicBallModel extends GeoModel<MagicaBall> {
    @Override
    public Identifier getModelResource(MagicaBall magicaBall) {
        return new Identifier(Soulstone.ID,"geo/magicaball.geo.json");
    }

    @Override
    public Identifier getTextureResource(MagicaBall magicaBall) {
        return new Identifier(Soulstone.ID,"textures/item/magicaball.png");
    }

    @Override
    public Identifier getAnimationResource(MagicaBall magicaBall) { return null; }
}
