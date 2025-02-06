package com.lf.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

// Tower类表示游戏中的防御塔实体
public class Tower {
    // 防御塔的物理刚体，用于处理物理相关行为
    private Body body;
    // 防御塔的精灵，用于图形渲染
    private Sprite sprite;
    // 防御塔的攻击范围
    private float attackRange = 100f;
    // 新增：用于记录激光终点（敌人位置）
    private Vector2 laserEndPoint;
    // 是否第一次更新
    private boolean firstUpdate = true;

    // 构造函数，用于创建防御塔实例
    public Tower(World world, float x, float y, Texture texture) {
        // 创建刚体定义
        BodyDef bodyDef = new BodyDef();
        // 设置为静态刚体，因为防御塔通常不会自行移动
        bodyDef.type = BodyDef.BodyType.StaticBody;
        // 设置刚体的初始位置
        bodyDef.position.set(x, y);

        // 在物理世界中创建刚体
        body = world.createBody(bodyDef);

        // 创建圆形形状，用于定义防御塔的碰撞范围
        CircleShape shape = new CircleShape();
        // 设置圆形形状的半径，这里以纹理宽度的一半作为半径
        shape.setRadius(texture.getWidth() / 2f);

        // 创建夹具定义，用于将形状与刚体关联，并设置物理属性
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        // 设置密度
        fixtureDef.density = 1f;

        // 为刚体添加夹具
        body.createFixture(fixtureDef);

        // 释放形状资源，因为已经将其与刚体关联，不再需要单独的形状对象
        shape.dispose();

        // 创建精灵
        sprite = new Sprite(texture);
        // 设置精灵的大小
        sprite.setSize(texture.getWidth(), texture.getHeight());
        // 设置精灵的原点为中心，方便旋转和定位
        sprite.setOriginCenter();
        // 设置精灵的位置
        sprite.setPosition(x - sprite.getWidth() / 2f, y - sprite.getHeight() / 2f);
        // 初始化激光终点
        laserEndPoint = new Vector2();

        body.setUserData(this);
    }

    // 更新方法，用于检查敌人是否在攻击范围内并进行攻击
    public void update(Enemy enemy) {
        //防止初始化时出现激光
        if (firstUpdate) {
            firstUpdate = false;
            return;
        }
        // 获取防御塔的位置
        Vector2 towerPosition = body.getPosition();
        // 获取敌人的位置
        Vector2 enemyPosition = enemy.getPosition();
        // 计算防御塔与敌人之间的距离
        float distance = towerPosition.dst(enemyPosition);
        // 如果敌人在攻击范围内
        if (distance <= attackRange) {
            // 让敌人受到伤害
            enemy.takeDamage();
            // 当攻击时，更新激光终点为敌人位置
            laserEndPoint.set(enemyPosition);
        }
    }

    // 获取防御塔的精灵
    public Sprite getSprite() {
        return sprite;
    }

    // 新增：获取激光终点
    public Vector2 getLaserEndPoint() {
        return laserEndPoint;
    }

    // 新增方法，获取攻击范围
    public float getAttackRange() {
        return attackRange;
    }
}