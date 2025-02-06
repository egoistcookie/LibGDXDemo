package com.lf.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.lf.debugRenderer.CustomBox2DDebugRenderer;
import com.lf.entities.Arrow;
import com.lf.entities.Enemy;
import com.lf.entities.Tower;
import com.lf.ui.GameUI;

import java.util.ArrayList;
import java.util.List;

// TowerDefenseGame类是游戏的核心类，管理游戏的主要逻辑和渲染
public class TowerDefenseGame extends ApplicationAdapter {
    // 正交相机，用于定义游戏的视图范围
    private OrthographicCamera camera;
    // 物理世界，用于管理游戏中的物理对象和模拟
    private World world;
    // 调试渲染器，用于渲染物理世界的调试信息
    private CustomBox2DDebugRenderer debugRenderer;
    // 游戏用户界面
    private GameUI gameUI;
    // 精灵批处理，用于高效地渲染精灵
    private SpriteBatch batch;
    // 游戏中的敌人对象
    private Enemy enemy;
    // 敌人的纹理，用于绘制敌人的图形
    private Texture enemyTexture;
    // 游戏中的防御塔对象
    private Tower tower;
    // 防御塔的纹理，用于绘制防御塔的图形
    private Texture towerTexture;
    // 箭矢的纹理，用于绘制箭矢的图形
    private Texture arrowTexture;
    // 新增：用于绘制形状（激光）的渲染器
    private ShapeRenderer shapeRenderer;
    private List<Enemy> enemies; // 敌人列表
    private List<Arrow> arrows; // 箭的列表

    // 创建方法，在游戏启动时调用，用于初始化游戏资源和对象
    @Override
    public void create() {
        // 创建正交相机，并设置其投影为非正交模式，视口大小为800x600
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        // 创建物理世界，重力向量为(0, 0)，不启用休眠
        world = new World(new Vector2(0, 0), false);

        // 创建调试渲染器
        debugRenderer = new CustomBox2DDebugRenderer();

        // 创建游戏用户界面
        gameUI = new GameUI();

        // 创建精灵批处理
        batch = new SpriteBatch();

        // 加载敌人的纹理
        enemyTexture = new Texture(Gdx.files.internal("enemy.png"));


        // 创建敌人列表
        enemies = new ArrayList<>();

        // 创建敌人对象
        enemy = new Enemy(world, 100, 100, enemyTexture);
        enemies.add(enemy);

        // 设置敌人的移动速度
        enemy.move(new Vector2(1, 0));

        // 加载防御塔的纹理
        towerTexture = new Texture(Gdx.files.internal("tower.png"));

        // 加载箭的纹理
        arrowTexture = new Texture(Gdx.files.internal("arrow.png"));
        // 创建防御塔对象，初始位置为(200, 150)
        tower = new Tower(world, 200, 150, towerTexture,arrowTexture);
        // 创建箭的列表
        arrows = new ArrayList<>();

        // 初始化形状渲染器
        shapeRenderer = new ShapeRenderer();
        // 设置形状渲染器自动选择形状类型
        shapeRenderer.setAutoShapeType(true);
    }

    // 渲染方法，在每一帧调用，用于更新游戏状态和绘制图形
    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        // 清除屏幕，设置背景颜色为黑色
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 推进物理世界的模拟，步长为1/60秒，速度迭代次数为6，位置迭代次数为2
        world.step(1 / 60f, 6, 2);

        // 更新相机
        camera.update();

        // 如果敌人存在，则更新敌人的状态

        // 更新敌人的位置和旋转角度
        for (Enemy enemy : enemies) {
            if (enemy!= null) {
                enemy.update();
                // 更新防御塔的状态，检查是否攻击敌人
            }
        }

        // 更新塔的逻辑
        tower.update(enemies,deltaTime);

        // 设置精灵批处理的投影矩阵为相机的投影矩阵
        batch.setProjectionMatrix(camera.combined);
        // 开始精灵批处理
        batch.begin();

        // 如果敌人存在，则绘制敌人的精灵
        // 渲染敌人
        for (Enemy enemy : enemies) {
            enemy.getSprite().draw(batch);
        }
        // 渲染塔
        tower.getSprite().draw(batch);
        // 渲染箭
        for (Arrow arrow : tower.arrows) {
            arrow.getSprite().draw(batch);
        }

        // 结束精灵批处理
        batch.end();

        // 在debugRenderer.render之前过滤掉防御塔刚体
        // 创建一个Array<Body>对象
        Array<Body> bodiesArray = new Array<>();
        // 调用getBodies方法，将世界中的刚体添加到bodiesArray中
        world.getBodies(bodiesArray);
        // 可以在这里对bodiesArray进行操作，例如遍历
        for (Body body : bodiesArray) {
            // 这里可以执行对每个刚体的操作，如打印位置等
            System.out.println("Body position: " + body.getUserData());
        }

        // 渲染物理世界的调试信息
        debugRenderer.render(world, camera.combined);

        // 渲染游戏用户界面
        gameUI.render();

        // 如果敌人存在且已死亡，则处理敌人死亡逻辑，例如移除敌人
//        if (enemy!= null && enemy.isDead()) {
//            world.destroyBody(enemy.getBody()); // 假设Enemy类有getBody方法返回刚体
//            enemy = null;
//            // 重新初始化敌人
//            enemy = new Enemy(world, 100, 100, enemyTexture);
//            enemy.setHealth(5);
//            enemy.move(new Vector2(1, 0));
//        }
    }

    // 调整大小方法，在窗口大小改变时调用
    @Override
    public void resize(int width, int height) {
        // 更新相机的视口大小
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();

        // 更新游戏用户界面的视口大小
        gameUI.resize(width, height);
    }

    // 释放资源方法，在游戏结束时调用，用于释放所有资源
    @Override
    public void dispose() {
        // 释放物理世界的资源
        world.dispose();
        // 释放调试渲染器的资源
        debugRenderer.dispose();
        // 释放游戏用户界面的资源
        gameUI.dispose();
        // 释放精灵批处理的资源
        batch.dispose();
        // 释放敌人纹理的资源
        enemyTexture.dispose();
        // 释放防御塔纹理的资源
        towerTexture.dispose();
        // 释放形状渲染器的资源
        shapeRenderer.dispose();
    }
}