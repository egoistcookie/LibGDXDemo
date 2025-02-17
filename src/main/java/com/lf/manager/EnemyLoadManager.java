package com.lf.manager;

import com.lf.config.CardTypeConfig;
import com.lf.config.EnemyLoadConfig;
import com.lf.config.EnemyTypeConfig;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// 敌人加载管理器类，负责读取yml文件并管理敌人的加载时序
public class EnemyLoadManager {

    private List<EnemyLoadConfig> enemyLoadConfigs; // 存储敌人加载信息的列表
    private List<EnemyTypeConfig> enemyTypeConfigs; // 存储敌人类型信息
    private List<CardTypeConfig> cardTypeConfigs; // 存储卡片类型信息
    private double elapsedTime; // 已经过去的时间

    // 构造函数，初始化敌人加载配置列表和已过去的时间
    public EnemyLoadManager() {
        this.enemyLoadConfigs = new ArrayList<>();
        this.enemyTypeConfigs = new ArrayList<>();
        this.cardTypeConfigs = new ArrayList<>();
        this.elapsedTime = 0.0;
        // 加载敌人时间配置文件
        loadEnemyTimeConfig();
        // 加载敌人类型配置文件
        loadEnemyTypeConfig();
        // 加载敌人类型配置文件
        loadCardTypeConfig();
    }

    // 加载卡片类型配置文件
    private void loadCardTypeConfig() {
        try {
            // 创建Yaml对象
            Yaml yaml = new Yaml();
            // 打开配置文件的输入流
            InputStream inputStream = this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("card_type_config.yml");
            // 读取配置文件内容
            Map<String, List<Map<String, Object>>> config = yaml.load(inputStream);
            // 获取卡片加载配置列表
            List<Map<String, Object>> configList = config.get("CardTypeConfigs");
            for (Map<String, Object> configMap : configList) {
                // 创建卡片加载配置对象
                CardTypeConfig cardTypeConfig = new CardTypeConfig();
                // 设置卡片类型
                cardTypeConfig.setCardType((String) configMap.get("cardType"));
                // 设置稀有度
                cardTypeConfig.setRarity((String) configMap.get("rarity"));
                // 设置攻击范围
                cardTypeConfig.setAttackRange(Float.parseFloat(configMap.get("attackRange")+""));
                // 设置攻击速度
                cardTypeConfig.setFireRate(Float.parseFloat(configMap.get("fireRate")+""));
                // 设置生命值
                cardTypeConfig.setMaxAttackCount((int) configMap.get("maxAttackCount"));
                // 设置地图模型贴图
                cardTypeConfig.setMapTexture((String) configMap.get("mapTexture"));
                // 设置卡片贴图
                cardTypeConfig.setCardTexture((String) configMap.get("cardTexture"));
                // 设置攻击贴图
                cardTypeConfig.setAttackTexture((String) configMap.get("attackTexture"));
                // 设置物品栏中贴图
                cardTypeConfig.setStuffTexture((String) configMap.get("stuffTexture"));
                // 将敌人加载配置对象添加到列表中
                cardTypeConfigs.add(cardTypeConfig);
            }
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
        }
    }

    // 加载敌人类型配置文件
    private void loadEnemyTypeConfig() {
        try {
            // 创建Yaml对象
            Yaml yaml = new Yaml();
            // 打开配置文件的输入流
            InputStream inputStream = this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("enemy_type_config.yml");
            // 读取配置文件内容
            Map<String, List<Map<String, Object>>> config = yaml.load(inputStream);
            // 获取敌人加载配置列表
            List<Map<String, Object>> configList = config.get("enemyTypeConfigs");
            for (Map<String, Object> configMap : configList) {
                // 创建敌人加载配置对象
                EnemyTypeConfig enemyTypeConfig = new EnemyTypeConfig();
                // 设置敌人类型
                enemyTypeConfig.setEnemyType((String) configMap.get("enemyType"));
                // 设置稀有度
                enemyTypeConfig.setRarity((String) configMap.get("rarity"));
                // 设置生命值
                enemyTypeConfig.setHealth((int) configMap.get("health"));
                // 设置移动速度
                enemyTypeConfig.setVelocity(Float.parseFloat(configMap.get("velocity")+""));
                // 设置贴图
                enemyTypeConfig.setMoveTexture((String) configMap.get("moveTexture"));
                // 设置经验值
                enemyTypeConfig.setExperience((int) configMap.get("experience"));
                // 将敌人加载配置对象添加到列表中
                enemyTypeConfigs.add(enemyTypeConfig);
            }
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
        }
    }
    // 加载敌人时间配置文件
    private void loadEnemyTimeConfig() {
        try {
            // 创建Yaml对象
            Yaml yaml = new Yaml();
            // 打开配置文件的输入流
            InputStream inputStream = this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("enemy_load_config.yml");
            // 读取配置文件内容
            Map<String, List<Map<String, Object>>> config = yaml.load(inputStream);
            // 获取敌人加载配置列表
            List<Map<String, Object>> configList = config.get("enemyLoadConfigs");
            for (Map<String, Object> configMap : configList) {
                // 创建敌人加载配置对象
                EnemyLoadConfig enemyLoadConfig = new EnemyLoadConfig();
                // 设置敌人类型
                enemyLoadConfig.setEnemyType((String) configMap.get("enemyType"));
                // 设置敌人名称
                enemyLoadConfig.setEnemyName((String) configMap.get("enemyName"));
                // 设置加载时间
                enemyLoadConfig.setLoadTime((double) configMap.get("loadTime"));
                // 将敌人加载配置对象添加到列表中
                enemyLoadConfigs.add(enemyLoadConfig);
            }
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
        }
    }

    // 更新时间的方法，根据已过去的时间判断是否需要加载敌人
    public List<String> update(double deltaTime) {
        // 更新已过去的时间
        elapsedTime += deltaTime;
        List<String> enemiesToLoad = new ArrayList<>();
        for (EnemyLoadConfig config : enemyLoadConfigs) {
            // 判断是否到达加载时间
            if (elapsedTime >= config.getLoadTime()) {
                // 将需要加载的敌人类型添加到列表中
                enemiesToLoad.add(config.getEnemyType());
            }
        }
        return enemiesToLoad;
    }

    public List<EnemyLoadConfig> getEnemyLoadConfigs() {
        return enemyLoadConfigs;
    }

    public List<EnemyTypeConfig> getEnemyTypeConfigs() {
        return enemyTypeConfigs;
    }

    public List<CardTypeConfig> getCardTypeConfigs() {
        return cardTypeConfigs;
    }

    public void setCardTypeConfigs(List<CardTypeConfig> cardTypeConfigs) {
        this.cardTypeConfigs = cardTypeConfigs;
    }

    public static void main(String[] args) {
        // 创建敌人加载管理器对象
        EnemyLoadManager enemyLoadManager = new EnemyLoadManager();
        // 模拟游戏循环，每秒更新一次
        for (int i = 0; i < 20; i++) {
            // 调用update方法更新时间，传入时间间隔1.0秒
            List<String> enemiesToLoad = enemyLoadManager.update(1.0);
            if (!enemiesToLoad.isEmpty()) {
                // 打印需要加载的敌人类型
                System.out.println("Time: " + (i + 1) + "s, Enemies to load: " + enemiesToLoad);
            }
        }
    }
}