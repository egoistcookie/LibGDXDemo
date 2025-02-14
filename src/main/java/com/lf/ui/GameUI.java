package com.lf.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.lf.core.MyDefenseGame;
import com.lf.screen.GameScreen;
import com.lf.screen.MainMenuScreen;

/**
 * GameUI类用于管理游戏的用户界面。
 * 它负责创建和管理舞台（Stage）、皮肤（Skin）以及处理渲染、调整大小和资源释放等操作。
 */
public class GameUI {

    // 舞台对象，用于管理和渲染游戏界面中的所有演员（Actors）
    private Stage stage;
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
    private Table blockingTable; // 用于阻止页面其他部分点击的覆盖层
    private MyDefenseGame game;

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
    public GameUI(GameScreen gameScreen, AssetManager assetManager, Stage stage, MyDefenseGame game) {
        this.assetManager = assetManager;
        this.gameScreen = gameScreen;
        this.game = game;
        // 创建一个新的Stage对象，并使用ScreenViewport作为视口
        // ScreenViewport会根据屏幕大小自动调整舞台的大小
        this.stage = stage;
        // 加载VisUI库，该库提供了一些预设的UI样式和组件
//        VisUI.load();
        // 获取VisUI库的默认皮肤
        skin = VisUI.getSkin();

        //金币初始为100
        gold = 1000;
        // 血量初始为3
        health = 3;
        goldLabel = new VisLabel("" + gold);
        healthLabel = new VisLabel("" + health);

        // 直接设置 goldLabel 的位置
        goldLabel.setPosition(Gdx.graphics.getWidth() - goldLabel.getWidth() - 5,
                Gdx.graphics.getHeight() - goldLabel.getHeight() - 10);
        this.stage.addActor(goldLabel);

        // 加载金币图标纹理
        Texture goldIconTexture = assetManager.get("gold.png", Texture.class);
        // 新增：创建金币图标
        Image goldIcon = new Image(goldIconTexture);
        // 新增：设置金币图标的位置
        goldIcon.setPosition(Gdx.graphics.getWidth() - goldLabel.getWidth() - goldIcon.getWidth() - 10 ,
                goldLabel.getY());
        this.stage.addActor(goldIcon);

        // 新增：设置血量标签的位置
        healthLabel.setPosition(Gdx.graphics.getWidth() - goldIcon.getWidth() - goldLabel.getWidth()  - healthLabel.getWidth() - 30,
                goldLabel.getY());
        this.stage.addActor(healthLabel);

        // 加载血量图标纹理
        Texture healthIconTexture = assetManager.get("health.png", Texture.class);
        // 新增：创建金币图标
        Image healthIcon = new Image(healthIconTexture);
        // 新增：设置金币图标的位置
        healthIcon.setPosition(Gdx.graphics.getWidth() - goldIcon.getWidth() - goldLabel.getWidth()  - healthLabel.getWidth() - healthIcon.getWidth() - 35,
                goldLabel.getY());
        this.stage.addActor(healthIcon);

        // 创建暂停按钮
        pauseButton = new TextButton("Pause", skin);
        // 获取屏幕的宽度和高度
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
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
        this.stage.addActor(pauseButton);

        // 创建恢复按钮
        resumeButton = new TextButton("Resume", skin);
        resumeButton.setSize(100, 30);
        // 计算按钮的位置，使其位于右下角
        float resumeButtonX = screenWidth - pauseButton.getWidth() - 10 - pauseButton.getWidth();
        float resumeButtonY = 10;
        resumeButton.setPosition(resumeButtonX, resumeButtonY);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                gameScreen.resume();
            }
        });
        this.stage.addActor(resumeButton);

        // 初始化覆盖层
        blockingTable = new Table();
        blockingTable.setFillParent(true);
        blockingTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("back.png")))));
        blockingTable.setColor(0, 0, 0, 0.5f); // 半透明黑色
        blockingTable.setVisible(false);
        blockingTable.setTouchable(Touchable.enabled);
        this.stage.addActor(blockingTable);

    }

    /**
     * 渲染方法，用于更新和绘制舞台中的所有演员。
     * 该方法通常在游戏的渲染循环中被调用。
     */
    public void render() {
        // 调用stage的act方法，更新舞台中所有演员的状态，例如处理输入、动画等
        stage.act(Gdx.graphics.getDeltaTime());
        // 调用stage的draw方法，绘制舞台中的所有演员
        stage.draw();
        if (health <= 0) {
            showGameOverDialog();
        }
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

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    // 新增：减少血量
    public void subHealth() {
        health--;
        healthLabel.setText("" + health);
    }

    // 新增：显示游戏结束对话框
    private void showGameOverDialog() {

        blockingTable.setVisible(true);
        gameScreen.pause();

        Dialog dialog = new Dialog("游戏结束", this.skin) {
            @Override
            protected void result(Object object) {
                if ("restart".equals(object)) {
                    // 重新开始游戏的逻辑
//                    restartGame();
                    gameScreen.resume();
                    blockingTable.setVisible(false);
                    game.setScreen(new MainMenuScreen(game,assetManager));
                }
            }
        };
        // 获取加载的中文字体
        BitmapFont customFont = this.assetManager.get("fonts/xinsongti.fnt", BitmapFont.class);
        // 字体大小倍率（以Hiero中生成的字体大小为基准）
        customFont.getData().setScale(0.5f);

        Label.LabelStyle labelStyle = new Label.LabelStyle(customFont, Color.BLACK);
        dialog.text("game over", labelStyle);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = customFont;
        buttonStyle.fontColor = Color.BLACK;
        dialog.button("restart", "restart", buttonStyle);
        dialog.show(stage);

        // 创建一个Label对象，用于显示提示文本，初始文本为空字符串
//        Label hintLabel = new Label("Game over",skin);
//        // 加载背景图片
//        Texture backgroundTexture = assetManager.get("alertTitle.png", Texture.class);
//        TextureRegion backgroundRegion = new TextureRegion(backgroundTexture);
//        Image backgroundImage = new Image(backgroundRegion);
//        // 创建一个Table作为提示框容器
//        Table dialogTable = new Table();
//        dialogTable.setBackground(new TextureRegionDrawable(backgroundRegion));
//        dialogTable.add(hintLabel).pad(20); // 添加一些内边距
//        // 创建重新开始按钮
//        TextButton restartButton = new TextButton("restart game", skin);
//        restartButton.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                // 这里添加重新开始游戏的逻辑
//                restartGame();
//            }
//        });
//        dialogTable.row();
//        dialogTable.add(restartButton).pad(20);
//        // 调整提示框的大小以适应文字长度
//        dialogTable.pack();
//
//        float x = (float) Gdx.graphics.getWidth() / 2 - dialogTable.getWidth() / 2;
//        float y = (float) Gdx.graphics.getHeight() / 2 - dialogTable.getHeight() / 2;
//        // 直接设置 dialogTable 的位置
//        dialogTable.setPosition(x, y);
//        // 将对话框添加到舞台
//        stage.addActor(dialogTable);
//
//        // 确保重新开始按钮在最上层，可点击
//        restartButton.toFront();

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