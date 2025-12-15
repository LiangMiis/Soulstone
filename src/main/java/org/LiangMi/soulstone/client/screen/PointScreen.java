package org.LiangMi.soulstone.client.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.LiangMi.soulstone.network.c2s.PointClientNetworking;

import java.util.HashMap;
import java.util.Map;

public class PointScreen extends Screen {
    private int getMaxLevel;
    private int availablePoints = 0;
    private Map<String, Integer> originalPoints = new HashMap<>(); // 原始点数（从服务器获取）
    private Map<String, Integer> pendingPoints = new HashMap<>(); // 待分配的加点（本地预览）
    private Map<String, Integer> currentPoints = new HashMap<>(); // 当前显示的点数（原始 + 待分配）

    // 按钮和文本的位置信息
    private Map<String, ButtonInfo> buttonInfos;
    private ButtonWidget confirmButton;
    private ButtonWidget closeButton;

    // UI 尺寸 - 采用与技能界面类似的风格
    private int backgroundWidth = 380;
    private int backgroundHeight = 260;
    private int left;
    private int top;

    // 滚动相关变量
    private int scrollY = 0;
    private int contentHeight = 0;
    private int visibleHeight = 180;
    private boolean isDraggingScrollbar = false;
    private int scrollbarWidth = 6;

    // 属性配置数组
    private static final String[] ATTRIBUTES = {
            "health", "attack", "defense", "mana",
            "arcane", "fire", "frost", "healing", "lightning", "soul"
    };

    private static final String[] ATTRIBUTE_NAMES = {
            "生命值", "攻击力", "防御力", "以太",
            "奥秘", "火焰", "寒冰", "治愈", "雷电", "灵魂"
    };

    private static final String[] ATTRIBUTE_ICONS = {
            "❤", "⚔", "🛡", "✨",
            "🔮", "🔥", "❄", "💚", "⚡", "💀"
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
        super(Text.literal("属性加点系统"));
        this.buttonInfos = new HashMap<>();

        // 初始化默认数据
        initializeDefaultAttributes();

        // 计算内容总高度
        this.contentHeight = ATTRIBUTES.length * 35 + 10;

        // 请求服务器发送最新数据
        PointClientNetworking.sendOpenScreenRequest();
    }

    private void initializeDefaultAttributes() {
        for (String attr : ATTRIBUTES) {
            originalPoints.put(attr, 0);
            pendingPoints.put(attr, 0);
            currentPoints.put(attr, 0);
        }
    }

    // 添加这个方法用于从网络包更新数据
    public void updateFromNetwork(int availablePoints, Map<String, Integer> assignedPoints, int gameLv) {
        this.availablePoints = availablePoints;
        this.getMaxLevel = gameLv;

        // 保存原始点数
        for (String attr : ATTRIBUTES) {
            int original = assignedPoints.getOrDefault(attr, 0);
            originalPoints.put(attr, original);
            // 重置待分配点数
            pendingPoints.put(attr, 0);
            // 更新当前显示点数
            currentPoints.put(attr, original);
        }
    }

    @Override
    protected void init() {
        super.init();

        this.left = (this.width - this.backgroundWidth) / 2;
        this.top = (this.height - this.backgroundHeight) / 2;

        createWidgets();
    }

    private void createWidgets() {
        // 清除现有组件
        this.clearChildren();
        buttonInfos.clear();

        int buttonWidth = 100;
        int buttonSpacing = 20;
        int totalButtonsWidth = 2 * buttonWidth + buttonSpacing;
        int buttonStartX = left + (backgroundWidth - totalButtonsWidth) / 2;

        // 确认按钮
        this.confirmButton = new CustomButton(
                buttonStartX,
                top + backgroundHeight - 30,
                buttonWidth,
                25,
                Text.literal("确认加点"),
                (button) -> onConfirmPoints(),
                this
        );

        // 关闭按钮
        this.closeButton = new CustomButton(
                buttonStartX + buttonWidth + buttonSpacing,
                top + backgroundHeight - 30,
                buttonWidth,
                25,
                Text.literal("关闭"),
                (button) -> this.close(),
                this
        );

        this.addDrawableChild(confirmButton);
        this.addDrawableChild(closeButton);
    }

    private String getEffectText(String attribute, int points) {
        return "+" + points;
    }

    private String getDescription(String attribute) {
        switch (attribute) {
            case "health":
                return "每点增加2点最大生命值";
            case "attack":
                return "每点增加1点基础攻击力";
            case "defense":
                return "每点增加1点基础防御力";
            case "mana":
                return "每点增加10点最大法力值";
            case "arcane":
                return "增强奥秘系法术效果";
            case "fire":
                return "增强火焰系法术效果";
            case "frost":
                return "增强寒冰系法术效果";
            case "healing":
                return "增强治愈系法术效果";
            case "lightning":
                return "增强雷电系法术效果";
            case "soul":
                return "增强灵魂系法术效果";
            default:
                return "增强对应属性效果";
        }
    }

    private void onPreviewPoint(String attribute, int amount) {
        // 本地预览加点，不消耗实际点数
        int original = originalPoints.getOrDefault(attribute, 0);
        int pending = pendingPoints.getOrDefault(attribute, 0);
        int totalPendingPoints = getTotalPendingPoints();

        if (amount > 0) {
            // 加点预览
            if (availablePoints > totalPendingPoints && (original + pending + amount) <= getMaxLevel) {
                pendingPoints.put(attribute, pending + amount);
                updateCurrentPoints(attribute);
            }
        } else if (amount < 0) {
            // 减点预览
            if (pending > 0) {
                pendingPoints.put(attribute, Math.max(0, pending + amount));
                updateCurrentPoints(attribute);
            }
        }
    }

    private void updateCurrentPoints(String attribute) {
        int original = originalPoints.getOrDefault(attribute, 0);
        int pending = pendingPoints.getOrDefault(attribute, 0);
        currentPoints.put(attribute, original + pending);
    }

    private void onConfirmPoints() {
        // 确认加点，发送所有待分配的加点
        int totalPendingPoints = getTotalPendingPoints();
        if (totalPendingPoints == 0) {
            // 如果没有待分配的加点，直接返回
            if (this.client != null && this.client.player != null) {
                this.client.player.sendMessage(Text.literal("§e没有分配点数可确认！"), false);
            }
            return;
        }

        for (String attr : ATTRIBUTES) {
            int pending = pendingPoints.getOrDefault(attr, 0);
            if (pending > 0) {
                // 发送网络包到服务器处理加点
                PointClientNetworking.sendAssignPoint(attr, pending);
            }
        }

        // 清空待分配点数（但保持界面打开）
        for (String attr : ATTRIBUTES) {
            pendingPoints.put(attr, 0);
        }

        // 更新可用点数（本地乐观更新）
        availablePoints -= totalPendingPoints;


        // 不关闭窗口，只刷新界面
        // 注意：这里不清除组件，但需要强制重绘
        this.init();
    }

    private int getTotalPendingPoints() {
        return pendingPoints.values().stream().mapToInt(Integer::intValue).sum();
    }

    private int getTotalAssignedPoints() {
        return currentPoints.values().stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 绘制半透明背景
        this.renderBackground(context);

        // 绘制GUI主背景（深色半透明）
        context.fill(left, top, left + backgroundWidth, top + backgroundHeight, 0xCC1A1A1A);

        // 绘制边框
        drawBorder(context);

        // 绘制标题区域
        context.fill(left + 5, top + 5, left + backgroundWidth - 5, top + 35, 0xCC333333);

        // 绘制标题 - 居中显示
        String titleText = "属性加点系统";
        int titleWidth = this.textRenderer.getWidth(titleText);
        int titleX = left + (backgroundWidth - titleWidth) / 2;
        context.drawTextWithShadow(this.textRenderer, Text.literal(titleText),
                titleX, top + 13, 0xFFFFFF);

        // 计算已使用的预览点数
        int usedPreviewPoints = getTotalPendingPoints();
        int remainingPoints = availablePoints - usedPreviewPoints;

        // 绘制可用点数（包括预览中的点数）
        String availableText = "§e可用点数: §a" + remainingPoints;
        if (usedPreviewPoints > 0) {
            availableText += " §7(预览: +" + usedPreviewPoints + ")";
        }
        int availableWidth = this.textRenderer.getWidth(availableText);
        context.drawTextWithShadow(this.textRenderer,
                Text.literal(availableText),
                left + backgroundWidth - availableWidth - 15, top + 13, 0xFFFFFF);

        // 绘制等级限制
        String levelText = "§7等级上限: §f" + getMaxLevel + " 级";
        context.drawTextWithShadow(this.textRenderer,
                Text.literal(levelText),
                left + 15, top + 13, 0xAAAAAA);

        // 启用剪裁区域，限制内容渲染范围
        context.enableScissor(left + 10, top + 40, left + backgroundWidth - 10, top + 40 + visibleHeight);

        // 绘制属性列表背景
        context.fill(left + 10, top + 40, left + backgroundWidth - 10, top + 40 + visibleHeight, 0xCC222222);

        // 绘制属性行背景（交替颜色）并更新按钮位置信息
        int startY = top + 40 - scrollY;
        updateButtonPositions(startY);

        for (int i = 0; i < ATTRIBUTES.length; i++) {
            String attr = ATTRIBUTES[i];
            int yPos = startY + i * 35;
            int currentPoint = currentPoints.getOrDefault(attr, 0);
            int pendingPoint = pendingPoints.getOrDefault(attr, 0);
            boolean atMaxLevel = currentPoint >= getMaxLevel;
            boolean hasPendingPoints = pendingPoint > 0;

            // 只绘制在可见区域内的背景
            if (yPos + 30 >= top + 40 && yPos <= top + 40 + visibleHeight) {
                // 绘制条目背景
                int bgColor = (i % 2 == 0) ? 0xCC2C2C2C : 0xCC3C3C3C;
                if (atMaxLevel) {
                    bgColor = 0xCC4A2C2C; // 达到最大等级时红色调
                } else if (hasPendingPoints) {
                    bgColor = 0xCC2C4A2C; // 有待分配点数时绿色调
                }
                context.fill(left + 10, yPos, left + backgroundWidth - 10, yPos + 30, bgColor);

                // 绘制条目边框
                int borderColor = hasPendingPoints ? 0xCC88FF88 : 0xCC555555;
                context.fill(left + 10, yPos, left + backgroundWidth - 10, yPos + 1, borderColor); // 上边框
                context.fill(left + 10, yPos + 29, left + backgroundWidth - 10, yPos + 30, borderColor); // 下边框

                // 绘制属性图标
                context.drawTextWithShadow(this.textRenderer,
                        Text.literal(ATTRIBUTE_ICONS[i]),
                        left + 20, yPos + 7, 0xFFFFFF);

                // 绘制属性名称
                int nameColor = atMaxLevel ? 0xFF888888 : (hasPendingPoints ? 0xFF88FF88 : 0xFFFFFF);
                context.drawTextWithShadow(this.textRenderer,
                        Text.literal(ATTRIBUTE_NAMES[i]),
                        left + 45, yPos + 7, nameColor);

                // 绘制当前点数（包括预览）
                String pointsText = currentPoint + " / " + getMaxLevel;
                if (pendingPoint > 0) {
                    pointsText += " §a(+" + pendingPoint + ")";
                }
                int pointsWidth = this.textRenderer.getWidth(pointsText);
                int pointsX = left + backgroundWidth - pointsWidth - 150; // 向左调整，为按钮留出空间
                int pointsColor = atMaxLevel ? 0xFFFF5555 : (hasPendingPoints ? 0xFF88FF88 : 0xFFFFFF);
                context.drawTextWithShadow(this.textRenderer,
                        Text.literal(pointsText),
                        pointsX, yPos + 7, pointsColor);

                // 绘制效果描述
                String desc = getDescription(attr);
                context.drawTextWithShadow(this.textRenderer,
                        Text.literal(desc),
                        left + 45, yPos + 19, 0xAAAAAA);
            }
        }

        // 动态渲染加点按钮（在剪裁区域内）
        renderAddPointButtons(context, mouseX, mouseY, delta);

        // 禁用剪裁区域
        context.disableScissor();

        // 绘制滚动条（如果需要）
        drawScrollbar(context, mouseX, mouseY);

        // 渲染按钮
        super.render(context, mouseX, mouseY, delta);

        // 绘制底部提示
        drawBottomTips(context);
    }

    private void drawBorder(DrawContext context) {
        int borderColor = 0xCCFFFFFF;
        // 上边框
        context.fill(left, top, left + backgroundWidth, top + 1, borderColor);
        // 下边框
        context.fill(left, top + backgroundHeight - 1, left + backgroundWidth, top + backgroundHeight, borderColor);
        // 左边框
        context.fill(left, top, left + 1, top + backgroundHeight, borderColor);
        // 右边框
        context.fill(left + backgroundWidth - 1, top, left + backgroundWidth, top + backgroundHeight, borderColor);
    }

    private void updateButtonPositions(int startY) {
        buttonInfos.clear();
        for (int i = 0; i < ATTRIBUTES.length; i++) {
            String attr = ATTRIBUTES[i];
            int yPos = startY + i * 35;
            buttonInfos.put(attr, new ButtonInfo(yPos));
        }
    }

    private void renderAddPointButtons(DrawContext context, int mouseX, int mouseY, float delta) {
        for (String attr : buttonInfos.keySet()) {
            ButtonInfo info = buttonInfos.get(attr);
            int yPos = info.yPos;
            int currentPoint = currentPoints.getOrDefault(attr, 0);
            int pendingPoint = pendingPoints.getOrDefault(attr, 0);
            int usedPreviewPoints = getTotalPendingPoints();

            // 只在可见区域内渲染按钮
            if (yPos + 25 >= top + 40 && yPos <= top + 40 + visibleHeight) {
                // 检查是否达到最大等级
                boolean atMaxLevel = currentPoint >= getMaxLevel;
                boolean canAdd = (availablePoints - usedPreviewPoints) > 0 && !atMaxLevel;
                boolean canRemove = pendingPoint > 0;

                // 按钮起始位置
                int buttonStartX = left + backgroundWidth - 110;

                // 显示加点按钮
                renderPointButton(context, buttonStartX, yPos + 5, 35, 20,
                        Text.literal("+"), canAdd, mouseX, mouseY, attr, true);

                // 显示减点按钮（如果有待分配点数）放在加点按钮右边
                if (canRemove) {
                    renderPointButton(context, buttonStartX + 40, yPos + 5, 35, 20,
                            Text.literal("-"), true, mouseX, mouseY, attr, false);
                }
            }
        }
    }

    private void renderPointButton(DrawContext context, int x, int y, int width, int height,
                                   Text text, boolean active, int mouseX, int mouseY,
                                   String attribute, boolean isAdd) {
        // 绘制按钮背景
        int bgColor = active ? (isAdd ? 0xCC446644 : 0xCC664444) : 0xCC333333; // 绿色或红色或灰色
        if (isMouseOverButton(mouseX, mouseY, x, y, width, height) && active) {
            bgColor = isAdd ? 0xCC55AA55 : 0xCCAA5555; // 悬停时更亮的颜色
        }

        context.fill(x, y, x + width, y + height, bgColor);

        // 绘制按钮边框
        int borderColor = active ? (isAdd ? 0xCC88FF88 : 0xCCFF8888) : 0xCC555555;
        if (isMouseOverButton(mouseX, mouseY, x, y, width, height) && active) {
            borderColor = 0xCCFFFFFF;
        }
        context.fill(x, y, x + width, y + 1, borderColor); // 上边框
        context.fill(x, y + height - 1, x + width, y + height, borderColor); // 下边框
        context.fill(x, y, x + 1, y + height, borderColor); // 左边框
        context.fill(x + width - 1, y, x + width, y + height, borderColor); // 右边框

        // 绘制按钮文本
        int textColor = active ? (isAdd ? 0xFF88FF88 : 0xFFFF8888) : 0xFF888888;
        int textX = x + (width - textRenderer.getWidth(text)) / 2;
        int textY = y + (height - 8) / 2;
        context.drawTextWithShadow(textRenderer, text, textX, textY, textColor);
    }

    private boolean isMouseOverButton(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private void drawScrollbar(DrawContext context, int mouseX, int mouseY) {
        if (contentHeight <= visibleHeight) {
            return; // 不需要滚动条
        }

        // 计算滚动条参数
        int scrollbarLeft = left + backgroundWidth - 15;
        int scrollbarHeight = (int) ((float) visibleHeight / contentHeight * visibleHeight);
        scrollbarHeight = Math.max(scrollbarHeight, 20); // 最小高度

        int scrollbarTop = top + 40 + (int) ((float) scrollY / (contentHeight - visibleHeight) * (visibleHeight - scrollbarHeight));

        // 滚动条背景
        context.fill(scrollbarLeft, top + 40, scrollbarLeft + scrollbarWidth, top + 40 + visibleHeight, 0xCC444444);

        // 滚动条滑块
        int scrollbarColor = isDraggingScrollbar || isMouseOverScrollbar(mouseX, mouseY) ? 0xCC888888 : 0xCC666666;
        context.fill(scrollbarLeft, scrollbarTop, scrollbarLeft + scrollbarWidth, scrollbarTop + scrollbarHeight, scrollbarColor);
    }

    private boolean isMouseOverScrollbar(int mouseX, int mouseY) {
        int scrollbarLeft = left + backgroundWidth - 15;
        return mouseX >= scrollbarLeft && mouseX <= scrollbarLeft + scrollbarWidth &&
                mouseY >= top + 40 && mouseY <= top + 40 + visibleHeight;
    }

    private void drawBottomTips(DrawContext context) {
        String tipText = "点击按钮预览加点 | 确认加点生效(不关闭窗口) | ESC关闭";
        int tipWidth = this.textRenderer.getWidth(tipText);
        int tipX = left + (backgroundWidth - tipWidth) / 2;
        context.drawTextWithShadow(this.textRenderer,
                Text.literal(tipText),
                tipX, top + backgroundHeight - 55, 0xAAAAAA);
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

        // 处理加点/减点按钮点击（预览模式）
        if (button == 0) {
            for (String attr : buttonInfos.keySet()) {
                ButtonInfo info = buttonInfos.get(attr);
                int yPos = info.yPos;

                // 只在可见区域内处理按钮点击
                if (yPos + 25 >= top + 40 && yPos <= top + 40 + visibleHeight) {
                    int buttonStartX = left + backgroundWidth - 110;

                    // 加点按钮
                    if (isMouseOverButton((int) mouseX, (int) mouseY, buttonStartX, yPos + 5, 35, 20)) {
                        int currentPoint = currentPoints.getOrDefault(attr, 0);
                        boolean atMaxLevel = currentPoint >= getMaxLevel;
                        int usedPreviewPoints = getTotalPendingPoints();

                        if (!atMaxLevel && (availablePoints - usedPreviewPoints) > 0) {
                            onPreviewPoint(attr, 1);
                            return true;
                        }
                    }

                    // 减点按钮（在加点按钮右边）
                    if (pendingPoints.getOrDefault(attr, 0) > 0) {
                        if (isMouseOverButton((int) mouseX, (int) mouseY, buttonStartX + 40, yPos + 5, 35, 20)) {
                            onPreviewPoint(attr, -1);
                            return true;
                        }
                    }
                }
            }
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
            double relativeY = mouseY - (top + 40);
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

    // 自定义按钮类，与技能界面风格一致
    private static class CustomButton extends ButtonWidget {
        private final PointScreen parent;

        public CustomButton(int x, int y, int width, int height, Text message, PressAction onPress, PointScreen parent) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
            this.parent = parent;
        }

        @Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            // 绘制按钮背景
            int bgColor = this.active ? 0xCC444444 : 0xCC333333;
            if (this.isHovered() && this.active) {
                bgColor = 0xCC555555;
            }
            context.fill(getX(), getY(), getX() + width, getY() + height, bgColor);

            // 绘制按钮边框
            int borderColor = this.active ? 0xCC888888 : 0xCC555555;
            if (this.isHovered() && this.active) {
                borderColor = 0xCCFFFFFF;
            }
            context.fill(getX(), getY(), getX() + width, getY() + 1, borderColor); // 上边框
            context.fill(getX(), getY() + height - 1, getX() + width, getY() + height, borderColor); // 下边框
            context.fill(getX(), getY(), getX() + 1, getY() + height, borderColor); // 左边框
            context.fill(getX() + width - 1, getY(), getX() + width, getY() + height, borderColor); // 右边框

            // 绘制按钮文本
            int textColor = this.active ? 0xFFFFFF : 0xAAAAAA;
            int textX = getX() + (width - parent.textRenderer.getWidth(this.getMessage())) / 2;
            int textY = getY() + (height - 8) / 2;
            context.drawTextWithShadow(parent.textRenderer, this.getMessage(), textX, textY, textColor);
        }
    }
}