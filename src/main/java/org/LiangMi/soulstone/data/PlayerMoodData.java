package org.LiangMi.soulstone.data;

import net.minecraft.nbt.NbtCompound;
public class PlayerMoodData {
    private float currentMood;
    private float maxMood;
    private float baseMultiplier; // 基础属性乘数

    public PlayerMoodData() {
        this.currentMood = 100.0f;
        this.maxMood = 100.0f;
        this.baseMultiplier = 1.0f;
    }

    public float getCurrentMood() {
        return currentMood;
    }

    public float getMaxMood() {
        return maxMood;
    }

    public float getMoodPercentage() {
        return currentMood / maxMood;
    }

    // 获取属性乘数（心情值越低，乘数越小）
    public float getAttributeMultiplier() {
        if (currentMood >= maxMood) {
            return 1.0f;
        }
        // 心情值在50%以上时轻微影响，50%以下时影响加剧
        float percentage = getMoodPercentage();
        if (percentage > 0.5f) {
            return 0.8f + (percentage - 0.5f) * 0.4f; // 50%-100%: 0.8-1.0
        } else {
            return percentage * 1.6f; // 0%-50%: 0.0-0.8
        }
    }

    public void addMood(float amount) {
        this.currentMood = Math.min(this.currentMood + amount, this.maxMood);
    }

    public void removeMood(float amount) {
        this.currentMood = Math.max(this.currentMood - amount, 0.0f);
    }

    public void setMood(float mood) {
        this.currentMood = Math.max(0.0f, Math.min(mood, this.maxMood));
    }

    public void setMaxMood(float maxMood) {
        this.maxMood = Math.max(1.0f, maxMood);
        this.currentMood = Math.min(this.currentMood, this.maxMood);
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putFloat("currentMood", currentMood);
        nbt.putFloat("maxMood", maxMood);
        nbt.putFloat("baseMultiplier", baseMultiplier);
        return nbt;
    }

    public static PlayerMoodData fromNbt(NbtCompound nbt) {
        PlayerMoodData data = new PlayerMoodData();
        data.currentMood = nbt.getFloat("currentMood");
        data.maxMood = nbt.getFloat("maxMood");
        data.baseMultiplier = nbt.getFloat("baseMultiplier");
        return data;
    }
}
