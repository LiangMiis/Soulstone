package org.LiangMi.soulstone.item.trinkets;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.LiangMi.soulstone.client.glint.GlintRenderTypes;
import org.LiangMi.soulstone.item.ModItems;

import java.util.List;

public class TrinketBass extends TrinketItem {
    private final String tooltip;       // 戒指提示文本


    public TrinketBass(String tooltip) {
        // 调用父类构造函数，设置最大堆叠数为1
        super(new Settings().maxCount(1));
        this.tooltip = tooltip;
        // 将当前戒指添加到所有戒指列表中
        ModItems.allTrinket.add(this);
    }
    // 设置物品不可附魔
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    // 设置物品不可修复
    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return false;
    }


    // 获取物品名称（添加淡紫色格式）
    @Override
    public Text getName(ItemStack stack) {
        return super.getName().copy().formatted(Formatting.LIGHT_PURPLE);
    }

    // 添加物品提示信息
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        // 添加装备槽位提示
        tooltip.add(
                Text.translatable("tooltip.ringsofascension.slot").formatted(Formatting.GRAY)
                        .append(Text.translatable("tooltip.ringsofascension.ring").formatted(Formatting.YELLOW)));

        // 添加空行分隔
        tooltip.add(Text.literal(""));
        // 添加佩戴效果提示
        tooltip.add(Text.translatable("tooltip.ringsofascension.worn").formatted(Formatting.GRAY));

        // 如果没有特定提示文本，则直接返回
        if(this.tooltip == null) return;

        // 添加特定提示文本
        tooltip.add(Text.translatable(this.tooltip).formatted(Formatting.BLUE));
    }


}

