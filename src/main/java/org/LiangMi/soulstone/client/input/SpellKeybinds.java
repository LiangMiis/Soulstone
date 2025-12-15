package org.LiangMi.soulstone.client.input;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.impl.util.log.Log;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.LiangMi.soulstone.client.screen.PointScreen;
import org.LiangMi.soulstone.client.screen.SpellGuiScreen;
import org.LiangMi.soulstone.system.SpellSystem;
import org.lwjgl.glfw.GLFW;

import static org.LiangMi.soulstone.Soulstone.LOGGER;

public class SpellKeybinds {
    public static int spellCooldown1 = 500;
    public static int spellCooldown2 = 500;
    public static int spellCooldown3 = 500;
    public static int spellCooldown4 = 500;
    private static KeyBinding spellKey1;
    private static KeyBinding spellKey2;
    private static KeyBinding spellKey3;
    private static KeyBinding spellKey4;
    private static KeyBinding gui;
    public static long lastUseTime1;
    public static long lastUseTime2;
    public static long lastUseTime3;
    public static long lastUseTime4;


    public static void register() {
        // 注册按键绑定
        spellKey1 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.soulstone.spell.key1",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                "key.soulstone.spell.keys"
        ));
        spellKey2 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.soulstone.spell.key2",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                "key.soulstone.spell.keys"
        ));
        spellKey3 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.soulstone.spell.key3",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "key.soulstone.spell.keys"
        ));
        spellKey4 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.soulstone.spell.key4",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "key.soulstone.spell.keys"
        ));
        gui = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.soulstone.spell.gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F8,
                "key.soulstone.spell.keys"
        ));

        // 注册按键事件
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (spellKey1.wasPressed()) {
                if (client.player != null) {
                    if (System.currentTimeMillis()>(lastUseTime1+spellCooldown1)){
                        SpellSystem.sendKeybindPacket("SpellKey1");
                        lastUseTime1 = System.currentTimeMillis();

                    }else{
                        client.player.sendMessage(Text.literal("Spell1 can be used again in " +
                                (((lastUseTime1 + spellCooldown1) - System.currentTimeMillis()) / 1000) + "s"), true);
                    }
                }
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (spellKey2.wasPressed()) {
                if (client.player != null) {
                    if (System.currentTimeMillis()>(lastUseTime2+spellCooldown2)){
                        SpellSystem.sendKeybindPacket("SpellKey2");
                        lastUseTime2 = System.currentTimeMillis();
                    }else{
                        client.player.sendMessage(Text.literal("Spell2 can be used again in " +
                                (((lastUseTime1 + spellCooldown1) - System.currentTimeMillis()) / 1000) + "s"), true);
                    }
                }
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (spellKey3.wasPressed()) {
                if (client.player != null) {
                    if (System.currentTimeMillis()>(lastUseTime3+spellCooldown3)){
                        SpellSystem.sendKeybindPacket("SpellKey3");
                        lastUseTime3 = System.currentTimeMillis();
                    }else{
                        client.player.sendMessage(Text.literal("Spell3 can be used again in " +
                                (((lastUseTime1 + spellCooldown1) - System.currentTimeMillis()) / 1000) + "s"), true);
                    }
                }
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (spellKey4.wasPressed()) {
                if (client.player != null) {
                    if (System.currentTimeMillis()>(lastUseTime4+spellCooldown4)){
                        SpellSystem.sendKeybindPacket("SpellKey4");
                        lastUseTime4 = System.currentTimeMillis();
                    }else{
                        client.player.sendMessage(Text.literal("Spell4 can be used again in " +
                                (((lastUseTime1 + spellCooldown1) - System.currentTimeMillis()) / 1000) + "s"), true);
                    }
                }
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (gui.wasPressed()) {
                if (client.player != null) {
                    client.setScreen(new SpellGuiScreen());
                }
            }
        });
    }
}
