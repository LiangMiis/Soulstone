package org.LiangMi.soulstone.client.input;


import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.client.gui.QuestScreen;
import org.lwjgl.glfw.GLFW;



public class Keybindings {

    public static KeyBinding OPEN_QUESTSCREEN_KEY;
    public static KeyBinding OPEN_ANCHOR_KEY;
    public static void initBindings() {
        OPEN_QUESTSCREEN_KEY = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "keybindings."+Soulstone.ID+".questscreen",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_G,
                        Soulstone.ModName()));
        OPEN_ANCHOR_KEY = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "keybindings." + Soulstone.ID + ".anchor",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_X,
                        Soulstone.ModName()));
    }

    public static void register(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (OPEN_QUESTSCREEN_KEY.wasPressed()&&client.player != null) {
                client.setScreen(new QuestScreen());
            }
        });
    }
}
