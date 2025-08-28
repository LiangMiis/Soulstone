package org.LiangMi.soulstone.api;

import net.minecraft.entity.player.PlayerEntity;

public class ManaInstance {
    // 当前剩余持续时间（单位：游戏刻）
    public double remainingduration = 0;
    // 魔力效果的总量（正值表示恢复，负值表示消耗）
    public double value = 0;
    // 当前剩余的魔力效果量（随时间递减）
    public double remainingvalue = 0;
    // 效果总持续时间（单位：游戏刻）
    public double duration = 4;
    // 关联的玩家实体
    public PlayerEntity player = null;

    /**
     * 魔力效果实例构造函数
     * @param player 目标玩家实体
     * @param duration 效果总持续时间（游戏刻）
     * @param value 魔力变化总量（正=恢复，负=消耗）
     */
    public ManaInstance(PlayerEntity player, double duration, double value) {
        this.player = player;           // 设置目标玩家
        this.duration = duration;       // 设置总持续时间S
        this.value = value;             // 设置魔力变化总量
        this.remainingvalue = value;    // 初始化剩余变化量（等于总量）
        this.remainingduration = duration; // 初始化剩余持续时间
    }

    /**
     * 每游戏刻更新效果状态
     * 将总效果量平均分配到每个tick执行
     */
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
