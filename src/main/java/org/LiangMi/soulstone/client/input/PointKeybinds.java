package org.LiangMi.soulstone.client.input;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.LiangMi.soulstone.client.screen.PointScreen;

public class PointKeybinds {
    private static KeyBinding openPointsScreen;

    public static void register() {
        // 注册按键绑定
        openPointsScreen = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.soulstone.open_points_screen",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                "category.soulstone.point_system"
        ));

        // 注册按键事件
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openPointsScreen.wasPressed()) {
                if (client.player != null) {
                    client.setScreen(new PointScreen(client.player));
                }
            }
        });
    }
}
