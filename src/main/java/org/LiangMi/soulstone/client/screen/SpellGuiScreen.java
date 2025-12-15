package org.LiangMi.soulstone.client.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.access.SpellAccess;
import org.LiangMi.soulstone.data.PlayerSpellData;
import org.LiangMi.soulstone.network.c2s.SpellClientNetworking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellGuiScreen extends Screen {
    // GUI基础尺寸
    private static final int GUI_WIDTH = 400;
    private static final int GUI_HEIGHT = 300;

    // 左侧技能列表配置
    private static final int SKILL_BAR_WIDTH = 180;
    private static final int SKILL_ITEM_HEIGHT = 50;
    private static final int SKILL_SPACING = 10;

    // 底部绑定按钮配置
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 30;
    private static final int BUTTON_ICON_SIZE = 18;
    private static final int BUTTON_SPACING = 5;
    private static final int BUTTON_MARGIN = 15;

    // 定义String类型的按键常量
    private static final String SPELL_KEY_1 = "SpellKey1";
    private static final String SPELL_KEY_2 = "SpellKey2";
    private static final String SPELL_KEY_3 = "SpellKey3";
    private static final String SPELL_KEY_4 = "SpellKey4";
    private static final List<String> SPELL_KEYS = List.of(SPELL_KEY_1, SPELL_KEY_2, SPELL_KEY_3, SPELL_KEY_4);

    // 滚动相关 - 改为像素级滚动，与PointScreen一致
    private int scrollY = 0; // 当前滚动位置（像素）
    private int contentHeight = 0; // 内容总高度（像素）
    private int visibleHeight = 0; // 可见区域高度（像素）
    private int maxScrollY = 0; // 最大滚动位置（像素）

    // 保留原有的滚动偏移，用于兼容原有逻辑
    private int scrollOffset = 0;

    // 数据存储
    private List<String> spellList = new ArrayList<>();
    private Map<String, String> keyBindMap = new HashMap<>();

    // 拖动相关
    private boolean isDragging = false;
    private int draggedSpellIndex = -1;
    private double dragStartX, dragStartY;
    private int selectedSpellIndex = 0;

    // 按钮位置数组
    private int[] buttonPositionsX = new int[4];

    // 添加数据是否已加载的标志
    private boolean dataLoaded = false;

    public SpellGuiScreen() {
        super(Text.literal("技能绑定界面"));
        initSpellData();
        SpellClientNetworking.sendOpenScreenRequest();
    }

    /**
     * 初始化数据
     */
    private void initSpellData() {
        try {
            // 计算滚动边界
            calculateScrollBounds();
            dataLoaded = true;

        } catch (Exception e) {
            e.printStackTrace();
            dataLoaded = false;
        }
    }

    /**
     * 计算滚动边界
     */
    private void calculateScrollBounds() {
        // 计算可见区域高度
        visibleHeight = GUI_HEIGHT - 30 - 45;

        // 计算内容总高度（像素）
        int totalItems = spellList.size();
        contentHeight = totalItems * (SKILL_ITEM_HEIGHT + SKILL_SPACING) + SKILL_SPACING;

        // 计算最大滚动位置
        maxScrollY = Math.max(0, contentHeight - visibleHeight);

        // 限制当前滚动位置在合法范围内
        scrollY = Math.max(0, Math.min(scrollY, maxScrollY));

        // 为了兼容原有逻辑，计算基于项数的滚动偏移
        int itemsPerPage = Math.max(1, visibleHeight / (SKILL_ITEM_HEIGHT + SKILL_SPACING));
        scrollOffset = Math.min(scrollOffset, Math.max(0, totalItems - itemsPerPage));
    }

    public void updateFromNetwork(List<String> spellList, Map<String, String> keyBindMap) {
        this.spellList = spellList;
        this.keyBindMap = keyBindMap;
        calculateScrollBounds(); // 数据更新后重新计算滚动边界
    }

    @Override
    protected void init() {
        super.init();

        if (!dataLoaded) {
            initSpellData();
        }

        int guiX = (this.width - GUI_WIDTH) / 2;
        int guiY = (this.height - GUI_HEIGHT) / 2;
        int bottomY = guiY + GUI_HEIGHT - 40;

        // 计算按钮总宽度和起始位置
        int totalButtonsWidth = 4 * BUTTON_WIDTH + 3 * BUTTON_SPACING;
        int startX = guiX + (GUI_WIDTH - totalButtonsWidth) / 2;

        // 初始化按钮位置数组
        buttonPositionsX = new int[4];

        // 添加4个绑定按钮
        for (int i = 0; i < 4; i++) {
            int buttonX = startX + i * (BUTTON_WIDTH + BUTTON_SPACING);
            buttonPositionsX[i] = buttonX;
            addBindButton(buttonX, bottomY, SPELL_KEYS.get(i));
        }
    }

    /**
     * 创建绑定按钮
     */
    private void addBindButton(int x, int y, String key) {
        this.addDrawableChild(new SkillBindButton(x, y, key, (button) -> {
            if (!spellList.isEmpty() && selectedSpellIndex < spellList.size()) {
                bindSpellToKey(key, spellList.get(selectedSpellIndex));
            }
        }, this));
    }

    /**
     * 绑定技能到按键
     */
    private void bindSpellToKey(String key, String spellId) {
        // 更新本地映射
        SpellClientNetworking.sendKeyBindsSpell(key, spellId);
        keyBindMap.put(key, spellId);

        // 显示绑定消息
        if (this.client != null && this.client.player != null) {
            this.client.player.sendMessage(Text.literal("§a已绑定技能: " + spellId + " 到 " + getFriendlyKeyName(key)), false);
        }
    }

    /**
     * 获取友好按键名称
     */
    public String getFriendlyKeyName(String key) {
        return switch (key) {
            case SPELL_KEY_1 -> "按键1";
            case SPELL_KEY_2 -> "按键2";
            case SPELL_KEY_3 -> "按键3";
            case SPELL_KEY_4 -> "按键4";
            default -> key;
        };
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 渲染背景
        this.renderBackground(context);

        int guiX = (this.width - GUI_WIDTH) / 2;
        int guiY = (this.height - GUI_HEIGHT) / 2;

        // 1. 绘制GUI背景
        context.fill(guiX, guiY, guiX + GUI_WIDTH, guiY + GUI_HEIGHT, 0xCC1A1A1A);
        // 边框
        context.drawBorder(guiX, guiY, GUI_WIDTH, GUI_HEIGHT, 0xFFFFFFFF);

        // 分隔线
        context.fill(guiX + SKILL_BAR_WIDTH + 10, guiY + 20,
                guiX + SKILL_BAR_WIDTH + 12, guiY + GUI_HEIGHT - 45, 0xCCFFFFFF);
        context.fill(guiX, guiY + GUI_HEIGHT - 45,
                guiX + GUI_WIDTH, guiY + GUI_HEIGHT - 43, 0xCCFFFFFF);

        // 2. 绘制标题
        context.drawTextWithShadow(this.textRenderer, Text.literal("技能列表"),
                guiX + 20, guiY + 10, 0xFFFFFF);

        // 3. 绘制技能列表 - 使用剪裁区域
        context.enableScissor(guiX + 10, guiY + 30, guiX + SKILL_BAR_WIDTH, guiY + GUI_HEIGHT - 45);
        renderSpellList(context, guiX, guiY);
        context.disableScissor();

        // 4. 绘制技能详情
        renderSpellDesc(context, guiX, guiY);

        // 5. 绘制拖动图标
        renderDraggedSpellIcon(context, mouseX, mouseY);

        // 6. 绘制子组件
        super.render(context, mouseX, mouseY, delta);

        // 7. 绘制滚动条（如果需要）
        if (contentHeight > visibleHeight) {
            drawScrollbar(context, guiX, guiY, mouseX, mouseY);
        }
    }

    /**
     * 绘制技能列表
     */
    private void renderSpellList(DrawContext context, int guiX, int guiY) {
        if (spellList.isEmpty()) {
            context.drawTextWithShadow(this.textRenderer, Text.literal("暂无可用技能"),
                    guiX + 20, guiY + 50, 0xFF888888);
            return;
        }

        // 计算起始项索引
        int startIndex = 0;
        int currentY = guiY + 30 - scrollY;

        // 遍历所有技能项
        for (int i = 0; i < spellList.size(); i++) {
            String spellId = spellList.get(i);
            int itemY = currentY;

            // 如果项在当前可见区域外（上方），跳过
            if (itemY + SKILL_ITEM_HEIGHT < guiY + 30) {
                currentY += SKILL_ITEM_HEIGHT + SKILL_SPACING;
                continue;
            }

            // 如果项在当前可见区域外（下方），停止绘制
            if (itemY > guiY + GUI_HEIGHT - 45) {
                break;
            }

            // 背景色
            int bgColor = 0xCC2C2C2C;
            if (i == selectedSpellIndex) bgColor = 0xCC4A4A4A;
            if (i == draggedSpellIndex) bgColor = 0xCC6A6A6A;

            // 绘制条目背景
            context.fill(guiX + 10, itemY, guiX + SKILL_BAR_WIDTH, itemY + SKILL_ITEM_HEIGHT, bgColor);
            context.drawBorder(guiX + 10, itemY, SKILL_BAR_WIDTH - 10, SKILL_ITEM_HEIGHT, 0xCCFFFFFF);

            // 技能图标（简化版）
            Identifier spellIcon = getSpellIconById(spellId);
            context.drawTexture(spellIcon, guiX + 20, itemY + 15, 0, 0, 20, 20, 20, 20);

            // 技能名称
            context.drawTextWithShadow(this.textRenderer, Text.literal(spellId),
                    guiX + 50, itemY + 20, 0xFFFFFF);

            // 技能描述
            String shortDesc = getShortSpellDescById(spellId);
            context.drawTextWithShadow(this.textRenderer, Text.literal(shortDesc),
                    guiX + 50, itemY + 35, 0xAAAAAA);

            currentY += SKILL_ITEM_HEIGHT + SKILL_SPACING;
        }
    }

    /**
     * 绘制技能详情
     */
    private void renderSpellDesc(DrawContext context, int guiX, int guiY) {
        if (spellList.isEmpty() || selectedSpellIndex >= spellList.size()) return;

        String selectedSpellId = spellList.get(selectedSpellIndex);

        // 标题区域
        context.fill(guiX + SKILL_BAR_WIDTH + 15, guiY + 20,
                guiX + GUI_WIDTH - 15, guiY + 40, 0xCC333333);
        context.drawTextWithShadow(this.textRenderer, Text.literal("技能详情"),
                guiX + SKILL_BAR_WIDTH + 30, guiY + 25, 0xFFFFAA);

        // 技能ID区域
        context.fill(guiX + SKILL_BAR_WIDTH + 15, guiY + 45,
                guiX + GUI_WIDTH - 15, guiY + 70, 0xCC444444);
        context.drawTextWithShadow(this.textRenderer, Text.literal("技能ID：" + selectedSpellId),
                guiX + SKILL_BAR_WIDTH + 20, guiY + 55, 0xFFFF88);

        // 描述区域
        context.fill(guiX + SKILL_BAR_WIDTH + 15, guiY + 75,
                guiX + GUI_WIDTH - 15, guiY + GUI_HEIGHT - 50, 0xCC222222);
        context.drawTextWithShadow(this.textRenderer, Text.literal("技能效果："),
                guiX + SKILL_BAR_WIDTH + 20, guiY + 80, 0x88FF88);

        // 描述文本
        String desc = getSpellDescById(selectedSpellId);
        List<OrderedText> wrappedDesc = this.textRenderer.wrapLines(Text.literal(desc),
                GUI_WIDTH - SKILL_BAR_WIDTH - 40);
        for (int i = 0; i < wrappedDesc.size(); i++) {
            context.drawTextWithShadow(this.textRenderer, wrappedDesc.get(i),
                    guiX + SKILL_BAR_WIDTH + 20,
                    guiY + 95 + (this.textRenderer.fontHeight + 2) * i,
                    0xAAAAAA);
        }
    }

    /**
     * 绘制拖动中的技能图标
     */
    private void renderDraggedSpellIcon(DrawContext context, int mouseX, int mouseY) {
        if (!isDragging || draggedSpellIndex < 0 || draggedSpellIndex >= spellList.size()) return;

        String spellId = spellList.get(draggedSpellIndex);
        Identifier spellIcon = getSpellIconById(spellId);

        context.fill(mouseX - 12, mouseY - 12, mouseX + 12, mouseY + 12, 0x80000000);
        context.drawTexture(spellIcon, mouseX - 10, mouseY - 10, 0, 0, 20, 20, 20, 20);
    }

    /**
     * 绘制滚动条
     */
    private void drawScrollbar(DrawContext context, int guiX, int guiY, int mouseX, int mouseY) {
        int scrollbarWidth = 6;
        int scrollbarLeft = guiX + SKILL_BAR_WIDTH - 10;

        // 计算滚动条滑块高度
        int scrollbarHeight = (int) ((float) visibleHeight / contentHeight * visibleHeight);
        scrollbarHeight = Math.max(scrollbarHeight, 20); // 最小高度

        // 计算滚动条滑块位置
        int scrollbarTop = guiY + 30 + (int) ((float) scrollY / (contentHeight - visibleHeight) * (visibleHeight - scrollbarHeight));

        // 滚动条背景
        context.fill(scrollbarLeft, guiY + 30, scrollbarLeft + scrollbarWidth, guiY + 30 + visibleHeight, 0xCC444444);

        // 滚动条滑块
        int scrollbarColor = isMouseOverScrollbar(mouseX, mouseY, guiX, guiY) ? 0xCC888888 : 0xCC666666;
        context.fill(scrollbarLeft, scrollbarTop, scrollbarLeft + scrollbarWidth, scrollbarTop + scrollbarHeight, scrollbarColor);
    }

    /**
     * 检查鼠标是否在滚动条上
     */
    private boolean isMouseOverScrollbar(int mouseX, int mouseY, int guiX, int guiY) {
        int scrollbarWidth = 6;
        int scrollbarLeft = guiX + SKILL_BAR_WIDTH - 10;
        return mouseX >= scrollbarLeft && mouseX <= scrollbarLeft + scrollbarWidth &&
                mouseY >= guiY + 30 && mouseY <= guiY + 30 + visibleHeight;
    }

    // ========== 鼠标交互 ==========
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int guiX = (this.width - GUI_WIDTH) / 2;
        int guiY = (this.height - GUI_HEIGHT) / 2;

        // 检查是否点击了滚动条
        if (button == 0 && isMouseOverScrollbar((int) mouseX, (int) mouseY, guiX, guiY)) {
            // 可以添加滚动条拖动逻辑，但这里先不实现
            return true;
        }

        // 检查技能条目点击
        if (!spellList.isEmpty()) {
            // 计算点击位置对应的技能索引
            int clickY = (int) mouseY - (guiY + 30) + scrollY;
            int clickedIndex = -1;

            // 遍历查找点击了哪个技能项
            int currentY = 0;
            for (int i = 0; i < spellList.size(); i++) {
                int itemHeight = SKILL_ITEM_HEIGHT + SKILL_SPACING;
                if (clickY >= currentY && clickY < currentY + SKILL_ITEM_HEIGHT) {
                    clickedIndex = i;
                    break;
                }
                currentY += itemHeight;
            }

            if (clickedIndex >= 0 && clickedIndex < spellList.size() &&
                    mouseX >= guiX + 10 && mouseX <= guiX + SKILL_BAR_WIDTH) {

                isDragging = true;
                draggedSpellIndex = clickedIndex;
                dragStartX = mouseX;
                dragStartY = mouseY;
                selectedSpellIndex = clickedIndex;
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0 && isDragging) {
            double dragDistance = Math.sqrt(Math.pow(mouseX - dragStartX, 2) + Math.pow(mouseY - dragStartY, 2));
            if (dragDistance > 5) {
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && isDragging) {
            isDragging = false;
            String targetKey = getKeyFromMousePos(mouseX, mouseY);
            if (targetKey != null && draggedSpellIndex >= 0 && draggedSpellIndex < spellList.size()) {
                bindSpellToKey(targetKey, spellList.get(draggedSpellIndex));
            }
            draggedSpellIndex = -1;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        int guiX = (this.width - GUI_WIDTH) / 2;
        int guiY = (this.height - GUI_HEIGHT) / 2;

        // 检查鼠标是否在技能列表区域内
        if (mouseX >= guiX && mouseX <= guiX + SKILL_BAR_WIDTH
                && mouseY >= guiY + 30 && mouseY <= guiY + GUI_HEIGHT - 45) {

            // 与PointScreen相同的滚动逻辑
            int scrollAmount = (int) (-amount * 20); // 滚动速度
            int newScrollY = Math.max(0, Math.min(maxScrollY, scrollY + scrollAmount));

            if (newScrollY != scrollY) {
                scrollY = newScrollY;

                // 更新基于项数的scrollOffset，用于兼容原有逻辑
                int itemsPerPage = Math.max(1, visibleHeight / (SKILL_ITEM_HEIGHT + SKILL_SPACING));
                scrollOffset = scrollY / (SKILL_ITEM_HEIGHT + SKILL_SPACING);

                // 确保selectedSpellIndex在可见区域内
                if (selectedSpellIndex < scrollOffset) {
                    selectedSpellIndex = scrollOffset;
                } else if (selectedSpellIndex >= scrollOffset + itemsPerPage) {
                    selectedSpellIndex = Math.min(spellList.size() - 1, scrollOffset + itemsPerPage - 1);
                }
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    // ========== 工具方法 ==========
    /**
     * 根据鼠标位置获取按键
     */
    private String getKeyFromMousePos(double mouseX, double mouseY) {
        int guiX = (this.width - GUI_WIDTH) / 2;
        int guiY = (this.height - GUI_HEIGHT) / 2;
        int bottomY = guiY + GUI_HEIGHT - 40;

        if (buttonPositionsX == null) return null;

        for (int i = 0; i < 4; i++) {
            if (i < buttonPositionsX.length) {
                int buttonX = buttonPositionsX[i];
                if (mouseX >= buttonX && mouseX <= buttonX + BUTTON_WIDTH
                        && mouseY >= bottomY && mouseY <= bottomY + BUTTON_HEIGHT) {
                    return SPELL_KEYS.get(i);
                }
            }
        }
        return null;
    }

    /**
     * 获取技能图标
     */
    public Identifier getSpellIconById(String spellId) {
        Identifier defaultIcon = Identifier.of(Soulstone.ID, "textures/gui/spell_default.png");
        if (spellId == null || spellId.isEmpty()) return defaultIcon;

        return Identifier.of(Soulstone.ID, "textures/gui/spell_" + spellId.toLowerCase() + ".png");
    }

    /**
     * 获取简短描述
     */
    private String getShortSpellDescById(String spellId) {
        return switch (spellId.toLowerCase()) {
            case "fire_blast" -> "火焰伤害";
            case "ice_shield" -> "防御护盾";
            case "thunder_bolt" -> "雷电攻击";
            case "heal" -> "治疗恢复";
            case "shield" -> "护盾保护";
            case "teleport" -> "瞬间移动";
            default -> "未知技能";
        };
    }

    /**
     * 获取完整描述
     */
    private String getSpellDescById(String spellId) {
        return switch (spellId.toLowerCase()) {
            case "fire_blast" -> "火焰冲击：发射火球，对目标造成20点火焰伤害，冷却15秒。消耗10法力值。";
            case "ice_shield" -> "寒冰护盾：5秒内免疫所有伤害，冷却60秒，消耗15法力值。";
            case "thunder_bolt" -> "雷电术：攻击3格内敌人，造成15点雷电伤害，冷却20秒。消耗12法力值。";
            case "heal" -> "治疗术：恢复自身20点生命值，冷却30秒。消耗15法力值。";
            case "shield" -> "护盾术：生成一个持续10秒的护盾，吸收最多50点伤害，冷却45秒。消耗20法力值。";
            case "teleport" -> "传送术：向前方传送最多8格距离，冷却25秒。消耗25法力值。";
            default -> "暂无技能描述。";
        };
    }

    /**
     * 获取绑定技能ID
     */
    public String getBoundSpellId(String key) {
        return keyBindMap.getOrDefault(key, "");
    }

    /**
     * 判断按键是否绑定
     */
    public boolean isKeyBound(String key) {
        // 检查本地映射
        boolean locallyBound = keyBindMap.containsKey(key) && !keyBindMap.get(key).isEmpty();

        // 如果可能，也从数据访问层检查
        if (this.client != null && this.client.player != null) {
            return SpellAccess.isKeyBound(this.client.player, key) || locallyBound;
        }

        return locallyBound;
    }

    // ========== 自定义按钮类 ==========
    public static class SkillBindButton extends ButtonWidget {
        private final String key;
        private final SpellGuiScreen parent;

        public SkillBindButton(int x, int y, String key, PressAction onPress, SpellGuiScreen parent) {
            super(x, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                    Text.literal(parent.getFriendlyKeyName(key)),
                    onPress,
                    DEFAULT_NARRATION_SUPPLIER);
            this.key = key;
            this.parent = parent;
        }

        @Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            // 按钮背景色
            int bgColor = parent.isKeyBound(key) ? 0xCC336633 : 0xCC333333;
            if (this.isHovered()) bgColor += 0x202020;

            context.fill(getX(), getY(), getX() + width, getY() + height, bgColor);
            context.drawBorder(getX(), getY(), width, height,
                    this.isHovered() ? 0xCCFFFFAA : 0xCCFFFFFF);

            // 技能图标
            String boundSpellId = parent.getBoundSpellId(key);
            Identifier spellIcon = parent.getSpellIconById(boundSpellId);
            int iconX = getX() + 5;
            int iconY = getY() + (height - BUTTON_ICON_SIZE) / 2;

            context.fill(iconX - 1, iconY - 1, iconX + BUTTON_ICON_SIZE + 1,
                    iconY + BUTTON_ICON_SIZE + 1, 0xCC222222);

            if (!boundSpellId.isEmpty()) {
                context.drawTexture(spellIcon, iconX, iconY, 0, 0,
                        BUTTON_ICON_SIZE, BUTTON_ICON_SIZE,
                        BUTTON_ICON_SIZE, BUTTON_ICON_SIZE);
            } else {
                context.drawTextWithShadow(parent.textRenderer, Text.literal("?"),
                        iconX + BUTTON_ICON_SIZE/2 - 2, iconY + BUTTON_ICON_SIZE/2 - 4,
                        0xAAAAAA);
            }

            // 按键文字
            String buttonText = parent.getFriendlyKeyName(key);
            int textColor = 0xFFFFFF;

            if (!boundSpellId.isEmpty()) {
                String shortSpellId = boundSpellId.length() > 6 ?
                        boundSpellId.substring(0, 6) + "..." : boundSpellId;
                buttonText += "\n" + shortSpellId;
                textColor = 0xAAFFAA;
            }

            List<OrderedText> textLines = parent.textRenderer.wrapLines(
                    Text.literal(buttonText), width - BUTTON_ICON_SIZE - 15);
            int textX = getX() + BUTTON_ICON_SIZE + 10;
            int textY = getY() + (height - (textLines.size() * parent.textRenderer.fontHeight)) / 2;

            for (int i = 0; i < textLines.size(); i++) {
                context.drawTextWithShadow(parent.textRenderer, textLines.get(i),
                        textX, textY + (i * parent.textRenderer.fontHeight), textColor);
            }

            // 悬停提示
            if (this.isHovered() && !boundSpellId.isEmpty()) {
                String spellName = boundSpellId;
                List<OrderedText> tooltipLines = parent.textRenderer.wrapLines(
                        Text.literal("已绑定: " + spellName), 150);
                int tooltipX = getX() + width + 5;
                int tooltipY = getY();
                int tooltipWidth = 160;
                int tooltipHeight = tooltipLines.size() * parent.textRenderer.fontHeight + 8;

                context.fill(tooltipX, tooltipY, tooltipX + tooltipWidth,
                        tooltipY + tooltipHeight, 0xDD000000);
                context.drawBorder(tooltipX, tooltipY, tooltipWidth, tooltipHeight, 0xFFFFFFFF);

                for (int i = 0; i < tooltipLines.size(); i++) {
                    context.drawTextWithShadow(parent.textRenderer, tooltipLines.get(i),
                            tooltipX + 4, tooltipY + 4 + (i * parent.textRenderer.fontHeight),
                            0xFFFFFF);
                }
            }
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}