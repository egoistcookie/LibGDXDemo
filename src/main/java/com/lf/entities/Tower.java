package com.lf.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.lf.screen.GameScreen;

import java.util.ArrayList;
import java.util.List;

// Tower类表示游戏中的防御塔实体
public class Tower {
    // 防御塔的物理刚体，用于处理物理相关行为
    private Body body;
    // 防御塔的精灵，用于图形渲染
    private Sprite sprite;
    // 防御塔唯一id
    private int towerId;
    // 防御塔类型
    private String towerType;
    // 防御塔的攻击范围
    private float attackRange = 100f;
    // 新增：用于记录激光终点（敌人位置）
    private Vector2 laserEndPoint;
    public List<Arrow> arrows; // 箭矢列表，用于存储发射的箭矢

    private World world;
    // 攻击箭矢贴图
    private Texture arrowTexture;
    private float timeSinceLastFire; // 距离上次发射的时间
    // 同时发动的攻击数
    private float attckCount;
    // 同时发动的攻击最大数
    private float maxAttckCount;
    private float fireRate; // 发射频率
    // 新增：存储动画帧的纹理数组
    private Texture[] animationFrames;
    // 新增：当前动画帧索引
    private int currentFrameIndex = 0;
    // 新增：动画计时器
    private float animationTimer = 0f;
    // 新增：动画帧切换时间间隔
    private float frameDuration = 0.4f;
    // 防御塔的当前等级，初始为1级
    private int level;
    // 防御塔的当前星级，初始为1星
    private int starLevel;
    // 防御塔的当前经验值，初始为0
    private int experience;
    // 等级数量标签
    private VisLabel levelLabel;
    // 星级标签
    private VisLabel starLevelLabel;
    // 构造函数，用于创建防御塔实例
    public Tower(World world, int towerId, String towerType,float x, float y, Texture arrowTexture, AssetManager assetManager, Stage stage, int level, int starLevel) {
        this.towerType = towerType;
        this.towerId = towerId;
        experience = 0;
        this.level = level;
        this.starLevel = starLevel;
        Texture texture1 = assetManager.get("tower/"+towerType+"1.png", Texture.class);
        animationFrames = new Texture[]{texture1, assetManager.get("tower/"+towerType+"2.png", Texture.class)}; ;
        // 创建刚体定义
        BodyDef bodyDef = new BodyDef();
        // 设置为静态刚体，因为防御塔通常不会自行移动
        bodyDef.type = BodyDef.BodyType.StaticBody;
        // 设置刚体的初始位置
        bodyDef.position.set(x, y);
        this.world = world; // 设置物理世界
        this.arrowTexture = arrowTexture; // 设置箭的纹理
        this.maxAttckCount = 3; // 同时攻击敌人数量
        this.timeSinceLastFire = 0;
        this.fireRate = 0.5f; // 攻击速度，即间隔多长时间（秒）射一箭

        // 在物理世界中创建刚体
        body = world.createBody(bodyDef);

        // 创建圆形形状，用于定义防御塔的碰撞范围
        CircleShape shape = new CircleShape();
        // 设置圆形形状的半径，这里以纹理宽度的一半作为半径
        shape.setRadius(texture1.getWidth() / 2f);

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
        sprite = new Sprite(texture1);
        // 设置精灵的大小
        sprite.setSize(texture1.getWidth(), texture1.getHeight());
        // 设置精灵的原点为中心，方便旋转和定位
        sprite.setOriginCenter();
        // 设置精灵的位置
        sprite.setPosition(x - sprite.getWidth() / 2f, y - sprite.getHeight() / 2f);
        // 初始化攻击的重点（即敌人位置）
        laserEndPoint = new Vector2();

        this.arrows = new ArrayList<>(); // 初始化箭矢列表

        //设置用户数据，以便CustomBox2DDebugRenderer能隐藏其刚体
        body.setUserData(this);

        // 等级标签
        levelLabel = new VisLabel("" + level);
        // 直接设置 goldLabel 的位置 ,显示在防御塔头顶
        levelLabel.setPosition(x-10,y+20);
        stage.addActor(levelLabel);

        // 等级标签
        starLevelLabel = new VisLabel(starLevel==1?"*":"**");
        // 直接设置 goldLabel 的位置 ,显示在防御塔头顶，等级上方的位置
        starLevelLabel.setPosition(x-10,y+30);
        stage.addActor(starLevelLabel);

    }

    // 更新方法，用于检查敌人是否在攻击范围内并进行攻击
    public void update(List<Enemy> enemies, float deltaTime) {
        // 计算攻击间隔时间
        timeSinceLastFire += deltaTime;
        // 计算动画间隔时间
        animationTimer += deltaTime;
        // 获取防御塔的位置
        Vector2 towerPosition = body.getPosition();
        // 是否攻击范围内有敌人
        boolean hasRangeEnemy = false;
        // 遍历敌人列表，检查是否有敌人进入攻击范围
        for (Enemy enemy : enemies) {
            if (body.getPosition().dst(enemy.getBody().getPosition()) < attackRange) {
                // 已达到攻击间隔时间，并且同时攻击数未达到上限，并且敌人未死亡，发起攻击
                if (timeSinceLastFire >= fireRate && attckCount < maxAttckCount && !enemy.getDead()) {
                    // 攻击介质应该从防御塔坐标的前方一点射出，会比较自然
                    Arrow arrow = new Arrow(world, body.getPosition().x + 20, body.getPosition().y +10, arrowTexture, enemy, this);
                    arrows.add(arrow); // 将箭添加到列表中
                    timeSinceLastFire = 0;
                    // 同时攻击数+1
                    attckCount ++;
                    // 敌人进入攻击范围再开始动作
                    hasRangeEnemy = true;
                }
            }
        }
        // 敌人进入攻击范围再开始动作
        if(hasRangeEnemy){
            if (animationTimer >= frameDuration) {
                // 切换到下一帧
                currentFrameIndex = (currentFrameIndex + 1) % animationFrames.length;
                // 更新精灵的纹理
                sprite.setTexture(animationFrames[currentFrameIndex]);
                // 重置动作计时器
                animationTimer = 0f;
            }
        }
        // 更新箭的位置
        for (int i = 0; i < arrows.size(); i++) {
            Arrow arrow = arrows.get(i);
            arrow.update();
            Enemy targetEnemy = arrow.getTarget();
            //如果攻击已经命中敌人，或者敌人在攻击介质的飞行过程中已经死亡，则销毁攻击介质
            if (arrow.isHit() || targetEnemy.getDead()) {
                // 箭已击中敌人，移除箭
                world.destroyBody(arrow.getBody()); // 销毁箭的刚体
                arrows.remove(i); // 从列表中移除箭
                i--; // 调整索引
                // 攻击命中敌人后，攻击数减一
                attckCount -- ;
            }
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

    public void dispose() {
        // 箭矢纹理释放
        arrowTexture = null;
        // 隐藏标签
        levelLabel.setVisible(false);
        starLevelLabel.setVisible(false);
    }

    /**
     * 增加经验值
     * @param addExperience
     */
    public void addExperience(int addExperience) {
        this.experience += addExperience;
        System.out.println("经验值增加后："+this.experience);
        // 计算等级
        calcLevel();
        // 显示等级
        showLevel();
    }

    private void showLevel() {
        levelLabel.setText("" + this.level);
        starLevelLabel.setText(starLevel==1?"*":"**");
    }

    // 检查是否满足升级条件的方法
    private void calcLevel() {
        // 经验值达到1，从1级升到2级，按下述逻辑例推，最高10级
        switch (experience) {
            //TODO 待优化
            case 1: level = 2;break;
            case 5: level = 3;break;
            case 10: level = 4;break;
            case 20: level = 5;break;
            case 30: level = 6;break;
            case 40: level = 7;break;
            case 50: level = 8;break;
            case 60: level = 9;break;
            case 70: level = 10;break;
            default:
                // 不满足升级条件，不做操作
                break;
        }
    }

    // 升星
    public boolean superPass(Stuff[] stuffes){
        // 实现代码 TODO
        String towerType = this.getTowerType();
        for(int i =0 ; i <stuffes.length ; i++){
            // 物品栏中是否有当前类型卡片
            if(stuffes[i]!=null && stuffes[i].getStuffType().equals(towerType)){
                Stuff stuff = stuffes[i];
                System.out.println("升星！");
                // 判断是否同星级且满等级
                if(this.getStarLevel()==stuff.getStuffStarLevel() && stuff.getStuffLevel() == 10){
                    // 物品栏中删去该卡片
                    stuffes[i] = null;
                    // 星级上升
                    this.starLevel ++;
                    // 显示等级
                    showLevel();
                    return true;
                }else{
                    System.out.println("素材星级："+stuff.getStuffStarLevel());
                    System.out.println("素材等级："+stuff.getStuffLevel());
                }
            }
        }
        return false;
    }
    public Body getBody() {
        return body;
    }
    public String getTowerType() {
        return towerType;
    }
    public void setTowerType(String towerType) {
        this.towerType = towerType;
    }
    public int getTowerId() {
        return towerId;
    }

    public void setTowerId(int towerId) {
        this.towerId = towerId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getStarLevel() {
        return starLevel;
    }

    public void setStarLevel(int starLevel) {
        this.starLevel = starLevel;
    }
}