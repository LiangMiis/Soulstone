package org.LiangMi.soulstone.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

// 自定义箱子屏幕类，继承自 Minecraft 的 HandledScreen
public class BoxScreen extends HandledScreen<ScreenHandler> {
    // GUI 纹理的路径，本例中使用发射器中的纹理
    private static final Identifier TEXTURE = new Identifier("minecraft", "textures/gui/container/dispenser.png");

    // 构造函数，接收屏幕处理器、玩家库存和标题
    public BoxScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        // 调用父类构造函数
        super(handler, inventory, title);
    }

    // 绘制背景方法，在每一帧被调用
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // 设置着色器为位置纹理程序
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        // 设置着色器颜色为白色（不透明）
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        // 设置着色器纹理为 GUI 纹理
        RenderSystem.setShaderTexture(0, TEXTURE);
        // 计算 GUI 在屏幕上的 x 坐标（居中）
        int x = (width - backgroundWidth) / 2;
        // 计算 GUI 在屏幕上的 y 坐标（居中）
        int y = (height - backgroundHeight) / 2;
        // 绘制纹理到屏幕上
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    // 渲染方法，在每一帧被调用
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 渲染背景
        renderBackground(context);
        // 调用父类的渲染方法
        super.render(context, mouseX, mouseY, delta);
        // 绘制鼠标悬停提示
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    // 初始化方法，在屏幕创建时调用
    @Override
    protected void init() {
        // 调用父类的初始化方法
        super.init();
        // 将标题居中
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }
}
