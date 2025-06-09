package org.LiangMi.soulstone.client.hui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.api.ManaInstance;
import org.LiangMi.soulstone.api.ManaInterface;
import org.LiangMi.soulstone.other.helper;

public class ManaBar {
    private static final Identifier fullManaBarLocation = new Identifier(Soulstone.ID,"textures/gui/manabars/full.png");
    private static final Identifier emptyManaBarLocation = new Identifier(Soulstone.ID,"textures/gui/manabars/empty.png");
    private static final Identifier intermediateManaBarLocation = new Identifier(Soulstone.ID, "textures/gui/manabars/intermediate.png");
    private static final Identifier mana_full = new Identifier(Soulstone.ID,"textures/gui/manabars/manaicon.png");
    private static final Identifier currentBarLocation = fullManaBarLocation;
    public static boolean isUseSeparateIcons = false;
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Identifier guiIconsLocation = new Identifier("minecraft","textures/gui/icons.png");

    private float intermediateMana =0;
    public static void isUseSeparateIconsIDEA(boolean is) {isUseSeparateIcons = is;}
    public void render(DrawContext context, float tickDelta){
        if(mc.cameraEntity instanceof PlayerEntity player && !mc.options.hudHidden && mc.interactionManager != null && mc.interactionManager.hasStatusBars()){
            int width = mc.getWindow().getScaledWidth();
            int height = mc.getWindow().getScaledHeight();
            float x = (float) width /12;
            float y = 0+43;
            y+=4;
            TextRenderer font = mc.textRenderer;
            renderManaBar(context,tickDelta,(int) x,(int) y,player);
            renderManaValue(font,context,(int) x,(int)y,player);
        }
    }

    private void renderManaValue(TextRenderer font, DrawContext context, int x, int y, PlayerEntity player) {
        ManaInterface manaInterface = (ManaInterface) player;
        y+= 1;
        if(isUseSeparateIcons){
            context.drawTexture(mana_full,x,y-10,0,0,7,7,7,7);
        }else {
            context.drawTexture(guiIconsLocation,x,y-10,52,0,9,9,256,256);
        }
        float MaxMana = (float) manaInterface.getMaxMana();
        float Mana = (float) manaInterface.getMana();
        String text;
        text = helper.KeepOneDecimal(Mana) + "/" + helper.KeepOneDecimal(MaxMana);
        context.drawText(font,text,x+10,y-9,0x4D6090,false);
    }

    private void renderManaBar(DrawContext context, float tickDelta, int x, int y, PlayerEntity player) {
        ManaInterface manaInterface = (ManaInterface) player;
        float maxMana = (float) manaInterface.getMaxMana();
        float mana = (float) manaInterface.getMana();

        int totalBarWidth = 30 + (int)((maxMana - 20) * 1); // 20点基础血量对应80像素
        totalBarWidth = MathHelper.clamp(totalBarWidth, 30, 300); // 限制在80-200像素之间

        float manaProportion;
        float intermediateProportion;

        int manaLong;

        if (intermediateMana > maxMana){
            intermediateMana = maxMana;
        }
        if (mana < intermediateMana){
            intermediateProportion = (intermediateMana-mana)/maxMana;
        }else{
            intermediateProportion = 0;
        }

        manaProportion = mana / maxMana;

        // 按比例计算各部分宽度（基于动态总长度）
        int healthWidth = (int) Math.ceil(totalBarWidth * manaProportion);
        int intermediateWidth = (int) Math.ceil(totalBarWidth * intermediateProportion);

        if(healthWidth<=295){
            manaLong=5;
        }else{
            manaLong=300-healthWidth;
        }
        // 计算并渲染吸收条（如果有吸收效果）
        float absorption = Math.min(player.getAbsorptionAmount(), maxMana);
        float absorptionProportion = absorption / maxMana;
        if (absorptionProportion > 1) absorptionProportion = 1F; // 确保比例不超过1
        int absorptionWidth = (int) Math.ceil(totalBarWidth * absorptionProportion);



        // 渲染空白部分（右侧未填充）
        context.drawTexture(
                emptyManaBarLocation,
                x + healthWidth + intermediateWidth-manaLong, y, // 目标位置
                healthWidth + intermediateWidth, 0,     // 纹理起点
                totalBarWidth - healthWidth - intermediateWidth, 5, // 渲染宽度
                300, 5                                   // 纹理尺寸（固定）
        );

        // 渲染基础健康条（当前生命值）
        context.drawTexture(
                currentBarLocation,
                x, y,                                   // 目标位置
                0, 0,                                   // 纹理起点
                healthWidth-manaLong, 5,                          // 渲染宽度
                300, 5                                    // 纹理尺寸（固定）
        );
        // 渲染中间过渡部分（动态变化部分）
        context.drawTexture(
                intermediateManaBarLocation,
                x + healthWidth-manaLong, y,                      // 目标位置（接在当前生命值后）
                healthWidth, 0,                          // 纹理起点
                intermediateWidth, 5,                    // 渲染宽度
                300, 5                                    // 纹理尺寸（固定）
        );
        if(totalBarWidth - healthWidth - intermediateWidth == 0){
            //渲染右侧小角
            context.drawTexture(
                    currentBarLocation,
                    x+totalBarWidth -manaLong, y,                                   // 目标位置
                    295, 0,                                   // 纹理起点
                    manaLong, 5,                          // 渲染宽度
                    300, 5                                    // 纹理尺寸（固定）
            );
        }else {
            context.drawTexture(
                    emptyManaBarLocation,
                    x+totalBarWidth -manaLong, y, // 目标位置
                    295, 0,     // 纹理起点
                    manaLong, 5, // 渲染宽度
                    300, 5                                   // 纹理尺寸（固定）
            );
        }



        // 更新中间生命值（平滑过渡效果）
        this.intermediateMana += (mana - intermediateMana) * tickDelta * 0.08;
        if (Math.abs(mana - intermediateMana) <= 0.25) {
            this.intermediateMana = mana;
        }
    }

}
