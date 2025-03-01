package com.lf.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.lf.core.MyDefenseGame;
import com.lf.manager.EnemyLoadManager;

// 主菜单界面类，实现 Screen 接口
public class MainMenuScreen implements Screen {
    // 舞台对象，用于管理和渲染界面元素
    private Stage stage;
    // 皮肤对象，用于管理界面元素的样式
    private Skin skin;
    // 游戏对象，用于切换屏幕
    private MyDefenseGame game;
    // 资源管理工具
    private AssetManager assetManager;

//    private Button startButton;
    // 开始游戏按钮
    private TextButton startButton;
    // 强化按钮
    private TextButton enhanceButton;
    // 强化按钮事件
    private ClickListener enhanceButtonClickListener;
    // 开始游戏事件监听
    private ClickListener startButtonClickListener;
    // 退出游戏按钮
    private TextButton exitButton;
    // 退出游戏事件监听
    private ClickListener exitButtonClickListener;

    // 构造函数，接收游戏对象作为参数
    public MainMenuScreen(MyDefenseGame game) {
        this.game = game;
        System.out.println(game);
        this.assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        // 创建舞台，使用 ScreenViewport 作为视口
        stage = new Stage(new ScreenViewport());
        // 获取 VisUI 的默认皮肤
        skin = VisUI.getSkin();
        if (skin == null) {
            System.out.println("皮肤加载失败");
        }

        // 创建表格布局，用于组织界面元素
        Table table = new Table();
        // 设置表格填充整个父容器
        table.setFillParent(true);
        // 将表格添加到舞台
        stage.addActor(table);

        // 获取加载的中文字体
        BitmapFont customFont = this.assetManager.get("fonts/xinsongti.fnt", BitmapFont.class);
        //"fonts/xinsongti.fnt"资源在游戏中被缩小放大过，此处重置回原大小
        customFont.getData().setScale(1f);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = customFont;
        buttonStyle.fontColor = Color.BLACK;
        Texture textureUp = this.assetManager.get("white.png", Texture.class);// 这里一个白色的纹理文件
        Texture textureDown = this.assetManager.get("black.png", Texture.class);// 这里一个黑色的纹理文件
        Drawable borderedDrawableUp = new TextureRegionDrawable(new TextureRegion(textureUp));
        Drawable borderedDrawableDown = new TextureRegionDrawable(new TextureRegion(textureDown));
        borderedDrawableUp.setMinWidth(120); // 设置最小宽度
        borderedDrawableUp.setMinHeight(30); // 设置最小高度
        borderedDrawableDown.setMinWidth(120); // 设置最小宽度
        borderedDrawableDown.setMinHeight(30); // 设置最小高度
        buttonStyle.up = borderedDrawableUp; // 设置按钮正常状态下的背景
        buttonStyle.down = borderedDrawableDown; // 设置按钮正常状态下的背景

        // 加一个空行
        table.row();

        // 创建“开始游戏”按钮
        startButton = new TextButton("开始游戏", buttonStyle);
        // 为按钮添加点击事件监听器
        startButtonClickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 处理点击事件，例如切换到GameScreen
                removeAllListener();
                // 点击按钮后切换到关卡选择界面
                game.setScreen(new SelectLevelScreen(game));
            }
        };
        startButton.addListener(startButtonClickListener);
        // 将按钮添加到表格中
        table.add(startButton).expandX().top().padTop(10).padLeft(200).padRight(200).fillX();
        // 加一个空行
        table.row();
//        ImageButton enhanceButton = new ImageButton(drawable);
        enhanceButton = new TextButton("强化防御塔", buttonStyle);
        // 为按钮添加点击事件监听器
        enhanceButtonClickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 处理点击事件，例如切换到 EnhanCard
                removeAllListener();
                // 点击按钮后切换到游戏界面
                game.setScreen(new BrowCardScreen(game));
            }
        };
        enhanceButton.addListener(enhanceButtonClickListener);
        // 将按钮添加到表格中
        table.add(enhanceButton).expandX().top().padTop(10).padLeft(200).padRight(200).fillX();
        // 加一个空行
        table.row();
        // 将按钮添加到表格中
        exitButton = new TextButton("退出游戏", buttonStyle);
//        exitButton = new ImageButton(drawable);
        // 为按钮添加点击事件监听器
        exitButtonClickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 释放资源
                dispose();
                // 点击按钮后退出游戏
                Gdx.app.exit();
            }
        };
        exitButton.addListener(exitButtonClickListener);
        table.add(exitButton).expandX().top().padTop(10).padLeft(200).padRight(200).fillX();

        // 重新计算表格布局
        table.layout();

        // 使舞台成为输入处理器，接收用户输入
        Gdx.input.setInputProcessor(stage);
    }

    // 切换到GameScreen的方法
    private void removeAllListener() {
        // 移除startButton的事件监听器
        startButton.removeListener(startButtonClickListener);
        enhanceButton.removeListener(enhanceButtonClickListener);
        exitButton.removeListener(exitButtonClickListener);
        // 切换到GameScreen的逻辑
        // 这里可以使用游戏的setScreen方法来切换屏幕
        // 例如：game.setScreen(new GameScreen());
    }


    @Override
    public void render(float delta) {
        // 清除屏幕
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // 更新舞台上的元素状态
        stage.act(delta);
        // 绘制舞台上的元素
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // 更新舞台视口的大小
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // 暂停时不做处理
    }

    @Override
    public void resume() {
        // 恢复时不做处理
    }

    @Override
    public void hide() {
        // 隐藏时不做处理
    }

    @Override
    public void dispose() {
        // 释放舞台和 VisUI 的资源
        stage.dispose();
        VisUI.dispose();
    }
}