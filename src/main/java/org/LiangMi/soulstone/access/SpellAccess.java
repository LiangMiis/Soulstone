package org.LiangMi.soulstone.access;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.LiangMi.soulstone.data.PlayerSpellData;
import org.LiangMi.soulstone.manager.SpellSystemManager;

import java.util.*;

public class SpellAccess {
    private static final String DATA_NAME = "spell_system";

    // 私有构造函数防止实例化
    private SpellAccess() {}

    // ========== 管理器相关方法 ==========

    public static SpellSystemManager getSpellManager(MinecraftServer server) {
        if (server == null) {
            return null;
        }

        // 获取主世界的持久状态管理器
        ServerWorld overworld = server.getWorld(World.OVERWORLD);
        if (overworld == null) {
            return null;
        }

        PersistentStateManager persistentStateManager = overworld.getPersistentStateManager();

        // 获取或创建法术系统管理器实例
        return persistentStateManager.getOrCreate(
                SpellSystemManager::fromNbt,  // 从NBT数据反序列化的方法引用
                SpellSystemManager::create,   // 创建新实例的方法引用
                DATA_NAME                     // 数据标识符
        );
    }

    // ========== 玩家数据操作（通用） ==========

    public static PlayerSpellData getPlayerData(PlayerEntity player) {
        if (player.getWorld().isClient()) {
            return new PlayerSpellData(); // 客户端返回新的默认数据实例
        }

        // 服务器端：从管理器获取玩家数据
        SpellSystemManager manager = getSpellManager(player.getServer());
        if (manager == null) {
            return new PlayerSpellData(); // 返回默认数据
        }

        return manager.getPlayerData(player.getUuid());
    }

    public static void setPlayerData(PlayerEntity player, PlayerSpellData data) {
        // 客户端不执行任何操作
        if (player.getWorld().isClient()) return;

        // 服务器端：更新玩家数据到管理器
        SpellSystemManager manager = getSpellManager(player.getServer());
            manager.setPlayerData(player.getUuid(), data);
            // markDirty 已经在 setPlayerData 中调用
    }

    // ========== 法术管理 ==========

    public static boolean addSpell(PlayerEntity player, String spell) {

        PlayerSpellData data = getPlayerData(player);
        boolean success = data.addSpell(spell);
        if (success){
            data.addSpell(spell);
            setPlayerData(player, data);
        }
        return success;
    }

    public static void removeSpell(PlayerEntity player, String spell) {
        if (player.getWorld().isClient()) return;

        PlayerSpellData data = getPlayerData(player);
        data.removeSpell(spell);
        setPlayerData(player, data);

    }

    public static void setSpellList(PlayerEntity player, List<String> spells) {
        if (player.getWorld().isClient()) return;

        PlayerSpellData data = getPlayerData(player);
        // 注意：PlayerSpellData 目前没有直接设置整个列表的方法
        // 这里需要先清除现有法术，然后添加新法术
        clearSpells(player);
        for (String spell : spells) {
            data.addSpell(spell);
        }
        setPlayerData(player, data);

    }

    public static void clearSpells(PlayerEntity player) {
        if (player.getWorld().isClient()) return;

        PlayerSpellData data = getPlayerData(player);
        // 由于 PlayerSpellData 没有 clearSpells 方法，我们需要获取列表并清除
        List<String> spells = data.getSpell();
        for (String spell : new ArrayList<>(spells)) {
            data.removeSpell(spell);
        }
        setPlayerData(player, data);

    }

    // ========== 键位绑定管理 ==========

    public static boolean addKeyBindsSpell(PlayerEntity player, String key, String spellId) {
        PlayerSpellData data = getPlayerData(player);
        boolean success = data.addKeyBindsSpell(key, spellId);
        if (success){
            data.addKeyBindsSpell(key, spellId);
            setPlayerData(player, data);
        }
        return success;
    }

    public static void removeKeyBindsSpell(PlayerEntity player, String key) {
        if (player.getWorld().isClient()) return;

        PlayerSpellData data = getPlayerData(player);
        // 注意：PlayerSpellData 的 addKeyBindsSpell 方法可以处理 null 值
        // 所以这里我们设置为 null，让 PlayerSpellData 将其转换为空字符串
        data.addKeyBindsSpell(key, null);
        setPlayerData(player, data);

    }

    public static void clearKeyBindsSpell(PlayerEntity player) {
        if (player.getWorld().isClient()) return;

        PlayerSpellData data = getPlayerData(player);
        // 获取所有键位并清除
        List<String> keyBindList = data.getKeyBinds();
        for (String key : keyBindList) {
            data.addKeyBindsSpell(key, null);
        }
        setPlayerData(player, data);

    }

    public static void bindSpellToNextAvailableKey(PlayerEntity player, String spellId) {
        if (player.getWorld().isClient()) return;

        PlayerSpellData data = getPlayerData(player);
        Map<String, String> keyBinds = data.getKeyBindsSpell();

        // 查找第一个空键位（值为 null 或空字符串）
        for (String key : data.getKeyBinds()) {
            String currentSpell = keyBinds.get(key);
            if (currentSpell == null || currentSpell.isEmpty()) {
                data.addKeyBindsSpell(key, spellId);
                setPlayerData(player, data);
                return;
            }
        }

        // 如果没有空键位，可以选择覆盖第一个键位或给出错误提示
        // 这里我们覆盖第一个键位
        if (!data.getKeyBinds().isEmpty()) {
            String firstKey = data.getKeyBinds().get(0);
            data.addKeyBindsSpell(firstKey, spellId);
            setPlayerData(player, data);
        }
    }

    // ========== 查询方法 ==========

    public static List<String> getPlayerSpells(PlayerEntity player) {
        PlayerSpellData data = getPlayerData(player);
        return new ArrayList<>(data.getSpell()); // 返回副本以防修改
    }

    public static boolean hasSpell(PlayerEntity player, String spell) {
        PlayerSpellData data = getPlayerData(player);
        return data.hasSpell(spell);
    }

    public static int getSpellCount(PlayerEntity player) {
        PlayerSpellData data = getPlayerData(player);
        return data.getSpellCount();
    }

    public static Map<String, String> getPlayerKeyBinds(PlayerEntity player) {
        PlayerSpellData data = getPlayerData(player);
        return new HashMap<>(data.getKeyBindsSpell()); // 返回副本以防修改
    }

    public static List<String> getPlayerKeyBindList(PlayerEntity player) {
        PlayerSpellData data = getPlayerData(player);
        return new ArrayList<>(data.getKeyBinds()); // 返回副本以防修改
    }

    public static String getSpellAtKey(PlayerEntity player, String key) {
        PlayerSpellData data = getPlayerData(player);
        return data.getKeyBindsSpell(key);
    }

    public static boolean isKeyBound(PlayerEntity player, String key) {
        PlayerSpellData data = getPlayerData(player);
        String spell = data.getKeyBindsSpell(key);
        return spell != null && !spell.isEmpty();
    }

    public static String getKeyForSpell(PlayerEntity player, String spell) {
        PlayerSpellData data = getPlayerData(player);
        Map<String, String> keyBinds = data.getKeyBindsSpell();

        for (Map.Entry<String, String> entry : keyBinds.entrySet()) {
            if (spell.equals(entry.getValue())) {
                return entry.getKey();
            }
        }

        return null;
    }

}