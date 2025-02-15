package com.lf.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.kotcrab.vis.ui.VisUI;
import com.lf.manager.EnemyLoadManager;
import com.lf.screen.GameScreen;
import com.lf.screen.MainMenuScreen;
import com.lf.ui.GameUI;

/**
 * 游戏
 */
public class MyDefenseGame extends Game {

    // 资源管理工具
    private AssetManager assetManager;
    // 敌人加载管理器
    private EnemyLoadManager enemyLoadManager;
    // 用于控制是否继续渲染的标志变量
    private boolean isGameOver = false;
    @Override
    public void create() {
        // 加载 VisUI 库
        VisUI.load();
        // 资源管理工具的初始化
        assetManager = new AssetManager();
        // 设置 FreeTypeFontGenerator 的加载器
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        // 设置 BitmapFont 的加载器
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        // 加载自定义字体
        FreetypeFontLoader.FreeTypeFontLoaderParameter fontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        fontParameter.fontFileName = "fonts/xinsongti.fnt"; // 字体文件路径下，需要有fnt和png两文件，都是通过Hiero工具生成
//        fontParameter.fontParameters.size = 12; // 字体大小
        assetManager.load("fonts/xinsongti.fnt", BitmapFont.class);
        // 加载提示框的背景图片
        assetManager.load("alertTitle.png", Texture.class);
        // 加载金币的背景图片
        assetManager.load("gold.png", Texture.class);
        // 加载血量的背景图片
        assetManager.load("health.png", Texture.class);
        // 加载箭矢的背景图片
        assetManager.load("arrow2.png", Texture.class);
        // 加载箭矢的背景图片
        assetManager.load("arrow3.png", Texture.class);
        // 加载防御塔1的背景图片
        assetManager.load("tower1.png", Texture.class);
        // 加载防御塔2的背景图片
        assetManager.load("tower2.png", Texture.class);
        // 加载一级箭塔的背景图片
        assetManager.load("arrowTower.png", Texture.class);
        // 加载地图的背景图片
        assetManager.load("map/map3.png", Texture.class);
        // 加载敌人的背景图片
        assetManager.load("enemy11.png", Texture.class);
        // 加载敌人的背景图片
        assetManager.load("enemy12.png", Texture.class);
        // 加载纯白背景
        assetManager.load("white.png", Texture.class);
        // 加载纯黑背景
        assetManager.load("black.png", Texture.class);
        // 等待字体加载完成
        assetManager.finishLoading();

        // 敌人管理工具的初始化
        enemyLoadManager = new EnemyLoadManager();

        // 设置当前屏幕为 MyScreen
        setScreen(new MainMenuScreen(this));
    }

    public void render() {
        if (this.screen != null) {
//            if(this.screen instanceof GameScreen){
//                GameUI gameUi = ((GameScreen) this.screen).getGameUI();
//                if(gameUi.getHealth() > 0){
//                    //判断游戏是否已经结束，已经结束则停止渲染
//                    this.screen.render(Gdx.graphics.getDeltaTime());
//                }else{
//                    gameUi.dispose();
//                }
//            }else{
                this.screen.render(Gdx.graphics.getDeltaTime());
//            }
        }
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public EnemyLoadManager getEnemyLoadManager() {
        return enemyLoadManager;
    }
}
