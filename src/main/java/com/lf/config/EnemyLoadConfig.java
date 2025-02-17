package com.lf.config;

// 敌人加载配置类，用于存储从yml文件中读取的敌人加载配置信息
public class EnemyLoadConfig {
    private String enemyType; // 敌人类型
    private String enemyName; // 敌人名称
    private double loadTime; // 加载时间

    // 获取敌人类型的方法
    public String getEnemyType() {
        return enemyType;
    }

    // 设置敌人类型的方法
    public void setEnemyType(String enemyType) {
        this.enemyType = enemyType;
    }

    // 获取加载时间的方法
    public double getLoadTime() {
        return loadTime;
    }

    // 设置加载时间的方法
    public void setLoadTime(double loadTime) {
        this.loadTime = loadTime;
    }

    public String getEnemyName() {
        return enemyName;
    }

    public void setEnemyName(String enemyName) {
        this.enemyName = enemyName;
    }
}
