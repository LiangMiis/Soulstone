package org.LiangMi.soulstone.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellRegistry;
import net.spell_engine.internals.casting.SpellCast;
import org.LiangMi.soulstone.api.ManaInterface;
import org.LiangMi.soulstone.api.SpellcostMixinInterface;
import org.LiangMi.soulstone.client.hud.Messages;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

// 使用Mixin技术将此类混入到SpellHelper类中
@Mixin(SpellHelper.class)
public class SpellHelperMixin {
    // 计算法术的魔力消耗
    private static float getManaCost(PlayerEntity player, Spell spell, SpellCast.Action action) {
        // 通过接口转换获取法术消耗值
        return ((SpellcostMixinInterface) spell.cost).getManaCost();
    }

    /**
     * 注入到法术施法尝试逻辑中
     * @param player 施法玩家实体
     * @param itemStack 使用的物品栈
     * @param spellId 法术标识符
     * @param cir 回调信息，用于返回尝试结果
     */
    @Inject(
            method = "attemptCasting(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Identifier;)Lnet/spell_engine/internals/casting/SpellCast$Attempt;",
            at = @At(value = "INVOKE", target = "Lnet/spell_engine/internals/SpellHelper;attemptCasting(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Identifier;Z)Lnet/spell_engine/internals/casting/SpellCast$Attempt;"),
            cancellable = true
    )
    private static void attempCasting(PlayerEntity player, ItemStack itemStack, Identifier spellId, CallbackInfoReturnable<SpellCast.Attempt> cir) {
        // 从注册表获取法术对象
        Spell spell = SpellRegistry.getSpell(spellId);
        // 创建消息对象（暂未使用）
        Messages messages = new Messages();

        // 检查玩家是否实现魔力接口
        if (player instanceof ManaInterface manaInterface) {
            // 验证玩家魔力是否不足
            if (manaInterface.getMana() <= ((SpellcostMixinInterface) spell.cost).getManaCost()) {
                // 取消施法并返回"none"状态
                cir.setReturnValue(SpellCast.Attempt.none());
            }
        }
    }

    /**
     * 注入到法术执行逻辑末尾
     * @param world 游戏世界
     * @param player 施法玩家
     * @param spellId 法术ID
     * @param targets 法术目标实体列表
     * @param action 施法动作类型
     * @param progress 施法进度
     * @param info 回调信息
     */
    @Inject(
            at = @At(value = "TAIL"),
            method = "performSpell"
    )
    private static void performSpell(World world, PlayerEntity player, Identifier spellId, List<Entity> targets, SpellCast.Action action, float progress, CallbackInfo info) {
        // 从注册表获取法术对象
        Spell spell = SpellRegistry.getSpell(spellId);
        // 检测是否为释放动作
        boolean released = action == SpellCast.Action.RELEASE;

        if (released) {
            // 检查玩家是否实现魔力接口
            if (player instanceof ManaInterface manaInterface) {
                // 验证魔力充足且非负
                if (manaInterface.getMana() >= 0 && manaInterface.getMana() >= ((SpellcostMixinInterface) spell.cost).getManaCost()) {
                    // 扣除魔力（负号表示消耗）
                    manaInterface.spendMana(-getManaCost(player, spell, action));
                }
            }
        }
    }
}
