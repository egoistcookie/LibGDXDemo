package com.lf.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.kotcrab.vis.ui.VisUI;
import com.lf.manager.EnemyLoadManager;
import com.lf.screen.MainMenuScreen;

/**
 * 游戏
 */
public class MyDefenseGame extends Game {

    // 资源管理工具
    private AssetManager assetManager;
    // 敌人加载管理器
    public static EnemyLoadManager enemyLoadManager;
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
//        FreetypeFontLoader.FreeTypeFontLoaderParameter fontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
//        fontParameter.fontFileName = "fonts/xinsongti.fnt"; // 字体文件路径下，需要有fnt和png两文件，都是通过Hiero工具生成
//        fontParameter.fontParameters.size = 12; // 字体大小
        assetManager.load("fonts/xinsongti.fnt", BitmapFont.class);
        assetManager.load("fonts/whiteYouYuan.fnt", BitmapFont.class);
        // 加载提示框的背景图片
        assetManager.load("alertTitle.png", Texture.class);
        // 加载金币的背景图片
        assetManager.load("gold.png", Texture.class);
        // 加载血量的背景图片
        assetManager.load("health.png", Texture.class);
        // 加载箭矢的背景图片
        assetManager.load("arrow-old.png", Texture.class);
        // 加载防御塔1的背景图片
        assetManager.load("tower1.png", Texture.class);
        // 加载防御塔2的背景图片
        assetManager.load("tower2.png", Texture.class);

        // 加载弓箭手的背景图片
        assetManager.load("tower/arrower.png", Texture.class);
        assetManager.load("tower/arrowerStuff.png", Texture.class);
        assetManager.load("tower/arrower1.png", Texture.class);
        assetManager.load("tower/arrower2.png", Texture.class);
        assetManager.load("tower/arrowerOneStar1.png", Texture.class);
        assetManager.load("tower/arrowerOneStar2.png", Texture.class);
        assetManager.load("tower/arrowerOneStar3.png", Texture.class);
        assetManager.load("tower/arrow1.png", Texture.class);
        assetManager.load("tower/arrow2.png", Texture.class);
        assetManager.load("tower/arrow3.png", Texture.class);

        // 加载阴阳师的背景图片
        assetManager.load("tower/yys.png", Texture.class);
        assetManager.load("tower/yysStuff.png", Texture.class);
        assetManager.load("tower/yys1.png", Texture.class);
        assetManager.load("tower/yys2.png", Texture.class);
        assetManager.load("tower/yysOneStar1.png", Texture.class);
        assetManager.load("tower/yysOneStar2.png", Texture.class);
        assetManager.load("tower/yysOneStar3.png", Texture.class);
        assetManager.load("tower/fan1.png", Texture.class);
        assetManager.load("tower/fan2.png", Texture.class);
        assetManager.load("tower/fan3.png", Texture.class);
        // 加载青龙-玄武-朱雀-白虎的背景图片
        assetManager.load("tower/azureDragon.png", Texture.class);
        assetManager.load("tower/azureDragonStuff.png", Texture.class);
        assetManager.load("tower/azureDragon1.png", Texture.class);
        assetManager.load("tower/azureDragon2.png", Texture.class);
        assetManager.load("tower/azureDragonOneStar1.png", Texture.class);
        assetManager.load("tower/azureDragonOneStar2.png", Texture.class);
        assetManager.load("tower/azureDragonOneStar3.png", Texture.class);
        assetManager.load("tower/dragonAttack1.png", Texture.class);
        assetManager.load("tower/dragonAttack2.png", Texture.class);
        assetManager.load("tower/dragonAttack3.png", Texture.class);
        assetManager.load("tower/blackTortoise.png", Texture.class);
        assetManager.load("tower/blackTortoiseStuff.png", Texture.class);
        assetManager.load("tower/blackTortoise1.png", Texture.class);
        assetManager.load("tower/blackTortoise2.png", Texture.class);
        assetManager.load("tower/blackTortoiseOneStar1.png", Texture.class);
        assetManager.load("tower/blackTortoiseOneStar2.png", Texture.class);
        assetManager.load("tower/blackTortoiseOneStar3.png", Texture.class);
        assetManager.load("tower/blackTortoiseAttack1.png", Texture.class);
        assetManager.load("tower/blackTortoiseAttack2.png", Texture.class);
        assetManager.load("tower/blackTortoiseAttack3.png", Texture.class);
        assetManager.load("tower/vermilion.png", Texture.class);
        assetManager.load("tower/vermilionStuff.png", Texture.class);
        assetManager.load("tower/vermilion1.png", Texture.class);
        assetManager.load("tower/vermilion2.png", Texture.class);
        assetManager.load("tower/vermilionOneStar1.png", Texture.class);
        assetManager.load("tower/vermilionOneStar2.png", Texture.class);
        assetManager.load("tower/vermilionOneStar3.png", Texture.class);
        assetManager.load("tower/vermilionAttack1.png", Texture.class);
        assetManager.load("tower/vermilionAttack2.png", Texture.class);
        assetManager.load("tower/vermilionAttack3.png", Texture.class);
        assetManager.load("tower/whiteTiger.png", Texture.class);
        assetManager.load("tower/whiteTigerStuff.png", Texture.class);
        assetManager.load("tower/whiteTiger1.png", Texture.class);
        assetManager.load("tower/whiteTiger2.png", Texture.class);
        assetManager.load("tower/whiteTigerOneStar1.png", Texture.class);
        assetManager.load("tower/whiteTigerOneStar2.png", Texture.class);
        assetManager.load("tower/whiteTigerOneStar3.png", Texture.class);
        // 加载招财童子的背景图片
        assetManager.load("tower/prosperityGirl.png", Texture.class);
        assetManager.load("tower/prosperityGirlStuff.png", Texture.class);
        assetManager.load("tower/prosperityGirl1.png", Texture.class);
        assetManager.load("tower/prosperityGirl2.png", Texture.class);
        assetManager.load("tower/prosperityGirlOneStar1.png", Texture.class);
        assetManager.load("tower/prosperityGirlOneStar2.png", Texture.class);
        assetManager.load("tower/prosperityGirlOneStar3.png", Texture.class);

        // 加载剑士的背景图片
        assetManager.load("tower/saber.png", Texture.class);
        assetManager.load("tower/saberStuff.png", Texture.class);
        assetManager.load("tower/saber1.png", Texture.class);
        assetManager.load("tower/saber2.png", Texture.class);
        assetManager.load("tower/saberOneStar1.png", Texture.class);
        assetManager.load("tower/saberOneStar2.png", Texture.class);
        assetManager.load("tower/saberOneStar3.png", Texture.class);
        assetManager.load("tower/whiteAttack1.png", Texture.class);
        assetManager.load("tower/whiteAttack2.png", Texture.class);
        assetManager.load("tower/whiteAttack3.png", Texture.class);

        // 加载剑仙的背景图片
        assetManager.load("tower/swordSaint.png", Texture.class);
        assetManager.load("tower/swordSaintStuff.png", Texture.class);
        assetManager.load("tower/swordSaint1.png", Texture.class);
        assetManager.load("tower/swordSaint2.png", Texture.class);
        assetManager.load("tower/swordSaintOneStar1.png", Texture.class);
        assetManager.load("tower/swordSaintOneStar2.png", Texture.class);
        assetManager.load("tower/swordSaintOneStar3.png", Texture.class);
        assetManager.load("tower/saintSword.png", Texture.class);
        assetManager.load("tower/saintSword1.png", Texture.class);
        assetManager.load("tower/saintSword2.png", Texture.class);
        assetManager.load("tower/saintSword3.png", Texture.class);

        // 加载死灵法师的背景图片
        assetManager.load("tower/necromancer.png", Texture.class);
        assetManager.load("tower/necromancerStuff.png", Texture.class);
        assetManager.load("tower/necromancer1.png", Texture.class);
        assetManager.load("tower/necromancer2.png", Texture.class);
        assetManager.load("tower/necromancerOneStar1.png", Texture.class);
        assetManager.load("tower/necromancerOneStar2.png", Texture.class);
        assetManager.load("tower/necromancerOneStar3.png", Texture.class);
        assetManager.load("tower/necromancerAttack1.png", Texture.class);
        assetManager.load("tower/necromancerAttack2.png", Texture.class);
        assetManager.load("tower/necromancerAttack3.png", Texture.class);
        // 亡灵战士贴图
        assetManager.load("enemy/ghostWarrior1.png", Texture.class);
        assetManager.load("enemy/ghostWarrior2.png", Texture.class);

        // 加载buff图标
        assetManager.load("buff/swifterArrow.png", Texture.class);

        // 加载一级箭塔的背景图片
        assetManager.load("arrowTower.png", Texture.class);
        // 加载地图的背景图片
        assetManager.load("map/map4.png", Texture.class);
        // 加载敌人贴图
        assetManager.load("enemy/saberOne1.png", Texture.class);
        assetManager.load("enemy/saberOne2.png", Texture.class);
        assetManager.load("enemy/minion1.png", Texture.class);
        assetManager.load("enemy/minion2.png", Texture.class);
        assetManager.load("enemy/ironcladGeneral1.png", Texture.class);
        assetManager.load("enemy/ironcladGeneral2.png", Texture.class);
        assetManager.load("enemy/archerOne1.png", Texture.class);
        assetManager.load("enemy/archerOne2.png", Texture.class);
        assetManager.load("enemy/foxSpirit1.png", Texture.class);
        assetManager.load("enemy/foxSpirit2.png", Texture.class);
        assetManager.load("enemy/paperPuppet1.png", Texture.class);
        assetManager.load("enemy/paperPuppet2.png", Texture.class);
        assetManager.load("enemy/stoneGuardian1.png", Texture.class);
        assetManager.load("enemy/stoneGuardian2.png", Texture.class);
        assetManager.load("enemy/Qilin1.png", Texture.class);
        assetManager.load("enemy/Qilin2.png", Texture.class);
        assetManager.load("enemy/Taotie1.png", Texture.class);
        assetManager.load("enemy/Taotie2.png", Texture.class);
        assetManager.load("enemy/thunderRoc1.png", Texture.class);
        assetManager.load("enemy/thunderRoc2.png", Texture.class);
        // 加载敌人的倒地背景图片
        assetManager.load("enemy/deathing1.png", Texture.class);
        // 加载敌人的倒地背景图片
        assetManager.load("enemy/deathing2.png", Texture.class);
        // 加载透明背景
        assetManager.load("transBack.png", Texture.class);
        // 加载纯白背景
        assetManager.load("white.png", Texture.class);
        // 加载纯黑背景
        assetManager.load("black.png", Texture.class);
        // 加载回收图标的背景图片
        assetManager.load("rollback.png", Texture.class);
        // 加载星的背景图片
        assetManager.load("Star1.png", Texture.class);
        assetManager.load("Star2.png", Texture.class);
        assetManager.load("Star3.png", Texture.class);
        // 加载回收图标的背景图片
        assetManager.load("super.png", Texture.class);
//        assetManager.load("default-scroll.png", Texture.class);
//        assetManager.load("default-scroll-knob.png", Texture.class);
        // 加载回收图标的背景图片
        assetManager.load("super.png", Texture.class);
        // 升级音效
        assetManager.load("wav/levelUp.mp3", Sound.class);
        // 升星音效
        assetManager.load("wav/starUp.mp3", Sound.class);
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
