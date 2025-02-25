package com.lf.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.VisUI;
import com.lf.config.CardTypeConfig;
import com.lf.core.MyDefenseGame;
import com.lf.manager.EnemyLoadManager;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;
import java.util.List;

public class EnhanceCardScreen implements Screen {

    // 游戏对象，用于切换屏幕
    private MyDefenseGame game;
    // 资源管理工具
    private final AssetManager assetManager;
    // 敌人加载管理工具
    private final EnemyLoadManager enemyLoadManager;
    // 舞台对象，用于管理和渲染界面元素
    private Stage stage;
    // 皮肤对象，用于管理界面元素的样式
    private final Skin skin;
    // 卡片配置对象
    private CardTypeConfig cardTypeConfig;

    private Table cardTable;
    // 自定义字体-包含中文
    private BitmapFont customFont;
    // 蓝色字体标签style
    private Label.LabelStyle labelStyle;

    public EnhanceCardScreen(MyDefenseGame game, CardTypeConfig cardTypeConfig) {
        this.game = game;
        System.out.println(game);
        this.assetManager = game.getAssetManager();
        this.enemyLoadManager = game.getEnemyLoadManager();
        // 获取VisUI库的默认皮肤
        this.skin = VisUI.getSkin();
        this.cardTypeConfig = cardTypeConfig;

        // 加载自定义字体
        customFont = this.assetManager.get("fonts/whiteYouYuan.fnt", BitmapFont.class);
        // 字体大小倍率（以Hiero中生成的字体大小为基准）
        customFont.getData().setScale(0.6f);
        // 蓝色字体样式
        labelStyle = new Label.LabelStyle(customFont, Color.WHITE);

    }

    @Override
    public void show() {
        stage = new Stage();

        // 创建表格布局，用于组织界面元素
        cardTable = new Table();
        // 设置表格填充整个父容器
        cardTable.setFillParent(true);

        stage.addActor(cardTable);

    }

    @Override
    public void render(float delta) {

        // 清除屏幕
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cardTable.clear();
        // 先展示一张图片
        Texture imageTextures = assetManager.get("tower/"+cardTypeConfig.getCardType() +".png", Texture.class);
        Image image = new Image(imageTextures);
        image.setTouchable(Touchable.enabled);
        cardTable.add(image).minWidth(500).width(500).minHeight(900).height(900); // 添加单元格，设置宽度和高度
        // 再用一张子表格展示属性
        // 生命值
        Table attruibuteTable = getAttruibuteTable();

        cardTable.add(attruibuteTable).minWidth(500).width(500).minHeight(50).height(50);
        // 重新计算表格布局
        cardTable.layout();

        // 更新舞台上的元素状态
        stage.act(delta);
        // 绘制舞台上的元素
        stage.draw();

        // 确保舞台可以处理输入事件
        Gdx.input.setInputProcessor(stage);
        // 处理用户输入
        handleInput();


    }

    // 获取属性表格
    private Table getAttruibuteTable() {

        // 攻击力
        Label cardTypeLabel = new Label("名称:" + cardTypeConfig.getCardType(), labelStyle);
        Label rarityLabel = new Label("稀有度:" + cardTypeConfig.getRarity(), labelStyle);
        Label powerLabel = new Label("攻击力:" + cardTypeConfig.getAttackPower(), labelStyle);
        Label fireRateLabel = new Label("攻击频率:" + cardTypeConfig.getFireRate(), labelStyle);
        Label attackRangeLabel = new Label("攻击范围:" + cardTypeConfig.getAttackRange(), labelStyle);
        Label maxAttackCount = new Label("最大攻击数:" + cardTypeConfig.getMaxAttackCount(), labelStyle);

        Table attruibuteTable = new Table();
        attruibuteTable.add(cardTypeLabel);
        attruibuteTable.row();
        attruibuteTable.row();
        attruibuteTable.row();
        attruibuteTable.row();

        attruibuteTable.add(rarityLabel);
        attruibuteTable.row();
        attruibuteTable.add(powerLabel);
        attruibuteTable.row();
        attruibuteTable.add(fireRateLabel);
        attruibuteTable.row();
        attruibuteTable.add(attackRangeLabel);
        attruibuteTable.row();
        attruibuteTable.add(maxAttackCount);
        attruibuteTable.row();
        // 增加按钮
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
        // 创建“强化卡片”按钮
        TextButton startButton = new TextButton("强化卡片", buttonStyle);
        // 为按钮添加点击事件监听器
        ClickListener startButtonClickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 点击按钮后切换到游戏界面
                enhanceCard();
            }
        };
        startButton.addListener(startButtonClickListener);
        attruibuteTable.add(startButton).top().padTop(10);
        attruibuteTable.row();


        // 创建“返回”按钮
        TextButton returnButton = new TextButton("返 回", buttonStyle);
        // 为按钮添加点击事件监听器
        ClickListener returnButtonClickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 点击按钮后切换到游戏界面
                retrunCardBrow();
            }
        };
        returnButton.addListener(returnButtonClickListener);
        attruibuteTable.add(returnButton).top().padTop(10);

        attruibuteTable.row();

        return attruibuteTable;
    }

    // 返回选卡界面
    private void retrunCardBrow() {
        // 处理点击事件，例如切换到GameScreen
        removeAllListener();
        // 点击按钮后切换到选卡界面
        game.setScreen(new BrowCardScreen(game));
    }

    // 强化卡片
    private void enhanceCard() {

        // 更新card_type_config.yml
        try {
            // 读取 YAML 文件
            Yaml yaml = new Yaml();
            // getResourceAsStream 有缓存
//            InputStream inputStream = this.getClass()
//                    .getClassLoader()
//                    .getResourceAsStream("card_type_config.yml");
            String filePath = System.getProperty("user.dir") + "/src/main/resources/card_type_config.yml"; // 写入到用户目录
            FileReader reader = new FileReader(filePath);
            // 读取配置文件内容
            Map<String, List<Map<String, Object>>> data = yaml.load(reader);
            reader.close();
            // 获取 cardTypeConfigs 列表
            List<Map<String, Object>> cardTypeConfigsRead = (List<Map<String, Object>>) data.get("cardTypeConfigs");
            // 遍历列表，找到目标 cardType
            for (Map<String, Object> config : cardTypeConfigsRead) {
                if (cardTypeConfig.getCardType().equals(config.get("cardType"))) {
                    // 更新 attackPower
                    int attackPower = (int) config.get("attackPower");
                    config.put("attackPower", attackPower + 1); // attackPower + 1
                    // 当前页面的值需要同步更新
                    cardTypeConfig.setAttackPower(attackPower + 1);
                    System.out.println("Updated " + cardTypeConfig.getCardType() + "'s attackPower to " + (attackPower + 1));
                    break;
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
        // 刷新配置
        game.getEnemyLoadManager().loadCardTypeConfig();
        // 重载一次当前页面，更新配置
//        game.setScreen(new EnhanceCardScreen(game,cardTypeConfig));

    }

    // handleInput方法用于处理用户的输入事件
    private void handleInput() {
        // 检查ESC是否刚刚被按下
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // 移除所有监听
            removeAllListener();
            // 切回选择卡片界面
            game.setScreen(new BrowCardScreen(game));
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
