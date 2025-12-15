package org.LiangMi.soulstone.manager;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 游戏阶段管理器
 * 用于管理玩家的游戏进度和等级系统
 * 采用单例模式管理所有玩家的阶段数据
 */
public class GameStageManager {
    /**
     * 玩家阶段数据存储
     * key: 玩家UUID
     * value: 玩家游戏阶段数据
     */
    private static final Map<UUID, PlayerGameStage> playerStages = new HashMap<>();

    // 等级常量定义
    public static final int LEVEL_20 = 20;  // 初级等级
    public static final int LEVEL_30 = 30;  // 中级等级
    public static final int LEVEL_40 = 40;  // 高级等级
    public static final int LEVEL_50 = 50;  // 顶级等级

    /**
     * 等级显示名称映射
     * key: 等级数值
     * value: 等级对应的显示名称
     */
    private static final Map<Integer, String> LEVEL_NAMES = new HashMap<>();

    // 静态初始化块 - 初始化等级名称映射
    static {
        LEVEL_NAMES.put(LEVEL_20, "初级探索者");
        LEVEL_NAMES.put(LEVEL_30, "中级冒险家");
        LEVEL_NAMES.put(LEVEL_40, "高级战士");
        LEVEL_NAMES.put(LEVEL_50, "传奇大师");
    }

    // 所有可用的等级数组（按升序排列）
    private static final int[] ALL_LEVELS = {LEVEL_20, LEVEL_30, LEVEL_40, LEVEL_50};

    /**
     * 玩家游戏阶段数据类
     * 存储单个玩家的游戏进度信息
     */
    public static class PlayerGameStage {
        private final UUID playerId;    // 玩家唯一标识符
        private int currentLevel;       // 当前等级（20、30、40、50之一）

        /**
         * 构造函数
         * @param playerId 玩家UUID
         */
        public PlayerGameStage(UUID playerId) {
            this.playerId = playerId;
            // 初始化时设置默认等级为20
            this.currentLevel = LEVEL_20;
        }

        /**
         * 获取当前等级
         * @return 当前等级数值
         */
        public int getCurrentLevel() {
            return currentLevel;
        }

        /**
         * 设置当前等级
         * @param level 要设置的等级（需通过有效性检查）
         */
        public void setCurrentLevel(int level) {
            this.currentLevel = level;
        }

        /**
         * 从NBT数据读取玩家阶段信息
         * @param nbt 包含玩家数据的NBT复合标签
         */
        public void readFromNbt(NbtCompound nbt) {
            currentLevel = nbt.getInt("CurrentLevel");
        }

        /**
         * 将玩家阶段信息写入NBT数据
         * @param nbt 目标NBT复合标签
         * @return 包含玩家数据的NBT复合标签
         */
        public NbtCompound writeToNbt(NbtCompound nbt) {
            nbt.putInt("CurrentLevel", currentLevel);
            return nbt;
        }
    }

    /**
     * 检查等级是否有效
     * @param level 要检查的等级
     * @return 如果等级在预定义等级列表中返回true，否则返回false
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
     * 获取等级对应的显示名称
     * @param level 等级数值
     * @return 等级名称，如果等级不存在则返回"未知等级"
     */
    public static String getLevelName(int level) {
        return LEVEL_NAMES.getOrDefault(level, "未知等级");
    }

    /**
     * 获取等级显示名称（包含等级数字）
     * @param level 等级数值
     * @return 格式化的等级显示名称，例如："初级探索者 (Lv.20)"
     */
    public static String getLevelDisplayName(int level) {
        return getLevelName(level) + " (Lv." + level + ")";
    }

    /**
     * 获取玩家的游戏阶段数据
     * 如果玩家没有阶段数据，则创建新的数据
     * @param player 服务器玩家实体
     * @return 玩家的游戏阶段数据对象
     */
    public static PlayerGameStage getPlayerStage(ServerPlayerEntity player) {
        return playerStages.computeIfAbsent(player.getUuid(), PlayerGameStage::new);
    }

    /**
     * 移除玩家的阶段数据
     * 通常在玩家退出游戏时调用
     * @param playerId 玩家UUID
     */
    public static void removePlayerStage(UUID playerId) {
        playerStages.remove(playerId);
    }

    // ==================== 新增的接口方法 ====================

    /**
     * 获取玩家当前等级
     * @param player 服务器玩家实体
     * @return 玩家当前等级数值
     */
    public static int getCurrentLevel(ServerPlayerEntity player) {
        PlayerGameStage stageData = getPlayerStage(player);
        return stageData.getCurrentLevel();
    }

    /**
     * 获取玩家当前等级的显示名称
     * @param player 服务器玩家实体
     * @return 玩家当前等级的显示名称
     */
    public static String getCurrentLevelName(ServerPlayerEntity player) {
        int level = getCurrentLevel(player);
        return getLevelDisplayName(level);
    }

    /**
     * 设置玩家当前等级
     * @param player 玩家实体
     * @param level 目标等级
     * @return 设置成功返回true，如果等级无效返回false
     */
    public static boolean setCurrentLevel(ServerPlayerEntity player, int level) {
        PlayerGameStage stageData = getPlayerStage(player);

        // 检查是否为有效等级
        if (!isValidLevel(level)) {
            return false;
        }

        stageData.setCurrentLevel(level);
        return true;
    }

    /**
     * 重置玩家等级到初始等级（20级）
     * @param player 玩家实体
     */
    public static void resetLevel(ServerPlayerEntity player) {
        PlayerGameStage stageData = getPlayerStage(player);
        stageData.setCurrentLevel(LEVEL_20);
    }

    /**
     * 获取所有可用的等级列表
     * @return 等级数组的副本（防止外部修改内部数据）
     */
    public static int[] getAllLevels() {
        return ALL_LEVELS.clone();
    }

    /**
     * 获取所有可用的等级字符串数组
     * 主要用于命令补全功能
     * @return 等级字符串数组，例如：["20", "30", "40", "50"]
     */
    public static String[] getAllLevelStrings() {
        String[] levelStrings = new String[ALL_LEVELS.length];
        for (int i = 0; i < ALL_LEVELS.length; i++) {
            levelStrings[i] = String.valueOf(ALL_LEVELS[i]);
        }
        return levelStrings;
    }

    /**
     * 获取下一等级
     * 按预定义的等级顺序查找
     * @param currentLevel 当前等级
     * @return 下一等级数值，如果已经是最高等级则返回当前等级
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
     * 获取上一等级
     * 按预定义的等级顺序查找
     * @param currentLevel 当前等级
     * @return 上一等级数值，如果已经是最低等级则返回当前等级
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
     * 获取玩家的阶段数据对象
     * 用于需要直接操作玩家数据的场景
     * @param player 玩家实体
     * @return 玩家的游戏阶段数据对象
     */
    public static PlayerGameStage getStageData(ServerPlayerEntity player) {
        return getPlayerStage(player);
    }
}