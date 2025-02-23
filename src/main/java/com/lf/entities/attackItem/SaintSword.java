package com.lf.entities.attackItem;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.lf.entities.enemy.Enemy;
import com.lf.entities.card.Card;
import com.lf.screen.GameScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SaintSword extends Arrow{

    private static final Logger log = LoggerFactory.getLogger(SaintSword.class);
    private float radius ;

    public SaintSword(World world, float x, float y, Texture texture, Enemy target, Card card) {
        super(world, x, y, texture, target, card);
    }

    public SaintSword(World world, float x, float y, Texture texture, Enemy target, Card card, float createTime) {
        super(world, x, y, texture, target, card, createTime);
    }

    public SaintSword(World world, float x, float y, Texture texture, Enemy target, Card card, float createTime, float radius) {
        super(world, x, y, texture, target, card, createTime);
        this.radius = radius;
    }

    /**
     * 重写仙剑的运动方式，有三种情况
     * 一、剑仙的攻击范围内没有敌人，仙剑按初始轨道绕剑仙飞行
     * 二、剑仙的攻击范围内存在活体敌人，仙剑飞向敌人，接触时造成伤害，且自身消散
     * 三、剑仙的攻击范围内存在刚死敌人（其他攻击介质致死），仙剑需飞回剑仙周身之初始轨道
     */
    public void update(float elapsedTimeSeconds, List<Enemy> enemies) {

        // 遍历敌人列表，检查是否有敌人进入剑仙的攻击范围
        boolean isExistEnemy = false;
        // 是否在原本的飞行轨道
        boolean isInRange = false;
        for (Enemy enemy : enemies) {
            if (card.body.getPosition().dst(enemy.getBody().getPosition()) < card.getAttackRange()) {
                // 设置仙剑的目标坐标为敌人
                this.setTarget(enemy);
                // 判断是否已经接触敌人
                if (!isHit) {
                    // BUG00002-20250222：若在箭矢飞行的过程中敌人就已经dead，索敌下一个，防止攻击浪费
                    if(target.getDead()){
                        // 释放音效资源
//                        this.arrowSound.dispose();
                        // 寻找下一个未死亡的敌人
                        continue;
                    }
                    // 若把body.setLinearVelocity和sprite.setPosition放在 continue 的判断之前，则有一个仙剑插在尸体上的画面，直到两秒后敌人从主界面被移除才会去找下一敌人
                    // 计算箭到目标敌人的方向向量
                    Vector2 direction = enemy.getBody().getPosition().sub(body.getPosition());
                    direction.nor(); // 归一化方向向量
                    // 设置仙剑的速度，使其向敌人移动
                    body.setLinearVelocity(direction.scl(60 * GameScreen.getSclRate()));
                    // 更新仙剑精灵的位置和旋转角度，使其与刚体同步
                    sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2f, body.getPosition().y - sprite.getHeight() / 2f);
                    // 计算仙剑需要旋转的角度：仙剑位置与敌人位置的向量夹角，加上135度（因为arrow图片本身是一张45度倾斜的箭矢贴图）
                    double angle = Math.atan2(direction.y, direction.x) + 3 * Math.PI / 4; // 加上 135 度（3 * Math.PI / 4 弧度）
                    sprite.setRotation((float) Math.toDegrees(angle));
                    // 存在未死亡敌人
                    isExistEnemy = true;
                    // 检查箭是否击中敌人
                    if (body.getPosition().dst(target.getBody().getPosition()) < 1f) {
                        // 按照防御塔的攻击力来造成伤害
                        target.takeDamage(card.getAttackPower());
                        //如果敌人死亡，卡片经验按照 敌人的经验值 增加
                        if(target.getDead()){
                            card.addExperience(target.getExperience());
                        }
                        // 标记箭已击中敌人
                        isHit = true;
                        // 释放音效资源，避免内存泄漏
                        this.arrowSound.dispose();
                    }
                }
            }
        }
        // 飞行半径
        float radius = this.getRadius();
        float distant = card.body.getPosition().dst(this.getBody().getPosition());
        // 判断剑仙与仙剑的距离是否小于等于飞行半径
        // 生成后的仙剑，与剑仙的距离可能是半径+或-0.00001（计算了自身的宽度）
        // 仙剑飞回来之后，可能与剑仙保持了更远的距离（0.02左右），但刻意保持一个0.01f，会有一种更cool的指向剑仙的飞剑的闪烁画面
        if(distant <= radius + 0.01f){
            isInRange = true;
        }
//        else{
//            System.out.println("distant:"+distant);
//            System.out.println("radius:"+radius);
//            System.out.println("isInRange:"+isInRange);
//        }
        // 如果剑仙攻击范围内没有敌人，且该仙剑距离剑仙的位置未到半径之内
        // 第二种运动方式：仙剑需飞回剑仙周身之初始轨道
        if(!isExistEnemy && !isInRange){
            // 设置仙剑的目标坐标为剑仙
//            this.setTarget(card.body);
            // 计算箭到剑仙的方向向量
            Vector2 direction = card.body.getPosition().sub(body.getPosition());
            direction.nor(); // 归一化方向向量
            // 设置仙剑的速度，使其向剑仙移动
            body.setLinearVelocity(direction.scl(60 * GameScreen.getSclRate()));
            // 更新仙剑精灵的位置和旋转角度，使其与刚体同步
            sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2f, body.getPosition().y - sprite.getHeight() / 2f);
            // 计算仙剑需要旋转的角度：仙剑位置与敌人位置的向量夹角，加上135度（因为arrow图片本身是一张45度倾斜的箭矢贴图）
            double angle = Math.atan2(direction.y, direction.x) + 3 * Math.PI / 4; // 加上 135 度（3 * Math.PI / 4 弧度）
            sprite.setRotation((float) Math.toDegrees(angle));
        }

        // 剑仙的攻击范围里没有敌人，且仙剑到剑仙的距离小于等于飞行半径
        // 第一种运动方式：仙剑按初始轨道绕剑仙飞行
        if(!isExistEnemy && isInRange){
            float angle = (elapsedTimeSeconds - this.getCreateTime()) * 2; // 角度随时间变化
            float arrowX = card.body.getPosition().x + radius * (float) Math.cos(angle);
            float arrowY = card.body.getPosition().y + radius * (float) Math.sin(angle);
            this.getBody().setTransform(arrowX, arrowY, 0);
            // 更新精灵的位置和旋转角度，使其与刚体同步
            sprite.setPosition(arrowX - sprite.getWidth() / 2f,
                    arrowY - sprite.getHeight() / 2f);// 计算角度
            // 使贴图角度也随时间变化
            // 计算目标点相对于当前位置的向量
            Vector2 direction = new Vector2(card.body.getPosition().x - sprite.getX(), card.body.getPosition().y - sprite.getY());
            // 剑的朝向与圆的半径垂直（加1个Math.PI，等于旋转45°）
            double arrowAngle = Math.atan2(direction.y, direction.x) + 1 * Math.PI / 4;
            sprite.setRotation((float) Math.toDegrees(arrowAngle));
        }


    }


    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
