package org.LiangMi.soulstone.manager;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameStageManager {
    private static final Map<UUID, PlayerGameStage> playerStages = new HashMap<>();

    // 定义等级常量
    public static final int LEVEL_20 = 20;
    public static final int LEVEL_30 = 30;
    public static final int LEVEL_40 = 40;
    public static final int LEVEL_50 = 50;

    // 等级显示名称
    private static final Map<Integer, String> LEVEL_NAMES = new HashMap<>();

    static {
        LEVEL_NAMES.put(LEVEL_20, "初级探索者");
        LEVEL_NAMES.put(LEVEL_30, "中级冒险家");
        LEVEL_NAMES.put(LEVEL_40, "高级战士");
        LEVEL_NAMES.put(LEVEL_50, "传奇大师");
    }

    // 所有可用的等级数组
    private static final int[] ALL_LEVELS = {LEVEL_20, LEVEL_30, LEVEL_40, LEVEL_50};

    public static class PlayerGameStage {
        private final UUID playerId;
        private int currentLevel;  // 当前等级
        private final Map<String, Integer> progress;
        private final Map<Integer, Boolean> unlockedLevels;  // 存储已解锁的等级

        public PlayerGameStage(UUID playerId) {
            this.playerId = playerId;
            this.currentLevel = LEVEL_20;  // 默认等级20
            this.progress = new HashMap<>();
            this.unlockedLevels = new HashMap<>();

            // 默认解锁等级20
            unlockedLevels.put(LEVEL_20, true);

            // 初始化进度
            progress.put("blocks_mined", 0);
            progress.put("mobs_killed", 0);
            progress.put("distance_traveled", 0);
            progress.put("unique_items", 0);
            progress.put("crafting_recipes", 0);
        }

        public int getCurrentLevel() {
            return currentLevel;
        }

        public void setCurrentLevel(int level) {
            if (isLevelUnlocked(level)) {
                this.currentLevel = level;
            }
        }

        public boolean unlockLevel(int level) {
            if (isValidLevel(level)) {
                unlockedLevels.put(level, true);
                return true;
            }
            return false;
        }

        public boolean isLevelUnlocked(int level) {
            return unlockedLevels.getOrDefault(level, false);
        }

        public void addProgress(String key, int amount) {
            progress.put(key, progress.getOrDefault(key, 0) + amount);
            checkLevelProgression();
        }

        public int getProgress(String key) {
            return progress.getOrDefault(key, 0);
        }

        private void checkLevelProgression() {
            // 根据进度自动解锁等级
            int blocksMined = getProgress("blocks_mined");
            int mobsKilled = getProgress("mobs_killed");
            int distance = getProgress("distance_traveled");

            // 解锁条件：达到一定进度后解锁下一等级
            if (blocksMined >= 500 && mobsKilled >= 50 && !isLevelUnlocked(LEVEL_30)) {
                unlockLevel(LEVEL_30);
            }

            if (blocksMined >= 1000 && mobsKilled >= 100 && distance >= 5000 && !isLevelUnlocked(LEVEL_40)) {
                unlockLevel(LEVEL_40);
            }

            if (blocksMined >= 2000 && mobsKilled >= 200 && distance >= 10000 && !isLevelUnlocked(LEVEL_50)) {
                unlockLevel(LEVEL_50);
            }
        }

        public void readFromNbt(NbtCompound nbt) {
            currentLevel = nbt.getInt("CurrentLevel");

            // 读取进度
            NbtCompound progressNbt = nbt.getCompound("Progress");
            for (String key : progressNbt.getKeys()) {
                progress.put(key, progressNbt.getInt(key));
            }

            // 读取解锁的等级
            NbtList unlockedList = nbt.getList("UnlockedLevels", 10);
            unlockedLevels.clear();
            for (int i = 0; i < unlockedList.size(); i++) {
                NbtCompound levelNbt = unlockedList.getCompound(i);
                int level = levelNbt.getInt("Level");
                unlockedLevels.put(level, true);
            }
        }

        public NbtCompound writeToNbt(NbtCompound nbt) {
            nbt.putInt("CurrentLevel", currentLevel);

            // 保存进度
            NbtCompound progressNbt = new NbtCompound();
            for (Map.Entry<String, Integer> entry : progress.entrySet()) {
                progressNbt.putInt(entry.getKey(), entry.getValue());
            }
            nbt.put("Progress", progressNbt);

            // 保存解锁的等级
            NbtList unlockedList = new NbtList();
            for (Map.Entry<Integer, Boolean> entry : unlockedLevels.entrySet()) {
                if (entry.getValue()) {
                    NbtCompound levelNbt = new NbtCompound();
                    levelNbt.putInt("Level", entry.getKey());
                    unlockedList.add(levelNbt);
                }
            }
            nbt.put("UnlockedLevels", unlockedList);

            return nbt;
        }
    }

    /**
     * 检查是否为有效等级
     */
    public static boolean isValidLevel(int level) {
        for (int l : ALL_LEVELS) {
            if (l == level) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取等级显示名称
     */
    public static String getLevelName(int level) {
        return LEVEL_NAMES.getOrDefault(level, "未知等级");
    }

    /**
     * 获取等级显示名称（带等级数字）
     */
    public static String getLevelDisplayName(int level) {
        return getLevelName(level) + " (Lv." + level + ")";
    }

    public static PlayerGameStage getPlayerStage(ServerPlayerEntity player) {
        return playerStages.computeIfAbsent(player.getUuid(), PlayerGameStage::new);
    }

    public static void removePlayerStage(UUID playerId) {
        playerStages.remove(playerId);
    }

    // 以下是新增的接口方法

    /**
     * 获取玩家当前等级
     */

    public static int getCurrentLevel(ServerPlayerEntity player) {
        PlayerGameStage stageData = getPlayerStage(player);
        return stageData.getCurrentLevel();
    }

    /**
     * 获取玩家当前等级的显示名称
     */

    public static String getCurrentLevelName(ServerPlayerEntity player) {
        int level = getCurrentLevel(player);
        return getLevelDisplayName(level);
    }

    /**
     * 设置玩家当前等级
     * @param player 玩家
     * @param level 目标等级
     * @return 是否成功设置（如果等级未解锁，则返回false）
     */

    public static boolean setCurrentLevel(ServerPlayerEntity player, int level) {
        PlayerGameStage stageData = getPlayerStage(player);

        // 检查是否为有效等级
        if (!isValidLevel(level)) {
            return false;
        }

        // 检查等级是否已解锁
        if (!stageData.isLevelUnlocked(level)) {
            return false;
        }

        stageData.setCurrentLevel(level);
        return true;
    }

    /**
     * 解锁特定等级
     * @param player 玩家
     * @param level 要解锁的等级
     * @return 是否成功解锁（如果不是有效等级或已经解锁，返回false）
     */

    public static boolean unlockLevel(ServerPlayerEntity player, int level) {
        PlayerGameStage stageData = getPlayerStage(player);

        // 检查是否为有效等级
        if (!isValidLevel(level)) {
            return false;
        }

        if (stageData.isLevelUnlocked(level)) {
            return false;
        }

        return stageData.unlockLevel(level);
    }

    /**
     * 直接解锁所有等级
     */

    public static void unlockAllLevels(ServerPlayerEntity player) {
        PlayerGameStage stageData = getPlayerStage(player);

        for (int level : ALL_LEVELS) {
            stageData.unlockLevel(level);
        }
    }

    /**
     * 获取玩家所有已解锁的等级
     */

    public static Map<Integer, Boolean> getUnlockedLevels(ServerPlayerEntity player) {
        PlayerGameStage stageData = getPlayerStage(player);

        Map<Integer, Boolean> unlocked = new HashMap<>();
        for (int level : ALL_LEVELS) {
            unlocked.put(level, stageData.isLevelUnlocked(level));
        }
        return unlocked;
    }

    /**
     * 重置玩家等级（回到等级20）
     */

    public static void resetLevel(ServerPlayerEntity player) {
        PlayerGameStage stageData = getPlayerStage(player);
        stageData.setCurrentLevel(LEVEL_20);
    }

    /**
     * 重置玩家所有进度和等级
     */

    public static void resetAllProgress(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        playerStages.put(playerId, new PlayerGameStage(playerId));
    }

    /**
     * 检查玩家是否已解锁特定等级
     */

    public static boolean hasLevelUnlocked(ServerPlayerEntity player, int level) {
        PlayerGameStage stageData = getPlayerStage(player);
        return stageData.isLevelUnlocked(level);
    }

    /**
     * 获取玩家的进度值
     */

    public static int getPlayerProgress(ServerPlayerEntity player, String progressKey) {
        PlayerGameStage stageData = getPlayerStage(player);
        return stageData.getProgress(progressKey);
    }

    /**
     * 增加玩家的进度值
     */

    public static void addPlayerProgress(ServerPlayerEntity player, String progressKey, int amount) {
        PlayerGameStage stageData = getPlayerStage(player);
        stageData.addProgress(progressKey, amount);
    }

    /**
     * 获取所有可用的等级列表
     */

    public static int[] getAllLevels() {
        return ALL_LEVELS.clone();
    }

    /**
     * 获取所有可用的等级字符串数组（用于命令补全）
     */

    public static String[] getAllLevelStrings() {
        String[] levelStrings = new String[ALL_LEVELS.length];
        for (int i = 0; i < ALL_LEVELS.length; i++) {
            levelStrings[i] = String.valueOf(ALL_LEVELS[i]);
        }
        return levelStrings;
    }

    /**
     * 获取下一等级（按数值顺序）
     */

    public static int getNextLevel(int currentLevel) {
        for (int i = 0; i < ALL_LEVELS.length - 1; i++) {
            if (ALL_LEVELS[i] == currentLevel) {
                return ALL_LEVELS[i + 1];
            }
        }
        return currentLevel; // 如果没有下一等级，返回当前等级
    }

    /**
     * 获取上一等级（按数值顺序）
     */

    public static int getPreviousLevel(int currentLevel) {
        for (int i = 1; i < ALL_LEVELS.length; i++) {
            if (ALL_LEVELS[i] == currentLevel) {
                return ALL_LEVELS[i - 1];
            }
        }
        return currentLevel; // 如果没有上一等级，返回当前等级
    }

    /**
     * 获取玩家的阶段数据对象（用于高级操作）
     */

    public static PlayerGameStage getStageData(ServerPlayerEntity player) {
        return getPlayerStage(player);
    }
}