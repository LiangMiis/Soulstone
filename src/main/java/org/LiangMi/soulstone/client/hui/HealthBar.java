package org.LiangMi.soulstone.client.hui;





import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.other.helper;

public class HealthBar {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Identifier fullHealthBarLocation = new Identifier(Soulstone.ID,"textures/gui/healthbars/full.png");
    private static final Identifier emptyHealthBarLocation = new Identifier(Soulstone.ID,"textures/gui/healthbars/empty.png");

    private static final Identifier absorptionBarLocation = new Identifier(Soulstone.ID,"textures/gui/healthbars/absorption.png");
    private static final Identifier currentBarLocation = fullHealthBarLocation;
    private static final Identifier intermediateHealthBarLocation = new Identifier(Soulstone.ID, "textures/gui/healthbars/intermediate.png");
    private static final Identifier heart_full = new Identifier(Soulstone.ID,"textures/gui/healthbars/fullicon.png");
    public static boolean isUseSeparateIcons = false;
    private static final Identifier guiIconsLocation = new Identifier("minecraft","textures/gui/icons.png");
    private float intermediateHealth =0;

    public static void isUseSeparateIconsIDEA(boolean is) {isUseSeparateIcons = is;}

    public void render(DrawContext context,float tickDelta){
        if(mc.cameraEntity instanceof PlayerEntity player && !mc.options.hudHidden && mc.interactionManager != null && mc.interactionManager.hasStatusBars()){
            int width = mc.getWindow().getScaledWidth();
            int height = mc.getWindow().getScaledHeight();
            float x = (float) width /12;
            float y = 0+23;
            y+=4;
            TextRenderer font = mc.textRenderer;
            updateBarTextures(player);
            renderHealthBar(context,tickDelta,(int) x,(int) y,player);
            renderHealthValue(font,context,(int) x,(int)y,player);
        }
    }
    public void updateBarTextures(PlayerEntity player) {
        // 优先级：凋零效果 > 中毒效果 > 冰冻状态 > 默认状态
        if (player.hasStatusEffect(StatusEffects.WITHER)) {

        } else if (player.hasStatusEffect(StatusEffects.POISON)) {

        } else if (player.isFrozen()) {

        } else {

        }
    }
    private void renderHealthValue(TextRenderer font,DrawContext context,int x,int y,PlayerEntity player){
        y+= 1;
        if(isUseSeparateIcons){
            context.drawTexture(heart_full,x,y-10,0,0,7,7,7,7);
        }else {
            context.drawTexture(guiIconsLocation,x,y-10,52,0,9,9,256,256);
        }
        float MaxHealth = player.getMaxHealth();
        float Health = Math.min(player.getHealth(),MaxHealth);
        float Absorption = player.getAbsorptionAmount();
        float ARMOR = player.getArmor();
        int xx = x + 10;
        String text;
        if(Absorption>0){
            text = helper.KeepOneDecimal(Health);
            context.drawText(font,text,xx,y-9,0x9B1D1D,false);
            xx = xx + font.getWidth(text);
            text = "+" + helper.KeepOneDecimal(Absorption);
            context.drawText(font,text,xx,y-9,0x9C971E,false);
            xx = xx + font.getWidth(text);
            text = "/" + helper.KeepOneDecimal(MaxHealth);
            context.drawText(font,text,xx,y-9,0x9B1D1D,false);
        } else {
            text = helper.KeepOneDecimal(Health) + "/" + helper.KeepOneDecimal(MaxHealth);
            context.drawText(font,text,x+10,y-9,0x9B1D1D,false);
        }
    }
    private void renderHealthBar(DrawContext context,float tickDelta,int x,int y,PlayerEntity player){
        float maxHealth = player.getMaxHealth();
        float health = Math.min(player.getHealth(),maxHealth);

        int totalBarWidth = 30 + (int)((maxHealth - 20) * 3); // 20点基础血量对应80像素
        totalBarWidth = MathHelper.clamp(totalBarWidth, 30, 300); // 限制在80-200像素之间

        float healthProportion;
        float intermediateProportion;

        int healthLong;

        if (intermediateHealth > maxHealth){
            intermediateHealth = maxHealth;
        }
        if (health < intermediateHealth){
            intermediateProportion = (intermediateHealth-health)/maxHealth;
        }else{
            intermediateProportion = 0;
        }

        healthProportion = health / maxHealth;

        // 按比例计算各部分宽度（基于动态总长度）
        int healthWidth = (int) Math.ceil(totalBarWidth * healthProportion);
        int intermediateWidth = (int) Math.ceil(totalBarWidth * intermediateProportion);

        if(healthWidth<=295){
            healthLong=5;
        }else{
            healthLong=300-healthWidth;
        }
        // 计算并渲染吸收条（如果有吸收效果）
        float absorption = Math.min(player.getAbsorptionAmount(), maxHealth);
        float absorptionProportion = absorption / maxHealth;
        if (absorptionProportion > 1) absorptionProportion = 1F; // 确保比例不超过1
        int absorptionWidth = (int) Math.ceil(totalBarWidth * absorptionProportion);



        // 渲染空白部分（右侧未填充）
        context.drawTexture(
                emptyHealthBarLocation,
                x + healthWidth + intermediateWidth-healthLong, y, // 目标位置
                healthWidth + intermediateWidth, 0,     // 纹理起点
                totalBarWidth - healthWidth - intermediateWidth, 5, // 渲染宽度
                300, 5                                   // 纹理尺寸（固定）
        );

        // 渲染基础健康条（当前生命值）
        context.drawTexture(
                currentBarLocation,
                x, y,                                   // 目标位置
                0, 0,                                   // 纹理起点
                healthWidth-healthLong, 5,                          // 渲染宽度
                300, 5                                    // 纹理尺寸（固定）
        );
        if (absorption > 0) {
            // 渲染吸收效果条（覆盖在基础健康条上）
            context.drawTexture(
                    absorptionBarLocation,
                    x, y,
                    0, 0,
                    absorptionWidth, 5,
                    300, 5);
        }
        // 渲染中间过渡部分（动态变化部分）
        context.drawTexture(
                intermediateHealthBarLocation,
                x + healthWidth-healthLong, y,                      // 目标位置（接在当前生命值后）
                healthWidth, 0,                          // 纹理起点
                intermediateWidth, 5,                    // 渲染宽度
                300, 5                                    // 纹理尺寸（固定）
        );
        if(totalBarWidth - healthWidth - intermediateWidth == 0){
            //渲染右侧小角
            context.drawTexture(
                    currentBarLocation,
                    x+totalBarWidth -healthLong, y,                                   // 目标位置
                    295, 0,                                   // 纹理起点
                    healthLong, 5,                          // 渲染宽度
                    300, 5                                    // 纹理尺寸（固定）
            );
        }else {
            context.drawTexture(
                    emptyHealthBarLocation,
                    x+totalBarWidth -healthLong, y, // 目标位置
                    295, 0,     // 纹理起点
                    healthLong, 5, // 渲染宽度
                    300, 5                                   // 纹理尺寸（固定）
            );
        }



        // 更新中间生命值（平滑过渡效果）
        this.intermediateHealth += (health - intermediateHealth) * tickDelta * 0.08;
        if (Math.abs(health - intermediateHealth) <= 0.25) {
            this.intermediateHealth = health;
        }
    }

}
