package com.lf.entities.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.lf.entities.card.NecromancerCard;
import com.lf.screen.GameScreen;
import com.lf.ui.GameUI;

import java.util.List;

/**
 * 死灵法师召唤出来的死灵战士，友军单位
 */
public class GhostWarrior extends Enemy{


    public GhostWarrior(World world, Stage stage, float x, float y, String enemyType, List<Vector2> pathPoints, GameUI gameUI, String enemyName) {
        super(world, stage,x, y, enemyType, pathPoints, gameUI,enemyName);
        // 死灵战士贴图朝左，需要先翻转一次
        sprite.flip(true, false);//水平翻转
    }

    /**
     * 更新亡灵战士的状态
     */
    public void update(float deltaTime, List<Enemy> enemies, NecromancerCard necromancerCard) {
        // 更新动画计时器，制造动画效果
        // 更新敌人位置，传入deltaTime渲染间隔时间，渲染敌人动作
        float deltaTimeGdx = Gdx.graphics.getDeltaTime();
        animationTimer += deltaTimeGdx;
        if (animationTimer >= frameDuration) {
            // 切换到下一帧
            currentFrameIndex = (currentFrameIndex + 1) % animationFrames.length;
            // 更新精灵的纹理
            sprite.setTexture(animationFrames[currentFrameIndex]);
            // 重置计时器
            animationTimer = 0f;
        }
        // 更新精灵的位置和旋转角度，使其与刚体同步
        sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2f, body.getPosition().y - sprite.getHeight() / 2f);
        // 旋转一百八十度
        double angle = 4 * Math.PI / 4; // 加上 180 度（4 * Math.PI / 4 弧度）
        sprite.setRotation((float) Math.toDegrees(angle));
        // 调用移动逻辑
        move(enemies,necromancerCard);
    }

    /**
     * 移动的三种方式：
     * 一、与敌人相遇，停止移动，贴图改为攻击贴图，每秒造成一次伤害
     * 二、范围内有敌人，则朝敌人移动
     * 三、范围内没有敌人，则朝之前的目标地点移动（目标路径为倒序的敌人行进路径）
     */
    public void move(List<Enemy> enemies, NecromancerCard necromancerCard) {

        boolean isExistEnemy = false;

        // 第一步：找到距离最近的敌人（若已有对手，则以对手作为最近敌人）
        Vector2 currentPosition = body.getPosition();
        // 对手 oppName 不为空的情况下，永远朝向对手移动
        Enemy dstSmallEnemy = getSamllEnemy(enemies,oppName);
        if(dstSmallEnemy != null){
            // 获取战士与最近敌人的距离
            Vector2 targetPoint = dstSmallEnemy.getBody().getPosition();
            float dst = currentPosition.dst(targetPoint);
            if (dst < 100f) {
                // 距离小于100，视为范围内有敌人
                isExistEnemy = true;

                // 如果作为对手的敌人死亡（可能被其他攻击致死），重置对手名
                if(oppName.equals(dstSmallEnemy.enemyName) && dstSmallEnemy.isDead){
                    // 又可以去阻拦别的敌人
                    oppName = "";
                }

                // 索敌：双向绑定：亡灵战士没有对手的时候，且范围内的该敌人也没有对手时，才可索敌
                if(oppName.isEmpty() && dstSmallEnemy.oppName.isEmpty()){
                    // 锁定对手
                    oppName = dstSmallEnemy.enemyName;
                    // 同时为敌人也锁定对手
                    dstSmallEnemy.oppName = enemyName;
                    System.out.println(enemyName+"绑定对手："+oppName);
                }
                // 与对手相距10像素之内时，视为接触
//                if(dst < 10f && !(!oppName.isEmpty() && oppName.equals(dstSmallEnemy.enemyName) &&
//                        !dstSmallEnemy.oppName.isEmpty())){
//                    // 何必去追，直接索敌最近单位
//                    System.out.println("距离很近："+oppName+":"+dstSmallEnemy.enemyName);
//                }
                // 移动方式一、与敌人相遇，停止移动，贴图改为攻击贴图，每秒造成一次伤害
                // 范围近到10像素内，且确认是对手，开始攻击与死亡判断
                if(dst < 10f && !oppName.isEmpty() && !dstSmallEnemy.oppName.isEmpty() && dstSmallEnemy.oppName.equals(enemyName)){
                    // 阻挡对手
                    dstSmallEnemy.setBlock(true);
                    // 自己也停止移动
                    body.setLinearVelocity(0,0);
                    // 开始攻击，战士攻击判定在先
                    float deltaTime = Gdx.graphics.getDeltaTime();
                    attackTimer += deltaTime;
                    // 攻击间隔时间为两倍的动画帧切换时间间隔
                    if (attackTimer >= 2*frameDuration && !this.isDead) {
                        dstSmallEnemy.takeDamage(this.getAttackPower());
                        // 重置计时器
                        attackTimer = 0f;
                    }
                    // 敌人的攻击计时器也开始运转
                    dstSmallEnemy.attackTimer += deltaTime;
                    // 战士攻击判定在先，因此即使敌人死了，也可以攻击战士一次，否则战士太强
                    if (dstSmallEnemy.attackTimer >= 2*dstSmallEnemy.frameDuration){ // && !enemy.isDead) {
                        // 被敌人攻击
                        this.takeDamage(dstSmallEnemy.getAttackPower());
                        // 重置敌人计时器
                        dstSmallEnemy.attackTimer = 0f;
                    }
                    // 如果战士死亡
                    if(this.isDead){
                        System.out.println(enemyName +"被"+oppName+"杀死:");
                        // 则放行敌人
                        dstSmallEnemy.setBlock(false);
                        // 敌人的对手置空
                        dstSmallEnemy.oppName = "";
                    }
                    // 如果敌人死亡
                    if(dstSmallEnemy.isDead){
                        System.out.println(dstSmallEnemy.enemyName +"被"+dstSmallEnemy.oppName+"杀死:");
                        // 战士又可以去阻拦别的敌人
                        oppName = "";
                        //如果敌人死亡，卡片经验按照 敌人的经验值 增加
                        necromancerCard.addExperience(dstSmallEnemy.getExperience());
                        // 等待下一次循环
                        return;
                    }
                }else{
                    // 移动范围二、范围内有敌人，则朝敌人移动
                    // 对手在10-100像素之间，则朝对手移动
                    Vector2 direction = targetPoint.cpy().sub(currentPosition).nor();
                    velocity = direction.scl(velocityFloat * GameScreen.getSclRate());
                    body.setLinearVelocity(velocity); // 设置移动速度（可根据需要调整速度值）
                    // 判断是否翻转
                    flip(targetPoint,currentPosition);
                }
            }
        }

        // 如果范围内存在敌人，就不要再继续往下判断
        if(isExistEnemy){
            return;
        }

        // 三、范围内没有敌人，则朝之前的目标地点移动
        if (currentPathIndex < pathPoints.size()) {
            Vector2 targetPoint = pathPoints.get(currentPathIndex);
            Vector2 direction = targetPoint.cpy().sub(currentPosition).nor();
            velocity = direction.scl(velocityFloat * GameScreen.getSclRate());
            body.setLinearVelocity(velocity); // 设置移动速度（可根据需要调整速度值）
            // 如果达到目标点
            if (currentPosition.dst(targetPoint) < 1f) {
                currentPathIndex++;
            }
            // 判断是否翻转
            flip(targetPoint,currentPosition);
        } else {
            // 亡灵战士到达终点就保持静止
            body.setLinearVelocity(0,0);
        }
    }

    // 寻找最近单位
    private Enemy getSamllEnemy(List<Enemy> enemies, String oppName) {
        Enemy dstSmallEnemy = null ;
        Vector2 currentPosition = body.getPosition();
        float dstSmall = 0f;
        boolean firstEnemy = true;
        // 对手不为空的情况下，永远朝向对手移动
        if(!oppName.isEmpty()){
            for (Enemy enemy : enemies) {
                if(enemy.getEnemyName().equals(oppName)){
                    dstSmallEnemy = enemy;
                    return dstSmallEnemy;
                }
            }
        }
        for (Enemy enemy : enemies) {
            // 绕开已死亡和被挡住的敌人
            if(enemy.isDead || enemy.isBlock){
                continue;
            }
            // 取法师与点的距离
            Vector2 targetPoint = enemy.getBody().getPosition();
            float dst = currentPosition.dst(targetPoint);
            if (firstEnemy) {
                firstEnemy = false;
                dstSmall = dst;
                dstSmallEnemy = enemy;
            } else {
                // 冒泡不断取最小值
                if (dst < dstSmall) {
                    dstSmall = dst;
                    dstSmallEnemy = enemy;
                }
            }
        }
        return dstSmallEnemy;
    }


}
