package com.lf.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.lf.config.EnemyLoadConfig;
import com.lf.core.MyDefenseGame;
import com.lf.debugRenderer.CustomBox2DDebugRenderer;
import com.lf.entities.*;
import com.lf.manager.EnemyLoadManager;
import com.lf.ui.GameUI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

// 游戏界面类，实现 Screen 接口
public class GameScreen implements Screen {
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
    // 防御塔选择框
    private TowerSelectionBox towerSelectionBox;
    // 防御塔对象集合
    private List<Tower> towers;
    // 防御塔数量
    private int towerCount;
    // 鼠标点击坐标
    private Vector2 clickPosition;
    // 新增：用于绘制形状（激光）的渲染器
    private ShapeRenderer shapeRenderer;
    // 敌人列表
    private List<Enemy> enemies;
    // 皮肤对象，用于管理界面元素的样式
    private Skin skin;
    // 背景纹理
    private Texture backgroundTexture;
    // 背景精灵
    private Sprite backgroundSprite;
    // 适配视口，用于根据窗口大小调整相机的视图
    private FitViewport viewport;
    // tmx地图
    private TiledMap map;
    // 敌人运动路径集合1
    private List<Vector2> pathPoints; // 使用 Vector2 存储点的坐标
    // 防御塔摆放范围
    private List<PolygonMapObject> towerRanges; // 防御塔可以摆放的位置，可能有多个多边形
    // 用于控制是否继续渲染的标志变量
    private static boolean isPaused = false;
    // 用于控制是否继续渲染的标志变量
    private static boolean isGameOver = false;
    // 控制当前防御塔摆放x坐标
    float showTowerX = 0;
    // 控制当前防御塔摆放y坐标
    float showTowerY = 0;
    // 舞台对象，用于管理和渲染游戏界面元素
    private Stage stage;
    // 资源加载管理工具
    private AssetManager assetManager;
    // 敌人加载管理工具
    private EnemyLoadManager enemyLoadManager;
    // 记录游戏进行的时间
    private float elapsedTimeSeconds;
    // 记录物品栏中物品集合
    private Stuff[] stuffes = new Stuff[6];

    // 控制游戏进行倍速
    private static float sclRate = 1f;

    // 构造函数，接收游戏对象作为参数
    public GameScreen(MyDefenseGame game) {
        towerCount =0;
        this.assetManager = game.getAssetManager();
        this.enemyLoadManager = game.getEnemyLoadManager();
        // 在游戏屏幕初始化时记录开始时间
        elapsedTimeSeconds = 0f;
        // 初始化暂停标志
        isPaused = false;
        // 创建舞台，使用 ScreenViewport 作为视口
        stage = new Stage(new ScreenViewport());
        // 创建正交相机，并设置其投影为非正交模式，视口大小为800x600
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        // 创建适配视口，初始视口大小为800x600
        viewport = new FitViewport(800, 600, camera);
        // 创建物理世界，重力向量为(0, 0)，不启用休眠
        world = new World(new Vector2(0, 0), true);
        Stuff firtstStuff = new Stuff("arrower","arrower",1, 1, 1);
        stuffes[0] = firtstStuff;
        stuffes[1] = firtstStuff;
        stuffes[2] = firtstStuff;
        // 创建游戏用户界面
        gameUI = new GameUI(this, stage, game, stuffes);
        // 加载地图
        // 解析地图中的路径数据
        parseMapPath();
        // 创建调试渲染器
        debugRenderer = new CustomBox2DDebugRenderer();

        // 创建精灵批处理
        batch = new SpriteBatch();

        // 创建敌人列表
        enemies = new ArrayList<>();

        backgroundTexture = new Texture(Gdx.files.internal("map/map3.png"));
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(camera.viewportWidth, camera.viewportHeight);

        // 创建防御塔选择框对象
        towerSelectionBox = new TowerSelectionBox(this.assetManager);
        // 初始化防御塔列表
        towers = new ArrayList<>();
        // 初始化鼠标点击位置向量
        clickPosition = new Vector2();

        // 初始化形状渲染器
        shapeRenderer = new ShapeRenderer();
        // 设置形状渲染器自动选择形状类型
        shapeRenderer.setAutoShapeType(true);
        // 获取 VisUI 的默认皮肤
        skin = VisUI.getSkin();
        if (skin == null) {
            System.out.println("皮肤加载失败");
        }
    }

    @Override
    public void show() {
        // 设置输入处理器为舞台
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if(!isGameOver && !isPaused){
            // 计算游戏已经进行的时间（单位：秒）
            elapsedTimeSeconds += delta;
            float seconds = elapsedTimeSeconds;
//            System.out.println("游戏已经进行了 " + seconds + " 秒");

            for (EnemyLoadConfig enemyLoadConfig : enemyLoadManager.getEnemyLoadConfigs()) {
                float loadTime = (float) enemyLoadConfig.getLoadTime();
                String enemyType = enemyLoadConfig.getEnemyType();
                if (seconds >= loadTime && !isEnemyAlreadySpawned(enemyType)) {
                    // 生成敌人
                    Texture[] enemyFrames = new Texture[]{new Texture("enemy11.png"), new Texture("enemy12.png")};
                    Enemy enemy = new Enemy(world, 555, 570, enemyFrames, pathPoints, gameUI, enemyType); // 假设Enemy类有相应的构造函数
                    enemies.add(enemy);
                }
            }

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

            // 更新敌人位置
            Iterator<Enemy> iterator = enemies.iterator();
            while (iterator.hasNext()) {
                Enemy enemy = iterator.next();
                if (enemy != null && !enemy.getDead()) {
                    enemy.update(deltaTime);
                    if (enemy.getDead()) {
                        world.destroyBody(enemy.getBody());
                        iterator.remove();
                    }
                }
            }

            // 设置精灵批处理的投影矩阵为相机的投影矩阵
            batch.setProjectionMatrix(camera.combined);
            // 开始精灵批处理
            batch.begin();

            backgroundSprite.draw(batch);

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
            // 先渲染防御塔，再渲染防御塔选择框，使其在防御塔上层显示
            towerSelectionBox.render(batch);

            // 如果敌人存在，则绘制敌人的精灵
            // 渲染敌人
            for (Enemy enemy : enemies) {
                if(!enemy.getDead()){
                    enemy.getSprite().draw(batch);
                }
            }

            // 结束精灵批处理
            batch.end();

            // 创建一个Array<Body>对象
//            Array<Body> bodiesArray = new Array<>();
//            // 调用getBodies方法，将世界中的刚体添加到bodiesArray中
//            world.getBodies(bodiesArray);
//            // 可以在这里对bodiesArray进行操作，例如遍历
//            for (Body body : bodiesArray) {
//                // 这里可以执行对每个刚体的操作，如打印位置等
//                System.out.println("Body position: " + body.getUserData());
//            }

            // 渲染物理世界的调试信息
            debugRenderer.render(world, camera.combined);
        }
        // 渲染游戏用户界面：按钮需要另外渲染，不可与游戏对象放在一起渲染
        gameUI.render();
    }

    /**
     * 判断该敌人是否已经生成过
     * @param enemyName 敌人名称
     * @return
     */
    private boolean isEnemyAlreadySpawned(String enemyName) {
        for (Enemy enemy : enemies) {
            if (enemy.getEnemyName()!=null && enemy.getEnemyName().equals(enemyName)) {
                return true;
            }
        }
        return false;
    }

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

    @Override
    public void pause() {
        isPaused = true;
    }

    @Override
    public void resume() {
        isPaused = false;
    }

    public void quickly() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
            // 提高倍速
            sclRate = 2f;
            }
        }, 0.1f);
    }

    public void slowly() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
            // 恢复倍速
            sclRate = 1f;
            }
        }, 0.1f);
    }

    @Override
    public void hide() {
        // 隐藏时不做处理
    }

    public void gameOver() {
        // 游戏结束
        // 延迟赋值，解决反复渲染最后两帧问题
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                // 游戏结束
                isGameOver = true;
            }
        }, 1f);
        // 释放敌人纹理的资源
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
    }

    // handleInput方法用于处理用户的输入事件
    private void handleInput() {
        // 检查鼠标左键是否刚刚被按下
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
//            createTowerBySelectBox();
            // 设置点击位置为鼠标当前的屏幕坐标
            clickPosition.set(Gdx.input.getX(), Gdx.input.getY());
            // 将屏幕坐标转换为世界坐标
            viewport.unproject(clickPosition);
            System.out.println("点击坐标：" + clickPosition.x + "y:" + clickPosition.y);

            // 如果防御塔选择框可见，则要先选择防御塔上的图标才能继续其他点击操作
            if (towerSelectionBox.isVisible()) {
                // 处理防御塔选择框的输入事件
                towerSelectionBox.handleInput(clickPosition);
                // 获取选择的防御塔操作 回收或者升星
                int selectedIndex = towerSelectionBox.getSelectedIndex();
                int selectedTowerId = towerSelectionBox.getTowerId();

                Iterator<Tower> iterator = towers.iterator();
                while (iterator.hasNext()) {
                    Tower tower = iterator.next();
                    if(tower.getTowerId() == selectedTowerId){
                        // 选择0位置，表示回收
                        if (selectedIndex == 0) {
                            // 查找第一个空闲位置
                            int index = 0;
                            while (index < stuffes.length && stuffes[index] != null) {
                                index++;
                            }
                            // 如果还有空闲位置，添加新元素
                            if (index < stuffes.length) {
                                stuffes[index] = new Stuff(tower.getTowerType(), tower.getTowerType(), tower.getTowerId(),
                                        tower.getLevel(), tower.getStarLevel());
                                // 经验值标签需要隐藏
                                tower.dispose();
                                // 回收
                                world.destroyBody(tower.getBody());
                                // 从towers中移除该元素
                                iterator.remove();
                            } else {
                                showAlertInfo("物品栏已满,无法再回收");
                            }
                        } else if (selectedIndex == 1) {
                            // 升星
                            if(!tower.superPass(stuffes)){
                                showAlertInfo("请确保物品栏中存在\n同星级且满级的素材");
                            }
                        }
                    }
                }
                //只要界面上存在选择框，那么无论再次点了哪里，选择框都应消失
                towerSelectionBox.hide();
            } else {
                // 遍历防御塔，判断鼠标点击位置是否命中防御塔
                Iterator<Tower> iterator = towers.iterator();
                while (iterator.hasNext()) {
                    Tower tower = iterator.next();
                    // 获取防御塔精灵的所在矩形（后期可以优化成非透明部位）
                    Rectangle towerBounds = tower.getSprite().getBoundingRectangle();
                    if (towerBounds.contains(clickPosition)) {
                        System.out.println("防御塔坐标：" + towerBounds.x + "y:" + towerBounds.y);
                        // 显示防御塔选择框，并设置其位置为点击位置，并获取到该位置的towerId
                        towerSelectionBox.show(clickPosition,tower.getTowerId());
                    }
                }
//                Rectangle rectangle = SpriteUtils.getNonTransparentBounds(tower.getSprite());
//                if(rectangle.contains(clickPosition)){
//                    System.out.println("防御塔非透明处的坐标："+rectangle.x+"y:"+rectangle.y);
//                }
            }
            // 重置选择的功能按钮编号
            towerSelectionBox.resetSelectedIndex();
        }
        // 数字键1到6的按钮绑定事件：消耗物品栏中卡片，生成对应防御塔
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            createTower(1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            createTower(2);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            createTower(3);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            createTower(4);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            createTower(5);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
            createTower(6);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            //抽卡快捷键
            getCard();
        }
    }

    /**
     * 传统的生成防御塔方式：通过防御塔备选框生成
     */
    private void createTowerBySelectBox() {

        // 设置点击位置为鼠标当前的屏幕坐标
//        clickPosition.set(Gdx.input.getX(), Gdx.input.getY());
//        // 将屏幕坐标转换为世界坐标
//        viewport.unproject(clickPosition);
//        System.out.println(clickPosition.x + "y:" +clickPosition.y);
//        //右下角为功能按钮位置，不可点击生成防御塔选择框
//        if(clickPosition.x > 370 && clickPosition.y < 45){
//            return;
//        }
//        //左下角为退出按钮位置，不可点击生成防御塔选择框
//        if(clickPosition.x < 200 && clickPosition.y < 45){
//            return;
//        }
//
//        // 如果防御塔选择框不可见
//        if (!towerSelectionBox.isVisible()) {
//            // 显示防御塔选择框，并设置其位置为点击位置
//            towerSelectionBox.show(clickPosition);
//            // 显示防御塔的位置，应该为第一次点击空白位置的位置
//            showTowerX = clickPosition.x;
//            showTowerY = clickPosition.y;
//        } else {
//            // 处理防御塔选择框的输入事件
//            towerSelectionBox.handleInput(clickPosition);
//            // 获取选择的防御塔索引
//            int selectedIndex = towerSelectionBox.getSelectedIndex();
//            // 如果选择的索引不为-1，表示选择了一个防御塔
//            if (selectedIndex != -1) {
//                // 获取选择的防御塔纹理
//                Texture towerTexture = towerSelectionBox.getTowerTextures().get(selectedIndex);
//                // 加载箭的纹理 默认1号箭矢
//                Texture arrowTexture = assetManager.get("arrow1.png", Texture.class);
//                //生成一号防御塔
//                if(selectedIndex == 0){
//                    arrowTexture = assetManager.get("arrow1.png", Texture.class);
//                }else if (selectedIndex == 1){
//                    //生成二号防御塔
//                    arrowTexture = assetManager.get("arrow2.png", Texture.class);
//                }
//                if(this.gameUI.getGold() < 100){
//                    showAlertInfo("您的金币不足.",0,0);
//                }else{
//                    boolean inRange = false;
//                    for(PolygonMapObject polygonMapObject : this.towerRanges ){
//                        //只要x和y坐标有位于其中一个防御塔允许摆放的多边形中，就可以创建新的防御塔对象
//                        if(isPointInPolygon(showTowerX,showTowerY,polygonMapObject.getPolygon())){
//                            inRange = true;
//                            this.gameUI.subGold(100);
//                            // 创建一个新的防御塔对象，位置为点击位置
//                            Tower tower = new Tower(world, "arrower",showTowerX, showTowerY, arrowTexture, assetManager, stage);
//                            // 将新的防御塔添加到防御塔列表中
//                            towers.add(tower);
//                            // 重置防御塔选择框的选择索引
//                            towerSelectionBox.resetSelectedIndex();
//                        }
//                    }
//                    if(!inRange){
//                        showAlertInfo("防御塔不能建在此处.",0,0);
//                    }
//                }
//            }else{
//                //如果点了其他位置，选择框应消失
//                towerSelectionBox.hide();
//            }
//        }
    }

    /**
     * 使用卡片创建防御单位
     * @param i 对应键盘数字位
     */
    private void createTower(int i) {
        // 设置点击位置为鼠标当前的屏幕坐标
        clickPosition.set(Gdx.input.getX(), Gdx.input.getY());
        // 将屏幕坐标转换为世界坐标
        viewport.unproject(clickPosition);
        // 判断对应物品栏中是否存在卡片
        if(stuffes[i-1] != null){
            Stuff stuff = stuffes[i-1];
            String towerType = stuff.getStuffType();
            // 加载箭的纹理 默认1号箭矢
            Texture arrowTexture = assetManager.get("arrow1.png", Texture.class);
            boolean inRange = false;
            for(PolygonMapObject polygonMapObject : this.towerRanges ){
                //只要x和y坐标有位于其中一个防御塔允许摆放的多边形中，就可以创建新的防御塔对象
                if(isPointInPolygon(clickPosition.x,clickPosition.y,polygonMapObject.getPolygon())){
                    inRange = true;
//                    this.gameUI.subGold(100);
                    // 创建一个新的防御塔对象，位置为点击位置，tower序号作为id
                    Tower tower = new Tower(world, towerCount++ ,towerType, clickPosition.x, clickPosition.y, arrowTexture, assetManager, stage,
                            stuff.getStuffLevel(), stuff.getStuffStarLevel());
                    // 将新的防御塔添加到防御塔列表中
                    towers.add(tower);
                    // 创建防御塔后，物品栏中卡片消失
                    stuffes[i-1] = null;
                }
            }
            if(!inRange){
                showAlertInfo("防御单位不能摆放在此处！",0,0);
            }
        }else{
            System.out.println("物品栏中对应位置无卡片！");
        }
    }

    /**
     * 调用提示弹窗方法未传坐标，则默认中间展示
     * @param alertInfo
     */
    public void showAlertInfo(String alertInfo){
        showAlertInfo(alertInfo, 0,0);
    }
    /**
     * 提示弹窗
     * @param alertInfo 提示信息
     * @param x X轴位置
     * @param y Y轴位置
     */
    public void showAlertInfo(String alertInfo, float x, float y) {
        // 获取加载的中文字体（直接从assetManager获取字体，体感showAlertInfo提速1秒）
        BitmapFont customFont = assetManager.get("fonts/xinsongti.fnt", BitmapFont.class);
        // 字体大小倍率
        customFont.getData().setScale(0.5f);
        // 创建LabelStyle时使用缩放后的字体
        Label.LabelStyle labelStyle = new Label.LabelStyle(customFont, Color.BLACK);


        // 创建一个Label对象，用于显示提示文本，初始文本为空字符串
        Label hintLabel = new Label(alertInfo, labelStyle);

        // 加载背景图片
        Texture backgroundTexture = assetManager.get("alertTitle.png", Texture.class);
        System.out.println("backgroundTexture.getWidth():"+backgroundTexture.getWidth());
        TextureRegion backgroundRegion = new TextureRegion(backgroundTexture);
        // 创建一个Table作为提示框容器
        Table dialogTable = new Table();
        dialogTable.setBackground(new TextureRegionDrawable(backgroundRegion));
        dialogTable.add(hintLabel).pad(0); // 添加一些内边距
        // 调整提示框的大小以适应文字长度
        dialogTable.pack();

        if(x == 0){
            x = (float) Gdx.graphics.getWidth() / 2 - hintLabel.getWidth() / 2 - (float) backgroundTexture.getWidth() / 4;//还要减去图片留白部分的宽度
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
    private void parseMapPath() {
        // 加载 TMX 地图文件
        map = new TmxMapLoader().load("map/冰天雪地1.tmx");

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
            if(!enemy.getDead()){
                enemy.dispose();
            }
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

    // 获取加速倍率
    public static float getSclRate() {
        return sclRate;
    }

    public void getCard() {
        System.out.println("开始抽卡！");
        if(gameUI.getGold() < 500){
            showAlertInfo("您的金币不足");
            return ;
        }else{
            this.gameUI.subGold(100);
        }
        // 获取一个1-100的随机数
        // 当随机数小于80时，生成最基础的arrower卡片

        // 大于80，小于95时，生成比较稀有的二级防御单位

        // 大于95时，生成最稀有的三级防御单位

        String towerType = "arrower";
        Stuff newStuff = new Stuff(towerType,towerType,0,1,1);
        // 查找第一个空闲位置
        int index = 0;
        while (index < stuffes.length && stuffes[index] != null) {
            index++;
        }
        // 如果还有空闲位置，添加新元素
        if (index < stuffes.length) {
            stuffes[index] = newStuff;
        }else{
            showAlertInfo("物品栏已满,无法再抽卡");
        }
    }
}