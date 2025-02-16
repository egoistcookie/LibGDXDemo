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
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.lf.core.MyDefenseGame;
import com.lf.entities.Stuff;
import com.lf.manager.EnemyLoadManager;
import com.lf.screen.GameScreen;
import com.lf.screen.MainMenuScreen;

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

        // 创建暂停按钮
        pauseButton = new TextButton("Pause", skin);
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
        getButton = new TextButton("Get", skin);
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
        resumeButton = new TextButton("Resume", skin);
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
        TextButton quicklyButton = new TextButton("quickly", skin);
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
        TextButton slowlyButton = new TextButton("slowly", skin);
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
        TextButton returnButton = new TextButton("return", skin);
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
            if(stuffes[i]!=null){
                // 假设图片命名为 image1.png 到 image6.png (图片的长宽比应该是8:3)
                imageTextures[i] = assetManager.get("tower/"+stuffes[i].getStuffType() +".png", Texture.class);
            }else{
                // 空白格子显示为黑色背景图
                imageTextures[i] = assetManager.get("black.png", Texture.class);
            }
            Image image = new Image(imageTextures[i]);
            // 将图片添加到表格中
            stuffTable.add(image).minHeight(30).minWidth(80);
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
    public void render() {

        // 绘制物品栏
        stuffTableRender();
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
     * 绘制物品栏
     */
    private void stuffTableRender() {
        //每次绘制前都先清空
        stuffTable.clear();
        // 加载六张不同的贴图
        Texture[] imageTextures = new Texture[6];
        for (int i = 0; i < 6; i++) {
            if(stuffes[i]!=null){
                // 假设图片命名为 image1.png 到 image6.png (图片的长宽比应该是8:3)
                imageTextures[i] = assetManager.get("tower/"+stuffes[i].getStuffType() +".png", Texture.class);
                // 设置纹理过滤方式为线性过滤
////        enemyTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            }else{
                // 空白格子显示为黑色背景图
                imageTextures[i] = assetManager.get("black.png", Texture.class);
            }
            Image image = new Image(imageTextures[i]);
            // 将图片添加到表格中
            stuffTable.add(image).minHeight(30).minWidth(80);
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
                    game.setScreen(new GameScreen(game));
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

}