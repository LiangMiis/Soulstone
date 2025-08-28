package org.LiangMi.soulstone.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;


@Environment(EnvType.CLIENT)
public class QuestScreen extends Screen {
    public QuestScreen() {
        super(Text.literal("教程"));
    }
    public ButtonWidget button1;
    public ButtonWidget button2;
    public static final Identifier QUEST_SCREEN = new Identifier("textures/gui/questscreen.png");

    @Override
    protected void init() {
        button();

    }
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int i = (this.width - 192) / 2;
        super.render(context, mouseX, mouseY, delta);
        context.drawTexture(QUEST_SCREEN, i, 2, 0, 0, 128, 256);
    }

    public void button(){
        button1 = ButtonWidget.builder(Text.literal("按钮 1"), button -> {
                    System.out.println("你点击了按钮 1！");
                })
                .dimensions(width / 2 - 205, 20, 200, 20)
                .tooltip(Tooltip.of(Text.literal("按钮 1 的提示")))
                .build();
        button2 = ButtonWidget.builder(Text.literal("按钮 2"), button -> {
                    System.out.println("你点击了按钮 2！");
                })
                .dimensions(width / 2 + 5, 20, 200, 20)
                .tooltip(Tooltip.of(Text.literal("按钮 2 的提示")))
                .build();
        addDrawableChild(button1);
        addDrawableChild(button2);

    }
}
