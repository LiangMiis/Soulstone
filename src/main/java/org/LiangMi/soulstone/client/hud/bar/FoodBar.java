package org.LiangMi.soulstone.client.hud.bar;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.other.BarHelper;

public class FoodBar {
    // 定义魔力条纹理的标识符
    private static final Identifier fullFoodBarLocation = new Identifier(Soulstone.ID,"textures/gui/foodbars/full.png");
    private static final Identifier emptyFoodBarLocation = new Identifier(Soulstone.ID,"textures/gui/foodbars/empty.png");
    private static final Identifier intermediateManaBarLocation = new Identifier(Soulstone.ID, "textures/gui/foodbars/intermediate.png");
    private static final Identifier food_full = new Identifier(Soulstone.ID,"textures/gui/foodbars/icon.png");
    private static final Identifier currentBarLocation = fullFoodBarLocation;
    private static final Identifier armor_full = new Identifier(Soulstone.ID,"textures/gui/foodbars/armor.png");

    // 是否使用独立图标的标志
    public static boolean isUseSeparateIcons = false;

    // Minecraft客户端实例
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    // 原版GUI图标位置
    private static final Identifier guiIconsLocation = new Identifier("minecraft","textures/gui/icons.png");

    // 中间过渡魔力值（用于平滑动画）
    private float intermediateMana =0;

    // 设置是否使用独立图标的方法
    public static void isUseSeparateIconsIDEA(boolean is) {isUseSeparateIcons = is;}

    // 主要的渲染方法
    public void render(DrawContext context, float tickDelta){
        // 检查渲染条件：玩家存在、HUD未隐藏、有状态栏
        if(mc.cameraEntity instanceof PlayerEntity player && !mc.options.hudHidden && mc.interactionManager != null && mc.interactionManager.hasStatusBars()){
            int width = mc.getWindow().getScaledWidth();
            int height = mc.getWindow().getScaledHeight();
            float x = (float) width /12; // 计算X坐标
            float y = 0 + 55; // 计算Y坐标（比生命值条低20像素）
            y+=4; // 微调Y坐标
            TextRenderer font = mc.textRenderer;
            HungerManager FoodData = player.getHungerManager();
            // 渲染魔力条
            renderFoodBar(context,tickDelta,(int) x,(int) y,player,FoodData);
            // 渲染魔力值文本
            renderFoodValue(font,context,(int) x,(int)y,player,FoodData);
        }
    }

    // 渲染魔力值文本的方法
    private void renderFoodValue(TextRenderer font, DrawContext context, int x, int y, PlayerEntity player, HungerManager FoodData) {

        y+= 1; // 微调Y坐标

        // 根据配置决定使用独立图标还是原版图标
        if(isUseSeparateIcons){
            context.drawTexture(food_full,x,y-10,0,0,7,7,7,7);
        }else {
            context.drawTexture(guiIconsLocation,x,y-10,52,0,9,9,256,256);
        }

        // 获取玩家的最大魔力和当前魔力
        float maxFood = FoodData.getPrevFoodLevel();
        float Food = Math.min(FoodData.getFoodLevel(), maxFood);
        String text;


        // 渲染魔力值文本（格式：当前值/最大值）
        text = BarHelper.KeepOneDecimal(Food) + "/" + BarHelper.KeepOneDecimal(maxFood);
        context.drawText(font,text,x+10,y-9,0xa87322,false); // 使用蓝色调表示魔力

        float maxAnchor = 100;

        float ARMORTOUGHNESS = (float) player.getAttributeValue(EntityAttributes.GENERIC_ARMOR);
                context.drawTexture(armor_full,
                        x, y + 5,
                        0, 0,
                        9, 9,
                        9, 9); // 韧性图标

            context.drawText(font, BarHelper.KeepOneDecimal(ARMORTOUGHNESS), x + 10, y + 4, 0x87CEEB, false);

    }

    // 渲染魔力条的方法
    private void renderFoodBar(DrawContext context, float tickDelta, int x, int y, PlayerEntity player, HungerManager FoodData) {
        // 获取玩家的最大生命值和当前生命值
        float maxFood = FoodData.getPrevFoodLevel();
        float Food = Math.min(FoodData.getFoodLevel(), maxFood);
        float saturationProportion = FoodData.getSaturationLevel() / maxFood;

        // 定义健康条的各个部分长度变量
        int leftHalf;
        int middle;
        int leftCorner = 4; // 左侧圆角的宽度
        int leftCornerStart = 0; // 左侧圆角在纹理中的起始位置
        int rightHalf;

        // 动态计算健康条总宽度（基于最大生命值）
        int totalBarWidth = 30 + (int) ((maxFood - 50) * 3); // 20点基础血量对应80像素
        totalBarWidth = MathHelper.clamp(totalBarWidth, 60, 300); // 限制在80-200像素之间

        // 计算当前生命值比例
        float healthProportion = Food/maxFood;
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
        if (intermediateMana > maxFood) {
            intermediateMana = maxFood;
        }

        // 计算中间过渡部分的比例
        if (Food < intermediateMana){
            intermediateProportion = (intermediateMana-Food)/maxFood;
        }else{
            intermediateProportion = 0;
        }

        // 计算当前生命值的像素宽度
        int healthWidth = (int) Math.ceil(totalBarWidth * healthProportion);
        // 计算中间过渡部分的像素宽度
        int intermediateWidth = (int) Math.ceil(totalBarWidth * intermediateProportion);

        // 计算并渲染吸收条（如果有吸收效果）
        float absorption = Math.min(player.getAbsorptionAmount(), maxFood);
        float absorptionProportion = absorption / maxFood;
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
                    emptyFoodBarLocation,
                    x,y,
                    0,0,
                    leftCorner,5,
                    300,5
            );
        }else {
            // 如果生命值足以填充左侧圆角，渲染填充圆角
            context.drawTexture(
                    fullFoodBarLocation,
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
                    emptyFoodBarLocation,
                    x+leftCorner,y,
                    leftHalfStart,0,
                    totalLeftHalf,5,
                    300,5
            );
        }

        // 渲染左半部分的填充部分
        context.drawTexture(
                fullFoodBarLocation,
                x+leftCorner,y,
                leftHalfStart,0,
                leftHalf,5,
                300,5
        );

        // 渲染中间部分
        if(healthWidth <= totalMiddle+10){
            // 如果生命值不足以填充中间部分，先渲染空白部分
            context.drawTexture(
                    emptyFoodBarLocation,
                    x+leftCorner+totalLeftHalf,y,
                    145,0,
                    10,5,
                    300,5
            );
        }

        // 渲染中间部分的填充部分
        context.drawTexture(
                fullFoodBarLocation,
                x+leftCorner+totalLeftHalf,y,
                145,0,
                middle,5,
                300,5
        );

        // 渲染右半部分
        if(healthWidth <= totalRightHalf){
            // 如果生命值不足以填充整个右半部分，先渲染空白部分
            context.drawTexture(
                    emptyFoodBarLocation,
                    x+leftCorner+totalLeftHalf+10,y,
                    rightHalfStart,0,
                    totalLeftHalf,5,
                    300,5
            );
        }

        // 渲染右半部分的填充部分
        context.drawTexture(
                fullFoodBarLocation,
                x+leftCorner+totalLeftHalf+10,y,
                rightHalfStart,0,
                rightHalf,5,
                300,5
        );

        // 渲染右侧圆角部分
        if(healthWidth<=rightCorner){
            // 如果生命值不足以填充右侧圆角，渲染空白圆角
            context.drawTexture(
                    emptyFoodBarLocation,
                    x+leftCorner+totalLeftHalf*2+10,y,
                    296,0,
                    rightCornerWidth,5,
                    300,5
            );
        }else {
            // 如果生命值足以填充右侧圆角，渲染填充圆角
            context.drawTexture(
                    fullFoodBarLocation,
                    x+leftCorner+totalLeftHalf*2+10,y,
                    296,0,
                    rightCornerWidth,5,
                    300,5
            );
        }
//        // 通过ManaInterface接口获取玩家的魔力值
//        ManaInterface manaInterface = (ManaInterface) player;
//        float maxMana = (float) manaInterface.getMaxMana();
//        float mana = (float) manaInterface.getMana();
//
//        // 动态计算魔力条总宽度（基于最大魔力值）
//        int totalBarWidth = 30 + (int)((maxMana - 20) * 1); // 20点基础魔力对应30像素
//        totalBarWidth = MathHelper.clamp(totalBarWidth, 30, 300); // 限制在30-300像素之间
//
//        float manaProportion;
//        float intermediateProportion;
//        int manaLong;
//
//        // 确保中间过渡值不超过最大魔力值
//        if (intermediateMana > maxMana){
//            intermediateMana = maxMana;
//        }
//
//        // 计算中间过渡部分的比例
//        if (mana < intermediateMana){
//            intermediateProportion = (intermediateMana-mana)/maxMana;
//        }else{
//            intermediateProportion = 0;
//        }
//
//        // 计算当前魔力值比例
//        manaProportion = mana / maxMana;
//
//        // 按比例计算各部分宽度（基于动态总长度）
//        int healthWidth = (int) Math.ceil(totalBarWidth * manaProportion);
//        int intermediateWidth = (int) Math.ceil(totalBarWidth * intermediateProportion);
//
//        // 计算魔力条末端特殊处理的长度
//        if(healthWidth<=295){
//            manaLong=5;
//        }else{
//            manaLong=300-healthWidth;
//        }
//
//        // 渲染空白部分（右侧未填充）
//        context.drawTexture(
//                emptyManaBarLocation,
//                x + healthWidth + intermediateWidth-manaLong, y, // 目标位置
//                healthWidth + intermediateWidth, 0,     // 纹理起点
//                totalBarWidth - healthWidth - intermediateWidth, 5, // 渲染宽度
//                300, 5                                   // 纹理尺寸（固定）
//        );
//
//        // 渲染基础魔力条（当前魔力值）
//        context.drawTexture(
//                currentBarLocation,
//                x, y,                                   // 目标位置
//                0, 0,                                   // 纹理起点
//                healthWidth-manaLong, 5,                          // 渲染宽度
//                300, 5                                    // 纹理尺寸（固定）
//        );
//
//        // 渲染中间过渡部分（动态变化部分）
//        context.drawTexture(
//                intermediateManaBarLocation,
//                x + healthWidth-manaLong, y,                      // 目标位置（接在当前魔力值后）
//                healthWidth, 0,                          // 纹理起点
//                intermediateWidth, 5,                    // 渲染宽度
//                300, 5                                    // 纹理尺寸（固定）
//        );
//
//        // 处理魔力条末端的特殊渲染（圆角或方角）
//        if(totalBarWidth - healthWidth - intermediateWidth == 0){
//            // 渲染右侧小角（当魔力条完全填满时）
//            context.drawTexture(
//                    currentBarLocation,
//                    x+totalBarWidth -manaLong, y,                                   // 目标位置
//                    295, 0,                                   // 纹理起点
//                    manaLong, 5,                          // 渲染宽度
//                    300, 5                                    // 纹理尺寸（固定）
//            );
//        }else {
//            // 渲染右侧空白部分的小角
//            context.drawTexture(
//                    emptyManaBarLocation,
//                    x+totalBarWidth -manaLong, y, // 目标位置
//                    295, 0,     // 纹理起点
//                    manaLong, 5, // 渲染宽度
//                    300, 5                                   // 纹理尺寸（固定）
//            );
//        }

        // 更新中间魔力值（平滑过渡效果）
        this.intermediateMana += (Food - intermediateMana) * tickDelta * 0.08;
        if (Math.abs(Food - intermediateMana) <= 0.25) {
            this.intermediateMana = Food;
        }
    }
}
