package org.LiangMi.soulstone.client.renderer;

import org.LiangMi.soulstone.client.model.MagicBallModel;
import org.LiangMi.soulstone.item.weapons.MagicaBall;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

public class MagicBallRenderer extends GeoItemRenderer<MagicaBall> {
    public MagicBallRenderer() {super(new MagicBallModel());}
}
