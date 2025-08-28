package org.LiangMi.soulstone.client.hud;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.spell_engine.client.gui.HudMessages;

public class Messages {
    public boolean NO_MANA_ERROR;

    private HudMessages.ErrorMessageState currentError;

    public static final int DEFAULT_ERROR_MESSAGE_DURATION = 20;
    public static final int DEFAULT_ERROR_MESSAGE_FADEOUT = 10;
    public void render(Boolean NO_MANA_ERROR){
        MutableText text;
        if(NO_MANA_ERROR){
            text = Text.translatable("NO_MANA");
        }else {
            text = null;
        }
        if(text != null){
            error(text);
        }

    }
    public void error(Text text){
        error(text,DEFAULT_ERROR_MESSAGE_DURATION,DEFAULT_ERROR_MESSAGE_FADEOUT);
    }
    public void error(Text text, int duration, int fadeOut) {
        currentError = new HudMessages.ErrorMessageState(text, duration, fadeOut);
    }
    public HudMessages.ErrorMessageState currentError() {
        return currentError;
    }

}
