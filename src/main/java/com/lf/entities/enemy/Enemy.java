package com.lf.entities.enemy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.lf.constants.EnemyState;
import com.lf.core.MyDefenseGame;
import com.lf.manager.EnemyLoadManager;
import com.lf.config.EnemyTypeConfig;
import com.lf.screen.GameScreen;
import com.lf.ui.GameUI;

import java.util.List;

// Enemy类表示游戏中的敌人实体
public class Enemy {
    // 敌人的物理刚体
    public Body body;
    // 敌人的精灵，用于渲染
    public Sprite sprite;
    // 敌人的血量
    public int health;
    // 敌人的经验值
    public int experience;
    // 敌人稀有度
    public String rarity;
    // 敌人贴图
    public String moveTexture;
    // 敌人名称
    public String enemyName;
    // 对手名称，防止多对一群殴
    public String oppName;
    // 敌人类型
    public String enemyType;
    // 敌人的初始位置
    public Vector2 initialPosition;
    // 敌人的移动速度
    public Vector2 velocity;
    // 敌人的移动速度
    public float velocityFloat;
    // 敌人的目标位置，用于移动到指定地点
    public Vector2 targetPosition;
    // 标记敌人是否正在移动
    public boolean isMoving;
    // 路径点集合
    public List<Vector2> pathPoints;
    // 最近经过的路径点
    public int currentPathIndex = 0;
    // 游戏用户界面
    public GameUI gameUI;
    // 新增：存储动画帧的纹理数组
    public Texture[] animationFrames;
    // 新增：当前动画帧索引
    public int currentFrameIndex = 0;
    // 新增：动画计时器
    public float animationTimer = 0f;
    // 新增：攻击计时器
    public float attackTimer = 0f;
    // 新增：动画帧切换时间间隔
    public float frameDuration = 0.2f;
    // 是否已死亡
    public boolean isDead = false;
    // 是否被阻挡
    public boolean isBlock = false;
    // 敌人状态
    public EnemyState enemyStatus;
    // 是否已消散
    public boolean isDisappearing;
    // 现在是否应该朝右
    public boolean isRight;
    // 原来是否朝右
    public boolean isRightOld;
    // 攻击力
    private int attackPower;
    // 收到伤害的标签
    public VisLabel takeDamageLabel;

    public Enemy(World world, Stage stage, float x, float y, String enemyType, List<Vector2> pathPoints, GameUI gameUI) {
        this.initialPosition = new Vector2(x, y); // 记录初始位置
        this.targetPosition = null; // 初始化目标位置为null
        this.isMoving = false; // 初始化移动状态为false
        enemyStatus = EnemyState.INITIAL;
        oppName = "";
        // 根据敌人类型初始化敌人属性：生命值、移动速度、贴图
        initEnemyAttribute(enemyType);
        // 按照moveTexture初始化贴图
        this.animationFrames = new Texture[]{new Texture("enemy/"+moveTexture+"1.png"), new Texture("enemy/"+moveTexture+"2.png")};

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

        // 伤害标签
        VisLabel.LabelStyle redLabelStyle = new VisLabel.LabelStyle(gameUI.getSkin().get("default", VisLabel.LabelStyle.class));
        redLabelStyle.fontColor = Color.RED;
        takeDamageLabel = new VisLabel("",redLabelStyle);
        // 直接设置 goldLabel 的位置 ,显示在敌人头顶
        takeDamageLabel.setPosition(x,y+20);
        stage.addActor(takeDamageLabel);

    }

    /**
     * 按地人类行为敌人初始化部分属性
     * @param enemyType 敌人类型
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
                this.setFrameDuration(enemyTypeConfig.getFrameDuration());
                this.setAttackPower(enemyTypeConfig.getAttackPower());
            }
        }

    }

    public Enemy(World world, Stage stage, float x, float y, String enemyType, List<Vector2> pathPoints, GameUI gameUI, String enemyName) {
        this(world,stage,x,y,enemyType,pathPoints,gameUI);
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
        // 如果被阻挡，则停止移动
        if(isBlock){
            body.setLinearVelocity(Vector2.Zero);
            return;
        }
        // 当前路径点小于路径集合大小，则继续前往下一路径点
        if (currentPathIndex < pathPoints.size()) {
            Vector2 targetPoint = pathPoints.get(currentPathIndex);
            Vector2 currentPosition = body.getPosition();
            Vector2 direction = targetPoint.cpy().sub(currentPosition).nor();
            velocity = direction.scl(velocityFloat * GameScreen.getSclRate());
            body.setLinearVelocity(velocity); // 设置移动速度（可根据需要调整速度值）
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
            // 游戏界面的血量值减少一点
            // BUG0003-20250223：敌人到达终点后不会立即消散，此后不能重复调用subHealth
            if(!this.isDead){
                gameUI.subHealth();
            }
            this.isDead = true;
            // 状态设置为已到达终点
            enemyStatus = EnemyState.REACHED_DESTINATION;
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
//        System.out.printf("%s受到了%d点伤害！！！%n",enemyName,damage);
//        System.out.printf("%s剩余生命值：%d！！！%n",enemyName,health);
        // 直接设置 goldLabel 的位置 ,显示在敌人头顶
        if(damage > 0){
            takeDamageLabel.setText("-" + damage);
            takeDamageLabel.setPosition(body.getPosition().x-10,body.getPosition().y+20);// 启用富文本支持
            // 使用Timer在1秒后移除提示窗口
            Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                takeDamageLabel.setText("");
            }
        }, 1f);
        }

        if (this.health <= 0) {
            // 敌人死亡，处理死亡逻辑
            this.isDead = true;
            // 状态设置为倒下
            enemyStatus = EnemyState.FALLEN;
            // 敌人死后，为界面增加10个金币
            this.gameUI.addGold(10);
            // 敌人死亡，重新生成
            // respawn(initialPosition);
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

    // 翻转方法：判断目标与当前位置的x轴大小之差
    public void flip(Vector2 targetPoint, Vector2 currentPosition) {

        //如果目标x大于当前x，则应朝右
        isRight = targetPoint.x > currentPosition.x;
        // 每次变向，都要翻转一次
        if(isRight != isRightOld){
            sprite.flip(true, false);//水平翻转
            isRightOld = isRight;
        }
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
        if(disappearing){
            // 状态设置为已消散
            enemyStatus = EnemyState.DISAPPEARED;
        }
    }

    public void setAnimationFrames(Texture[] animationFrames) {
        this.animationFrames = animationFrames;
    }

    public void setPathPoints(){
        this.pathPoints = pathPoints;
    }

    public float getFrameDuration() {
        return frameDuration;
    }

    public void setFrameDuration(float frameDuration) {
        this.frameDuration = frameDuration;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public void setBlock(boolean block) {
        isBlock = block;
        if(block){
            enemyStatus = EnemyState.BLOCKED;
        }else if(enemyStatus == EnemyState.BLOCKED){
            // BUG0004-20250223：已经倒地的敌人，不能重新更新回moving状态（可能同时有两次setBlock）
            enemyStatus = EnemyState.MOVING;
        }
        System.out.println(enemyName+":"+block+":enemyStatus:"+enemyStatus);
    }

    public EnemyState getEnemyStatus() {
        return enemyStatus;
    }

    public void setEnemyStatus(EnemyState enemyStatus) {
        this.enemyStatus = enemyStatus;
    }


    public void setDead(boolean dead) {
        this.isDead = dead;
    }
    public boolean getDead() {
        return this.isDead;
    }
}