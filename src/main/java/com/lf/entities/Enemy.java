package com.lf.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Enemy {
    private Body body; // 敌人的物理刚体
    private Sprite sprite; // 敌人的精灵，用于渲染
    private int health = 10; // 敌人的血量，初始为10

    public Enemy(World world, float x, float y, Texture texture) {
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

    public void takeDamage() {
        if (health > 0) {
            health--;
        }
    }

    public boolean isDead() {
        return health <= 0;
    }

    public Body getBody() {
        return body;
    }

    // 处理敌人受到伤害的方法
    public void takeDamage(float damage) {
        // 这里可以添加具体的伤害处理逻辑，例如减少敌人的生命值等
        System.out.println("Enemy took " + damage + " damage.");
    }
}