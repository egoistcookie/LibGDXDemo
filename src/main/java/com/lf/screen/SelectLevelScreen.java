package com.lf.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.kotcrab.vis.ui.VisUI;
import com.lf.core.MyDefenseGame;

/**
 * 关卡选择视图
 */
public class SelectLevelScreen implements Screen {
    // 游戏对象，用于切换屏幕
    private MyDefenseGame game;
    // 资源管理工具
    private final AssetManager assetManager;
    // 舞台对象，用于管理和渲染界面元素
    private Stage stage;
    // 皮肤对象，用于管理界面元素的样式
    private final Skin skin;
    // 地图选择滚动条
    private ScrollPane levelPane;
    // 地图缩略图表格
    private Table mapTable;
    // 标签风格
    private Label.LabelStyle labelStyle;

    public SelectLevelScreen(MyDefenseGame game){
        this.game = game;
        this.assetManager = game.getAssetManager();
        // 获取VisUI库的默认皮肤
        skin = VisUI.getSkin();

        BitmapFont customFont = assetManager.get("fonts/whiteYouYuan.fnt", BitmapFont.class);
        // 字体大小倍率
        customFont.getData().setScale(0.5f);
        // 创建LabelStyle时使用缩放后的字体
        labelStyle = new Label.LabelStyle(customFont, Color.GREEN);

    }
    @Override
    public void show() {

        stage = new Stage();

        // 创建表格布局，用于组织界面元素
        mapTable = new Table();
        // 获取默认的 ScrollPaneStyle
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        // 设置滚动条的背景和滑块（Knob）的 Drawable
        Image defaultScroll = new Image(assetManager.get("default-scroll-knob.png", Texture.class));
        Image vScroll = new Image(assetManager.get("default-scroll.png", Texture.class));
        // 滚动条背景
//        scrollPaneStyle.background = vScroll.getDrawable();
        // 纵向滚动条贴图
//        scrollPaneStyle.vScrollKnob = vScroll.getDrawable();
        // 横向滚动条贴图
        scrollPaneStyle.hScrollKnob = defaultScroll.getDrawable();
        // 创建 ScrollPane 并应用样式
        levelPane = new ScrollPane(mapTable, scrollPaneStyle);
        levelPane.setWidth(1200);
        levelPane.setHeight(800);
//        levelPane.setHeight(Gdx.graphics.getHeight()); // 设置滚动面板高度为屏幕高度
//        levelPane.setFadeScrollBars(true); // 始终显示滚动条
        // 确保 scrollPane 可以滚动
        levelPane.setScrollingDisabled(false, false);// 确保水平和垂直滚动都启用
        levelPane.setForceScroll(false, true);// 只允许垂直滚动

        stage.addActor(levelPane);
    }

    @Override
    public void render(float delta) {

        // 每次绘制前都先清空
        mapTable.clear();
        // 第一行添加文字
        for (int i = 1; i <= 4; i++) {
            Label levelLabel = new Label("第 "+i+" 关",labelStyle);
            mapTable.add(levelLabel).minWidth(40).width(40).minHeight(40).height(40)
                    .center();//.padLeft(10);
        }
        // 换行
        mapTable.row();
        // 第二行添加图片
        for (int i = 1; i <= 4; i++) {

            // 创建一个 Stack 布局来组合图片和数字标签
            Stack stack = new Stack();
            // 空label
            Label countLabel = new Label("",skin);
            // 空starLabel
            Label starLabel = new Label("",skin);
            Texture imageTextures = assetManager.get("map/map"+i +".png", Texture.class);
            Image image = new Image(imageTextures);
            image.setTouchable(Touchable.enabled);
            // 为每张图片添加点击事件
            int finalI = i;
            image.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    System.out.println("Image clicked: " + finalI);
                    // 切到游戏界面
                    game.setScreen(new GameScreen(game,finalI));
                    return true;
                }
            });
            stack.add(image);
            // label必须放在image下add才能显示出来
            stack.add(countLabel);
            stack.add(starLabel);
            // 将 Stack 布局添加到表格中
            mapTable.add(image).minWidth(400).width(400).minHeight(300).height(300)
                    .padLeft(10); // 添加单元格，设置宽度和高度
        }

        // 清除屏幕
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // 更新舞台上的元素状态
        stage.act(delta);
        // 绘制舞台上的元素
        stage.draw();

        // 确保舞台可以处理输入事件
        Gdx.input.setInputProcessor(stage);
        // 处理用户输入
        handleInput();

    }

    // handleInput方法用于处理用户的输入事件
    private void handleInput() {
        // 检查ESC是否刚刚被按下
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // 移除所有监听
            removeAllListener();
            // 切回主界面
            game.setScreen(new MainMenuScreen(game));
        }
    }

    // 移除所有监听的方法
    private void removeAllListener() {
        // 遍历 cardTable 的所有子元素
        for (Actor actor : mapTable.getChildren()) {
            // 移除所有 InputListener
            actor.clearListeners();
        }
    }


    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
