package org.LiangMi.soulstone.data;

public class QuestData {
    private String category;
    private int num;
    private String type;
    private int code;

    public QuestData(String category,int num,String type,int code){
        this.category = category;
        this.num = num;
        this.type = type;
        this.code = code;
    }
    public String getCategory(){ return  category; }
    public int getNum() { return num; }

    public String getType() { return type; }
    public int getCode() { return code; }

    @Override
    public String toString() {
        return category + " " + num + " " + type + " " + code;
    }
}
