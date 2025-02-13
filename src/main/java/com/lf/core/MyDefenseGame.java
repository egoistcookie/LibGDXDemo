package com.lf.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.lf.screen.MainMenuScreen;

/**
 * 游戏
 */
public class MyDefenseGame extends Game {
    // 资源管理工具
    private AssetManager assetManager;
    @Override
    public void create() {

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
        // 加载箭矢的背景图片
        assetManager.load("arrow1.png", Texture.class);
        // 加载防御塔1的背景图片
        assetManager.load("tower1.png", Texture.class);
        // 加载箭矢的背景图片
        assetManager.load("tower1.png", Texture.class);
        // 加载地图的背景图片
        assetManager.load("map/map3.png", Texture.class);
        // 加载敌人的背景图片
        assetManager.load("enemy11.png", Texture.class);
        // 加载敌人的背景图片
        assetManager.load("enemy12.png", Texture.class);
        // 等待字体加载完成
        assetManager.finishLoading();
        // 设置当前屏幕为 MyScreen
        setScreen(new MainMenuScreen(this,assetManager));
    }
}
