package com.lf.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.lf.entities.Enemy;
import com.lf.ui.GameUI;

public class TowerDefenseGame extends ApplicationAdapter {
    private OrthographicCamera camera; // 相机
    private World world; // 物理世界
    private Box2DDebugRenderer debugRenderer; // 调试渲染器
    private GameUI gameUI; // 游戏 UI
    private SpriteBatch batch; // 精灵批处理，用于渲染精灵
    private Enemy enemy; // 敌人对象
    private Texture enemyTexture; // 敌人的纹理

    @Override
    public void create() {
        // 初始化相机
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        // 初始化物理世界
        world = new World(new Vector2(0, 0), false);

        // 初始化调试渲染器
        debugRenderer = new Box2DDebugRenderer();

        // 初始化游戏 UI
        gameUI = new GameUI();

        // 初始化精灵批处理
        batch = new SpriteBatch();

        // 加载敌人的纹理
        enemyTexture = new Texture(Gdx.files.internal("enemy.png"));

        // 创建敌人对象
        enemy = new Enemy(world, 100, 100, enemyTexture);

        // 设置敌人的移动速度
        enemy.move(new Vector2(1, 0));
    }

    @Override
    public void render() {
        // 清屏
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 更新物理世界
        world.step(1 / 60f, 6, 2);

        // 更新相机
        camera.update();

        // 更新敌人的位置和旋转角度
        enemy.update();

        // 开始精灵批处理
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // 渲染敌人
        enemy.getSprite().draw(batch);

        // 结束精灵批处理
        batch.end();

        // 渲染调试信息
        debugRenderer.render(world, camera.combined);

        // 渲染游戏 UI
        gameUI.render();
    }

    @Override
    public void resize(int width, int height) {
        // 更新相机的视口大小
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();

        // 更新游戏 UI 的视口大小
        gameUI.resize(width, height);
    }

    @Override
    public void dispose() {
        // 释放资源
        world.dispose();
        debugRenderer.dispose();
        gameUI.dispose();
        batch.dispose();
        enemyTexture.dispose();
    }
}