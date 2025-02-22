package com.lf.entities.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.lf.entities.card.NecromancerCard;
import com.lf.screen.GameScreen;
import com.lf.ui.GameUI;

import java.util.List;

/**
 * 死灵法师召唤出来的死灵战士，友军单位
 */
public class GhostWarrior extends Enemy{


    public GhostWarrior(World world, float x, float y, String enemyType, List<Vector2> pathPoints, GameUI gameUI, String enemyName) {
        super(world, x, y, enemyType, pathPoints, gameUI,enemyName);
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
     * 敌人的移动逻辑
     */
    public void move(List<Enemy> enemies, NecromancerCard necromancerCard) {

        boolean isExistEnemy = false;
        // 移动的优先级：
        // 一、范围内有敌人，则朝敌人移动
        // 二、与敌人相遇，停止移动，贴图改为攻击贴图，每秒造成一次伤害
        // 三、范围内没有敌人，则朝之前的目标地点移动

        // 第一步：找到距离最近的敌人
        Vector2 currentPosition = body.getPosition();
        // 对手不为空的情况下，永远朝向对手移动
        Enemy dstSmallEnemy = getSamllEnemy(enemies,oppName);

        if(dstSmallEnemy != null){
            // 获取战士与最近敌人的距离
            Vector2 targetPoint = dstSmallEnemy.getBody().getPosition();
            float dst = currentPosition.dst(targetPoint);
            if (dst < 100f) {
                isExistEnemy = true;

                // 如果作为对手的敌人死亡（可能被其他攻击致死）
                if(oppName.equals(dstSmallEnemy.enemyName) && dstSmallEnemy.isDead){
                    // 又可以去阻拦别的敌人
                    oppName = "";
                    // 向下一个最近的目标移动
//                    return;
                }

                // 先索敌：双向绑定：亡灵战士没有对手的时候，才可以阻挡敌人，且只能锁住没有对手的敌人
                if(oppName.isEmpty() && dstSmallEnemy.oppName.isEmpty()){
                    // 锁定对手
                    oppName = dstSmallEnemy.enemyName;
                    // 同时为敌人也锁定对手
                    dstSmallEnemy.oppName = enemyName;

                    System.out.println(enemyName+"绑定对手："+oppName);
                }

                // 与对手相距10像素之内时，视为接触
                if(dst < 10f && !(!oppName.isEmpty() && oppName.equals(dstSmallEnemy.enemyName) &&
                        !dstSmallEnemy.oppName.isEmpty())){
                    // 何必去追，直接索敌最近单位
                    System.out.println("距离很近："+oppName+":"+dstSmallEnemy.enemyName);
                }
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
                    // 战士攻击判定在先，因此即使敌人死了，也可以攻击战士一次，否则太强
                    if (dstSmallEnemy.attackTimer >= 2*dstSmallEnemy.frameDuration){ // && !enemy.isDead) {
                        // 被敌人攻击
                        this.takeDamage(dstSmallEnemy.getAttackPower());
                        // 重置计时器
                        dstSmallEnemy.attackTimer = 0f;
                    }
                    // 如果自己先死
                    if(this.isDead){
                        // 则放行敌人
                        dstSmallEnemy.setBlock(false);
                        dstSmallEnemy.oppName = "";
                    }
                    // 如果敌人死亡
                    if(dstSmallEnemy.isDead){
                        System.out.println(dstSmallEnemy.enemyName +"被"+dstSmallEnemy.oppName+"杀死:");
                        // 又可以去阻拦别的敌人
                        oppName = "";
                        return;
                    }
                }else{

                    System.out.println(enemyName+"朝："+dstSmallEnemy.enemyName+"移动");
                    // 对手未死，则朝对手移动
                    Vector2 direction = targetPoint.cpy().sub(currentPosition).nor();
                    velocity = direction.scl(velocityFloat * GameScreen.getSclRate());
                    body.setLinearVelocity(velocity); // 设置移动速度（可根据需要调整速度值）

                    //如果目标x大于当前x，则应朝右
                    isRight = targetPoint.x > currentPosition.x;
                    // 每次变向，都要翻转一次
                    if(isRight != isRightOld){
                        sprite.flip(true, false);//水平翻转
                        isRightOld = isRight;
                    }
                }

            }
        }


//        for(Enemy enemy : enemies){
//            // 目标为敌人
//            Vector2 targetPoint = enemy.getBody().getPosition();
//            float dst = currentPosition.dst(targetPoint);
//            if (dst < 100f) {
//                isExistEnemy = true;
//
//                // 先索敌：双向绑定：亡灵战士没有对手的时候，才可以阻挡敌人，且只能锁住没有对手的敌人
//                if("".equals(oppName) && "".equals(enemy.oppName)){
//                    // 锁定对手
//                    oppName = enemy.enemyName;
//                    // 同时为敌人也锁定对手
//                    enemy.oppName = enemyName;
//                    System.out.println(enemyName+"绑定："+oppName);
//                }else{
//                    // 向下一个目标移动
//                    continue;
//                }
//
//                // 如果作为对手的敌人死亡（可能被其他攻击致死）
//                if(oppName.equals(enemy.enemyName) && enemy.isDead){
//                    // 又可以去阻拦别的敌人
//                    oppName = "";
//                    // 向下一个目标移动
//                    continue;
//                }
//
//                // 与对手相距10像素之内时，视为接触
//                System.out.println(oppName +"对象:"+enemy.enemyName);
//                System.out.println(enemy.oppName +"对象:"+enemyName);
//                if(dst < 10f && !oppName.isEmpty() && oppName.equals(enemy.enemyName) &&
//                        !enemy.oppName.isEmpty()){
//                    // 阻挡对手
//                    enemy.setBlock(true);
//                    // 自己也停止移动
//                    body.setLinearVelocity(0,0);
//                    // 开始攻击，战士攻击判定在先
//                    float deltaTime = Gdx.graphics.getDeltaTime();
//                    attackTimer += deltaTime;
//                    // 攻击间隔时间为两倍的动画帧切换时间间隔
//                    if (attackTimer >= 2*frameDuration && !this.isDead) {
//                        enemy.takeDamage(this.getAttackPower());
//                        // 重置计时器
//                        attackTimer = 0f;
//                    }
//                    // 敌人的攻击计时器也开始运转
//                    enemy.attackTimer += deltaTime;
//                    // 战士攻击判定在先，因此即使敌人死了，也可以攻击战士一次，否则太强
//                    if (enemy.attackTimer >= 2*enemy.frameDuration){ // && !enemy.isDead) {
//                        // 被敌人攻击
//                        this.takeDamage(enemy.getAttackPower());
//                        // 重置计时器
//                        enemy.attackTimer = 0f;
//                    }
//                    // 如果自己先死
//                    if(this.isDead){
//                        // 则放行敌人
//                        enemy.setBlock(false);
//                        enemy.oppName = "";
//                    }
//                    // 如果敌人死亡
//                    if(enemy.isDead){
//                        // 又可以去阻拦别的敌人
//                        oppName = "";
//                    }
//                    continue;
//                }else{
//
//                    // 对手未死，则朝对手移动
//                    Vector2 direction = targetPoint.cpy().sub(currentPosition).nor();
//                    velocity = direction.scl(velocityFloat * GameScreen.getSclRate());
//                    body.setLinearVelocity(velocity); // 设置移动速度（可根据需要调整速度值）
//
//                    //如果目标x大于当前x，则应朝右
//                    isRight = targetPoint.x > currentPosition.x;
//                    // 每次变向，都要翻转一次
//                    if(isRight != isRightOld){
//                        sprite.flip(true, false);//水平翻转
//                        isRightOld = isRight;
//                    }
//                }
//            }
//        }

        // 如果范围内存在敌人，就不要再继续往下判断
        if(isExistEnemy){
            return;
        }

        // 范围内没有敌人，则朝之前的目标地点移动
        if (currentPathIndex < pathPoints.size()) {
            Vector2 targetPoint = pathPoints.get(currentPathIndex);
            Vector2 direction = targetPoint.cpy().sub(currentPosition).nor();
            velocity = direction.scl(velocityFloat * GameScreen.getSclRate());
            body.setLinearVelocity(velocity); // 设置移动速度（可根据需要调整速度值）
            // 如果达到目标点
            if (currentPosition.dst(targetPoint) < 1f) {
                currentPathIndex++;
            }
            //如果目标x大于当前x，则应朝右
            isRight = targetPoint.x > currentPosition.x;
            // 每次变向，都要翻转一次
            if(isRight != isRightOld){
                sprite.flip(true, false);//水平翻转
                isRightOld = isRight;
            }
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
