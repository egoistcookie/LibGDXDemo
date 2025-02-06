package com.lf.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

// 箭矢类，用于管理箭矢的逻辑和渲染
public class Arrow {
    private Sprite sprite; // 箭矢的精灵，用于渲染
    private Vector2 position; // 箭矢的位置
    private Vector2 velocity; // 箭矢的速度

    public Arrow(Texture texture, float x, float y) {
        this.position = new Vector2(x, y); // 初始化箭矢的位置
        this.sprite = new Sprite(texture); // 创建箭矢的精灵
        this.sprite.setPosition(x, y); // 设置精灵的位置
        this.velocity = new Vector2(100, 0); // 初始化箭矢的速度
    }

    // 更新箭矢的位置
    public void update() {
        position.add(velocity.cpy().scl(Gdx.graphics.getDeltaTime())); // 根据速度和时间更新位置
        sprite.setPosition(position.x, position.y); // 更新精灵的位置
    }

    // 渲染箭矢
    public void render(SpriteBatch batch) {
        sprite.draw(batch); // 绘制箭矢
    }

    // 判断箭矢是否超出边界
    public boolean isOutOfBounds() {
        return position.x > 800; // 如果箭矢的 x 坐标大于 800，则认为超出边界
    }

    // 释放箭矢的纹理资源
    public void dispose() {
        sprite.getTexture().dispose(); // 释放纹理资源
    }
}