package com.lf.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;

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
    public GameUI(AssetManager assetManager) {
        // 创建一个新的Stage对象，并使用ScreenViewport作为视口
        // ScreenViewport会根据屏幕大小自动调整舞台的大小
        stage = new Stage(new ScreenViewport());
        // 加载VisUI库，该库提供了一些预设的UI样式和组件
//        VisUI.load();
        // 获取VisUI库的默认皮肤
        skin = VisUI.getSkin();

        // 创建一个新的Table对象，Table是一种布局管理器，可用于组织和排列UI元素
        Table table = new Table();
        // 设置Table的大小以填充其父容器
        table.setFillParent(true);
        // 将Table添加到Stage中，使其成为舞台的一部分
        stage.addActor(table);
        //金币初始为100
        gold = 1000;// 加载背景图片
        goldLabel = new VisLabel("" + gold);

//        Texture goldIconTexture = assetManager.get("gold.png", Texture.class);
        // 加载金币图标纹理
        Texture goldIconTexture = new Texture(Gdx.files.internal("gold.png"));
        // 新增：创建金币图标
        com.badlogic.gdx.scenes.scene2d.ui.Image goldIcon = new com.badlogic.gdx.scenes.scene2d.ui.Image(goldIconTexture);
        // 直接设置 goldLabel 的位置
        goldLabel.setPosition(Gdx.graphics.getWidth() - goldLabel.getWidth() - 10,
                Gdx.graphics.getHeight() - goldLabel.getHeight() - 10);
        stage.addActor(goldLabel);
        // 新增：设置金币图标的位置
        goldIcon.setPosition(goldLabel.getX() - goldIcon.getWidth() - 5, goldLabel.getY());
        stage.addActor(goldIcon);

        // 直接设置 goldLabel 的位置
//        goldLabel.setPosition(Gdx.graphics.getWidth() - goldLabel.getWidth() - 10,
//                Gdx.graphics.getHeight() - goldLabel.getHeight() - 10);
//        stage.addActor(goldLabel);
        // 创建金币数量标签
//        table.add(goldLabel).top().right().pad(10); // 将标签放置在右上角并添加一些内边距
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

}