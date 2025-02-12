package com.lf.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.lf.debugRenderer.CustomBox2DDebugRenderer;
import com.lf.entities.Arrow;
import com.lf.entities.Enemy;
import com.lf.entities.Tower;
import com.lf.entities.TowerSelectionBox;
import com.lf.ui.GameUI;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;

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
    private List<PolygonMapObject> towerRanges; // 防御塔可以摆放的位置，可能有多个多边形
    //字体加载管理工具
    private AssetManager assetManager;
    //字体加载器
    private FreetypeFontLoader freeTypeFontLoader;

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
//        enemyTexture = new Texture(Gdx.files.internal("enemy1.png"));// 设置纹理过滤方式为线性过滤
//        enemyTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // 创建敌人列表
        enemies = new ArrayList<>();

        // 创建敌人对象
        Texture[] enemyFrames = new Texture[]{new Texture("enemy11.png"), new Texture("enemy12.png")};
        // 游戏中的敌人对象
        Enemy enemy1 = new Enemy(world, 550, 550, enemyFrames, pathPoints, gameUI);
        enemies.add(enemy1);
        Enemy enemy2 = new Enemy(world, 540, 560, enemyFrames, pathPoints, gameUI);
        enemies.add(enemy2);
        Enemy enemy3 = new Enemy(world, 555, 570, enemyFrames, pathPoints, gameUI);
        enemies.add(enemy3);

        // 设置敌人的移动速度
        enemy1.move();
        enemy2.move();
        enemy3.move();

        backgroundTexture = new Texture(Gdx.files.internal("map3.png"));
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(camera.viewportWidth, camera.viewportHeight);

        // 加载箭的纹理
        arrowTexture = new Texture(Gdx.files.internal("arrow1.png"));

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

        // 字体管理工具的初始化
        assetManager = new AssetManager();
        // 设置 FreeTypeFontGenerator 的加载器
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        // 设置 BitmapFont 的加载器
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        // 加载自定义字体
        FreetypeFontLoader.FreeTypeFontLoaderParameter fontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        fontParameter.fontFileName = "fonts/xinsongti.fnt"; // 字体文件路径下，需要有fnt和png两文件，都是通过Hiero工具生成
//        fontParameter.fontParameters.size = 12; // 字体大小
        assetManager.load("fonts/xinsongti.fnt", BitmapFont.class);
        // 加载提示框的背景图片
        assetManager.load("alertTitle.png", Texture.class);
        // 等待字体加载完成
        assetManager.finishLoading();
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
                enemy.update(deltaTime);
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
                    if(this.gameUI.getGold() < 100){
                        showAlertInfo("您的金币不足.",0,0);
                    }else{
                        boolean inRange = false;
                        for(PolygonMapObject polygonMapObject : this.towerRanges ){
                            //只要x和y坐标有位于其中一个防御塔允许摆放的多边形中，就可以创建新的防御塔对象
                            if(isPointInPolygon(showTowerX,showTowerY,polygonMapObject.getPolygon())){
                                inRange = true;
                                this.gameUI.subGold(100);
                                // 创建一个新的防御塔对象，位置为点击位置
                                Tower tower = new Tower(world, showTowerX, showTowerY, towerTexture, arrowTexture);
                                // 将新的防御塔添加到防御塔列表中
                                towers.add(tower);
                                // 重置防御塔选择框的选择索引
                                towerSelectionBox.resetSelectedIndex();
                            }
                        }
                        if(!inRange){
                            showAlertInfo("防御塔不能建在此处.",0,0);
                        }
                    }
                }else{
                    //如果点了其他位置，选择框应消失
                    towerSelectionBox.hide();
                }
            }
        }
    }

    /**
     * 提示弹窗
     * @param alertInfo 提示信息
     * @param x X轴位置
     * @param y Y轴位置
     */
    private void showAlertInfo(String alertInfo, float x, float y) {
        // 获取加载的字体
        BitmapFont customFont = assetManager.get("fonts/xinsongti.fnt", BitmapFont.class);
        // 字体大小倍率（以Hiero中生成的字体大小为基准）
        customFont.getData().setScale(0.8f);
        this.gameUI.getSkin().add("default",customFont);
        Label.LabelStyle labelStyle = new Label.LabelStyle(customFont, Color.BLACK);
        // 创建一个Label对象，用于显示提示文本，初始文本为空字符串
        Label hintLabel = new Label(alertInfo, labelStyle);

        // 加载背景图片
        Texture backgroundTexture = assetManager.get("alertTitle.png", Texture.class);
        TextureRegion backgroundRegion = new TextureRegion(backgroundTexture);
        Image backgroundImage = new Image(backgroundRegion);
        // 创建一个Table作为提示框容器
        Table dialogTable = new Table();
        dialogTable.setBackground(new TextureRegionDrawable(backgroundRegion));
        dialogTable.add(hintLabel).pad(20); // 添加一些内边距
        // 调整提示框的大小以适应文字长度
        dialogTable.pack();

        if(x == 0){
            x = (float) Gdx.graphics.getWidth() / 2 - hintLabel.getWidth() / 2;
        }
        if(y == 0){
            y = (float) Gdx.graphics.getHeight() / 2 - hintLabel.getHeight() / 2;
        }
        // 直接设置 label 的位置
        dialogTable.setPosition(x , y);
        // 将窗口添加到舞台
        this.gameUI.getStage().addActor(dialogTable);
        // 使用Timer在1秒后移除提示窗口
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                dialogTable.remove();
            }
        }, 1f);

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
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
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
    public static String generateChineseCharacters() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0x4E00; i <= 0x9FA5; i++) {
            sb.append((char) i);
        }
        return sb.toString();
    }
    public static void main(String[] args) {
            String chineseCharacters = generateChineseCharacters();
            System.out.println(chineseCharacters);

    }
    private void parseMapPath() {
        // 加载 TMX 地图文件
        map = new TmxMapLoader().load("冰天雪地1.tmx");

        // 初始化路径点列表
        pathPoints = new ArrayList<>();

        // 初始化防御塔摆放范围
        towerRanges = new ArrayList<>();

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
                // 将点的坐标和 order 值存储到 Vector2 中
                Vector2 point = new Vector2(x, y);
                // 将点添加到列表中
                pathPoints.add(point);
            }
            // 获取towerRange1和towerRange2的多边形对象，保存进防御塔允许摆放的范围list
            if (object instanceof PolygonMapObject) {
                if ("towerRange".equals(object.getName())) {
                    towerRanges.add((PolygonMapObject) object);
                }
            }
        }
    }
    // 检查点是否在多边形内
    private boolean isPointInPolygon(float x, float y, Polygon polygon) {
        // 将世界坐标转换为多边形的本地坐标，获取多边形经过变换后的顶点数组
        float[] vertices = polygon.getTransformedVertices();
        // 计算多边形的顶点数量，由于顶点数组是二维坐标（x,y）交替存储，所以顶点数量为数组长度的一半
        int numVertices = vertices.length / 2;
        // 用于标记点是否在多边形内，初始化为 false
        boolean inside = false;
        // 遍历多边形的所有边，i 表示当前顶点的索引，j 表示前一个顶点的索引
        for (int i = 0, j = numVertices - 1; i < numVertices; j = i++) {
            // 获取当前顶点的 x 坐标，i * 2 是因为顶点数组是二维坐标交替存储
            float xi = vertices[i * 2];
            // 获取当前顶点的 y 坐标
            float yi = vertices[i * 2 + 1];
            // 获取前一个顶点的 x 坐标
            float xj = vertices[j * 2];
            // 获取前一个顶点的 y 坐标
            float yj = vertices[j * 2 + 1];
            // 判断从点 (x,y) 水平向右的射线是否与当前边相交
            // (yi > y) != (yj > y) 表示边的两个端点在射线的两侧
            // x < (xj - xi) * (y - yi) / (yj - yi) + xi 表示点 (x,y) 在边的左侧
            boolean intersect = ((yi > y) != (yj > y))
                    && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
            // 如果射线与当前边相交，则将 inside 取反
            if (intersect) {
                inside = !inside;
            }
        }
        // 返回点是否在多边形内的结果
        return inside;
    }

}