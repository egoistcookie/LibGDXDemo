package com.lf.entities.card;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.lf.entities.enemy.Enemy;
import com.lf.entities.attackItem.SaintSword;
import com.lf.screen.GameScreen;

import java.util.List;
import java.util.Random;

/**
 * 特殊SSR卡-剑仙，自定义其攻击方式
 */
public class SwordSaintCard extends Card{

    // 攻击贴图
    public Texture attackTexture;
    // 记录卡片放置后已经过去的时间
    private float elapsedTimeSeconds;
    // 新增：生成剑的计时器
    public float swordTimer = 0f;
    // 生成剑的间隔时间：默认1秒
    private float swordIntervaltime = 1f;
    // 仙剑数量
    private int swordCount;
    // 最大仙剑数量
    private int maxSwordCount;

    public SwordSaintCard(World world, GameScreen gameScreen, int towerId, String cardType, float x, float y, AssetManager assetManager, Stage stage, int experience, int starLevel) {
        super(world, gameScreen, towerId, cardType, x, y, assetManager, stage, experience, starLevel);
        elapsedTimeSeconds = 0f;
        // 最大仙剑数量：10*星级
        maxSwordCount = 10*starLevel;
        swordCount = 0;
        attackTexture = assetManager.get("tower/saintSword.png", Texture.class);
    }

    // 自定义攻击方式
    public void update(List<Enemy> enemies, float deltaTime) {

        // 获取卡片位置
        Vector2 towerPosition = super.body.getPosition();

        // 计算游戏已经进行的时间（单位：秒）
        elapsedTimeSeconds += deltaTime;
        swordTimer += deltaTime;
        // 添加绕圆形飞行的箭的逻辑
        float centerX = towerPosition.x;  // 圆心的x坐标
        float centerY = towerPosition.y; // 圆心的y坐标

        // 飞行半径在50到100之间，随机产生
        Random random = new Random();
        int initRadius = random.nextInt(51) + 50;
        // 每次大于生成剑的间隔时间，且生成的仙剑数量少于最大数量，就会生成一柄剑
        if(swordTimer > swordIntervaltime && swordCount <= maxSwordCount){
            // 计时器重置
            swordTimer = 0;
            System.out.println("该生成新的剑了");
            // 初始位置不变，始终位于卡片的头顶
            SaintSword saintSword = new SaintSword(world, centerX, centerY + initRadius, attackTexture, null, this, elapsedTimeSeconds , initRadius);
            arrows.add(saintSword); // 将箭添加到列表中
            swordCount ++ ;
        }
        // 更新每柄仙剑的位置
        for (int i = 0; i < arrows.size(); i++) {
            SaintSword saintSword = (SaintSword) arrows.get(i);
            // 仙剑的移动方法交给仙剑自己处理
            saintSword.update(elapsedTimeSeconds,enemies);
            //如果仙剑已经命中敌人，则销毁仙剑
            if (saintSword.isHit()){ // || (targetEnemy != null && targetEnemy.getDead()) ) {
                // 箭已击中敌人，移除箭
                world.destroyBody(saintSword.getBody()); // 销毁箭的刚体
                arrows.remove(i); // 从列表中移除箭
                i--; // 调整索引
                // 攻击命中敌人后，攻击数减一
                attckCount -- ;
                swordCount -- ;
            }
        }

    }

}
