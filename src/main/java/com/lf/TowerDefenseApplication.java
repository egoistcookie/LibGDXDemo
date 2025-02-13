package com.lf;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.lf.core.TowerDefenseGame;
import com.lf.ui.MainMenuScreen;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

        // 创建 ApplicationAdapter 实现类的实例
        TowerDefenseGame adapter = new TowerDefenseGame();

        // 创建 Screen 实现类的实例，并传入 ApplicationAdapter 实例
//        MainMenuScreen screen = new MainMenuScreen(adapter);

        // 创建游戏实例
        Game game = new Game() {
            @Override
            public void create() {
                // 设置当前屏幕为 MyScreen
                setScreen(new MainMenuScreen(adapter));
            }
        };

        // 配置 Lwjgl3Application
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Tower Defense Game1");
        config.setWindowedMode(800, 600);

        // 启动 Lwjgl3Application
        new Lwjgl3Application(game, config);
    }
}