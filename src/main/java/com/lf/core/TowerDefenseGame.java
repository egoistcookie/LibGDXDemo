package com.lf.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.lf.debugRenderer.CustomBox2DDebugRenderer;
import com.lf.entities.Arrow;
import com.lf.entities.Enemy;
import com.lf.entities.Tower;
import com.lf.entities.TowerSelectionBox;
import com.lf.ui.GameUI;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;

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
    // 敌人的纹理，用于绘制敌人的图形
    private Texture enemyTexture;
    // 游戏中的防御塔对象
    private Tower tower;
    private TowerSelectionBox towerSelectionBox;
    private List<Tower> towers;
    private Vector2 clickPosition;
    // 防御塔的纹理，用于绘制防御塔的图形
    private Texture towerTexture;
    // 箭矢的纹理，用于绘制箭矢的图形
    private Texture arrowTexture;
    // 新增：用于绘制形状（激光）的渲染器
    private ShapeRenderer shapeRenderer;
    private List<Enemy> enemies; // 敌人列表
    private Texture backgroundTexture;
    private Sprite backgroundSprite;
    // 适配视口，用于根据窗口大小调整相机的视图
    private FitViewport viewport;
    private TiledMap map;
    private List<Vector2> pathPoints; // 使用 Vector2 存储点的坐标

    float showTowerX = 0;
    float showTowerY = 0;

    // 创建方法，在游戏启动时调用，用于初始化游戏资源和对象
    @Override
    public void create() {
        // 创建正交相机，并设置其投影为非正交模式，视口大小为800x600
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        // 创建适配视口，初始视口大小为800x600
        viewport = new FitViewport(800, 600, camera);
        // 创建物理世界，重力向量为(0, 0)，不启用休眠
        world = new World(new Vector2(0, 0), false);

        // 加载地图
        // 解析地图中的路径数据
        parseMapPath();
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
        // 游戏中的敌人对象
        Enemy enemy1 = new Enemy(world, 550, 550, enemyTexture, pathPoints);
        enemies.add(enemy1);
        Enemy enemy2 = new Enemy(world, 540, 560, enemyTexture, pathPoints);
        enemies.add(enemy2);
        Enemy enemy3 = new Enemy(world, 555, 570, enemyTexture, pathPoints);
        enemies.add(enemy3);

        // 设置敌人的移动速度
        enemy1.move();
        enemy2.move();
        enemy3.move();
        // 设置敌人的目标位置
//        enemy1.setTargetPosition(new Vector2(500, 300));
//        // 设置敌人的目标位置
//        enemy2.setTargetPosition(new Vector2(600, 200));
//        // 设置敌人的目标位置
//        enemy3.setTargetPosition(new Vector2(700, 400));

        backgroundTexture = new Texture(Gdx.files.internal("map1.png"));
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(camera.viewportWidth, camera.viewportHeight);

        // 加载防御塔的纹理
//        towerTexture = new Texture(Gdx.files.internal("tower1.png"));

        // 加载箭的纹理
        arrowTexture = new Texture(Gdx.files.internal("arrow1.png"));
        // 创建防御塔对象，初始位置为(200, 150)
//        tower = new Tower(world, 200, 150, towerTexture, arrowTexture);

        // 创建防御塔选择框对象
        towerSelectionBox = new TowerSelectionBox();
        // 初始化防御塔列表
        towers = new ArrayList<>();
        // 初始化鼠标点击位置向量
        clickPosition = new Vector2();

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
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 推进物理世界的模拟，步长为1/60秒，速度迭代次数为6，位置迭代次数为2
        world.step(1 / 60f, 6, 2);
        // 处理用户输入
        handleInput();
        // 更新相机
        camera.update();

        // 更新敌人的位置和旋转角度
        for (Enemy enemy : enemies) {
            if (enemy!= null) {
                enemy.update();
                // 更新防御塔的状态，检查是否攻击敌人
            }
        }

        // 设置精灵批处理的投影矩阵为相机的投影矩阵
        batch.setProjectionMatrix(camera.combined);
        // 开始精灵批处理
        batch.begin();

        backgroundSprite.draw(batch);

        // 渲染防御塔选择框
        towerSelectionBox.render(batch);
        // 遍历防御塔列表，渲染每个防御塔的精灵
        for (Tower tower : towers) {
            // 更新塔的逻辑：用于检查敌人是否在攻击范围内并进行攻击
            tower.update(enemies,deltaTime);
            // 渲染塔
            tower.getSprite().draw(batch);
            // 渲染箭
            for (Arrow arrow : tower.arrows) {
                arrow.getSprite().draw(batch);
            }
        }

        // 如果敌人存在，则绘制敌人的精灵
        // 渲染敌人
        for (Enemy enemy : enemies) {
            enemy.getSprite().draw(batch);
        }

        // 结束精灵批处理
        batch.end();

        // 创建一个Array<Body>对象
        Array<Body> bodiesArray = new Array<>();
        // 调用getBodies方法，将世界中的刚体添加到bodiesArray中
        world.getBodies(bodiesArray);
        // 可以在这里对bodiesArray进行操作，例如遍历
        for (Body body : bodiesArray) {
            // 这里可以执行对每个刚体的操作，如打印位置等
//            System.out.println("Body position: " + body.getUserData());
        }

        // 渲染物理世界的调试信息
        debugRenderer.render(world, camera.combined);

        // 渲染游戏用户界面
        gameUI.render();

    }

    // handleInput方法用于处理用户的输入事件
    private void handleInput() {
        // 检查鼠标左键是否刚刚被按下
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            // 设置点击位置为鼠标当前的屏幕坐标
            clickPosition.set(Gdx.input.getX(), Gdx.input.getY());
            // 将屏幕坐标转换为世界坐标
            viewport.unproject(clickPosition);

            // 如果防御塔选择框不可见
            if (!towerSelectionBox.isVisible()) {
                // 显示防御塔选择框，并设置其位置为点击位置
                towerSelectionBox.show(clickPosition);
                // 显示防御塔的位置，应该为第一次点击空白位置的位置
                showTowerX = clickPosition.x;
                showTowerY = clickPosition.y;
            } else {
                // 处理防御塔选择框的输入事件
                towerSelectionBox.handleInput(clickPosition);
                // 获取选择的防御塔索引
                int selectedIndex = towerSelectionBox.getSelectedIndex();
                // 如果选择的索引不为-1，表示选择了一个防御塔
                if (selectedIndex != -1) {
                    // 获取选择的防御塔纹理
                    Texture towerTexture = towerSelectionBox.getTowerTextures().get(selectedIndex);
                    // 加载箭的纹理
                    Texture arrowTexture = new Texture(Gdx.files.internal("arrow1.png"));
                    // 创建一个新的防御塔对象，位置为点击位置
                    Tower tower = new Tower(world, showTowerX, showTowerY, towerTexture, arrowTexture);
                    // 将新的防御塔添加到防御塔列表中
                    towers.add(tower);
                    // 重置防御塔选择框的选择索引
                    towerSelectionBox.resetSelectedIndex();
                }else{
                    //如果点了其他位置，选择框应消失
                    towerSelectionBox.hide();
                }
            }
        }
    }
    // 调整大小方法，在窗口大小改变时调用
    @Override
    public void resize(int width, int height) {
        // 更新相机的视口大小
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();

        backgroundSprite.setSize(width, height);

        // 更新游戏用户界面的视口大小
        gameUI.resize(width, height);
        // 更新视口的大小
        viewport.update(width, height);
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
//        towerTexture.dispose();
        // 释放形状渲染器的资源
        shapeRenderer.dispose();
        // 释放背景图的资源
        backgroundTexture.dispose();
        // 释放防御塔选择框的资源
        towerSelectionBox.dispose();
        // 遍历防御塔列表，释放每个防御塔的资源
        for (Tower tower : towers) {
            tower.dispose();
        }
    }
    public static void main(String[] args) {
        // 创建游戏实例
        new TowerDefenseGame().parseMapPath();
    }
    private void parseMapPath() {
        // 加载 TMX 地图文件
        map = new TmxMapLoader().load("冰天雪地.tmx");

        // 初始化路径点列表
        pathPoints = new ArrayList<>();

        // 获取名为 "path" 的对象层
        MapLayer pathLayer = map.getLayers().get("path");

        // 获取对象层中的所有对象
        MapObjects objects = pathLayer.getObjects();

        // 遍历对象层中的每个对象
        for (MapObject object : objects) {
            // 检查对象是否包含自定义属性 "order"
            if (object.getProperties().containsKey("order")) {
                // 获取点的 x 和 y 坐标
                float x = object.getProperties().get("x", Float.class);
                float y = object.getProperties().get("y", Float.class);

                // 获取自定义属性 "order" 的值
                String order = object.getProperties().get("order", String.class);

                // 将点的坐标和 order 值存储到 Vector2 中
                Vector2 point = new Vector2(x, y);
//                point.set(Float.parseFloat(order), Float.parseFloat(order)); // 使用 Vector2 的 set 方法存储 order 值（可选）

                // 将点添加到列表中
                pathPoints.add(point);
            }
        }

        // 根据 "order" 属性对点进行排序
//        Collections.sort(pathPoints, new Comparator<Vector2>() {
//            @Override
//            public int compare(Vector2 p1, Vector2 p2) {
//                // 比较两个点的 order 值
//                return Float.compare(p1.x, p2.x); // 假设 order 值存储在 x 中
//            }
//        });

        // 现在 pathPoints 列表中的点已经按照 "order" 属性从小到大排序
        // 你可以将这些点用于你的塔防游戏逻辑
        for (Vector2 point : pathPoints) {
            System.out.println("Point: (" + point.x + ", " + point.y + "), Order: " + point.x);
        }
    }
}