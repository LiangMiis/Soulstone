package org.LiangMi.soulstone.client.hud.bar;





import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.other.BarHelper;

public class HealthBar {// 自定义HUD渲染器，用于显示玩家的生命值、吸收值和护甲值
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    // 定义各种健康条纹理的标识符
    private static final Identifier fullHealthBarLocation = new Identifier(Soulstone.ID, "textures/gui/healthbars/full.png");
    private static final Identifier emptyHealthBarLocation = new Identifier(Soulstone.ID, "textures/gui/healthbars/empty.png");
    private static final Identifier absorptionBarLocation = new Identifier(Soulstone.ID, "textures/gui/healthbars/absorption.png");
    private static final Identifier currentBarLocation = fullHealthBarLocation;
    private static final Identifier intermediateHealthBarLocation = new Identifier(Soulstone.ID, "textures/gui/healthbars/intermediate.png");
    private static final Identifier heart_full = new Identifier(Soulstone.ID, "textures/gui/healthbars/fullicon.png");
    // 是否使用独立图标的标志
    public static boolean isUseSeparateIcons = false;
    // 原版GUI图标位置
    private static final Identifier guiIconsLocation = new Identifier("minecraft", "textures/gui/icons.png");
    // 中间过渡生命值（用于平滑动画）
    private float intermediateHealth = 0;

    // 设置是否使用独立图标的方法
    public static void isUseSeparateIconsIDEA(boolean is) {
        isUseSeparateIcons = is;
    }

    // 主要的渲染方法
    public void render(DrawContext context, float tickDelta) {
        // 检查渲染条件：玩家存在、HUD未隐藏、有状态栏
        if (mc.cameraEntity instanceof PlayerEntity player && !mc.options.hudHidden && mc.interactionManager != null && mc.interactionManager.hasStatusBars()) {
            int width = mc.getWindow().getScaledWidth();
            int height = mc.getWindow().getScaledHeight();
            float x = (float) width / 12; // 计算X坐标
            float y = 0 + 23; // 计算Y坐标
            y += 4; // 微调Y坐标
            TextRenderer font = mc.textRenderer;
            // 更新健康条纹理（根据玩家状态）
            updateBarTextures(player);
            // 渲染健康条
            renderHealthBar(context, tickDelta, (int) x, (int) y, player);
            // 渲染健康值文本
            renderHealthValue(font, context, (int) x, (int) y, player);
        }
    }

    // 根据玩家状态更新健康条纹理（当前实现为空，但预留了状态判断）
    public void updateBarTextures(PlayerEntity player) {
        // 优先级：凋零效果 > 中毒效果 > 冰冻状态 > 默认状态
        if (player.hasStatusEffect(StatusEffects.WITHER)) {
            // 凋零效果时的纹理处理（待实现）
        } else if (player.hasStatusEffect(StatusEffects.POISON)) {
            // 中毒效果时的纹理处理（待实现）
        } else if (player.isFrozen()) {
            // 冰冻状态时的纹理处理（待实现）
        } else {
            // 默认状态的纹理处理（待实现）
        }
    }

    // 渲染健康值文本的方法
    private void renderHealthValue(TextRenderer font, DrawContext context, int x, int y, PlayerEntity player) {
        y += 1; // 微调Y坐标
        // 根据配置决定使用独立图标还是原版图标
        if (isUseSeparateIcons) {
            context.drawTexture(heart_full, x, y - 10, 0, 0, 7, 7, 7, 7);
        } else {
            context.drawTexture(guiIconsLocation, x, y - 10, 52, 0, 9, 9, 256, 256);
        }

        // 获取玩家的各种健康相关数值
        float MaxHealth = player.getMaxHealth();
        float Health = Math.min(player.getHealth(), MaxHealth);
        float Absorption = player.getAbsorptionAmount();
        float ARMOR = player.getArmor();
        int xx = x + 10;
        String text;

        // 如果有吸收效果，分别渲染生命值和吸收值
        if (Absorption > 0) {
            text = BarHelper.KeepOneDecimal(Health);
            context.drawText(font, text, xx, y - 9, 0x9B1D1D, false); // 生命值颜色
            xx = xx + font.getWidth(text);
            text = "+" + BarHelper.KeepOneDecimal(Absorption);
            context.drawText(font, text, xx, y - 9, 0x9C971E, false); // 吸收值颜色
            xx = xx + font.getWidth(text);
            text = "/" + BarHelper.KeepOneDecimal(MaxHealth);
            context.drawText(font, text, xx, y - 9, 0x9B1D1D, false);
        } else {
            // 没有吸收效果，只渲染生命值
            text = BarHelper.KeepOneDecimal(Health) + "/" + BarHelper.KeepOneDecimal(MaxHealth);
            context.drawText(font, text, x + 10, y - 9, 0x9B1D1D, false);
        }
    }

    // 渲染健康条的方法
    // 渲染健康条的方法，使用分段式纹理来构建健康条
    private void renderHealthBar(DrawContext context, float tickDelta, int x, int y, PlayerEntity player) {
        // 获取玩家的最大生命值和当前生命值
        float maxHealth = player.getMaxHealth();
        float health = Math.min(player.getHealth(), maxHealth);

        // 定义健康条的各个部分长度变量
        int leftHalf;
        int middle;
        int leftCorner = 4; // 左侧圆角的宽度
        int leftCornerStart = 0; // 左侧圆角在纹理中的起始位置
        int rightHalf;

        // 动态计算健康条总宽度（基于最大生命值）
        int totalBarWidth = 30 + (int) ((maxHealth - 20) * 3); // 20点基础血量对应80像素
        totalBarWidth = MathHelper.clamp(totalBarWidth, 60, 300); // 限制在80-200像素之间

        // 计算当前生命值比例
        float healthProportion = health/maxHealth;
        float intermediateProportion;

        // 定义右侧圆角参数
        int rightCornerWidth = 4; // 右侧圆角的宽度
        int rightCorner = totalBarWidth-rightCornerWidth; // 右侧圆角的位置

        // 计算健康条各部分的总长度
        int totalMiddle = totalBarWidth/2-5; // 中间部分总长度
        int totalLeftHalf = totalMiddle-leftCorner; // 左半部分总长度
        int totalRightHalf = totalBarWidth-rightCornerWidth; // 右半部分总长度

        // 计算各部分在纹理中的起始位置
        float leftHalfStart = leftCorner; // 左半部分在纹理中的起始位置
        int rightHalfStart = totalMiddle+10; // 右半部分在纹理中的起始位置

        // 确保中间过渡值不超过最大生命值
        if (intermediateHealth > maxHealth) {
            intermediateHealth = maxHealth;
        }

        // 计算中间过渡部分的比例
        if (health < intermediateHealth){
            intermediateProportion = (intermediateHealth-health)/maxHealth;
        }else{
            intermediateProportion = 0;
        }

        // 计算当前生命值的像素宽度
        int healthWidth = (int) Math.ceil(totalBarWidth * healthProportion);
        // 计算中间过渡部分的像素宽度
        int intermediateWidth = (int) Math.ceil(totalBarWidth * intermediateProportion);

        // 计算并渲染吸收条（如果有吸收效果）
        float absorption = Math.min(player.getAbsorptionAmount(), maxHealth);
        float absorptionProportion = absorption / maxHealth;
        if (absorptionProportion > 1) absorptionProportion = 1F; // 确保比例不超过1
        int absorptionWidth = (int) Math.ceil(totalBarWidth * absorptionProportion);

        // 根据当前生命值宽度计算左半部分和中间部分的实际长度
        if(healthWidth>=totalLeftHalf){
            leftHalf = totalLeftHalf; // 左半部分完全填充
            middle = healthWidth-totalLeftHalf; // 中间部分部分填充
            if(healthWidth>=10){
                middle = 10; // 中间部分最大长度为10
            }
        }else{
            leftHalf = healthWidth; // 左半部分部分填充
            middle = 0; // 中间部分不填充
        }

        // 根据当前生命值宽度计算右半部分的实际长度
        if(healthWidth>=totalRightHalf){
            rightHalf = totalLeftHalf; // 右半部分完全填充
        }else{
            rightHalf = healthWidth-totalBarWidth/2-10; // 右半部分部分填充
        }

        // 渲染左侧圆角部分
        if(healthWidth<=leftCorner){
            // 如果生命值不足以填充左侧圆角，渲染空白圆角
            context.drawTexture(
                    emptyHealthBarLocation,
                    x,y,
                    0,0,
                    leftCorner,5,
                    300,5
            );
        }else {
            // 如果生命值足以填充左侧圆角，渲染填充圆角
            context.drawTexture(
                    fullHealthBarLocation,
                    x,y,
                    0,0,
                    leftCorner,5,
                    300,5
            );
        }

        // 渲染左半部分
        if(healthWidth <= totalLeftHalf){
            // 如果生命值不足以填充整个左半部分，先渲染空白部分
            context.drawTexture(
                    emptyHealthBarLocation,
                    x+leftCorner,y,
                    leftHalfStart,0,
                    totalLeftHalf,5,
                    300,5
            );
        }

        // 渲染左半部分的填充部分
        context.drawTexture(
                fullHealthBarLocation,
                x+leftCorner,y,
                leftHalfStart,0,
                leftHalf,5,
                300,5
        );

        // 渲染中间部分
        if(healthWidth <= totalMiddle+10){
            // 如果生命值不足以填充中间部分，先渲染空白部分
            context.drawTexture(
                    emptyHealthBarLocation,
                    x+leftCorner+totalLeftHalf,y,
                    145,0,
                    10,5,
                    300,5
            );
        }

        // 渲染中间部分的填充部分
        context.drawTexture(
                fullHealthBarLocation,
                x+leftCorner+totalLeftHalf,y,
                145,0,
                middle,5,
                300,5
        );

        // 渲染右半部分
        if(healthWidth <= totalRightHalf){
            // 如果生命值不足以填充整个右半部分，先渲染空白部分
            context.drawTexture(
                    emptyHealthBarLocation,
                    x+leftCorner+totalLeftHalf+10,y,
                    rightHalfStart,0,
                    totalLeftHalf,5,
                    300,5
            );
        }

        // 渲染右半部分的填充部分
        context.drawTexture(
                fullHealthBarLocation,
                x+leftCorner+totalLeftHalf+10,y,
                rightHalfStart,0,
                rightHalf,5,
                300,5
        );

        // 渲染右侧圆角部分
        if(healthWidth<=rightCorner){
            // 如果生命值不足以填充右侧圆角，渲染空白圆角
            context.drawTexture(
                    emptyHealthBarLocation,
                    x+leftCorner+totalLeftHalf*2+10,y,
                    296,0,
                    rightCornerWidth,5,
                    300,5
            );
        }else {
            // 如果生命值足以填充右侧圆角，渲染填充圆角
            context.drawTexture(
                    fullHealthBarLocation,
                    x+leftCorner+totalLeftHalf*2+10,y,
                    296,0,
                    rightCornerWidth,5,
                    300,5
            );
        }

        // 更新中间生命值（平滑过渡效果）
        this.intermediateHealth += (health - intermediateHealth) * tickDelta * 0.08;
        if (Math.abs(health - intermediateHealth) <= 0.25) {
            this.intermediateHealth = health;
        }
    }
}
