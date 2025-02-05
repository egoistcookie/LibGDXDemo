package com.lf;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.lf.core.TowerDefenseGame;
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
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Tower Defense Game");
        config.setWindowedMode(800, 600);
        new Lwjgl3Application(new TowerDefenseGame(), config);
    }
}