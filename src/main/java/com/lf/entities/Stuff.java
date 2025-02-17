package com.lf.entities;

//物品：可以是防御塔卡片、装备等
public class Stuff {
    //物品名称
    private String stuffName;
    //物品贴图名称
    private String stuffType;
    //物品id
    private int stuffId;
    //物品经验值
    private int stuffExp;
    //物品星级
    private int stuffStarLevel;

    public Stuff() {
        this("default","default");
    }

    public Stuff(String stuffName) {
        this.stuffName = stuffName;
        this.stuffType = stuffName;
    }

    public Stuff(String stuffName, String stuffType) {
        this.stuffName = stuffName;
        this.stuffType = stuffType;
    }

    public Stuff(String stuffName, String stuffType, int stuffId) {
        this.stuffName = stuffName;
        this.stuffType = stuffType;
        this.stuffId = stuffId;
    }

    /**
     *
     * @param stuffName 物品名称
     * @param stuffType 物品类型
     * @param stuffId 物品编号
     * @param stuffExp 物品经验值
     * @param stuffStarLevel 物品星级
     */
    public Stuff(String stuffName, String stuffType, int stuffId, int stuffExp, int stuffStarLevel) {
        this.stuffName = stuffName;
        this.stuffType = stuffType;
        this.stuffId = stuffId;
        this.stuffExp = stuffExp;
        this.stuffStarLevel = stuffStarLevel;
    }

    public String getStuffName() {
        return stuffName;
    }

    public void setStuffName(String stuffName) {
        this.stuffName = stuffName;
    }

    public String getStuffType() {
        return stuffType;
    }

    public void setStuffType(String stuffType) {
        this.stuffType = stuffType;
    }

    public int getStuffId() {
        return stuffId;
    }

    public void setStuffId(int stuffId) {
        this.stuffId = stuffId;
    }

    public int getStuffExp() {
        return stuffExp;
    }

    public void setStuffExp(int stuffExp) {
        this.stuffExp = stuffExp;
    }

    public int getStuffStarLevel() {
        return stuffStarLevel;
    }

    public void setStuffStarLevel(int stuffStarLevel) {
        this.stuffStarLevel = stuffStarLevel;
    }
}
