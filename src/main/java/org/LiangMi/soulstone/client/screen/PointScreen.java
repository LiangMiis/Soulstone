package org.LiangMi.soulstone.client.screen;


import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.LiangMi.soulstone.network.packet.c2s.PointClientNetworking;

import java.util.HashMap;
import java.util.Map;

public class PointScreen extends Screen {
    private int getMaxLevel;
    private int availablePoints = 0;
    private Map<String, Integer> assignedPoints = new HashMap<>();

    // 按钮和文本的位置信息
    private Map<String, ButtonInfo> buttonInfos;
    private ButtonWidget resetButton;
    private ButtonWidget closeButton;

    // UI 尺寸 - 增加宽度保持居中
    private int backgroundWidth = 320; // 从256增加到320
    private int backgroundHeight = 200;
    private int left;
    private int top;

    // 滚动相关变量
    private int scrollY = 0;
    private int contentHeight = 0;
    private int visibleHeight = 140; // 可见区域高度
    private boolean isDraggingScrollbar = false;
    private int scrollbarWidth = 6;
    private int scrollbarLeft;

    // 属性配置数组
    private static final String[] ATTRIBUTES = {
            "health", "attack", "defense", "speed","mana",
            "arcane","fire","frost","healing","lightning","soul",
            "critical_chance","critical_damage","haste",
    };

    private static final String[] ATTRIBUTE_NAMES = {
            "生命值", "攻击力", "防御力", "移动速度","以太",
            "奥秘","火焰","寒冰","治愈","雷电","灵魂",
            "法术暴击概率","法术暴击伤害","法术施法速度"
    };

    // 按钮信息类
    private static class ButtonInfo {
        int yPos;
        boolean visible;
        ButtonInfo(int yPos) {
            this.yPos = yPos;
            this.visible = true;
        }
    }

    public PointScreen(PlayerEntity player) {
        super(Text.literal("加点系统"));
        this.buttonInfos = new HashMap<>();

        // 初始化默认数据
        initializeDefaultAttributes();

        // 计算内容总高度
        this.contentHeight = ATTRIBUTES.length * 25 + 10;

        // 请求服务器发送最新数据
        PointClientNetworking.sendOpenScreenRequest();
    }

    private void initializeDefaultAttributes() {
        for (String attr : ATTRIBUTES) {
            assignedPoints.put(attr, 0);
        }
    }

    // 添加这个方法用于从网络包更新数据
    public void updateFromNetwork(int availablePoints, Map<String, Integer> assignedPoints,int gameLv) {
        this.availablePoints = availablePoints;
        this.assignedPoints = new HashMap<>(assignedPoints);
        this.getMaxLevel = gameLv;
    }

    @Override
    protected void init() {
        super.init();

        this.left = (this.width - this.backgroundWidth) / 2;
        this.top = (this.height - this.backgroundHeight) / 2;
        this.scrollbarLeft = left + backgroundWidth - scrollbarWidth - 10;

        createWidgets();
    }

    private void createWidgets() {
        // 清除现有组件
        this.clearChildren();
        buttonInfos.clear();

        // 重置按钮（固定在底部，不受滚动影响）
        this.resetButton = ButtonWidget.builder(
                Text.literal("重置所有点数"),
                button -> onResetPoints()
        ).dimensions(left + 50, top + 180, 100, 20).build();

        // 关闭按钮（固定在底部，不受滚动影响）
        this.closeButton = ButtonWidget.builder(
                Text.literal("关闭"),
                button -> this.close()
        ).dimensions(left + 170, top + 180, 100, 20).build();

        this.addDrawableChild(resetButton);
        this.addDrawableChild(closeButton);

        // 不在这里创建属性按钮，它们在render中动态渲染
    }

    private String getEffectText(String attribute, int points) {
        switch (attribute) {
            case "health": return points + "❤";
            case "attack": return points + "⚔";
            case "defense": return points + "🛡";
            case "speed": return points + "%";
            default: return "";
        }
    }

    private void onAssignPoint(String attribute, int amount) {
        // 发送网络包到服务器处理加点
        PointClientNetworking.sendAssignPoint(attribute, amount);

        // 乐观更新：立即更新本地显示，等待服务器确认
        int current = assignedPoints.getOrDefault(attribute, 0);
        int newValue = current + amount;

        if (newValue >= 0 && availablePoints >= amount && amount > 0) {
            assignedPoints.put(attribute, newValue);
            availablePoints -= amount;
        } else if (amount < 0 && current > 0) {
            assignedPoints.put(attribute, newValue);
            availablePoints -= amount; // amount 为负，所以减去负数是加
        }
    }

    private void onResetPoints() {
        // 发送网络包到服务器处理重置
        PointClientNetworking.sendResetPoints();

        // 乐观更新：立即重置本地显示
        availablePoints = getTotalPoints();
        for (String attr : assignedPoints.keySet()) {
            assignedPoints.put(attr, 0);
        }
    }


    private int getTotalPoints() {
        // 获取总点数（已分配 + 可用）
        return availablePoints + getTotalAssignedPoints();
    }

    private int getTotalAssignedPoints() {
        return assignedPoints.values().stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        // 绘制主背景
        context.fill(left, top, left + backgroundWidth, top + backgroundHeight, 0xFFC6C6C6);
        context.fill(left + 5, top + 5, left + backgroundWidth - 5, top + backgroundHeight - 5, 0xFF000000);

        // 绘制标题区域
        context.fill(left + 5, top + 5, left + backgroundWidth - 5, top + 25, 0xFF333333);

        // 绘制标题 - 居中显示
        int titleWidth = this.textRenderer.getWidth("加点系统");
        int titleX = left + (backgroundWidth - titleWidth) / 2;
        context.drawText(this.textRenderer, Text.literal("加点系统"),
                titleX, top + 11, 0xFFFFFF, false);

        // 绘制可用点数 - 调整位置
        String availableText = "可用点数: " + availablePoints;
        int availableWidth = this.textRenderer.getWidth(availableText);
        int availableX = left + backgroundWidth - availableWidth - 15;
        context.drawText(this.textRenderer,
                Text.literal(availableText),
                availableX, top + 11, 0xFFFFFF, false);

        // 启用剪裁区域，限制内容渲染范围
        context.enableScissor(left + 5, top + 30, left + backgroundWidth - 5, top + 30 + visibleHeight);

        // 绘制属性列表背景
        context.fill(left + 5, top + 30, left + backgroundWidth - 5, top + 30 + visibleHeight, 0xFF222222);

        // 绘制属性行背景（交替颜色）并更新按钮位置信息
        int startY = top + 30 - scrollY;
        updateButtonPositions(startY);

        for (int i = 0; i < ATTRIBUTES.length; i++) {
            String attr = ATTRIBUTES[i];
            int yPos = startY + i * 25;
            int currentPoints = assignedPoints.getOrDefault(attr, 0);

            // 只绘制在可见区域内的背景
            if (yPos + 18 >= top + 30 && yPos <= top + 30 + visibleHeight) {
                int bgColor = (i % 2 == 0) ? 0x44222222 : 0x44333333;
                context.fill(left + 5, yPos - 2, left + backgroundWidth - 5, yPos + 18, bgColor);

                // 绘制属性名称和点数 - 调整位置
                context.drawText(this.textRenderer,
                        Text.literal(ATTRIBUTE_NAMES[i] + ": " + currentPoints),
                        left + 20, yPos, 0xFFFFFF, false);

                // 绘制效果文本 - 调整位置
                String effectText = getEffectText(attr, currentPoints);
                int effectWidth = this.textRenderer.getWidth(effectText);
                int effectX = left + backgroundWidth - effectWidth - 35;
                context.drawText(this.textRenderer,
                        Text.literal(effectText),
                        effectX, yPos, 0xAAAAAA, false);
            }
        }

        // 动态渲染属性按钮（在剪裁区域内）
        renderAttributeButtons(context, mouseX, mouseY, delta);

        // 禁用剪裁区域
        context.disableScissor();

        // 绘制滚动条（如果需要）
        drawScrollbar(context, mouseX, mouseY);

        // 渲染固定按钮（在剪裁区域外）
        resetButton.render(context, mouseX, mouseY, delta);
        closeButton.render(context, mouseX, mouseY, delta);

        // 绘制滚动提示（如果需要）
        if (contentHeight > visibleHeight) {
            String scrollHint = "使用鼠标滚轮滚动";
            int hintWidth = this.textRenderer.getWidth(scrollHint);
            int hintX = left + (backgroundWidth - hintWidth) / 2;
            context.drawText(this.textRenderer,
                    Text.literal(scrollHint),
                    hintX, top + 170, 0xAAAAAA, false);
        }
    }

    private void updateButtonPositions(int startY) {
        buttonInfos.clear();
        for (int i = 0; i < ATTRIBUTES.length; i++) {
            String attr = ATTRIBUTES[i];
            int yPos = startY + i * 25;
            buttonInfos.put(attr, new ButtonInfo(yPos));
        }
    }

    private void renderAttributeButtons(DrawContext context, int mouseX, int mouseY, float delta) {
        for (String attr : buttonInfos.keySet()) {
            ButtonInfo info = buttonInfos.get(attr);
            int yPos = info.yPos;
            int currentPoints = assignedPoints.getOrDefault(attr, 0);

            // 只在可见区域内渲染按钮
            if (yPos + 15 >= top + 30 && yPos <= top + 30 + visibleHeight) {
                // 检查是否达到最大等级
                boolean atMaxLevel = currentPoints >= getMaxLevel;
                boolean canAdd = availablePoints > 0 && !atMaxLevel;
                boolean canRemove = currentPoints > 0;

                // 加点按钮 - 调整位置，放在属性名称和效果文本中间
                int buttonAreaWidth = backgroundWidth - 50; // 减去边距
                int buttonStartX = left + 120; // 更靠右
                renderButton(context, buttonStartX, yPos, 20, 15,
                        Text.literal("+"), canAdd, mouseX, mouseY);

                // 减点按钮 - 调整位置
                renderButton(context, buttonStartX + 35, yPos, 20, 15,
                        Text.literal("-"), canRemove, mouseX, mouseY);
            }
        }
    }

    private void renderButton(DrawContext context, int x, int y, int width, int height,
                              Text text, boolean active, int mouseX, int mouseY) {
        // 绘制按钮背景
        int bgColor = active ? 0xFF555555 : 0xFF333333;
        if (isMouseOverButton(mouseX, mouseY, x, y, width, height) && active) {
            bgColor = 0xFF666666;
        }

        context.fill(x, y, x + width, y + height, bgColor);
        context.drawBorder(x, y, width, height, active ? 0xFF888888 : 0xFF444444);

        // 绘制按钮文本
        int textColor = active ? 0xFFFFFF : 0x888888;
        int textX = x + (width - textRenderer.getWidth(text)) / 2;
        int textY = y + (height - 8) / 2;
        context.drawText(textRenderer, text, textX, textY, textColor, false);
    }

    private boolean isMouseOverButton(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private void drawScrollbar(DrawContext context, int mouseX, int mouseY) {
        if (contentHeight <= visibleHeight) {
            return; // 不需要滚动条
        }

        // 计算滚动条参数
        int scrollbarHeight = (int) ((float) visibleHeight / contentHeight * visibleHeight);
        scrollbarHeight = Math.max(scrollbarHeight, 10); // 最小高度

        int scrollbarTop = top + 30 + (int) ((float) scrollY / (contentHeight - visibleHeight) * (visibleHeight - scrollbarHeight));

        // 滚动条背景
        context.fill(scrollbarLeft, top + 30, scrollbarLeft + scrollbarWidth, top + 30 + visibleHeight, 0xFF555555);

        // 滚动条滑块
        int scrollbarColor = isDraggingScrollbar || isMouseOverScrollbar(mouseX, mouseY) ? 0xFF888888 : 0xFF666666;
        context.fill(scrollbarLeft, scrollbarTop, scrollbarLeft + scrollbarWidth, scrollbarTop + scrollbarHeight, scrollbarColor);
    }

    private boolean isMouseOverScrollbar(int mouseX, int mouseY) {
        return mouseX >= scrollbarLeft && mouseX <= scrollbarLeft + scrollbarWidth &&
                mouseY >= top + 30 && mouseY <= top + 30 + visibleHeight;
    }

    // 检查鼠标是否在界面内
    private boolean isMouseOverScreen(double mouseX, double mouseY) {
        return mouseX >= left && mouseX <= left + backgroundWidth &&
                mouseY >= top && mouseY <= top + backgroundHeight;
    }

    // 检查鼠标是否在属性按钮上
    private boolean isMouseOverAttributeButton(double mouseX, double mouseY) {
        for (String attr : buttonInfos.keySet()) {
            ButtonInfo info = buttonInfos.get(attr);
            int yPos = info.yPos;

            // 只在可见区域内检查按钮
            if (yPos + 15 >= top + 30 && yPos <= top + 30 + visibleHeight) {
                int buttonStartX = left + 120;
                // 加点按钮
                if (isMouseOverButton((int)mouseX, (int)mouseY, buttonStartX, yPos, 20, 15)) {
                    return true;
                }

                // 减点按钮
                if (isMouseOverButton((int)mouseX, (int)mouseY, buttonStartX + 35, yPos, 20, 15)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        // 鼠标滚轮滚动
        if (isMouseOverScreen(mouseX, mouseY)) {
            int scrollAmount = (int) (-amount * 20); // 滚动速度
            int newScrollY = Math.max(0, Math.min(contentHeight - visibleHeight, scrollY + scrollAmount));

            // 更新滚动位置
            if (newScrollY != scrollY) {
                scrollY = newScrollY;
                // 清除buttonInfos，强制在下一帧重新计算
                buttonInfos.clear();
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 检查是否点击了滚动条
        if (button == 0 && isMouseOverScrollbar((int) mouseX, (int) mouseY)) {
            isDraggingScrollbar = true;
            return true;
        }

        // 处理属性按钮点击
        if (button == 0 && isMouseOverAttributeButton(mouseX, mouseY)) {
            for (String attr : buttonInfos.keySet()) {
                ButtonInfo info = buttonInfos.get(attr);
                int yPos = info.yPos;
                int currentPoints = assignedPoints.getOrDefault(attr, 0);

                // 只在可见区域内处理按钮点击
                if (yPos + 15 >= top + 30 && yPos <= top + 30 + visibleHeight) {
                    int buttonStartX = left + 120;
                    // 加点按钮
                    if (isMouseOverButton((int)mouseX, (int)mouseY, buttonStartX, yPos, 20, 15)) {
                        boolean atMaxLevel = currentPoints >= getMaxLevel;
                        if (availablePoints > 0 && !atMaxLevel) {
                            onAssignPoint(attr, 1);
                            return true;
                        }
                    }

                    // 减点按钮
                    if (isMouseOverButton((int)mouseX, (int)mouseY, buttonStartX + 35, yPos, 20, 15)) {
                        if (currentPoints > 0) {
                            onAssignPoint(attr, -1);
                            return true;
                        }
                    }
                }
            }
        }

        // 处理固定按钮点击
        if (resetButton.isMouseOver(mouseX, mouseY) && resetButton.active) {
            return resetButton.mouseClicked(mouseX, mouseY, button);
        }

        if (closeButton.isMouseOver(mouseX, mouseY)) {
            return closeButton.mouseClicked(mouseX, mouseY, button);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            isDraggingScrollbar = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDraggingScrollbar) {
            // 拖动滚动条
            double relativeY = mouseY - (top + 30);
            double percentage = relativeY / visibleHeight;
            int newScrollY = (int) (percentage * (contentHeight - visibleHeight));
            newScrollY = Math.max(0, Math.min(contentHeight - visibleHeight, newScrollY));

            // 更新滚动位置
            if (newScrollY != scrollY) {
                scrollY = newScrollY;
                // 清除buttonInfos，强制在下一帧重新计算
                buttonInfos.clear();
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}