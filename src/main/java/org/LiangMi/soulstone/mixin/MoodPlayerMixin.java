package org.LiangMi.soulstone.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.LiangMi.soulstone.access.MoodAccess;
import org.LiangMi.soulstone.data.PlayerMoodData;
import org.LiangMi.soulstone.event.MoodEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class MoodPlayerMixin {
    @Inject(method = "applyDamage", at = @At("HEAD"))
    private void onApplyDamage(DamageSource source, float amount, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (!player.getWorld().isClient && player instanceof ServerPlayerEntity && amount > 0) {
            PlayerMoodData moodData = MoodAccess.getPlayerMood(player);
            float moodLoss = Math.min(amount * 5.0f, 20.0f); // 最多损失20点心情
            moodData.removeMood(moodLoss);
            MoodAccess.setPlayerMood(player, moodData);

            // 立即更新属性
            org.LiangMi.soulstone.system.MoodAttributeSystem.updatePlayerAttributes((ServerPlayerEntity) player);

            // 可选：发送心情变化消息
            if (moodLoss > 5.0f) {
                player.sendMessage(net.minecraft.text.Text.literal("§c你感到疼痛，心情变差了..."), true);
            }
        }
    }
    @Inject(method = "eatFood", at = @At("TAIL"))
    private void onEatFood(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        // 当玩家吃完食物时调用
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (!world.isClient && player instanceof ServerPlayerEntity && stack.getItem().isFood()) {
            MoodEventHandler.onFoodEaten((ServerPlayerEntity) player, stack);
        }
    }
}
