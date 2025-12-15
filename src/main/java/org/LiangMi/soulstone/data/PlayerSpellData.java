package org.LiangMi.soulstone.data;


import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerSpellData {
    private final Map<String, String> keyBindsSpell;
    private final List<String> spellList;
    private final List<String> keyBinds;

    public PlayerSpellData() {
        keyBindsSpell = new HashMap<>();
        spellList = new ArrayList<>();
        keyBinds = new ArrayList<>();
        initKeySpell();
    }

    private void initKeySpell() {
        // 使用空字符串而不是 null
        keyBindsSpell.put("SpellKey1", "");
        keyBindsSpell.put("SpellKey2", "");
        keyBindsSpell.put("SpellKey3", "");
        keyBindsSpell.put("SpellKey4", "");

        keyBinds.add(0, "SpellKey1");
        keyBinds.add(1, "SpellKey2");
        keyBinds.add(2, "SpellKey3");
        keyBinds.add(3, "SpellKey4");

    }

    public String getKeyBindsSpell(String key) {
        return keyBindsSpell.get(key);
    }

    public List<String> getKeyBinds() {
        return keyBinds;
    }

    public Map<String, String> getKeyBindsSpell() {
        return keyBindsSpell;
    }

    public boolean addKeyBindsSpell(String key, String spellId) {
        if (spellId != null && key != null) {
            keyBindsSpell.put(key, spellId);
            return true;
        }
        return false;
    }

    public boolean addSpell(String spell) {
        if (spell != null && !spellList.contains(spell)) {
            spellList.add(spell);
            return true;
        }
        return false;
    }

    public void removeSpell(String spell) {
        // 修复：避免重复删除
        if (spell != null) {
            spellList.remove(spell);
        }
    }

    public List<String> getSpell() {
        return spellList;
    }

    public boolean hasSpell(String spell) {
        return spellList.contains(spell);
    }

    public int getSpellCount() {
        return spellList.size();
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();

        // 1. 存储键位绑定
        NbtCompound keyBindsSpellNbt = new NbtCompound();
        for (Map.Entry<String, String> entry : this.keyBindsSpell.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            // 处理 null 值
            keyBindsSpellNbt.putString(key, value != null ? value : "");
        }
        nbt.put("keyBindsSpell", keyBindsSpellNbt);

        // 2. 存储法术列表
        NbtList spellListNbt = new NbtList();
        for (String spell : this.spellList) {
            if (spell != null) {
                spellListNbt.add(NbtString.of(spell));
            }
        }
        nbt.put("spellList", spellListNbt);

        // 3. 存储键位列表
        NbtList keyBindsNbt = new NbtList();
        for (String key : this.keyBinds) {
            if (key != null) {
                keyBindsNbt.add(NbtString.of(key));
            }
        }
        nbt.put("keyBinds", keyBindsNbt);

        return nbt;
    }

    public static PlayerSpellData fromNbt(NbtCompound nbt) {
        PlayerSpellData data = new PlayerSpellData();

        // 清空初始化数据
        data.keyBindsSpell.clear();
        data.spellList.clear();
        data.keyBinds.clear();

        // 1. 读取键位绑定
        if (nbt.contains("keyBindsSpell")) {
            NbtCompound keyBindsSpellNbt = nbt.getCompound("keyBindsSpell");
            for (String key : keyBindsSpellNbt.getKeys()) {
                String value = keyBindsSpellNbt.getString(key);
                data.keyBindsSpell.put(key, value);
            }
        }

        // 2. 读取法术列表
        if (nbt.contains("spellList")) {
            NbtList spellListNbt = nbt.getList("spellList", 8); // 8 表示字符串类型
            for (int i = 0; i < spellListNbt.size(); i++) {
                String spell = spellListNbt.getString(i);
                if (spell != null && !spell.isEmpty()) {
                    data.spellList.add(spell);
                }
            }
        }

        // 3. 读取键位列表
        if (nbt.contains("keyBinds")) {
            NbtList keyBindsNbt = nbt.getList("keyBinds", 8);
            for (int i = 0; i < keyBindsNbt.size(); i++) {
                String key = keyBindsNbt.getString(i);
                if (key != null && !key.isEmpty()) {
                    data.keyBinds.add(key);
                }
            }
        }

        // 如果 keyBinds 为空，重新初始化
        if (data.keyBinds.isEmpty()) {
            data.keyBinds.add(0, "SpellKey1");
            data.keyBinds.add(1, "SpellKey2");
            data.keyBinds.add(2, "SpellKey3");
            data.keyBinds.add(3, "SpellKey4");
        }

        return data;
    }
}

