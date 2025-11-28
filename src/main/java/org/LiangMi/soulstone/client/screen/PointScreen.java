package org.LiangMi.soulstone.client.screen;


import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.LiangMi.soulstone.network.packet.c2s.PointClientNetworking;

import java.util.HashMap;
import java.util.Map;

public class PointScreen extends Screen {
    private final PlayerEntity player;
    private int availablePoints = 0;
    private Map<String, Integer> assignedPoints = new HashMap<>();
    private Map<String, ButtonWidget> assignButtons;
    private Map<String, ButtonWidget> removeButtons;
    private ButtonWidget resetButton;
    private ButtonWidget closeButton;

    // UI 尺寸
    private int backgroundWidth = 256;
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
            "health", "attack", "defense", "speed",
            "mining_speed", "luck", "experience",
            "jump_height", "swim_speed", "fall_resistance",
            "knockback_resistance", "critical_chance", "critical_damage"
    };

    private static final String[] ATTRIBUTE_NAMES = {
            "生命值", "攻击力", "防御力", "移动速度",
            "挖掘速度", "幸运值", "经验加成",
            "跳跃高度", "游泳速度", "摔落抗性",
            "击退抗性", "暴击几率", "暴击伤害"
    };

    public PointScreen(PlayerEntity player) {
        super(Text.literal("加点系统"));
        this.player = player;
        this.assignButtons = new HashMap<>();
        this.removeButtons = new HashMap<>();

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
    public void updateFromNetwork(int availablePoints, Map<String, Integer> assignedPoints) {
        this.availablePoints = availablePoints;
        this.assignedPoints = new HashMap<>(assignedPoints);

        // 如果界面已经初始化，更新按钮状态
        if (this.client != null && this.client.currentScreen == this) {
            updateButtonStates();
        }
    }

    @Override
    protected void init() {
        super.init();

        this.left = (this.width - this.backgroundWidth) / 2;
        this.top = (this.height - this.backgroundHeight) / 2;
        this.scrollbarLeft = left + backgroundWidth - scrollbarWidth - 5;

        createWidgets();
        updateButtonStates();
    }

    private void createWidgets() {
        // 清除现有组件
        this.clearChildren();
        assignButtons.clear();
        removeButtons.clear();

        // 属性列表 - 根据滚动位置调整Y坐标
        int startY = top + 30 - scrollY;

        for (int i = 0; i < ATTRIBUTES.length; i++) {
            String attr = ATTRIBUTES[i];
            String name = ATTRIBUTE_NAMES[i];
            int yPos = startY + i * 25;
            int currentPoints = assignedPoints.getOrDefault(attr, 0);

            // 只创建在可见区域内的组件
            if (yPos + 20 >= top + 30 && yPos <= top + 30 + visibleHeight) {
                // 属性名称和当前点数
                TextWidget pointsText = new TextWidget(
                        left + 15, yPos, 90, 12,
                        Text.literal(name + ": " + currentPoints),
                        this.textRenderer
                );
                this.addDrawableChild(pointsText);

                // 加点按钮
                ButtonWidget addButton = ButtonWidget.builder(
                        Text.literal("+"),
                        button -> onAssignPoint(attr, 1)
                ).dimensions(left + 110, yPos, 20, 15).build();

                // 减点按钮
                ButtonWidget removeButton = ButtonWidget.builder(
                        Text.literal("-"),
                        button -> onAssignPoint(attr, -1)
                ).dimensions(left + 135, yPos, 20, 15).build();

                this.addDrawableChild(addButton);
                this.addDrawableChild(removeButton);

                assignButtons.put(attr, addButton);
                removeButtons.put(attr, removeButton);

                // 当前效果显示
                String effectText = getEffectText(attr, currentPoints);
                TextWidget effectWidget = new TextWidget(
                        left + 160, yPos, 85, 12,
                        Text.literal(effectText),
                        this.textRenderer
                );
                this.addDrawableChild(effectWidget);
            }
        }

        // 重置按钮（固定在底部，不受滚动影响）
        this.resetButton = ButtonWidget.builder(
                Text.literal("重置所有点数"),
                button -> onResetPoints()
        ).dimensions(left + 20, top + 180, 100, 20).build();

        // 关闭按钮（固定在底部，不受滚动影响）
        this.closeButton = ButtonWidget.builder(
                Text.literal("关闭"),
                button -> this.close()
        ).dimensions(left + 130, top + 180, 100, 20).build();

        this.addDrawableChild(resetButton);
        this.addDrawableChild(closeButton);
    }

    private String getEffectText(String attribute, int points) {
        switch (attribute) {
            case "health": return points + "❤";
            case "attack": return points + "⚔";
            case "defense": return points + "🛡";
            case "speed": return points + "%";
            case "mining_speed": return (points * 10) + "%";
            case "luck": return (points * 5) + "%";
            case "experience": return (points * 10) + "%";
            case "jump_height": return (points * 5) + "%";
            case "swim_speed": return (points * 5) + "%";
            case "fall_resistance": return "-" + (points * 5) + "%";
            case "knockback_resistance": return (points * 3) + "%";
            case "critical_chance": return points + "%";
            case "critical_damage": return (points * 2) + "%";
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
            updateButtonStates();
        } else if (amount < 0 && current > 0) {
            assignedPoints.put(attribute, newValue);
            availablePoints -= amount; // amount 为负，所以减去负数是加
            updateButtonStates();
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
        updateButtonStates();
    }

    private void updateButtonStates() {
        // 更新按钮状态
        for (String attr : assignButtons.keySet()) {
            ButtonWidget addBtn = assignButtons.get(attr);
            ButtonWidget removeBtn = removeButtons.get(attr);
            int currentPoints = assignedPoints.getOrDefault(attr, 0);

            // 检查是否达到最大等级
            boolean atMaxLevel = currentPoints >= getMaxLevel(attr);

            addBtn.active = availablePoints > 0 && !atMaxLevel;
            removeBtn.active = currentPoints > 0;
        }

        resetButton.active = getTotalAssignedPoints() > 0;

        // 重新创建组件以更新文本
        createWidgets();
    }

    private int getMaxLevel(String attribute) {
        // 根据属性返回最大等级
        switch (attribute) {
            case "health": return 100;
            case "attack": return 50;
            case "defense": return 50;
            case "speed": return 30;
            case "mining_speed": return 50;
            case "luck": return 20;
            case "experience": return 30;
            case "jump_height": return 20;
            case "swim_speed": return 20;
            case "fall_resistance": return 20;
            case "knockback_resistance": return 30;
            case "critical_chance": return 30;
            case "critical_damage": return 25;
            default: return 50;
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

        // 绘制内容区域（带剪裁，防止内容渲染到外面）
        context.enableScissor(left + 5, top + 30, left + backgroundWidth - 5, top + 30 + visibleHeight);

        // 绘制属性列表背景
        context.fill(left + 5, top + 30, left + backgroundWidth - 5, top + 30 + visibleHeight, 0xFF222222);

        // 绘制属性行背景（交替颜色）
        int startY = top + 30 - scrollY;
        for (int i = 0; i < ATTRIBUTES.length; i++) {
            int yPos = startY + i * 25;

            // 绘制属性行背景（交替颜色）
            if (yPos >= top + 30 && yPos <= top + 30 + visibleHeight) {
                int bgColor = (i % 2 == 0) ? 0x44222222 : 0x44333333;
                context.fill(left + 5, yPos - 2, left + backgroundWidth - 5, yPos + 18, bgColor);
            }
        }

        context.disableScissor();

        // 绘制标题
        context.drawText(this.textRenderer, Text.literal("加点系统"),
                left + 8, top + 11, 0xFFFFFF, false);

        // 绘制可用点数
        context.drawText(this.textRenderer,
                Text.literal("可用点数: " + availablePoints),
                left + 160, top + 11, 0xFFFFFF, false);

        // 绘制滚动条（如果需要）
        drawScrollbar(context, mouseX, mouseY);

        // 渲染所有组件（按钮和文本）
        super.render(context, mouseX, mouseY, delta);

        // 绘制滚动提示（如果需要）
        if (contentHeight > visibleHeight) {
            context.drawText(this.textRenderer,
                    Text.literal("使用鼠标滚轮滚动"),
                    left + 150, top + 170, 0xAAAAAA, false);
        }
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

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        // 鼠标滚轮滚动
        if (isMouseOverScreen(mouseX, mouseY)) {
            int scrollAmount = (int) (-amount * 20); // 滚动速度
            scrollY = Math.max(0, Math.min(contentHeight - visibleHeight, scrollY + scrollAmount));
            createWidgets(); // 重新创建组件以更新位置
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
            scrollY = (int) (percentage * (contentHeight - visibleHeight));
            scrollY = Math.max(0, Math.min(contentHeight - visibleHeight, scrollY));
            createWidgets(); // 重新创建组件以更新位置
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
