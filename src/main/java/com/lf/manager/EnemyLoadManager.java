package com.lf.manager;

import com.lf.config.CardTypeConfig;
import com.lf.config.EnemyLoadConfig;
import com.lf.config.EnemyTypeConfig;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// 敌人加载管理器类，负责读取yml文件并管理敌人的加载时序
public class EnemyLoadManager {

    // 存储敌人加载信息的列表
    private final List<EnemyLoadConfig> enemyLoadConfigs;
    // 存储敌人类型信息
    private final List<EnemyTypeConfig> enemyTypeConfigs;
    // 存储卡片类型信息
    private final List<CardTypeConfig> cardTypeConfigs;

    // 构造函数，初始化敌人加载配置列表和已过去的时间
    public EnemyLoadManager() {
        this.enemyLoadConfigs = new ArrayList<>();
        this.enemyTypeConfigs = new ArrayList<>();
        this.cardTypeConfigs = new ArrayList<>();
        // 加载敌人时间配置文件
        loadEnemyTimeConfig();
        // 加载敌人类型配置文件
        loadEnemyTypeConfig();
        // 加载卡片类型配置文件
        loadCardTypeConfig();
    }

    // 加载卡片类型配置文件
    public void loadCardTypeConfig() {
        try {
            // 创建Yaml对象
            Yaml yaml = new Yaml();
            cardTypeConfigs.clear();
            // 打开配置文件的输入流
            // BUG0005-20250225：getResourceAsStream 读取有缓存，强化卡片时需要动态加载，不能使用缓存，改为从FileReader获取
            String filePath = System.getProperty("user.dir") + "/src/main/resources/card_type_config.yml";
            FileReader writer = new FileReader(filePath);
            // 读取配置文件内容
            Map<String, List<Map<String, Object>>> data = yaml.load(writer);
            writer.close();
//            InputStream inputStream = this.getClass()
//                    .getClassLoader()
//                    .getResourceAsStream("card_type_config.yml");
//            // 读取配置文件内容
//            Map<String, List<Map<String, Object>>> config = yaml.load(inputStream);
            // 获取卡片加载配置列表
            List<Map<String, Object>> configList = data.get("cardTypeConfigs");
            for (Map<String, Object> configMap : configList) {
                // 创建卡片加载配置对象
                CardTypeConfig cardTypeConfig = new CardTypeConfig();
                // 设置卡片类型
                cardTypeConfig.setCardType((String) configMap.get("cardType"));
                // 设置稀有度
                cardTypeConfig.setRarity((String) configMap.get("rarity"));
                // 设置卡片等级
                cardTypeConfig.setCardLevel((int) configMap.get("cardLevel"));
                // 设置最大攻击数
                cardTypeConfig.setAttackPower((int) configMap.get("attackPower"));
                // 设置攻击范围
                cardTypeConfig.setAttackRange(Float.parseFloat(configMap.get("attackRange")+""));
                // 设置攻击速度
                cardTypeConfig.setFireRate(Float.parseFloat(configMap.get("fireRate")+""));
                // 设置最大攻击数
                cardTypeConfig.setMaxAttackCount((int) configMap.get("maxAttackCount"));
                // 设置地图模型贴图
                cardTypeConfig.setMapTexture((String) configMap.get("mapTexture"));
                // 设置卡片贴图
                cardTypeConfig.setCardTexture((String) configMap.get("cardTexture"));
                // 设置攻击贴图
                cardTypeConfig.setAttackTexture((String) configMap.get("attackTexture"));
                // 设置物品栏中贴图
                cardTypeConfig.setStuffTexture((String) configMap.get("stuffTexture"));
                // 设置杀敌数
                cardTypeConfig.setKillCount((int) configMap.get("killCount"));
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
                // 设置动画帧切换时间间隔
                enemyTypeConfig.setFrameDuration(Float.parseFloat(configMap.get("frameDuration")+""));
                // 设置经验值
                enemyTypeConfig.setAttackPower((int) configMap.get("attackPower"));
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

    public List<EnemyLoadConfig> getEnemyLoadConfigs() {
        return enemyLoadConfigs;
    }

    public List<EnemyTypeConfig> getEnemyTypeConfigs() {
        return enemyTypeConfigs;
    }

    public List<CardTypeConfig> getCardTypeConfigs() {
        return cardTypeConfigs;
    }

    public static void main(String[] args) {
    }
}