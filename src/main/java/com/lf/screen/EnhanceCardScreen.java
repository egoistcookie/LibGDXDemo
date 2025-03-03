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
import com.badlogic.gdx.utils.Timer;
import com.kotcrab.vis.ui.VisUI;
import com.lf.config.CardTypeConfig;
import com.lf.core.MyDefenseGame;
import com.lf.manager.EnemyLoadManager;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

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
        Label cardLevelLabel = new Label("卡片等级:" + cardTypeConfig.getCardLevel(), labelStyle);
        Label powerLabel = new Label("攻击力:" + cardTypeConfig.getAttackPower(), labelStyle);
        Label fireRateLabel = new Label("攻击频率:" + cardTypeConfig.getFireRate(), labelStyle);
        Label attackRangeLabel = new Label("攻击范围:" + cardTypeConfig.getAttackRange(), labelStyle);
        Label maxAttackCount = new Label("最大攻击数:" + cardTypeConfig.getMaxAttackCount(), labelStyle);
        Label killCountLabel = new Label("杀敌数:" + cardTypeConfig.getKillCount(), labelStyle);

        Table attruibuteTable = new Table();
        attruibuteTable.add(cardTypeLabel);
        attruibuteTable.row();
        attruibuteTable.add(rarityLabel).padTop(10);
        attruibuteTable.row();
        attruibuteTable.add(cardLevelLabel).padTop(10);
        attruibuteTable.row();
        attruibuteTable.add(powerLabel).padTop(10);;
        attruibuteTable.row();
        attruibuteTable.add(fireRateLabel).padTop(10);;
        attruibuteTable.row();
        attruibuteTable.add(attackRangeLabel).padTop(10);;
        attruibuteTable.row();
        attruibuteTable.add(maxAttackCount).padTop(10);;
        attruibuteTable.row();
        attruibuteTable.add(killCountLabel).padTop(10);;
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

        // 判断杀敌数和等级是否匹配，每杀敌100可升一级
        int killCount = cardTypeConfig.getKillCount();
        if(killCount/100 > cardTypeConfig.getCardLevel()-1){
            System.out.println("可升级");
        }else{
            showAlertInfo("不可升级 练练再来",0,0);
            System.out.println("不可升级，练练再来");
            return;
        }
        // 更新card_type_config.yml
        try {
            // 读取 YAML 文件
            Yaml yaml = new Yaml();
            String filePath = System.getProperty("user.dir") + "/src/main/resources/card_type_config.yml"; // 写入到用户目录
            FileReader reader = new FileReader(filePath);
            // 读取配置文件内容
            Map<String, List<Map<String, Object>>> data = yaml.load(reader);
            reader.close();
            // 获取 cardTypeConfigs 列表
            List<Map<String, Object>> cardTypeConfigsRead = data.get("cardTypeConfigs");
            // 遍历列表，找到目标 cardType
            for (Map<String, Object> config : cardTypeConfigsRead) {
                if (cardTypeConfig.getCardType().equals(config.get("cardType"))) {
                    // 生成随机数，随机提升攻击力、攻击范围、攻击速度、最大攻击数，概率分别为60%/20%/15%/5%
                    int randomNumber = ThreadLocalRandom.current().nextInt(1, 101);
                    System.out.println("生成的提升随机数是: " + randomNumber);
                    if(randomNumber >= 95){
                        // 更新 最大攻击数
                        int maxAttackCount = (int) config.get("maxAttackCount");
                        config.put("maxAttackCount", maxAttackCount + 1);
                        // 当前页面的值需要同步更新
                        cardTypeConfig.setMaxAttackCount(maxAttackCount + 1);
                    }else if(randomNumber >= 80){
                        // 更新 攻击速度
                        float fireRate = Float.parseFloat(config.get("fireRate")+"");
                        config.put("fireRate", fireRate*0.9f);
                        // 当前页面的值需要同步更新
                        cardTypeConfig.setFireRate(fireRate*0.9f);
                    }else if(randomNumber >= 60){
                        // 更新 攻击范围
                        float attackRange = Float.parseFloat(config.get("attackRange")+"");
                        config.put("attackRange", attackRange + 10);
                        // 当前页面的值需要同步更新
                        cardTypeConfig.setAttackRange(attackRange + 10);
                    }else{
                        // 更新 攻击力
                        int attackPower = (int) config.get("attackPower");
                        config.put("attackPower", attackPower + 1);
                        // 当前页面的值需要同步更新
                        cardTypeConfig.setAttackPower(attackPower + 1);
                    }
                    // 更新 卡片等级
                    int cardLevel = (int) config.get("cardLevel");
                    config.put("cardLevel", cardLevel + 1);
                    cardTypeConfig.setCardLevel(cardLevel + 1);
                    System.out.println("Updated " + cardTypeConfig.getCardType() + "'s cardLevel to " + (cardLevel + 1));
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
        stage.addActor(dialogTable);
        // 使用Timer在1秒后移除提示窗口
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                dialogTable.remove();
            }
        }, 1f);

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

    public static void main(String[] args) {

        // 创建第一个链表表示数字 342
        LinkedList<Integer> l1 = new LinkedList<>();
        l1.add(2);
        l1.add(4);
        l1.add(3);

        // 创建第二个链表表示数字 4465
        LinkedList<Integer> l2 = new LinkedList<>();
        l2.add(5);
        l2.add(6);
        l2.add(4);
        l2.add(4);

        LinkedList<Integer> result = addTwoNumbers(l1, l2);

        // 打印结果链表
        for (int num : result) {
            System.out.print(num + " ");
        }

    }

    /**
     * 两数之和
     * @param l1 非空链表1，表示非负整数，每位数字按照逆序存储
     * @param l2 非空链表2，表示非负整数，每位数字按照逆序存储
     * @return 表示和的链表
     */
    public static LinkedList<Integer> addTwoNumbers(LinkedList<Integer> l1, LinkedList<Integer> l2) {
        LinkedList<Integer> result = new LinkedList<>();
        int carry = 0;
//        int i = 0;
//        // 同时遍历两个链表
//        while (i < l1.size() || i < l2.size()) {
//            int num1 = (i < l1.size()) ? l1.get(i) : 0;
//            int num2 = (i < l2.size()) ? l2.get(i) : 0;
//            int sum = num1 + num2 + carry;
//            carry = sum / 10;
//            result.add(sum % 10);
//            i++;
//        }

        for (int i=0; i<l1.size() || i<l2.size() ;i++){
            int num1 = (i < l1.size()) ? l1.get(i) : 0;
            int num2 = (i < l2.size()) ? l2.get(i) : 0;
            int sum = num1+num2+carry;
            carry = sum / 10;
            result.add(sum%10);
        }

        // 处理最后可能的进位
        if (carry > 0) {
            result.add(carry);
        }

        return result;
    }


}
