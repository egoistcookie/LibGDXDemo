package com.lf.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.lf.ui.GameUI;

import java.util.List;

public class Enemy {
    private Body body; // 敌人的物理刚体
    private Sprite sprite; // 敌人的精灵，用于渲染
    private int health = 5; // 敌人的血量，初始为5
    private Vector2 initialPosition; // 敌人的初始位置
    private Vector2 velocity; // 敌人的移动速度
    private Vector2 targetPosition; // 敌人的目标位置，用于移动到指定地点
    private boolean isMoving; // 标记敌人是否正在移动
    private List<Vector2> pathPoints;
    private int currentPathIndex = 0;
    // 游戏用户界面
    private GameUI gameUI;

    public void setPathPoints(){
        this.pathPoints = pathPoints;
    }

    public Enemy(World world, float x, float y, Texture texture, List<Vector2> pathPoints, GameUI gameUI) {
        this.initialPosition = new Vector2(x, y); // 记录初始位置
        this.targetPosition = null; // 初始化目标位置为null
        this.isMoving = false; // 初始化移动状态为false

        this.pathPoints = pathPoints;
        // 创建刚体定义
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody; // 设置为动态刚体
        bodyDef.position.set(x, y); // 设置初始位置

        // 在物理世界中创建刚体
        body = world.createBody(bodyDef);

        // 创建圆形形状
        CircleShape shape = new CircleShape();
        shape.setRadius(texture.getWidth() / 2f); // 以纹理宽度的一半作为半径

        // 创建夹具定义
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        // 设置碰撞过滤器
        fixtureDef.filter.categoryBits = 0x0002;  // 假设敌人的类别为2
        fixtureDef.filter.maskBits = 0x0001;     // 只与地面等类别碰撞，假设地面类别为1
        // 为刚体添加夹具
        body.createFixture(fixtureDef);

        // 释放形状资源
        shape.dispose();

        // 创建精灵
        sprite = new Sprite(texture);
        sprite.setSize(texture.getWidth(), texture.getHeight()); // 设置精灵大小
        sprite.setOriginCenter(); // 设置精灵的原点为中心
        // 设置用户数据
        body.setUserData(this);

        this.gameUI = gameUI;
    }

    /**
     * 设置敌人的目标位置，并开始移动
     * @param target 目标位置
     */
    public void setTargetPosition(Vector2 target) {
        this.targetPosition = target; // 设置目标位置
        this.isMoving = true; // 标记敌人开始移动
    }

    /**
     * 敌人的移动逻辑
     */
    public void move() {
        if (currentPathIndex < pathPoints.size()) {
            Vector2 targetPoint = pathPoints.get(currentPathIndex);
            Vector2 currentPosition = body.getPosition();
            Vector2 direction = targetPoint.cpy().sub(currentPosition).nor();
            velocity = direction.scl(10f);
            body.setLinearVelocity(velocity); // 设置移动速度（可根据需要调整速度值）
            if (currentPosition.dst(targetPoint) < 1f) {
                currentPathIndex++;
            }
        } else {
            // 敌人到达终点
            body.setLinearVelocity(Vector2.Zero);
        }
        //敌人朝固定位置移动的逻辑
//        if (isMoving && targetPosition != null) { // 如果敌人正在移动且目标位置不为空
//            Vector2 currentPosition = body.getPosition(); // 获取敌人当前位置
//            Vector2 direction = targetPosition.cpy().sub(currentPosition); // 计算敌人到目标位置的方向向量
//            float distance = direction.len(); // 计算敌人到目标位置的距离
//
//            if (distance > 0.1f) { // 如果距离大于0.1f（可根据需要调整）
//                direction.nor(); // 归一化方向向量
//                velocity = direction.scl(2f); // 设置移动速度（可根据需要调整速度值）
//                body.setLinearVelocity(velocity); // 设置刚体的线性速度
//            } else {
//                isMoving = false; // 到达目标位置，停止移动
//                targetPosition = null; // 清空目标位置
//                body.setLinearVelocity(Vector2.Zero); // 停止刚体的移动
//            }
//        }
    }

    /**
     * 更新敌人的状态
     */
    public void update() {
        move(); // 调用移动逻辑

        // 更新精灵的位置和旋转角度，使其与刚体同步
        sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2f, body.getPosition().y - sprite.getHeight() / 2f);
        sprite.setRotation((float) Math.toDegrees(body.getAngle()));
    }

    public Sprite getSprite() {
        return sprite; // 获取精灵对象
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public boolean isDead() {
        return health <= 0;
    }

    public Body getBody() {
        return body;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void takeDamage(int damage) {
        this.health -= damage; // 敌人受到伤害
        if (this.health <= 0) {
            // 敌人死亡，处理死亡逻辑
            // body.setActive(false); // 使刚体失效
            // 敌人死亡，重新生成
            respawn(initialPosition);
        }
    }

    private void respawn(Vector2 initialPosition) {
        // 销毁当前刚体
        body.getWorld().destroyBody(body);

        // 重新创建刚体
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(initialPosition);

        body = body.getWorld().createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(sprite.getWidth() / 2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        // 设置碰撞过滤器
        fixtureDef.filter.categoryBits = 0x0002;  // 假设敌人的类别为2
        fixtureDef.filter.maskBits = 0x0001;     // 只与地面等类别碰撞，假设地面类别为1
        body.createFixture(fixtureDef);
        shape.dispose();

        // 重置生命值
        this.health = 5;
        // 设置用户数据，以便CustomBox2DDebugRenderer能隐藏其刚体
        body.setUserData(this);
        // 设置相同的速度
        body.setLinearVelocity(velocity);
        // 从最初位置开始移动
        currentPathIndex = 0;
        // 敌人死后，为界面增加10个金币
        gameUI.addGold(10);
    }
}