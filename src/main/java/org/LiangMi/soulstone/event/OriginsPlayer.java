package org.LiangMi.soulstone.event;


import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginRegistry;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Map;

public class OriginsPlayer {

    private static void hasOrigins(){
        for (Map.Entry<Identifier, Origin> entry : OriginRegistry.entries()) {
            Identifier id = entry.getKey();
            Origin origin = entry.getValue();

            // 在这里处理每个条目
            System.out.println("ID: " + id + " | Origin: " + origin);

            // 如果需要根据条件停止遍历
            if (id.toString().equals("origin:human")) {
                System.out.println("Found special ID, stopping early");
                break; // 提前终止遍历
            }
        }
    }
    public static OriginComponent getOriginComponent(PlayerEntity player) {
        return ModComponents.ORIGIN.get(player);
    }
    public static boolean hasOrigin(PlayerEntity player, Identifier targetOriginId) {
        OriginComponent component = getOriginComponent(player);
        if (component == null) return false;

        Map<OriginLayer, Origin> origins = component.getOrigins();

        for (Origin origin : origins.values()) {
            if (origin != null && origin.getIdentifier().equals(targetOriginId)) {
                return true;
            }
        }
        return false;
    }
}
