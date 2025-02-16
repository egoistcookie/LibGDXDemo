package com.lf.entities;

//物品：可以是防御塔卡片、装备等
public class Stuff {
    //物品名称
    private String stuffName;
    //物品贴图名称
    private String stuffTextureName;
    //物品id
    private int stuffId;

    public Stuff() {
        this("default","default");
    }

    public Stuff(String stuffName) {
        this.stuffName = stuffName;
        this.stuffTextureName = stuffName;
    }

    public Stuff(String stuffName, String stuffTextureName) {
        this.stuffName = stuffName;
        this.stuffTextureName = stuffTextureName;
    }

    public Stuff(String stuffName, String stuffTextureName, int stuffId) {
        this.stuffName = stuffName;
        this.stuffTextureName = stuffTextureName;
        this.stuffId = stuffId;
    }

    public String getStuffName() {
        return stuffName;
    }

    public void setStuffName(String stuffName) {
        this.stuffName = stuffName;
    }

    public String getStuffTextureName() {
        return stuffTextureName;
    }

    public void setStuffTextureName(String stuffTextureName) {
        this.stuffTextureName = stuffTextureName;
    }

    public int getStuffId() {
        return stuffId;
    }

    public void setStuffId(int stuffId) {
        this.stuffId = stuffId;
    }
}
