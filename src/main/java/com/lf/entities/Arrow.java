package com.lf.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.lf.screen.GameScreen;


// Arrow类表示游戏中的箭实体
public class Arrow {
    private Body body; // 箭的物理刚体
    private Sprite sprite; // 箭的精灵，用于渲染
    private Enemy target; // 箭的目标敌人
    private boolean isHit; // 标记箭是否已经击中敌人
    private Sound arrowSound; // 箭矢射出的音效
    private Tower tower; // 箭矢所属的防御塔

    public Arrow(World world, float x, float y, Texture texture, Enemy target, Tower tower) {
        // 创建刚体定义
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody; // 设置为动态刚体
        bodyDef.position.set(x, y); // 设置初始位置
        this.tower = tower;

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

        // 加载音效
        this.arrowSound = Gdx.audio.newSound(Gdx.files.internal("wav/arror_start.wav")); // 加载箭矢出现的音效文件
        // 播放音效
        this.arrowSound.play(0.5f); //表示以 50% 的音量播放音效
    }

    public void update() {
        if (!isHit) {
            // 计算箭到目标敌人的方向向量
            Vector2 direction = target.getBody().getPosition().sub(body.getPosition());
            direction.nor(); // 归一化方向向量

            // 设置箭的速度，使其缓缓射向敌人
            body.setLinearVelocity(direction.scl(60 * GameScreen.getSclRate()));

            // 更新精灵的位置和旋转角度，使其与刚体同步
            sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2f, body.getPosition().y - sprite.getHeight() / 2f);

            // 计算箭矢需要旋转的角度：箭矢位置与敌人位置的夹角，加上135度（因为arrow图片本身是一张45度倾斜的箭矢贴图）
            double angle = Math.atan2(direction.y, direction.x) + 3 * Math.PI / 4; // 加上 135 度（3 * Math.PI / 4 弧度）
            sprite.setRotation((float) Math.toDegrees(angle));
//            sprite.setRotation((float) Math.toDegrees(body.getAngle()));

            //有可能在箭矢飞行的过程中敌人就已经dead
            if(target.getDead()){
                this.arrowSound.dispose(); // 释放音效资源
                return;
            }

            // 检查箭是否击中敌人
            if (body.getPosition().dst(target.getBody().getPosition()) < 1f) {
                target.takeDamage(1); // 敌人受到一点伤害
                //如果敌人死亡，防御塔经验按照 敌人的经验值 增加
                if(target.getDead()){
                    tower.addExperience(target.getExperience());
                }
                isHit = true; // 标记箭已击中敌人
                // 释放音效资源，避免内存泄漏
                this.arrowSound.dispose(); // 释放音效资源
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

    // 返回攻击的目标
    public Enemy getTarget() {
        return target;
    }
}