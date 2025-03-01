package com.lf.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.*;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.kotcrab.vis.ui.VisUI;
import com.lf.config.CardTypeConfig;
import com.lf.core.MyDefenseGame;
import com.lf.manager.EnemyLoadManager;

public class BrowCardScreen implements Screen {
    // 游戏对象，用于切换屏幕
    private MyDefenseGame game;
    // 资源管理工具
    private final AssetManager assetManager;
    // 敌人加载管理工具
    private EnemyLoadManager enemyLoadManager;
    // 舞台对象，用于管理和渲染界面元素
    private Stage stage;
    // 皮肤对象，用于管理界面元素的样式
    private final Skin skin;

    private ScrollPane scrollPane;

    private Table cardTable;
    // 构造方法
    public BrowCardScreen(MyDefenseGame game) {
        this.game = game;
        this.assetManager = game.getAssetManager();
        this.enemyLoadManager = game.getEnemyLoadManager();
        // 获取VisUI库的默认皮肤
        skin = VisUI.getSkin();
    }

    @Override
    public void show() {
        stage = new Stage();

        // 创建表格布局，用于组织界面元素
        cardTable = new Table();
        cardTable.setWidth(1140); // 设置表格宽度为900


        scrollPane = new ScrollPane(cardTable);
        scrollPane.setWidth(1200);
//            scrollPane.setHeight(1080);
        scrollPane.setHeight(Gdx.graphics.getHeight()); // 设置滚动面板高度为屏幕高度
        // 获取ScrollPane的样式
//        ScrollPane.ScrollPaneStyle style = scrollPane.getStyle();
//        Image defaultScroll = new Image(assetManager.get("default-scroll-knob.png", Texture.class));
//        Image vScroll = new Image(assetManager.get("default-scroll.png", Texture.class));
        // 设置垂直滚动条始终显示
//        style.vScrollKnob = defaultScroll.getDrawable();
//        style.vScroll = vScroll.getDrawable();
        scrollPane.setFadeScrollBars(false); // 始终显示滚动条
//        scrollPane.setStyle(style);
        // 确保 scrollPane 可以滚动
        scrollPane.setScrollingDisabled(false, false);// 确保水平和垂直滚动都启用
        scrollPane.setForceScroll(false, true);// 只允许垂直滚动

        stage.addActor(scrollPane);
    }

    @Override
    public void render(float delta) {

        //每次绘制前都先清空
        cardTable.clear();

        // 获取敌人列表
        List<CardTypeConfig> cardList = enemyLoadManager.getCardTypeConfigs(); // 假设EnemyLoadManager有一个getList方法返回列表
        if (cardList != null) {
            for (int i = 0; i < cardList.size(); i++) {
                CardTypeConfig cardTypeConfig = cardList.get(i);
                // 创建一个 Stack 布局来组合图片和数字标签
                Stack stack = new Stack();
                // 空label
                Label countLabel = new Label("",skin);
                // 空starLabel
                Label starLabel = new Label("",skin);
                Texture imageTextures = assetManager.get("tower/"+cardTypeConfig.getCardType() +".png", Texture.class);
                Image image = new Image(imageTextures);
                image.setTouchable(Touchable.enabled);
                // 为每张图片添加点击事件
                image.addListener(new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        System.out.println("Image clicked: " + cardTypeConfig.getCardType());
                        // 切到强化界面
                        game.setScreen(new EnhanceCardScreen(game,cardTypeConfig));
                        return true;
                    }
                });
                stack.add(image);
                // label必须放在image下add才能显示出来
                stack.add(countLabel);
                stack.add(starLabel);
                // 将 Stack 布局添加到表格中
                cardTable.add(image).minWidth(190).width(190).minHeight(360).height(360); // 添加单元格，设置宽度和高度
//                cardTable.add(stack).minWidth(190).width(190).minHeight(360).height(360); // 添加单元格，设置宽度和高度
                if ((i + 1) % 3 == 0) { // 每6个单元格换行，因为900/150 = 6
                    cardTable.row();
                }
            }
        }
        // 强制表格布局计算
        cardTable.pack(); // 重新计算表格的大小
        cardTable.layout();
        // 强制滚动面板重新计算滚动区域
        scrollPane.layout();

//        scrollPane.setScrollPercentY(0); // 滚动到顶部

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
        for (Actor actor : cardTable.getChildren()) {
            // 移除所有 InputListener
            actor.clearListeners();
        }
    }

    @Override
    public void resize(int width, int height) {

        scrollPane.setWidth(width);
        scrollPane.setHeight(height);
        scrollPane.layout();
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
