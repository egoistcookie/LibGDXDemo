package com.lf;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.lf.core.MyDefenseGame;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 塔防游戏主程序：水墨风格塔防游戏
 */
@SpringBootApplication
public class TowerDefenseApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(TowerDefenseApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
//        config.setTitle("Tower Defense Game");
//        config.setWindowedMode(800, 600);
//        new Lwjgl3Application(new TowerDefenseGame(), config);

        // 创建 Screen 实现类的实例，并传入 ApplicationAdapter 实例
//        MainMenuScreen screen = new MainMenuScreen(adapter);

        // 创建游戏实例
        MyDefenseGame game = new MyDefenseGame();

        // 配置 Lwjgl3Application
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Tower");
        config.setWindowedMode(800, 600);
        // 设置最小尺寸和最大尺寸
        config.setWindowSizeLimits(400,300,1200,900);

        // 启动 Lwjgl3Application
        new Lwjgl3Application(game, config);
    }
}