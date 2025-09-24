package org.LiangMi.soulstone.client.renderer.armor;

import org.LiangMi.soulstone.item.armor.SoulArmor;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class ArmorRenderer extends GeoArmorRenderer<SoulArmor> {
    public ArmorRenderer() {
        super(new ArmorModel());
    }
}
