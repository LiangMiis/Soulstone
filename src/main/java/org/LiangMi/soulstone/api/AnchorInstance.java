package org.LiangMi.soulstone.api;

import net.minecraft.entity.player.PlayerEntity;

public class AnchorInstance {
    public double remainingduration = 0;
    public double value = 0;
    public double remainingvalue = 0;
    public double duration = 4;
    public PlayerEntity player = null;
    public AnchorInstance(PlayerEntity player, double duration, double value) {
        this.player = player;           // 设置目标玩家
        this.duration = duration;       // 设置总持续时间S
        this.value = value;             // 设置魔力变化总量
        this.remainingvalue = value;    // 初始化剩余变化量（等于总量）
        this.remainingduration = duration; // 初始化剩余持续时间
    }
    public void tick() {
        // 检查效果是否仍在持续
        if (this.remainingduration > 0) {
            // 计算本tick应执行的效果量：总量/持续时间
            double tickValue = this.value / duration;

            // 更新剩余效果量
            this.remainingvalue -= tickValue;

            // 减少剩余持续时间
            this.remainingduration--;
        }
    }
}
