package com.lf.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.lf.core.MyDefenseGame;
import com.lf.entities.Stuff;
import com.lf.manager.EnemyLoadManager;
import com.lf.screen.GameScreen;
import com.lf.screen.MainMenuScreen;
import com.lf.util.GameUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * GameUI类用于管理游戏的用户界面。
 * 它负责创建和管理舞台（Stage）、皮肤（Skin）以及处理渲染、调整大小和资源释放等操作。
 */
public class GameUI {

    // 舞台对象，用于管理和渲染游戏界面中的所有演员（Actors）
    private Stage stage;// 在某个类中，假设这里有一个SpriteBatch实例
    private SpriteBatch batch;
    // 皮肤对象，用于存储和管理界面元素的样式
    private Skin skin;
    // 金币数量标签
    private final VisLabel goldLabel;
    // 新增：金币数量
    private int gold;

    public int getHealth() {
        return health;
    }

    // 新增：血量数量
    private int health;
    // 新增：血量数量标签
    private VisLabel healthLabel;
    // 资源加载管理工具
    private AssetManager assetManager;
    // 游戏界面
    GameScreen gameScreen;
    // 暂停按钮
    private Button pauseButton;
    // 恢复按钮
    private Button resumeButton;
    // 游戏主体对象，负责切换界面
    private MyDefenseGame game;
    // 敌人对象管理器
    private EnemyLoadManager enemyLoadManager;
    // 新增：游戏对象区域
    private Table gameObjectTable;
    // 新增：按钮区域
    private Table buttonTable;
    // 用于控制是否继续渲染的标志变量
    private boolean isGameOver;
    // 记录物品栏中物品集合
    private Stuff[] stuffes = new Stuff[6];
    // 物品栏
    private Table stuffTable;
    // 抽卡按钮
    private Button getButton;
    // 白色字体-默认
    private BitmapFont whiteFont;
    // 自定义字体-包含中文
    private BitmapFont customFont;
    // 自定义字体-白色底色
    private BitmapFont whiteCustomFont;
    // 黑色背景图
    private Texture blackTexture;
    // 透明背景图
    private Texture transBackTexture;
    // 透明特效
    private Image transBackImage;
    // 一号特效 swifterArrow
    private Image swiftImage;
    // 白色字体-默认
    private Label.LabelStyle whiteLabelStyle;
    // 当前展示的卡片image
    private Image showCardImage;
    // 卡片image集合
    private HashMap<String,Image> cardImages;
    // arrower卡片image
    private Image arrowerImage;
    // yys卡片image
    private Image yysImage;
    // saber卡片image
    private Image saberImage;
    // swordSaint 卡片image
    private Image swordSaintImage;
    // necromancer 卡片image
    private Image necromancerImage;
    // azureDragon 卡片image
    private Image azureDragonImage;
    private Image blackTortoiseImage;
    private Image vermilionImage;
    private Image whiteTigerImage;
    private Image prosperityGirlImage;
    // 倒计时label
    private VisLabel countdownLabel;
    // 游戏已进行时间
    private float gameRunTime;
    // 倒计时出现的间隔时间
    private final float durationTime = 120f;
    // 上次倒计时出现的时间
    private float lastCountTime = 0f;
    // 绿色标签风格
    private VisLabel.LabelStyle greenLbelStyle;
    // buff特效image
    private Image buffImage;
    // 蓝色字体标签style
    private Label.LabelStyle blueLabelStyle;
    // 蓝色字体标签style
    private Label.LabelStyle whiteCustomLabelStyle;
    // 悬浮框管理器
    private TooltipManager tooltipManager;
    // 急湍甚箭
    private Label swifterArrowLabel;
    private Tooltip<Label> swifterArrowTooltip;

    public Skin getSkin() {
        return skin;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    /**
     * 构造函数，用于初始化GameUI对象。
     * 在构造函数中，会创建舞台、加载VisUI库、获取皮肤，并创建一个填充父容器的表格添加到舞台中。
     */
    public GameUI(GameScreen gameScreen, Stage stage, MyDefenseGame game, Stuff[] stuffes) {
        this.isGameOver = false;
        this.assetManager = game.getAssetManager();
        this.gameScreen = gameScreen;
        this.game = game;
        this.enemyLoadManager = game.getEnemyLoadManager();
        this.stage = stage;
        this.stuffes = stuffes;
        // 获取VisUI库的默认皮肤
        skin = VisUI.getSkin();
        batch = new SpriteBatch();
        whiteFont = new BitmapFont();
        // 加载自定义字体
        customFont = this.assetManager.get("fonts/xinsongti.fnt", BitmapFont.class);
        whiteCustomFont = this.assetManager.get("fonts/whiteYouYuan.fnt", BitmapFont.class);
        whiteCustomFont.getData().setScale(0.6f);
        // 白色字体样式
        whiteLabelStyle = new Label.LabelStyle(whiteFont, Color.WHITE);
        // 字体大小倍率（以Hiero中生成的字体大小为基准）
        customFont.getData().setScale(0.6f);
        // 蓝色字体样式
        blueLabelStyle = new Label.LabelStyle(customFont, Color.BLUE);
        // 白色幼圆字体样式
        whiteCustomLabelStyle = new Label.LabelStyle(whiteCustomFont, Color.WHITE);
        // 黑色背景图
        blackTexture = assetManager.get("black.png", Texture.class);

        // 初始化游戏对象区域和按钮区域
        gameObjectTable = new Table();
        buttonTable = new Table();

        //金币初始为100
        gold = 1000;
        // 血量初始为3
        health = 3;
        goldLabel = new VisLabel("" + gold);
        healthLabel = new VisLabel("" + health);

        // 获取屏幕的宽度
        int screenWidth = Gdx.graphics.getWidth();
        // 直接设置 goldLabel 的位置
        goldLabel.setPosition(screenWidth - goldLabel.getWidth() - 5,
                Gdx.graphics.getHeight() - goldLabel.getHeight() - 10);
        gameObjectTable.addActor(goldLabel);


        // 透明背景
        transBackTexture = assetManager.get("transBack.png", Texture.class);
        // 透明特效图标
        transBackImage = new Image(transBackTexture);

        // 实例化悬浮框管理器
        tooltipManager = TooltipManager.getInstance();
        tooltipManager.initialTime = 0.2f; // 设置悬浮框显示的延迟时间
        // 创建悬浮框
        swifterArrowLabel = new Label("急湍甚箭:弓箭手提速百分之二十", blueLabelStyle);
        swifterArrowTooltip = new Tooltip<>(swifterArrowLabel, tooltipManager);
        swifterArrowTooltip.setInstant(true); // 设置为立即显示

        // swifter特效图标背景
        swiftImage = new Image(assetManager.get("buff/swifterArrow.png", Texture.class));
        // 新增：创建buff图标 初始为透明背景
        buffImage = new Image(transBackTexture);
        // 新增：设置buff图标的位置：位于屏幕左上角
        buffImage.setPosition(10 , Gdx.graphics.getHeight() - 40);
        buffImage.setSize(30,30);
        buffImage.addListener(swifterArrowTooltip);

        // 模拟鼠标进入事件，强制显示Tooltip
//        InputEvent enterEvent = new InputEvent();
//        enterEvent.setType(InputEvent.Type.enter);
//        buffImage.fire(enterEvent);
//        swifterArrowTooltip.hide(); // 立即隐藏

        gameObjectTable.addActor(buffImage);

        // 初始化各类卡片的image以备用
        cardImages = new HashMap<>();
        arrowerImage = new Image(assetManager.get("tower/arrower.png", Texture.class));
        yysImage = new Image(assetManager.get("tower/yys.png", Texture.class));
        saberImage = new Image(assetManager.get("tower/saber.png", Texture.class));
        swordSaintImage = new Image(assetManager.get("tower/swordSaint.png", Texture.class));
        necromancerImage = new Image(assetManager.get("tower/necromancer.png", Texture.class));
        azureDragonImage = new Image(assetManager.get("tower/azureDragon.png", Texture.class));
        blackTortoiseImage = new Image(assetManager.get("tower/blackTortoise.png", Texture.class));
        whiteTigerImage = new Image(assetManager.get("tower/whiteTiger.png", Texture.class));
        vermilionImage = new Image(assetManager.get("tower/vermilion.png", Texture.class));
        prosperityGirlImage = new Image(assetManager.get("tower/prosperityGirl.png", Texture.class));
        cardImages.put("azureDragon",azureDragonImage);
        cardImages.put("blackTortoise",blackTortoiseImage);
        cardImages.put("whiteTiger",whiteTigerImage);
        cardImages.put("vermilion",vermilionImage);
        cardImages.put("prosperityGirl",prosperityGirlImage);
        cardImages.put("arrower",arrowerImage);
        cardImages.put("yys",yysImage);
        cardImages.put("saber",saberImage);
        cardImages.put("swordSaint",swordSaintImage);
        cardImages.put("necromancer",necromancerImage);
        // 创建用来显示卡片的image
        // 初始为透明背景
        showCardImage = new Image(transBackTexture);
        // 设置卡片的位置：位于屏幕正中间
        showCardImage.setSize(200,400);
        showCardImage.setPosition((float) Gdx.graphics.getWidth() /2 - showCardImage.getWidth()/2 ,
                (float) Gdx.graphics.getHeight() /2 - showCardImage.getHeight()/2 );
        gameObjectTable.addActor(showCardImage);

        // 加载金币图标纹理
        Texture goldIconTexture = assetManager.get("gold.png", Texture.class);
        // 新增：创建金币图标
        Image goldIcon = new Image(goldIconTexture);
        // 新增：设置金币图标的位置
        goldIcon.setPosition(screenWidth - goldLabel.getWidth() - goldIcon.getWidth() - 10 ,
                goldLabel.getY());
        gameObjectTable.addActor(goldIcon);

        // 新增：设置血量标签的位置
        healthLabel.setPosition(screenWidth - goldIcon.getWidth() - goldLabel.getWidth()  - healthLabel.getWidth() - 30,
                goldLabel.getY());
        gameObjectTable.addActor(healthLabel);

        // 加载血量图标纹理
        Texture healthIconTexture = assetManager.get("health.png", Texture.class);
        // 新增：创建金币图标
        Image healthIcon = new Image(healthIconTexture);
        // 新增：设置金币图标的位置
        healthIcon.setPosition(screenWidth - goldIcon.getWidth() - goldLabel.getWidth()  - healthLabel.getWidth() - healthIcon.getWidth() - 35,
                goldLabel.getY());
        gameObjectTable.addActor(healthIcon);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = whiteCustomFont;
        buttonStyle.fontColor = Color.WHITE;
        Texture textureUp = this.assetManager.get("white.png", Texture.class);// 这里一个白色的纹理文件
        Texture textureDown = this.assetManager.get("black.png", Texture.class);// 这里一个黑色的纹理文件
        Drawable borderedDrawableUp = new TextureRegionDrawable(new TextureRegion(textureUp));
        Drawable borderedDrawableDown = new TextureRegionDrawable(new TextureRegion(textureDown));
        borderedDrawableUp.setMinWidth(120); // 设置最小宽度
        borderedDrawableUp.setMinHeight(40); // 设置最小高度
        borderedDrawableDown.setMinWidth(120); // 设置最小宽度
        borderedDrawableDown.setMinHeight(40); // 设置最小高度
        buttonStyle.up = borderedDrawableDown; // 设置按钮正常状态下的背景
        buttonStyle.down = borderedDrawableDown; // 设置按钮正常状态下的背景
        // 创建暂停按钮
        pauseButton = new TextButton("暂停", buttonStyle);
        pauseButton.setSize(100, 30);
        // 计算按钮的位置，使其位于右下角
        float buttonX = screenWidth - pauseButton.getWidth();
        float buttonY = 10;
        pauseButton.setPosition(buttonX, buttonY);
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                gameScreen.pause();
            }
        });
        buttonTable.addActor(pauseButton);

        //初始化右下角物品栏
        initStuffTable();

        // 创建抽卡按钮，位于物品栏左侧
        getButton = new TextButton("抽卡", buttonStyle);
        getButton.setSize(50, 28);
        // 计算按钮的位置，使其位于右下角
        float buttonGX = screenWidth - stuffTable.getWidth() - getButton.getWidth() - 20;
        float buttonGY = pauseButton.getHeight() + 22;  // 22 是与 pauseButton 的间距
        getButton.setPosition(buttonGX, buttonGY);
        getButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                gameScreen.getCard();
            }
        });
        buttonTable.addActor(getButton);

        // 创建恢复按钮
        resumeButton = new TextButton("恢复", buttonStyle);
        resumeButton.setSize(100, 30);
        // 计算按钮的位置，使其位于右下角
        float resumeButtonX = screenWidth - pauseButton.getWidth() - resumeButton.getWidth() - 10;
        float resumeButtonY = 10;
        resumeButton.setPosition(resumeButtonX, resumeButtonY);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                gameScreen.resume();
            }
        });
        buttonTable.addActor(resumeButton);

        // 创建加快按钮
        TextButton quicklyButton = new TextButton("加速", buttonStyle);
        quicklyButton.setSize(100, 30);
        // 计算按钮的位置，使其位于右下角
        float buttonQx = screenWidth - pauseButton.getWidth() - resumeButton.getWidth() - quicklyButton.getWidth() - 15;
        float buttonQy = 10;
        quicklyButton.setPosition(buttonQx, buttonQy);
        quicklyButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                gameScreen.quickly();
            }
        });
        buttonTable.addActor(quicklyButton);

        // 创建减慢按钮
        TextButton slowlyButton = new TextButton("减速", buttonStyle);
        slowlyButton.setSize(100, 30);
        // 计算按钮的位置，使其位于右下角
        float buttonSx = screenWidth - slowlyButton.getWidth() - slowlyButton.getWidth() - quicklyButton.getWidth() - slowlyButton.getWidth() - 20;
        float buttonSy = 10;
        slowlyButton.setPosition(buttonSx, buttonSy);
        slowlyButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                gameScreen.slowly();
            }
        });
        buttonTable.addActor(slowlyButton);

        // 创建退出按钮
        TextButton returnButton = new TextButton("返回", buttonStyle);
        returnButton.setSize(100, 30);
        // 计算按钮的位置，使其位于左下角
        float buttonRx = 10;
        float buttonRy = 10;
        returnButton.setPosition(buttonRx, buttonRy);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                // 弹出提示框，是否确定退出
                showReturnConfirmationDialog();
            }
        });
        buttonTable.addActor(returnButton);

        // 倒计时提示框放在最后add，会显示在其他label的顶层
        // 倒计时提示框 位于屏幕中线距离y轴顶点200的位置
        greenLbelStyle = new VisLabel.LabelStyle();
        greenLbelStyle.font = whiteCustomFont;
        greenLbelStyle.fontColor = Color.RED;
        countdownLabel = new VisLabel("下一波即将刷新 倒计时：秒", greenLbelStyle);
        countdownLabel.setPosition((float) screenWidth /2 - countdownLabel.getWidth()/2,
                goldLabel.getY() - countdownLabel.getHeight() - 20);
        gameObjectTable.addActor(countdownLabel);

        // 将游戏对象区域和按钮区域添加到舞台
        this.stage.addActor(gameObjectTable);
        this.stage.addActor(buttonTable);
    }

    private void initStuffTable() {
        // 获取屏幕的宽度
        int screenWidth = Gdx.graphics.getWidth();
        // 创建一个黑色边框的 Drawable
        Texture texture = new Texture(Gdx.files.internal("white.png")); // 这里假设存在一个白色的纹理文件
        NinePatch patch = new NinePatch(new TextureRegion(texture), 1, 1, 1, 1);
        Drawable borderedDrawable = new NinePatchDrawable(patch);
        borderedDrawable.setMinWidth(244); // 设置最小宽度
        borderedDrawable.setMinHeight(63); // 设置最小高度

        // 创建一个六格展示的表格
        stuffTable = new Table();  // 创建一个新的表格用于展示图片
        stuffTable.setSize(244,63);
        stuffTable.defaults().pad(1);  // 设置默认的单元格间距
        // 设置表格的背景为带有黑色边框的 Drawable
        stuffTable.setBackground(borderedDrawable);
        // 加载六张不同的贴图
        Texture[] imageTextures = new Texture[6];
        for (int i = 0; i < 6; i++) {
            // 创建一个 Stack 布局来组合图片和数字标签
            Stack stack = new Stack();
            // 空label
            Label countLabel = new Label("",skin);
            if(stuffes[i]!=null){
                Stuff stuff = stuffes[i];
                // 假设图片命名为 image1.png 到 image6.png (图片的长宽比应该是8:3)
                imageTextures[i] = assetManager.get("tower/"+stuff.getStuffType() +"Stuff.png", Texture.class);
                // 值为计算后的stuff等级
                countLabel = new Label(String.valueOf(GameUtil.calcLevel(stuff.getStuffExp())), whiteLabelStyle);
                // 设置 Label 的对齐方式为右下角
                countLabel.setAlignment(Align.topRight);
            }else{
                // 空白格子显示为黑色背景图
                imageTextures[i] = blackTexture;
            }
            Image image = new Image(imageTextures[i]);
            image.setSize(80,30);
            stack.add(image);
            // label必须放在image下add才能显示出来
            stack.add(countLabel);
            // 将 Stack 布局添加到表格中
            stuffTable.add(stack).minHeight(30).minWidth(80);
            // 每添加三个图片换行一次，实现两排展示
            if ((i + 1) % 3 == 0) {
                stuffTable.row();
            }
        }
        // 计算 stuffTable 的位置，使其位于右下角 pauseButton 按钮上方
        float tableX = screenWidth - stuffTable.getWidth() - 10;
        float tableY = pauseButton.getHeight() + 20;  // 20 是与 pauseButton 的间距
        stuffTable.setPosition(tableX, tableY);
        // 将 stuffTable 添加到舞台
        this.stage.addActor(stuffTable);
    }

    /**
     * 渲染方法，用于更新和绘制舞台中的所有演员。
     * 该方法通常在游戏的渲染循环中被调用。
     */
    public void render(float elapsedTimeSeconds) {

        // 绘制物品栏
        stuffTableRender();
        // 绘制倒计时
        countdownRender(elapsedTimeSeconds);
        // 开始绘制
        batch.begin();
        // 调用stage的act方法，更新舞台中所有演员的状态，例如处理输入、动画等
        stage.act(Gdx.graphics.getDeltaTime());
        // 调用stage的draw方法，绘制舞台中的所有演员
        stage.draw();

        // 分别更新游戏对象区域和按钮区域
//        gameObjectTable.act(Gdx.graphics.getDeltaTime());
//        this.buttonTable.act(Gdx.graphics.getDeltaTime());
//
//        // 分别绘制游戏对象区域和按钮区域
//        gameObjectTable.draw(batch, 1f);
//        this.buttonTable.draw(batch, 1f);

        if (health <= 0 && !isGameOver) {
            // 血量等于0之后，只需要进入一次即可，不要反复提示
            isGameOver = true;
            // 先提示
            gameScreen.showAlertInfo("Game over",0,0);
            // 再出现按钮
//            Timer.schedule(new Timer.Task() {
//                @Override
//                public void run() {
                    showGameOverDialog();
//                }
//            }, 1.5f);
        }
        // 结束绘制
        batch.end();
    }

    /**
     * 绘制倒计时
     * @param elapsedTimeSeconds 游戏进行时间
     */
    private void countdownRender(float elapsedTimeSeconds) {
        gameRunTime = elapsedTimeSeconds;
        // 如果游戏运行时间与上一次出现倒计时提示的差值达到了间隔时间+10，则出现倒计时，持续时间10秒
        if(gameRunTime - lastCountTime >= durationTime-10){
            float countTime = lastCountTime + durationTime - gameRunTime;
            countdownLabel.setText("下一波即将刷新 倒计时" + Math.round(countTime) + "秒");
        }else{
            countdownLabel.setText("");
        }
        // 如果游戏运行时间与上一次出现倒计时提示的差值，达到了倒计时的间隔时间，则重新开始计时
        if(gameRunTime - lastCountTime >= durationTime){
            lastCountTime = gameRunTime;
        }
    }

    /**
     * 绘制物品栏
     */
    private void stuffTableRender() {
        //每次绘制前都先清空
        stuffTable.clear();
        // 加载六张不同的贴图
        Texture[] imageTextures = new Texture[6];
        for (int i = 0; i < 6; i++) {
            // 创建一个 Stack 布局来组合图片和数字标签
            Stack stack = new Stack();
            // 空label
            Label countLabel = new Label("",skin);
            // 空starLabel
            Label starLabel = new Label("",skin);
            if(stuffes[i]!=null){
                Stuff stuff = stuffes[i];
                // （贴图图片的长宽比应该是8:3，且以Stuff.png为后缀)
                // TODO：贴图的加载不要每次都从asserManager获取，占用内存，后期改为初始化时同统一获取所有贴图
                imageTextures[i] = assetManager.get("tower/"+stuff.getStuffType() +"Stuff.png", Texture.class);
                // 计算等级
                countLabel = new Label(String.valueOf(GameUtil.calcLevel(stuff.getStuffExp())), whiteLabelStyle);
                // 星级
                starLabel = new Label(stuff.getStuffStarLevel()==3?"***":stuff.getStuffStarLevel()==2?"**":"*", whiteLabelStyle);
                // 设置登记标签的对齐方式为右上角
                countLabel.setAlignment(Align.topRight);
                // 设置星级标签的对齐方式为左上角
                starLabel.setAlignment(Align.topLeft);
            }else{
                // 空白格子显示为黑色背景图
                imageTextures[i] = assetManager.get("black.png", Texture.class);
            }
            Image image = new Image(imageTextures[i]);
            stack.add(image);
            // label必须放在image下add才能显示出来
            stack.add(countLabel);
            stack.add(starLabel);
            // 将 Stack 布局添加到表格中
            stuffTable.add(stack).minHeight(30).minWidth(80);
            // 每添加三个图片换行一次，实现两排展示
            if ((i + 1) % 3 == 0) {
                stuffTable.row();
            }
        }
    }

    /**
     * 退出游戏的确认框
     */
    private void showReturnConfirmationDialog() {
        // 游戏暂停
        gameScreen.pause();
        Dialog dialog = new Dialog("确认退出", skin) {
            @Override
            protected void result(Object object) {
                if ("yes".equals(object)) {
                    // 先保存杀敌数
                    saveKillCount();
                    // 用户选择“是”，游戏返回主界面
                    game.setScreen(new MainMenuScreen(game));
                }else{
                    // 用户选择“否”，对话框会自动关闭
                    gameScreen.resume();
                }
            }
        };
        // 加载自定义字体
        BitmapFont customFont = this.assetManager.get("fonts/xinsongti.fnt", BitmapFont.class);
        // 字体大小倍率（以Hiero中生成的字体大小为基准）
        customFont.getData().setScale(0.5f);
        // 创建文本标签样式，使用自定义字体
        Label.LabelStyle labelStyle = new Label.LabelStyle(customFont, Color.BLACK);
        // 设置对话框内容，并应用自定义字体样式
        dialog.text("你是否确定要退出？", labelStyle);
        // 创建按钮样式，使用自定义字体
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = customFont;
        buttonStyle.fontColor = Color.BLACK;
        // 添加“是”和“否”按钮
        dialog.button("是", "yes", buttonStyle);
        dialog.button("否", "no", buttonStyle);
        dialog.toFront();
        float buttonRx = 300;
        float buttonRy = 300;
        dialog.setPosition(buttonRx, buttonRy);
//        buttonTable.add(dialog);
        // 显示对话框
        dialog.show(stage);
    }

    // 保存杀敌数到配置文件
    private void saveKillCount() {

        Map<String, Integer> killCountMap = gameScreen.getKillCountList();
        // 更新card_type_config.yml
        try {
            // 读取 YAML 文件
            Yaml yaml = new Yaml();
            String filePath = System.getProperty("user.dir") + "/src/main/resources/card_type_config.yml"; // 写入到用户目录
            FileReader reader = new FileReader(filePath);
            // 读取配置文件内容
            Map<String, java.util.List<Map<String, Object>>> data = yaml.load(reader);
            reader.close();
            // 获取 cardTypeConfigs 列表
            java.util.List<Map<String, Object>> cardTypeConfigsRead = (java.util.List<Map<String, Object>>) data.get("cardTypeConfigs");
            // 遍历列表，找到目标 cardType
            for (Map<String, Object> config : cardTypeConfigsRead) {
                String cardType = (String) config.get("cardType");
                if (killCountMap.get(cardType) != null) {
                    // 更新 attackPower
                    int killCount = killCountMap.get(cardType);
                    config.put("killCount", killCount); // attackPower + 1
                    System.out.println("Updated " + config.get("cardType") + "'s killCount to " + killCount);
                }
            }
            // 将更新后的内容写入外部文件
            System.out.println(filePath);
            FileWriter writer = new FileWriter(filePath);
            yaml.dump(data, writer);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("YAML file updated successfully!");

    }

    /**
     * 调整大小方法，当窗口大小改变时调用该方法。
     * 该方法会更新舞台的视口大小，以适应新的窗口尺寸。
     *
     * @param width 新的窗口宽度
     * @param height 新的窗口高度
     */
    public void resize(int width, int height) {
        // 获取舞台的视口，并调用其update方法来更新视口的大小
        // 第三个参数true表示立即应用更新
        stage.getViewport().update(width, height, true);
    }

    /**
     * 资源释放方法，用于释放GameUI对象所占用的资源。
     * 该方法通常在游戏关闭或不再需要该UI时调用，以防止内存泄漏。
     */
    public void dispose() {
        // 调用stage的dispose方法，释放舞台所占用的资源
        stage.dispose();
        // 调用VisUI的dispose方法，释放VisUI库所占用的资源
        VisUI.dispose();
    }

    public void setGold(int gold) {
        goldLabel.setText("" + gold);
    }

    public int getGold() {
        return this.gold;
    }

    /**
     * 增加金币
     * @param addGold
     */
    public void addGold(int addGold) {
        this.gold += addGold;
        goldLabel.setText("" + this.gold);
    }
    /**
     * 减少金币
     * @param subGold
     */
    public void subGold(int subGold) {
        this.gold -= subGold;
        goldLabel.setText("" + this.gold);
    }

    public Stage getStage() {
        return stage;
    }

    // 新增：减少血量
    public void subHealth() {
        health--;
        healthLabel.setText("" + health);
    }

    // 新增：显示游戏结束对话框
    private void showGameOverDialog() {

        gameScreen.pause();

        Array<Actor> actores = this.stage.getActors();
        for(Actor actor: actores){
            if(actor instanceof Dialog dialogItem){
                // 防止重复addActor
                if(dialogItem.getTitleLabel()!=null && dialogItem.getTitleLabel().equals("游戏结束")){
                    return ;
                }
            }
        }

        Dialog dialog = new Dialog("游戏结束", this.skin);
        // 获取加载的中文字体
        BitmapFont customFont = this.assetManager.get("fonts/xinsongti.fnt", BitmapFont.class);
        // 字体大小倍率（以Hiero中生成的字体大小为基准）
        customFont.getData().setScale(0.5f);
        Label.LabelStyle labelStyle = new Label.LabelStyle(customFont, Color.RED);
        // 创建 Label 并设置样式
        Label gameOverLabel = new Label("建议调整策略", labelStyle);
        // 设置Label的文本居中对齐
        gameOverLabel.setAlignment(Align.center);
        gameOverLabel.getStyle().background = skin.newDrawable("white");

        // 设置 Dialog 的填充和对齐方式
        dialog.padTop(20);
        dialog.setPosition(Math.round((stage.getWidth() - dialog.getWidth()) / 2), Math.round((stage.getHeight() - dialog.getHeight()) / 2));

        // 获取 Dialog 的内容表
        Table contentTable = dialog.getContentTable();
        // 清除默认的填充和间距
        contentTable.clearChildren();
        // 确保 Table 填满 Dialog
        contentTable.setFillParent(true);
        // 添加标签并设置布局规则
        contentTable.add(gameOverLabel).expandX().top().padTop(10).center().fillX();
        // 将 Label 添加到内容表的顶部
        contentTable.row();

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = customFont;
        buttonStyle.fontColor = Color.BLACK;
        Texture texture = new Texture(Gdx.files.internal("white.png")); // 这里假设存在一个白色的纹理文件
        Drawable borderedDrawable = new TextureRegionDrawable(new TextureRegion(texture));
        borderedDrawable.setMinWidth(90); // 设置最小宽度
        borderedDrawable.setMinHeight(30); // 设置最小高度
//        borderedDrawable.setLeftWidth(2); // 设置左边框宽度
//        borderedDrawable.setRightWidth(2); // 设置右边框宽度
//        borderedDrawable.setTopHeight(2); // 设置上边框宽度
//        borderedDrawable.setBottomHeight(2); // 设置下边框宽度
        buttonStyle.up = borderedDrawable; // 设置按钮正常状态下的背景

        // 原代码：dialog.button("restart", "restart", buttonStyle);
        // 替换为以下代码
        TextButton restartButton = new TextButton("重新开始", buttonStyle);
        restartButton.setName("重新开始");  // 设置按钮的名称，对应原代码中的第二个参数
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                dialog.hide();  // 隐藏对话框
                if ("重新开始".equals(restartButton.getName())) {
                    // 重新开始游戏的逻辑
                    // restartGame();
//                    gameScreen.resume();
                    game.setScreen(new GameScreen(game,1));
                }
            }
        });
        // 添加标签并设置布局规则
        contentTable.add(restartButton).expandX().top().padTop(30).center().fillX();

        contentTable.row();

//        dialog.button("return", "return", buttonStyle);
        TextButton returnButton = new TextButton("回到菜单", buttonStyle);
        returnButton.setName("回到菜单");  // 设置按钮的名称，对应原代码中的第二个参数
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();  // 隐藏对话框
                if ("回到菜单".equals(returnButton.getName())) {
                    // 重新开始游戏的逻辑
                    // restartGame();
                    gameScreen.resume();
                    game.setScreen(new MainMenuScreen(game));
                }
            }
        });
        // 添加标签并设置布局规则
        contentTable.add(returnButton).expandX().top().padTop(30).center().fillX();

        dialog.setSize(180,200);
        this.stage.addActor(dialog);
//        dialog.setSize(100,200);


        // 使用 Timer 在 3 秒后移除提示窗口（可根据需要调整时间）
//        Timer.schedule(new Timer.Task() {
//            @Override
//            public void run() {
//                dialogTable.remove();
//                blockingTable.remove();
//            }
//        }, 3f);
        // 以此停止GameScreen的reader渲染
//        this.gameScreen.gameOver();

    }

    // 新增：重新开始游戏的方法
    private void restartGame() {
        // 重置金币和血量
        gold = 1000;
        health = 3;
        goldLabel.setText("" + gold);
        healthLabel.setText("" + health);
        // 其他重置逻辑，如重置敌人、防御塔等
    }

    public Image getBuffImage() {
        return buffImage;
    }

    /**
     * 更新buff类型
     * @param buffType buff类型
     */
    public void setBuffImage(String buffType) {
        if(buffType!=null && assetManager.get("buff/"+buffType+".png", Texture.class)!=null){
            buffImage.setDrawable(swiftImage.getDrawable());
            buffImage.addListener(swifterArrowTooltip);
        }else{
            // 若未触发，或找不到特效png，则赋值为透明特效图标
            buffImage.setDrawable(transBackImage.getDrawable());
            buffImage.removeListener(swifterArrowTooltip);
        }
    }

    /**
     * 显示卡片图片
     * @param cardType
     */
    public void showCardImage(String cardType){
        if(cardImages.get(cardType) != null){
            showCardImage.setDrawable(cardImages.get(cardType).getDrawable());
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    // 2秒后自动消失
                    showCardImage.setDrawable(transBackImage.getDrawable());
                }
            }, 2f);
        }
    }
}