package org.LiangMi.soulstone.data;


import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.HashMap;
import java.util.Map;
public class PlayerPointData {
    private int availablePoints; // 可用的加点值
    private Map<String, Integer> assignedPoints; // 已分配的属性点
    private int totalPointsEarned; // 总共获得的加点值

    public PlayerPointData() {
        this.availablePoints = 0;
        this.assignedPoints = new HashMap<>();
        this.totalPointsEarned = 0;
        initializeDefaultAttributes();
    }

    private void initializeDefaultAttributes() {
        // 初始化所有可加点的属性
        assignedPoints.put("health", 0);
        assignedPoints.put("attack", 0);
        assignedPoints.put("defense", 0);
        assignedPoints.put("speed", 0);
        assignedPoints.put("mining_speed", 0);
        assignedPoints.put("luck", 0);
        assignedPoints.put("experience", 0);
    }

    public int getAvailablePoints() {
        return availablePoints;
    }

    public void addPoints(int points) {
        this.availablePoints += points;
        this.totalPointsEarned += points;
    }

    public boolean assignPoint(String attribute, int amount) {
        if (availablePoints < amount) return false;
        if (!assignedPoints.containsKey(attribute)) return false;

        assignedPoints.put(attribute, assignedPoints.get(attribute) + amount);
        availablePoints -= amount;
        return true;
    }

    public boolean resetPoints() {
        if (totalPointsEarned == 0) return false;

        // 返还所有已分配的点数
        for (String attribute : assignedPoints.keySet()) {
            availablePoints += assignedPoints.get(attribute);
            assignedPoints.put(attribute, 0);
        }
        return true;
    }

    public int getAssignedPoints(String attribute) {
        return assignedPoints.getOrDefault(attribute, 0);
    }

    public Map<String, Integer> getAllAssignedPoints() {
        return new HashMap<>(assignedPoints);
    }

    public int getTotalPointsEarned() {
        return totalPointsEarned;
    }

    // 注册新的可加点属性
    public void registerAttribute(String attribute) {
        assignedPoints.putIfAbsent(attribute, 0);
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("availablePoints", availablePoints);
        nbt.putInt("totalPointsEarned", totalPointsEarned);

        NbtCompound assignedNbt = new NbtCompound();
        for (Map.Entry<String, Integer> entry : assignedPoints.entrySet()) {
            assignedNbt.putInt(entry.getKey(), entry.getValue());
        }
        nbt.put("assignedPoints", assignedNbt);

        return nbt;
    }

    public static PlayerPointData fromNbt(NbtCompound nbt) {
        PlayerPointData data = new PlayerPointData();
        data.availablePoints = nbt.getInt("availablePoints");
        data.totalPointsEarned = nbt.getInt("totalPointsEarned");

        if (nbt.contains("assignedPoints")) {
            NbtCompound assignedNbt = nbt.getCompound("assignedPoints");
            for (String key : assignedNbt.getKeys()) {
                data.assignedPoints.put(key, assignedNbt.getInt(key));
            }
        }

        return data;
    }
}
