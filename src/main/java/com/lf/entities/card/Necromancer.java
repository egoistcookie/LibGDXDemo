package com.lf.entities.card;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.lf.entities.Enemy;
import com.lf.entities.attackItem.SaintSword;
import com.lf.screen.GameScreen;

import java.util.List;
import java.util.Random;

/**
 * SSR卡片：死灵法师
 * 攻击方式：召唤死灵士兵攻击敌人
 */
public class Necromancer extends Card{
    // 记录卡片放置后已经过去的时间
    private float elapsedTimeSeconds;

    public Necromancer(World world, GameScreen gameScreen, int towerId, String cardType, float x, float y, AssetManager assetManager, Stage stage, int experience, int starLevel) {
        super(world, gameScreen, towerId, cardType, x, y, assetManager, stage, experience, starLevel);
        elapsedTimeSeconds = 0f;
    }

    // 自定义攻击方式
    public void update(List<Enemy> enemies, float deltaTime) {

        // 攻击方式一：传统模式，自行出手攻击
        super.update(enemies,deltaTime);

        // 攻击方式二：召唤士兵，士兵有其独立的生命历程和攻击方式
        // 获取卡片位置
        Vector2 towerPosition = super.body.getPosition();

        // 计算游戏已经进行的时间（单位：秒）
        elapsedTimeSeconds += deltaTime;

    }


}
