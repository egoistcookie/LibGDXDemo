package com.lf.config;

// 敌人类型配置类，用于存储从yml文件中读取的敌人类型对应的属性信息
public class CardTypeConfig {
    // 敌人类型
    private String cardType;
    // 稀有度
    private String rarity;
    // 卡片等级
    private int cardLevel;
    // 攻击力
    private int attackPower;
    // 攻击范围
    private float attackRange;
    // 攻击速度
    private float fireRate;
    // 最大攻击数
    private int maxAttackCount;
    // 卡片贴图
    private String cardTexture;
    // 物品栏贴图
    private String stuffTexture;
    // 地图中贴图
    private String mapTexture;
    // 攻击贴图
    private String attackTexture;
    // 杀敌数
    private int killCount;

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public int getMaxAttackCount() {
        return maxAttackCount;
    }

    public void setMaxAttackCount(int maxAttackCount) {
        this.maxAttackCount = maxAttackCount;
    }

    public float getAttackRange() {
        return attackRange;
    }

    public void setAttackRange(float attackRange) {
        this.attackRange = attackRange;
    }

    public String getAttackTexture() {
        return attackTexture;
    }

    public void setAttackTexture(String attackTexture) {
        this.attackTexture = attackTexture;
    }

    public String getCardTexture() {
        return cardTexture;
    }

    public void setCardTexture(String cardTexture) {
        this.cardTexture = cardTexture;
    }

    public String getStuffTexture() {
        return stuffTexture;
    }

    public void setStuffTexture(String stuffTexture) {
        this.stuffTexture = stuffTexture;
    }

    public String getMapTexture() {
        return mapTexture;
    }

    public void setMapTexture(String mapTexture) {
        this.mapTexture = mapTexture;
    }

    public float getFireRate() {
        return fireRate;
    }

    public void setFireRate(float fireRate) {
        this.fireRate = fireRate;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    public int getKillCount() {
        return killCount;
    }

    public void setKillCount(int killCount) {
        this.killCount = killCount;
    }

    public int getCardLevel() {
        return cardLevel;
    }

    public void setCardLevel(int cardLevel) {
        this.cardLevel = cardLevel;
    }
}
