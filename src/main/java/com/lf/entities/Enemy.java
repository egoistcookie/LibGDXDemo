package com.lf.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.lf.core.MyDefenseGame;
import com.lf.manager.EnemyLoadManager;
import com.lf.config.EnemyTypeConfig;
import com.lf.screen.GameScreen;
import com.lf.ui.GameUI;

import java.util.List;

// Enemy类表示游戏中的敌人实体
public class Enemy {
    private Body body; // 敌人的物理刚体
    private Sprite sprite; // 敌人的精灵，用于渲染
    private int health = 5; // 敌人的血量，初始为5
    // 敌人的经验值
    private int experience;
    private String rarity = "N"; // 敌人稀有度
    private String moveTexture; // 敌人贴图

    private String enemyName;
    private String enemyType;
    private Vector2 initialPosition; // 敌人的初始位置

    private Vector2 velocity; // 敌人的移动速度
    private float velocityFloat; // 敌人的移动速度
    private Vector2 targetPosition; // 敌人的目标位置，用于移动到指定地点
    private boolean isMoving; // 标记敌人是否正在移动
    private List<Vector2> pathPoints;
    private int currentPathIndex = 0;
    // 游戏用户界面
    private GameUI gameUI;

    // 新增：存储动画帧的纹理数组
    private Texture[] animationFrames;
    // 新增：当前动画帧索引
    private int currentFrameIndex = 0;
    // 新增：动画计时器
    private float animationTimer = 0f;
    // 新增：动画帧切换时间间隔
    private float frameDuration = 0.2f;

    private boolean isDead = false;

    private boolean isDisappearing;


    public void setDead(boolean dead) {
        this.isDead = dead;
    }
    public boolean getDead() {
        return this.isDead;
    }

    public void setPathPoints(){
        this.pathPoints = pathPoints;
    }

    public Enemy(World world, float x, float y, String enemyType, List<Vector2> pathPoints, GameUI gameUI) {
        this.initialPosition = new Vector2(x, y); // 记录初始位置
        this.targetPosition = null; // 初始化目标位置为null
        this.isMoving = false; // 初始化移动状态为false
        // 根据敌人类型初始化敌人属性：生命值、移动速度、贴图
        initEnemyAttribute(enemyType);
        // 按照moveTexture初始化贴图
        this.animationFrames = new Texture[]{new Texture(moveTexture+"1.png"), new Texture(moveTexture+"2.png")};

        this.pathPoints = pathPoints;
        isDisappearing = false;
        // 创建刚体定义
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody; // 设置为动态刚体
        bodyDef.position.set(x, y); // 设置初始位置

        // 在物理世界中创建刚体
        body = world.createBody(bodyDef);

        // 创建圆形形状
        CircleShape shape = new CircleShape();
        shape.setRadius(animationFrames[0].getWidth() / 2f); // 以纹理宽度的一半作为半径

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
        sprite = new Sprite(animationFrames[0]);
        sprite.setSize(animationFrames[0].getWidth()/4f, animationFrames[0].getHeight()/5f); // 设置精灵大小
        sprite.flip(false, true);//水平翻转
        sprite.setOriginCenter(); // 设置精灵的原点为中心
        // 设置用户数据
        body.setUserData(this);

        this.gameUI = gameUI;
    }

    /**
     * 按地人类行为敌人初始化部分属性
     * @param enemyType
     */
    private void initEnemyAttribute(String enemyType) {
        // 按类型为属性赋值
        EnemyLoadManager enemyLoadManager = MyDefenseGame.enemyLoadManager;
        for (EnemyTypeConfig enemyTypeConfig : enemyLoadManager.getEnemyTypeConfigs()) {
            if(enemyTypeConfig.getEnemyType()!=null && enemyTypeConfig.getEnemyType().equals(enemyType)){
                this.setHealth(enemyTypeConfig.getHealth());
                this.setVelocityFloat(enemyTypeConfig.getVelocity());
                this.setRarity(enemyTypeConfig.getRarity());
                this.setMoveTexture(enemyTypeConfig.getMoveTexture());
                this.setExperience(enemyTypeConfig.getExperience());
            }
        }

    }

    public Enemy(World world, float x, float y, String enemyType, List<Vector2> pathPoints, GameUI gameUI, String enemyName) {
        this(world,x,y,enemyType,pathPoints,gameUI);
        this.enemyName = enemyName;
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
            velocity = direction.scl(velocityFloat * GameScreen.getSclRate());
            body.setLinearVelocity(velocity); // 设置移动速度（可根据需要调整速度值）
            if (currentPosition.dst(targetPoint) < 1f) {
                currentPathIndex++;
            }
        } else {
            // 敌人到达终点
//            body.setLinearVelocity(Vector2.Zero);
            // 血量值减少一点
            gameUI.subHealth();
            // 敌人模型消失
//            body.getWorld().destroyBody(body);
            this.isDead = true;
//            body.setActive(false);
//            setDead(true);
        }
    }

    /**
     * 更新敌人的状态
     */
    public void update(float deltaTime) {
        // 更新动画计时器，制造动画效果
        animationTimer += deltaTime;
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
        move();

        // 计算敌人需要旋转的角度：箭矢位置与敌人位置的夹角，加上135度（因为arrow图片本身是一张45度倾斜的箭矢贴图）
//        if(currentPathIndex < pathPoints.size()){
//            Vector2 targetPoint = pathPoints.get(currentPathIndex);
////            Vector2 currentPosition = body.getPosition();
//            // 针对有明显朝向的敌人，如果目标x坐标大于当前x坐标，则水平翻转一下(只有第一次才需要翻转 待改)
////        if(targetPoint.x > currentPosition.x){
////            sprite.flip(true, false);//水平翻转
////        }
//            double angle = Math.atan2(targetPoint.y, targetPoint.x) + 4 * Math.PI / 4; // 加上 180 度（4 * Math.PI / 4 弧度）
//            sprite.setRotation((float) Math.toDegrees(angle));
////        sprite.setRotation((float) Math.toDegrees(body.getAngle()));
//        }
    }

    public Sprite getSprite() {
        return sprite; // 获取精灵对象
    }

    public Vector2 getPosition() {
        return body.getPosition();
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
            this.isDead = true;
//             body.setActive(false); // 使刚体失效
//            body.getWorld().destroyBody(body);
            // 敌人死后，为界面增加10个金币
            this.gameUI.addGold(10);
            // 敌人死亡，重新生成
//            respawn(initialPosition);
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
        this.gameUI.addGold(10);
    }

    public void dispose() {
        // 纹理要释放
        for(Texture texture :animationFrames){
            texture.dispose();
        }
    }

    public String getEnemyType() {
        return enemyType;
    }

    public void setEnemyType(String enemyType) {
        this.enemyType = enemyType;
    }

    public String getEnemyName() {
        return enemyName;
    }

    public void setEnemyName(String enemyName) {
        this.enemyName = enemyName;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public float getVelocityFloat() {
        return velocityFloat;
    }

    public void setVelocityFloat(float velocityFloat) {
        this.velocityFloat = velocityFloat;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getMoveTexture() {
        return moveTexture;
    }

    public void setMoveTexture(String moveTexture) {
        this.moveTexture = moveTexture;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    //生成yml文本
    public static void main(String[] args) {
        System.out.println("# 敌人加载配置列表，每个元素代表一个敌人的加载配置\n" +
                "enemyLoadConfigs:");
        for(float i = 1; i <110;i ++ ){
            float j = i*2;//2秒间隔
            float loadtime = 2+j;
            System.out.println(String.format("  - # %s",i));
            System.out.println(String.format("    enemyType: saberOne",i));
            System.out.println(String.format("    enemyName: \"Enemy %s\"",i));
            System.out.println(String.format("    loadTime: %s",loadtime));
        }
    }

    public boolean isDisappearing() {
        return isDisappearing;
    }

    public void setDisappearing(boolean disappearing) {
        isDisappearing = disappearing;
    }

    public void setAnimationFrames(Texture[] animationFrames) {
        this.animationFrames = animationFrames;
    }
}