package com.lf.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;
import java.util.List;

// Tower类表示游戏中的防御塔实体
public class Tower {
    // 防御塔的物理刚体，用于处理物理相关行为
    private Body body;
    private Vector2 position; // 防御塔的位置
    // 防御塔的精灵，用于图形渲染
    private Sprite sprite;
    // 防御塔的攻击范围
    private float attackRange = 100f;
    // 新增：用于记录激光终点（敌人位置）
    private Vector2 laserEndPoint;
    // 是否第一次更新
    private boolean firstUpdate = true;

    private float attackInterval = 2f; // 攻击间隔，2秒一次
    public List<Arrow> arrows; // 箭矢列表，用于存储发射的箭矢

    private World world;
    private Texture arrowTexture;
    private float timeSinceLastFire; // 距离上次发射的时间
    private float fireRate; // 发射频率
    // 构造函数，用于创建防御塔实例
    public Tower(World world, float x, float y, Texture texture, Texture arrowTexture) {
        // 创建刚体定义
        BodyDef bodyDef = new BodyDef();
        // 设置为静态刚体，因为防御塔通常不会自行移动
        bodyDef.type = BodyDef.BodyType.StaticBody;
        // 设置刚体的初始位置
        bodyDef.position.set(x, y);
        this.position = new Vector2(x, y); // 初始化防御塔的位置
        this.world = world; // 设置物理世界
        this.arrowTexture = arrowTexture; // 设置箭的纹理

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
        fixtureDef.filter.categoryBits = 0x0003;  // 假设防御塔的类别为3
        fixtureDef.filter.maskBits = 0x0001;     // 只与地面等类别碰撞，假设地面类别为1

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
        // 初始化攻击的重点（即敌人位置）
        laserEndPoint = new Vector2();

        this.arrows = new ArrayList<>(); // 初始化箭矢列表

        // 启动定时器，每隔 attackInterval 秒执行一次攻击逻辑
//        Timer.schedule(new Timer.Task() {
//            @Override
//            public void run() {
//                attack(); // 执行攻击逻辑
//            }
//        }, attackInterval, attackInterval);

        //设置用户数据，以便CustomBox2DDebugRenderer能隐藏其刚体
        body.setUserData(this);
        this.timeSinceLastFire = 0;
        this.fireRate = 2.0f; // 每2秒发射一次
    }

    // 更新方法，用于检查敌人是否在攻击范围内并进行攻击
    public void update(List<Enemy> enemies, float deltaTime) {
        // 获取防御塔的位置
        Vector2 towerPosition = body.getPosition();
        // 获取敌人的位置
//        Vector2 enemyPosition = enemy.getPosition();
//        // 计算防御塔与敌人之间的距离
//        float distance = towerPosition.dst(enemyPosition);
//        // 如果敌人在攻击范围内
//        if (distance <= attackRange) {
//            // 让敌人受到伤害
//            enemy.takeDamage();
//            // 当攻击时，更新激光终点为敌人位置
//            laserEndPoint.set(enemyPosition);
//        }
        // 遍历敌人列表，检查是否有敌人进入攻击范围
        for (Enemy enemy : enemies) {
            if (body.getPosition().dst(enemy.getBody().getPosition()) < attackRange) {
                // 敌人进入攻击范围，生成箭
                timeSinceLastFire += deltaTime;
                if (timeSinceLastFire >= fireRate) {
                    Arrow arrow = new Arrow(world, body.getPosition().x, body.getPosition().y, arrowTexture, enemy);
                    arrows.add(arrow); // 将箭添加到列表中
                    timeSinceLastFire = 0;
                }
            }
        }
        // 更新箭的位置
        for (int i = 0; i < arrows.size(); i++) {
            Arrow arrow = arrows.get(i);
            arrow.update();
            if (arrow.isHit()) {
                // 箭已击中敌人，移除箭
                world.destroyBody(arrow.getBody()); // 销毁箭的刚体
                arrows.remove(i); // 从列表中移除箭
                i--; // 调整索引
            }
        }
//        Iterator<Arrow> iterator = arrows.iterator();
//        while (iterator.hasNext()) {
//            Arrow arrow = iterator.next();
//            if (arrow.isOutOfBounds()) {
//                iterator.remove(); // 使用迭代器的 remove 方法安全删除元素
//            }
//        }
    }

    // 渲染防御塔和箭矢
//    public void render(SpriteBatch batch) {
//        sprite.draw(batch); // 绘制防御塔
//        for (Arrow arrow : arrows) {
//            arrow.render(batch); // 绘制箭矢
//        }
//    }

    // 攻击方法，用于创建并发射箭矢
//    private void attack() {
//        Texture arrowTexture = new Texture(Gdx.files.internal("arrow.png")); // 加载箭矢的纹理
//        Arrow arrow = new Arrow(arrowTexture, position.x, position.y); // 创建箭矢对象
//        arrows.add(arrow); // 将箭矢添加到箭矢列表中
//    }

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