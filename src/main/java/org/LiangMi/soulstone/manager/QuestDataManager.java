package org.LiangMi.soulstone.manager;

import org.LiangMi.soulstone.data.QuestData;

import java.util.*;

public class QuestDataManager {
    private Map<Integer, QuestData> dataByCode = new HashMap<>();
    private List<QuestData> allData = new ArrayList<>();

    public void addData(QuestData data) {
        dataByCode.put(data.getCode(), data);
        allData.add(data);
    }
    public QuestData searchByCode(int code) {
        return dataByCode.get(code);
    }
    // 搜索包含特定关键词的数据
    public List<QuestData> searchByKeyword(String keyword) {
        List<QuestData> result = new ArrayList<>();
        for (QuestData data : allData) {
            if (data.toString().contains(keyword)) {
                result.add(data);
            }
        }
        return result;
    }
    // 获取所有数据
    public List<QuestData> getAllData() {
        return new ArrayList<>(allData);
    }
}
