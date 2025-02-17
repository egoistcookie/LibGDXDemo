package com.lf.manager;

// 敌人类型配置类，用于存储从yml文件中读取的敌人类型对应的属性信息
public class EnemyTypeConfig {
    // 敌人类型
    private String enemyType;
    // 稀有度
    private String rarity;
    // 生命值
    private int health;
    // 移动速度
    private float velocity;
    // 移动贴图
    private String moveTexture;
    // 经验值
    private int experience;

    public String getEnemyType() {
        return enemyType;
    }

    public void setEnemyType(String enemyType) {
        this.enemyType = enemyType;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public String getMoveTexture() {
        return moveTexture;
    }

    public void setMoveTexture(String moveTexture) {
        this.moveTexture = moveTexture;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }
}
