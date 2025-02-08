package com.lf.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Enemy {
    private Body body; // 敌人的物理刚体
    private Sprite sprite; // 敌人的精灵，用于渲染
    private int health = 5; // 敌人的血量，初始为0
    private Vector2 initialPosition; // 敌人的初始位置
    private Vector2 velocity; // 敌人的移动速度

    public Enemy(World world, float x, float y, Texture texture) {
        //每次new都有10点
        this.initialPosition = new Vector2(x, y); // 记录初始位置
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
        //设置用户数据
        body.setUserData(this);
    }

    public void move(Vector2 velocity) {
        this.velocity = velocity; // 记录移动速度
        body.setLinearVelocity(velocity); // 设置刚体的线性速度
    }

    public void update() {
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
            //body.setActive(false); // 使刚体失效
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
        //设置用户数据，以便CustomBox2DDebugRenderer能隐藏其刚体
        body.setUserData(this);
        // 设置相同的速度
        body.setLinearVelocity(velocity);
    }

}