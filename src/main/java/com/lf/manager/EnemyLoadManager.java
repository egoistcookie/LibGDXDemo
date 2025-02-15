package com.lf.manager;

import com.lf.config.EnemyLoadConfig;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 敌人加载管理器类，负责读取yml文件并管理敌人的加载时序
public class EnemyLoadManager {

    private List<EnemyLoadConfig> enemyLoadConfigs; // 存储敌人加载配置的列表
    private double elapsedTime; // 已经过去的时间

    // 构造函数，初始化敌人加载配置列表和已过去的时间
    public EnemyLoadManager() {
        this.enemyLoadConfigs = new ArrayList<>();
        this.elapsedTime = 0.0;
        // 调用loadConfig方法加载配置文件
        loadConfig();
    }

    // 加载配置文件的方法
    private void loadConfig() {
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