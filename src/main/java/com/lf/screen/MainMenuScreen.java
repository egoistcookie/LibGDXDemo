package com.lf.screen;

import com.badlogic.gdx.Game;
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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.lf.core.MyDefenseGame;

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
    private ImageButton startButton;
    // 开始游戏事件监听
    private ClickListener startButtonClickListener;
    // 退出游戏按钮
    private ImageButton exitButton;
    // 退出游戏事件监听
    private ClickListener exitButtonClickListener;

    // 构造函数，接收游戏对象作为参数
    public MainMenuScreen(MyDefenseGame game, AssetManager assetManager) {
        this.game = game;
        this.assetManager = assetManager;
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

        // 获取按钮的背景图片
        Texture buttonTexture = assetManager.get("alertTitle.png", Texture.class);
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));

        // 获取加载的字体
        BitmapFont customFont = assetManager.get("fonts/xinsongti.fnt", BitmapFont.class);
        Label.LabelStyle labelStyle = new Label.LabelStyle(customFont, Color.WHITE);
        // 加一个空行
        table.row();
        // 创建“开始游戏”按钮
        startButton = new ImageButton(drawable);
        // 为按钮添加点击事件监听器
        startButtonClickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 处理点击事件，例如切换到GameScreen
                removeAllListener();
                // 点击按钮后切换到游戏界面
                game.setScreen(new GameScreen(game,assetManager));
            }
        };
        startButton.addListener(startButtonClickListener);
        // 将按钮添加到表格中
        table.add(startButton);
        // 将Label放在按钮上面
        Label startLabel = new Label("开始游戏", labelStyle);
        table.add(startLabel).pad(-startButton.getHeight() / 2 + 50, -startButton.getWidth() / 2 - 100, 0, 0);
        // 加一个空行
        table.row();
        ImageButton enhanceButton = new ImageButton(drawable);
        // 将按钮添加到表格中
        table.add(enhanceButton);
        Label enhanceLabel = new Label("强化防御塔", labelStyle);
        table.add(enhanceLabel).pad(-enhanceButton.getHeight() / 2 + 70, -enhanceButton.getWidth() / 2 -100 , 0, 0);
        // 加一个空行
        table.row();
        // 将按钮添加到表格中
        exitButton = new ImageButton(drawable);
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
        table.add(exitButton);
        Label exitLabel = new Label("退出游戏", labelStyle);
        table.add(exitLabel).pad(-exitButton.getHeight() / 2 + 70, -exitButton.getWidth() / 2 -120 , 0, 0);

        // 重新计算表格布局
        table.layout();

//        TextButton startButton = new VisTextButton("开始游戏");
//        startButton.setPosition(100, 100); // 设置按钮位置
//        startButton.setSize(200, 50); // 设置按钮大小
//        // 为按钮添加点击事件监听器
//        startButton.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                // 点击按钮后切换到游戏界面
//                adapter.create();
//            }
//        });
//        // 将按钮直接添加到舞台中
//        stage.addActor(startButton);

        // 创建“开始游戏”按钮
//        TextButton startButton = new VisTextButton("Start Game");
//        startButton.setPosition(100, 100); // 设置按钮位置
//        startButton.setSize(200, 50); // 设置按钮大小

        // 使舞台成为输入处理器，接收用户输入
        Gdx.input.setInputProcessor(stage);
    }

    // 切换到GameScreen的方法
    private void removeAllListener() {
        // 移除startButton的事件监听器
        startButton.removeListener(startButtonClickListener);
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