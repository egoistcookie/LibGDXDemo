package com.lf.entities.card;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.lf.entities.enemy.Enemy;
import com.lf.entities.enemy.GhostWarrior;
import com.lf.screen.GameScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * SSR卡片：死灵法师
 * 攻击方式：召唤死灵士兵攻击敌人
 */
public class NecromancerCard extends Card{
    // 记录卡片放置后已经过去的时间
    private float elapsedTimeSeconds;

    // 新增：生成战士的计时器
    public float warriorTimer = 0f;
    // 生成战士的间隔时间：默认1秒
    private float warriorIntervaltime = 5f;
    // 战士数量
    private int warriorCount;
    // 最大战士数量
    private final int maxWarriorCount;
    // 战士列表，用于存储已生成的战士
    public List<GhostWarrior> ghostWarriores;
    // 战士运动路径集合1
    private List<Vector2> pathPoints1; // 使用 Vector2 存储点的坐标
    // 战士运动路径集合2
    private List<Vector2> pathPoints2;

    public NecromancerCard(World world, GameScreen gameScreen, int towerId, String cardType, float x, float y, AssetManager assetManager, Stage stage, int experience, int starLevel) {
        super(world, gameScreen, towerId, cardType, x, y, assetManager, stage, experience, starLevel);
        elapsedTimeSeconds = 0f;
        // 最大战士数量：1*星级
        maxWarriorCount = 3*starLevel;
        warriorCount = 0;
        pathPoints1 = gameScreen.getPathPoints1();
        pathPoints2 = gameScreen.getPathPoints2();
        ghostWarriores = new ArrayList<>();
    }

    // 自定义攻击方式
    public void update(List<Enemy> enemies, float deltaTime) {

        // 攻击方式一：传统模式，自行出手攻击
        super.update(enemies,deltaTime);

        // 攻击方式二：召唤战士，战士有其独立的生命历程和攻击方式
        // 计算游戏已经进行的时间（单位：秒）
        elapsedTimeSeconds += deltaTime;
        warriorTimer += deltaTime;

        // 每次大于生成战士的间隔时间，且生成的战士数量少于最大数量，就会生成战士
        if(warriorTimer > warriorIntervaltime && warriorCount < maxWarriorCount){
            // 计时器重置
            warriorTimer = 0;
            System.out.println("该生成新的战士了");
            // 在亡灵法师下侧坐标生成
            Vector2 masterPosition = super.body.getPosition();
            // 处理路径，沿着敌人行进路线倒序往上走，先去到距离最近的点
            float dstSmall = 0f;
            boolean firstVector = true;
            int dstSmallInt = 0;
            // 随机挑选1或2线路作为基准线路 basePathPoints
            Random random = new Random();
            int initPathPointsNo = random.nextInt(2);
            System.out.println(initPathPointsNo);
            List<Vector2> basePathPoints = pathPoints1;
            if(initPathPointsNo ==1){
                basePathPoints = pathPoints2;
            }
            System.out.printf("选择%s号线路%n",initPathPointsNo);
            Vector2 dstSmallVector = masterPosition;
            for(int i=0 ; i <basePathPoints.size(); i++){
                Vector2 vector2 = basePathPoints.get(i);
                // 取法师与点的距离
                float dst = body.getPosition().dst(vector2);
                if(firstVector){
                    firstVector = false;
                    dstSmall = dst;
                    dstSmallVector = vector2;
                }else{
                    // 冒泡取一个最小值
                    if(dst < dstSmall){
                        dstSmall = dst;
                        dstSmallVector = vector2;
                        dstSmallInt = i;
                    }
                }
            }
            System.out.printf("路径集合PathPoints1中距离法师最近的点为第%d个点%n",dstSmallInt);
            System.out.println("路径集合PathPoints1中距离法师最近的点为："+dstSmallVector.x+":"+dstSmallVector.y);
            // 顺序倒置，把PathPoints1中剩余的点放入一个新的pathPoints，让战士倒着走这条路径
            List<Vector2> newPathPoints = new ArrayList<>();
            for(int j=dstSmallInt ; j>=0; j--){
                // 将点添加到列表中
                newPathPoints.add(basePathPoints.get(j));
            }
            GhostWarrior warrior = new GhostWarrior(world,stage,dstSmallVector.x,dstSmallVector.y,"ghostWarrior",newPathPoints,gameScreen.getGameUI(),"ghostWarrior "+warriorCount);
            ghostWarriores.add(warrior);
            warriorCount ++ ;
        }

        // 更新每个战士的位置
        for (int i = 0; i < ghostWarriores.size(); i++) {
            GhostWarrior warrior = ghostWarriores.get(i);
            // 战士的移动方法交给战士自己处理
            warrior.update(elapsedTimeSeconds,enemies,this);
            //如果战士已经死亡，则销毁战士
            if (warrior.getDead()){ // || (targetEnemy != null && targetEnemy.getDead()) ) {
                System.out.println("战士go die");
                world.destroyBody(warrior.getBody()); // 销毁战士的刚体
                ghostWarriores.remove(i); // 从列表中移除战士
                i--; // 调整索引
                warriorCount -- ;
            }
        }

    }

    // 亡灵法师自定义dispose方法
    public void dispose() {
        super.dispose();
        // 如果回收亡灵法师，其麾下亡灵战士的对手需要释放阻塞状态
        for(GhostWarrior warrior : ghostWarriores){
            // 根据名称获取到敌人
            Enemy enemy = gameScreen.getEnemyByName(warrior.oppName);
            if(enemy!=null){
                // 放行敌人
                enemy.setBlock(false);
                // 将敌人索敌置空
                enemy.oppName = "";
            }
        }
    }

    public static void main(String[] args) {

        Random random = new Random();
        int initPathPointsNo = random.nextInt(2);
        System.out.println(initPathPointsNo);
    }

}
