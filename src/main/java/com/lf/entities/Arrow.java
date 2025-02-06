// FILEPATH: LibGDXDemo/src/main/java/com/lf/entities/Arrow.java
package com.lf.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Arrow {
    private Body body; // 箭的物理刚体
    private Sprite sprite; // 箭的精灵，用于渲染
    private Enemy target; // 箭的目标敌人
    private boolean isHit; // 标记箭是否已经击中敌人

    public Arrow(World world, float x, float y, Texture texture, Enemy target) {
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
        fixtureDef.filter.categoryBits = 0x0004;  // 假设箭矢的类别为4
        fixtureDef.filter.maskBits = 0x0001;     // 只与地面等类别碰撞，假设地面类别为1

        // 为刚体添加夹具
        body.createFixture(fixtureDef);

        // 释放形状资源
        shape.dispose();

        // 创建精灵
        sprite = new Sprite(texture);
        sprite.setSize(texture.getWidth(), texture.getHeight()); // 设置精灵大小
        sprite.setOriginCenter(); // 设置精灵的原点为中心

        this.target = target; // 设置箭的目标敌人
        this.isHit = false; // 初始化箭未击中敌人
        //设置用户数据，以便CustomBox2DDebugRenderer能隐藏其刚体
        body.setUserData(this);
    }

    public void update() {
        if (!isHit) {
            // 计算箭到目标敌人的方向向量
            Vector2 direction = target.getBody().getPosition().sub(body.getPosition());
            direction.nor(); // 归一化方向向量

            // 设置箭的速度，使其缓缓射向敌人
            body.setLinearVelocity(direction.scl(5));

            // 更新精灵的位置和旋转角度，使其与刚体同步
            sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2f, body.getPosition().y - sprite.getHeight() / 2f);
            sprite.setRotation((float) Math.toDegrees(body.getAngle()));

            // 检查箭是否击中敌人
            if (body.getPosition().dst(target.getBody().getPosition()) < 0.5f) {
                target.takeDamage(1); // 敌人受到一点伤害
                isHit = true; // 标记箭已击中敌人
            }
        }
    }

    public Sprite getSprite() {
        return sprite; // 获取精灵对象
    }

    public Body getBody() {
        return body; // 获取刚体对象
    }

    public boolean isHit() {
        return isHit; // 返回箭是否已击中敌人
    }
}