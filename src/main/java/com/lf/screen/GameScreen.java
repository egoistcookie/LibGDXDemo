package com.lf.screen;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
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
import com.badlogic.gdx.math.Vector3;
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
import com.lf.config.CardTypeConfig;
import com.lf.config.EnemyLoadConfig;
import com.lf.constants.EnemyState;
import com.lf.core.MyDefenseGame;
import com.lf.debugRenderer.CustomBox2DDebugRenderer;
import com.lf.entities.*;
import com.lf.entities.attackItem.Arrow;
import com.lf.entities.card.Card;
import com.lf.entities.card.NecromancerCard;
import com.lf.entities.card.SwordSaintCard;
import com.lf.entities.enemy.Enemy;
import com.lf.entities.enemy.GhostWarrior;
import com.lf.manager.EnemyLoadManager;
import com.lf.ui.GameUI;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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
    // 卡片操作框
    private TowerSelectionBox towerSelectionBox;
    // 防御塔对象集合
    private List<Card> cardes;
    // 防御塔数量
    private int towerCount;
    // 弓箭手数量
    private int arrowerCount;
    // 各种防御塔的数量
    private Map<String, Integer> cardCountMap;
    // 卡片是否已经加载的集合
    private Map<String, Boolean> isCardLoaded;
    // 鼠标点击坐标
    private Vector2 clickPosition;
    // 新增：用于绘制形状（激光）的渲染器
    private ShapeRenderer shapeRenderer;
    // 敌人列表
    private List<Enemy> enemies;
    // 敌人列表（包含已经死亡的敌人）
    private List<Enemy> enemiesTotol;
    // 皮肤对象，用于管理界面元素的样式
    private Skin skin;
    // 背景纹理
    private Texture backgroundTexture;
    // 新增：存储倒地贴图
    private Texture[] deathingTexture;
    // 背景精灵
    private Sprite backgroundSprite;
    // 适配视口，用于根据窗口大小调整相机的视图
    private FitViewport viewport;
    // tmx地图
    private TiledMap map;
    // 敌人运动路径集合1
    private List<Vector2> pathPoints1; // 使用 Vector2 存储点的坐标
    // 敌人运动路径集合2
    private List<Vector2> pathPoints2; // 使用 Vector2 存储点的坐标
    // 线路1的初始位置
    private Vector2 firstVector1;
    // 线路2的初始位置
    private Vector2 firstVector2;
    // 防御塔摆放范围
    private List<PolygonMapObject> towerRanges; // 防御塔可以摆放的位置，可能有多个多边形
    // 用于控制是否继续渲染的标志变量
    private static boolean isPaused = false;
    // 用于控制是否继续渲染的标志变量
    private static boolean isGameOver = false;
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
    private ShaderProgram shaderProgram;
    // 白色粒子特效
    private ParticleEffect whiteEffect;
    private float time;
    // 场地buff集合
    private Map<String, Float> buffMap;
    // 杀敌数集合
    private Map<String, Integer> killCountList;
    // 地图编号
    private Integer mapNo;

    /**
     * 带关卡编号的构造方法
     * @param game 接收游戏对象作为参数
     * @param mapNo 关卡编号
     */
    public GameScreen(MyDefenseGame game , int mapNo) {
        towerCount =0;
        this.assetManager = game.getAssetManager();
        this.enemyLoadManager = game.getEnemyLoadManager();
        this.mapNo = mapNo;
        System.out.println(mapNo);
        // 在游戏屏幕初始化时记录开始时间
        elapsedTimeSeconds = 0f;
        // 初始化暂停标志
        isPaused = false;
        // 创建舞台，使用 ScreenViewport 作为视口
        stage = new Stage(new ScreenViewport());
        // 创建正交相机，并设置其投影为非正交模式，视口大小为1200x900
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1200, 900);
        // 创建适配视口，初始视口大小为1200x900
        viewport = new FitViewport(1200, 900, camera);
        // 创建物理世界，重力向量为(0, 0)，不启用休眠
        world = new World(new Vector2(0, 0), true);
        // 初始化三张arrower卡片
        Stuff firtstStuff = new Stuff("blackTortoise","blackTortoise",1, 1, 1);
        stuffes[0] = firtstStuff;
        Stuff stuff2 = new Stuff("vermilion","vermilion",1, 1, 1);
        stuffes[1] = stuff2;
        Stuff stuff3 = new Stuff("whiteTiger","whiteTiger",1, 1, 1);
        stuffes[2] = stuff3;
        Stuff stuff4 = new Stuff("prosperityGirl","prosperityGirl",1, 1, 1);
        stuffes[3] = stuff4;
        // 创建游戏用户界面
        gameUI = new GameUI(this, stage, game, stuffes);
        // 解析地图中的路径数据
        parseMapPath();
        // 创建调试渲染器
        debugRenderer = new CustomBox2DDebugRenderer();
        // 创建精灵批处理
        batch = new SpriteBatch();
        // 创建敌人列表
        enemies = new ArrayList<>();
        // 创建敌人列表（包含已经死亡的敌人）
        enemiesTotol = new ArrayList<>();
        // 创建卡片数量map
        cardCountMap = new HashMap<>();
        // 创建卡片加载集合
        isCardLoaded = new HashMap<>();
        Texture deathingTexture1 = assetManager.get("enemy/deathing2.png", Texture.class);
        Texture deathingTexture2 = deathingTexture1;
        deathingTexture = new Texture[]{deathingTexture1, deathingTexture2}; ;
        // 初始化场地buff
        buffMap = new HashMap<>();
        // 以配置文件内容，初始化杀敌数集合
        killCountList = new HashMap<>();
        for (CardTypeConfig cardLoadConfig : enemyLoadManager.getCardTypeConfigs()) {
            killCountList.put(cardLoadConfig.getCardType(),cardLoadConfig.getKillCount());
        }
        // 加载地图背景
        backgroundTexture = assetManager.get("map/map"+mapNo+".png", Texture.class);
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(camera.viewportWidth, camera.viewportHeight);
        // 创建卡片操作框对象
        towerSelectionBox = new TowerSelectionBox(this.assetManager);
        // 初始化防御塔列表
        cardes = new ArrayList<>();
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
        // 创建 Shader 程序
        String vertexShader = Gdx.files.internal("vertex.glsl").readString();
        String fragmentShader = Gdx.files.internal("cool_fragment.glsl").readString();
        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);

        whiteEffect = new ParticleEffect();
        whiteEffect.load(Gdx.files.internal("whitePix.p"), Gdx.files.internal(""));

        if (!shaderProgram.isCompiled()) {
            Gdx.app.error("Shader", shaderProgram.getLog());
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
            // 遍历load配置文件，按其中时间轴来添加敌人enemies
            for (EnemyLoadConfig enemyLoadConfig : enemyLoadManager.getEnemyLoadConfigs()) {
                // 敌人加载时间，以秒为单位
                float loadTime = (float) enemyLoadConfig.getLoadTime();
                String enemyName = enemyLoadConfig.getEnemyName();
                String enemyType = enemyLoadConfig.getEnemyType();
                // 以enemyName作为唯一标识，来确保敌人不会重复生成
                if (seconds >= loadTime && !isEnemyAlreadySpawned(enemyName)) {
                    // 生成敌人
                    Enemy enemy = new Enemy(world, stage, firstVector1.x, firstVector1.y, enemyType, pathPoints1, gameUI, enemyName);
                    enemies.add(enemy);
                    enemiesTotol.add(enemy);
                    // 生成双倍敌人，按照order2路线行进
                    Enemy enemy2 = new Enemy(world, stage, firstVector2.x, firstVector2.y, enemyType, pathPoints2, gameUI, enemyName+"-order2");
                    enemies.add(enemy2);
                    enemiesTotol.add(enemy2);
                }
            }
            // 清除屏幕，设置背景颜色为黑色
            Gdx.gl.glClearColor(1, 1, 1, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // 推进物理世界的模拟，步长为1/60秒，速度迭代次数为6，位置迭代次数为2
            world.step(1 / 60f, 6, 2);
            // 处理用户输入
            handleInput();
            // 更新相机
            camera.update();
            // 更新敌人位置，传入deltaTime渲染间隔时间，渲染敌人动作
            float deltaTime = Gdx.graphics.getDeltaTime();
            Iterator<Enemy> iterator = enemies.iterator();
            while (iterator.hasNext()) {
                Enemy enemy = iterator.next();
//                if (enemy != null && !enemy.getDead()) {
                    enemy.update(deltaTime);
                    // 倒地或者已达到终点，都需要停止移动，并消散
                    if (enemy.enemyStatus == EnemyState.FALLEN || enemy.enemyStatus == EnemyState.REACHED_DESTINATION) {
                        // 倒下，则更换为倒地贴图
                        if(enemy.enemyStatus == EnemyState.FALLEN){
                            // 更换为倒地贴图
                            enemy.setAnimationFrames(deathingTexture);
                        }
                        // 停止移动
                        enemy.getBody().setActive(false);
                        // 2秒后消散
                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                            // 敌人死亡，设置一个标志表示该敌人即将消失
                            if (!enemy.isDisappearing()) {
                                enemy.setDisappearing(true);
                            }
                            }
                        }, 2f); // 延迟2秒
                    }
                    // 2秒后从 enemies 中删除
                    if (enemy.enemyStatus == EnemyState.DISAPPEARED){
                        world.destroyBody(enemy.getBody());
                        iterator.remove();
                    }
//                }
            }
            // 设置精灵批处理的投影矩阵为相机的投影矩阵
            batch.setProjectionMatrix(camera.combined);
            // 开始精灵批处理
            batch.begin();
            // 背景精灵绘制
            backgroundSprite.draw(batch);
            time += Gdx.graphics.getDeltaTime();

            // 遍历防御塔列表，渲染每个防御塔的精灵
            int arrowerCount = 0;
            int yysCount = 0;
            int saberCount = 0;
            for (Card card : cardes) {
                // 获取各卡片数量
                if("arrower".equals(card.getCardType())){
                    arrowerCount ++ ;
                }else if("yys".equals(card.getCardType())){
                    yysCount ++ ;
                }else if("saber".equals(card.getCardType())){
                    saberCount ++ ;
                }
                // 加载卡片图片（只有第一次显示才加载）
                cardImageLoad(card.getCardType());
                // 渲染塔 发光特效
//                time = 0.1f + Gdx.graphics.getDeltaTime(); // 加了0.1f就会变成固定半隐半现
                // 更新塔的逻辑：用于检查敌人是否在攻击范围内并进行攻击
                card.update(enemies,deltaTime);
                // 渲染升级特效
                if(card.getEffectType()!=null && "whiteEffect".equals(card.getEffectType())){
                    float durationTime = card.getEffectDuration();
                    durationTime = durationTime - Gdx.graphics.getDeltaTime();
                    if(durationTime >=0 ){
                        // 白色粒子特效渲染：代表升级或者升星
                        whiteEffect.setPosition(card.getSprite().getX() + card.getSprite().getWidth() / 2,
                                card.getSprite().getY() + card.getSprite().getHeight() / 2);
                        whiteEffect.draw(batch, Gdx.graphics.getDeltaTime());
                        // 持续时间减少相应时间
                        card.setEffectDuration(durationTime);
                    }else{
                        card.setEffectDuration(0f);
                    }
                }
                // 渲染鼠标选中tower特效
                // 获取鼠标在世界坐标系中的位置
                Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(mousePos);
                // 获取塔的矩形区域
                Rectangle towerRect = new Rectangle(card.getSprite().getX(), card.getSprite().getY(), card.getSprite().getWidth(), card.getSprite().getHeight());
                // 检查鼠标是否在塔的矩形区域内
                if (towerRect.contains(mousePos.x, mousePos.y)) {
                    // 为卡片制造一种若隐若现特效（fragment.glsl）
                    batch.setShader(shaderProgram);
                    shaderProgram.setUniformf("u_time", time);
                    shaderProgram.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                    card.getSprite().draw(batch, 1);
                    batch.setShader(null);
                }else{
                    // 无特效渲染塔
                    card.getSprite().draw(batch);
                }
                // 渲染箭
                for (Arrow arrow : card.arrows) {
                    arrow.getSprite().draw(batch);
                }
                // 亡灵法师，还要渲染她的战士
                if("necromancer".equals(card.getCardType())){
                    for (GhostWarrior warrior : ((NecromancerCard)card).ghostWarriores) {
                        warrior.getSprite().draw(batch);
                    }
                }

            }
            // 先渲染防御塔，再渲染卡片操作框，使其在防御塔上层显示
            towerSelectionBox.render(batch);
            // 如果敌人存在，则绘制敌人的精灵
            for (Enemy enemy : enemies) {
                if(!enemy.isDisappearing()){
                    enemy.getSprite().draw(batch);
                }
            }
            // 为各种card的数量赋值
            cardCountMap.clear();
            cardCountMap.put("arrowerCount",arrowerCount);
            cardCountMap.put("yysCount",yysCount);
            cardCountMap.put("saberCount",saberCount);
            // 渲染特效图标
            buffRender(batch);

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
        gameUI.render(elapsedTimeSeconds);
    }

    // 判断card是否初次加载，如果是初次加载则显示卡片
    private void cardImageLoad(String cardType) {
        // 为空或者获取到为false都代表没有加载过
        if(isCardLoaded.get(cardType)==null || !isCardLoaded.get(cardType)){
            gameUI.showCardImage(cardType);
            isCardLoaded.put(cardType, true);
        }
    }

    // 渲染场地buff
    private void buffRender(SpriteBatch batch) {
        // 根据场上card类型和数量来渲染场地buff
        if(!cardCountMap.isEmpty() && cardCountMap.get("arrowerCount")!=null && cardCountMap.get("arrowerCount") >= 3){
            gameUI.setBuffImage("swifterArrow");
//            System.out.println("arrowerCount:" + cardCountMap.get("arrowerCount"));
            // 已经赋予了buff就不要重复再赋
            if(!buffMap.isEmpty() && buffMap.get("arrowerRate")!=1.2f){
                // 攻击速度提升1.2倍
                buffMap.put("arrowerRate",1.2f);
            }
        }else{
            // buff还原
            buffMap.put("arrowerRate",1f);
            gameUI.setBuffImage(null);
        }
    }

    @Override
    public void resize(int width, int height) {
        // 定义你期望的世界宽高比，这里假设为 4:3
        float desiredAspectRatio = 4f / 3f;
        // 计算当前窗口的宽高比
        float currentAspectRatio = (float) width / height;
        if (currentAspectRatio > desiredAspectRatio) {
            // 窗口过宽，需要调整宽度以匹配期望的宽高比
            int newWidth = (int) (height * desiredAspectRatio);
            viewport.update(newWidth, height, true);
        } else {
            // 窗口过高，需要调整高度以匹配期望的宽高比
            int newHeight = (int) (width / desiredAspectRatio);
            viewport.update(width, newHeight, true);
        }
        // 更新相机的视口大小
        camera.setToOrtho(false, viewport.getWorldWidth(), viewport.getWorldHeight());
        // 更新背景图片的大小，使其与相机视口大小一致
        backgroundSprite.setSize(camera.viewportWidth, camera.viewportHeight);
        // 更新视口的大小
        camera.update();

        // 更新游戏用户界面的视口大小
//        gameUI.resize(width, height);
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

            // 如果卡片操作框可见，则要先选择防御塔上的图标才能继续其他点击操作
            if (towerSelectionBox.isVisible()) {
                // 处理卡片操作框的输入事件
                towerSelectionBox.handleInput(clickPosition);
                // 获取选择的防御塔操作 回收或者升星
                int selectedIndex = towerSelectionBox.getSelectedIndex();
                int selectedTowerId = towerSelectionBox.getTowerId();

                Iterator<Card> iterator = cardes.iterator();
                while (iterator.hasNext()) {
                    Card card = iterator.next();
                    if(card.getTowerId() == selectedTowerId){
                        // 选择0位置，表示回收
                        if (selectedIndex == 0) {
                            // 查找第一个空闲位置
                            int index = 0;
                            while (index < stuffes.length && stuffes[index] != null) {
                                index++;
                            }
                            // 如果还有空闲位置，添加新元素
                            if (index < stuffes.length) {
                                stuffes[index] = new Stuff(card.getCardType(), card.getCardType(), card.getTowerId(),
                                        card.getExperience(), card.getStarLevel());
                                // 经验值标签需要隐藏
                                card.dispose();
                                // 回收
                                world.destroyBody(card.getBody());
                                // 从towers中移除该元素
                                iterator.remove();
                            } else {
                                showAlertInfo("物品栏已满,无法再回收");
                            }
                        } else if (selectedIndex == 1) {
                            // 升星
                            if(card.getLevel()<10){
                                showAlertInfo("防御单位等级不够");
                            }else if(!card.superPass(stuffes)){
                                showAlertInfo("请确保物品栏中存在\n同星级且满级的素材");
                            }
                        }
                    }
                }
                //只要界面上存在选择框，那么无论再次点了哪里，选择框都应消失
                towerSelectionBox.hide();
            } else {
                // 遍历防御塔，判断鼠标点击位置是否命中防御塔
                Iterator<Card> iterator = cardes.iterator();
                while (iterator.hasNext()) {
                    Card card = iterator.next();
                    // 获取防御塔精灵的所在矩形（后期可以优化成非透明部位）
                    Rectangle towerBounds = card.getSprite().getBoundingRectangle();
                    if (towerBounds.contains(clickPosition)) {
                        System.out.println("防御塔坐标：" + towerBounds.x + "y:" + towerBounds.y);
                        // 显示卡片操作框，并设置其位置为点击位置，并获取到该位置的towerId
                        towerSelectionBox.show(clickPosition, card.getTowerId());
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
//        //右下角为功能按钮位置，不可点击生成卡片操作框
//        if(clickPosition.x > 370 && clickPosition.y < 45){
//            return;
//        }
//        //左下角为退出按钮位置，不可点击生成卡片操作框
//        if(clickPosition.x < 200 && clickPosition.y < 45){
//            return;
//        }
//
//        // 如果卡片操作框不可见
//        if (!towerSelectionBox.isVisible()) {
//            // 显示卡片操作框，并设置其位置为点击位置
//            towerSelectionBox.show(clickPosition);
//            // 显示防御塔的位置，应该为第一次点击空白位置的位置
//            showTowerX = clickPosition.x;
//            showTowerY = clickPosition.y;
//        } else {
//            // 处理卡片操作框的输入事件
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
//                            // 重置卡片操作框的选择索引
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
            boolean inRange = false;
            for(PolygonMapObject polygonMapObject : this.towerRanges ){
                //只要x和y坐标有位于其中一个防御塔允许摆放的多边形中，就可以创建新的防御塔对象
                if(isPointInPolygon(clickPosition.x,clickPosition.y,polygonMapObject.getPolygon())){
                    inRange = true;
//                    this.gameUI.subGold(100);
                    Card card = null;
                    // 创建一个新的防御塔对象，位置为点击位置，tower序号作为id
                    if("swordSaint".equals(towerType)){
                        card = new SwordSaintCard(world, this, towerCount++ ,towerType, clickPosition.x, clickPosition.y, assetManager, stage,
                                stuff.getStuffExp(), stuff.getStuffStarLevel());
                    }else if("necromancer".equals(towerType)){
                        card = new NecromancerCard(world, this, towerCount++ ,towerType, clickPosition.x, clickPosition.y, assetManager, stage,
                                stuff.getStuffExp(), stuff.getStuffStarLevel());
                    }else{
                        card = new Card(world, this, towerCount++ ,towerType, clickPosition.x, clickPosition.y, assetManager, stage,
                                stuff.getStuffExp(), stuff.getStuffStarLevel());
                    }
                    // 将新的防御塔添加到防御塔列表中
                    cardes.add(card);
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
        map = new TmxMapLoader().load("map/map"+mapNo+".tmx");

        // 初始化路径点列表
        pathPoints1 = new ArrayList<>();
        pathPoints2 = new ArrayList<>();

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
                // 记录首节点，作为刷怪初始位置
                int orderValue = Integer.parseInt(object.getProperties().get("order")+"");
                if(orderValue == 1){
                    firstVector1 = new Vector2(x, y);
                }
                // 将点的坐标和 order 值存储到 Vector2 中
                Vector2 point = new Vector2(x, y);
                // 将点添加到列表中
                pathPoints1.add(point);
            }
            // 敌人运动轨迹2
            if (object.getProperties().containsKey("order2")) {
                // 获取点的 x 和 y 坐标
                float x = object.getProperties().get("x", Float.class);
                float y = object.getProperties().get("y", Float.class);
                // 记录首节点，作为刷怪初始位置
                int orderValue = Integer.parseInt(object.getProperties().get("order2")+"");
                if(orderValue == 1){
                    firstVector2 = new Vector2(x, y);
                }
                // 将点的坐标和 order 值存储到 Vector2 中
                Vector2 point = new Vector2(x, y);
                // 将点添加到列表中
                pathPoints2.add(point);
            }
            // 获取towerRange1和towerRange2的多边形对象，保存进防御塔允许摆放的范围list
            if (object instanceof PolygonMapObject) {
                if ("towerRange".equals(object.getName())) {
                    towerRanges.add((PolygonMapObject) object);
                }
            }
        }
    }
    /**
     * 判断该敌人是否已经生成过
     * @param enemyName 敌人名称
     * @return
     */
    private boolean isEnemyAlreadySpawned(String enemyName) {
        // BUG0001-20250221：应该将已经死亡过的敌人也纳入判断范畴
        for (Enemy enemy : enemiesTotol) {
            if (enemy.getEnemyName()!=null && enemy.getEnemyName().equals(enemyName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查点是否在多边形内
     * @param x 当前位置的x坐标
     * @param y 当前位置的y坐标
     * @param polygon 防御塔允许摆放的多边形
     * @return
     */
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
        // 粒子特效释放
        whiteEffect.dispose();
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
        // 释放卡片操作框的资源
        towerSelectionBox.dispose();
        // 遍历防御塔列表，释放每个防御塔的资源
        for (Card card : cardes) {
            card.dispose();
        }
    }

    // 获取加速倍率
    public static float getSclRate() {
        return sclRate;
    }

    /**
     * 抽卡，卡片放入物品栏，并扣除金币
     */
    public void getCard() {
        System.out.println("开始抽卡！");
        if(gameUI.getGold() < 100){
            showAlertInfo("您的金币不足");
            return ;
        }
        // 生成1到100的随机数（包含1和100）
        int randomNumber = ThreadLocalRandom.current().nextInt(1, 101);
        System.out.println("生成的稀有度随机数是: " + randomNumber);
        // 先生成一个随机数，选中一个 稀有度-rarity
        // N-S-SR-SSR 对应概率 60%-20%-15%-5%
        String rarity = "N";
        if(randomNumber>=95){
            rarity = "SSR";
        }else if (randomNumber>=80){
            rarity = "SR";
        }else if (randomNumber>=60){
            rarity = "S";
        }
        // 获取该rarity的所有卡片
        List<CardTypeConfig> cardTypes = new ArrayList<>();
        for (CardTypeConfig cardTypeConfig : enemyLoadManager.getCardTypeConfigs()) {
            if (rarity.equals(cardTypeConfig.getRarity())){
                cardTypes.add(cardTypeConfig);
            }
        }
        // 再生成一次随机数，选中一个 towerType
        int cardTypeNumber = ThreadLocalRandom.current().nextInt(0, cardTypes.size());
        System.out.println("生成的卡片随机数是: " + cardTypeNumber);
        String towerType = cardTypes.get(cardTypeNumber).getCardType();
        Stuff newStuff = new Stuff(towerType,towerType,0,1,1);
        // 查找第一个空闲位置
        int index = 0;
        while (index < stuffes.length && stuffes[index] != null) {
            index++;
        }
        // 如果还有空闲位置，添加新元素
        if (index < stuffes.length) {
            stuffes[index] = newStuff;
            // 卡到位了再扣钱
            this.gameUI.subGold(100);
        }else{
            showAlertInfo("物品栏已满,无法再抽卡");
        }
    }

    public Map<String, Float> getRateBuff() {
        return buffMap;
    }

    public void setRateBuff(Map<String, Float> rateBuffMap) {
        this.buffMap = rateBuffMap;
    }

    public GameUI getGameUI() {
        return gameUI;
    }

    public List<Vector2> getPathPoints1() {
        return pathPoints1;
    }

    public List<Vector2> getPathPoints2() {
        return pathPoints2;
    }

    public void setPathPoints2(List<Vector2> pathPoints2) {
        this.pathPoints2 = pathPoints2;
    }

    /**
     * 根据enemy名称，从游戏界面获取到对应enemy
     * @param oppName
     * @return
     */
    public Enemy getEnemyByName(String oppName) {
        Enemy objEnemy = null;
        for (Enemy enemy : enemies){
            if(enemy.getEnemyName().equals(oppName)){
                objEnemy = enemy;
            }
        }
        return objEnemy;

    }

    public Map<String, Integer> getKillCountList() {
        return killCountList;
    }

    public void setKillCountList(Map<String, Integer> killCountList) {
        this.killCountList = killCountList;
    }

    public Integer getMapNo() {
        return mapNo;
    }
}