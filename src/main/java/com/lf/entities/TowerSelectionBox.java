package com.lf.entities;
// 引入LibGDX的相关类，用于游戏开发中的各种功能
import com.badlogic.gdx.Gdx;
// 引入Texture类，用于处理游戏中的纹理（图片）
import com.badlogic.gdx.graphics.Texture;
// 引入SpriteBatch类，用于高效地批量绘制精灵（纹理）
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
// 引入Rectangle类，用于表示矩形区域，可用于碰撞检测等
import com.badlogic.gdx.math.Rectangle;
// 引入Vector2类，用于表示二维向量，可用于表示位置、速度等
import com.badlogic.gdx.math.Vector2;
// 引入Array类，用于存储和管理对象数组
import com.badlogic.gdx.utils.Array;

// 定义一个名为TowerSelectionBox的公共类，用于处理防御塔选择框的逻辑
public class TowerSelectionBox {
    // 存储防御塔纹理的数组
    private Array<Texture> towerTextures;
    // 存储每个防御塔纹理对应的矩形区域的数组，用于检测点击事件
    private Array<Rectangle> textureRectangles;
    // 表示选择框是否可见的布尔值
    private boolean isVisible;
    // 选择框的位置，使用Vector2表示二维坐标
    private Vector2 position;
    // 记录当前选中的防御塔纹理的索引，-1表示未选中
    private int selectedIndex;

    // 构造函数，用于初始化TowerSelectionBox对象
    public TowerSelectionBox() {
        // 初始化存储防御塔纹理的数组
        towerTextures = new Array<>();
        // 初始化存储纹理矩形区域的数组
        textureRectangles = new Array<>();
        // 初始时选择框不可见
        isVisible = false;
        // 初始化选择框的位置
        position = new Vector2();
        // 初始时未选中任何防御塔纹理
        selectedIndex = -1;

        // 调用加载防御塔纹理的方法
        loadTowerTextures();
    }

    // 私有方法，用于加载防御塔的纹理
    private void loadTowerTextures() {
        // 这里添加你的防御塔图片路径，将防御塔图片加载为纹理并添加到数组中
        towerTextures.add(new Texture(Gdx.files.internal("tower1.png")));
        towerTextures.add(new Texture(Gdx.files.internal("tower2.png")));

        // 初始化每个纹理对应的矩形区域
        for (int i = 0; i < towerTextures.size; i++) {
            // 获取当前索引的纹理
            Texture texture = towerTextures.get(i);
            // 创建一个矩形区域，根据纹理的宽度和位置计算矩形的位置和大小
            textureRectangles.add(new Rectangle(position.x + i * (texture.getWidth() + 10), position.y, texture.getWidth(), texture.getHeight()));
        }
    }

    // 公共方法，用于显示选择框
    public void show(Vector2 clickPosition) {
        // 设置选择框的位置为点击位置
        position.set(clickPosition);
        // 将选择框设置为可见
        isVisible = true;
        // 更新纹理矩形区域的位置
        updateTextureRectangles();
    }

    // 私有方法，用于更新纹理矩形区域的位置
    private void updateTextureRectangles() {
        for (int i = 0; i < textureRectangles.size; i++) {
            // 获取当前索引的矩形区域
            Rectangle rect = textureRectangles.get(i);
            // 根据纹理的宽度和选择框的位置更新矩形的位置
            rect.setPosition(position.x + i * (towerTextures.get(i).getWidth() + 10), position.y);
        }
    }

    // 公共方法，用于隐藏选择框
    public void hide() {
        // 将选择框设置为不可见
        isVisible = false;
    }

    // 公共方法，用于检查选择框是否可见
    public boolean isVisible() {
        return isVisible;
    }

    // 公共方法，用于渲染选择框中的防御塔纹理
    public void render(SpriteBatch batch) {
        // 只有当选择框可见时才进行渲染
        if (isVisible) {
            for (int i = 0; i < towerTextures.size; i++) {
                // 获取当前索引的纹理
                Texture texture = towerTextures.get(i);
                // 使用SpriteBatch绘制纹理到对应的矩形区域
                batch.draw(texture, textureRectangles.get(i).x, textureRectangles.get(i).y);
            }
        }
    }

    // 公共方法，用于处理用户的输入（触摸事件）
    public void handleInput(Vector2 touchPoint) {
        // 只有当选择框可见时才处理输入
        if (isVisible) {
            for (int i = 0; i < textureRectangles.size; i++) {
                // 获取当前索引的矩形区域
                Rectangle rect = textureRectangles.get(i);
                // 检查触摸点是否在矩形区域内
                if (rect.contains(touchPoint)) {
                    // 记录选中的纹理索引
                    selectedIndex = i;
                    // 隐藏选择框
                    hide();
                    // 退出循环，因为已经找到选中的纹理
                    break;
                }
            }
        }
    }

    // 公共方法，用于获取当前选中的防御塔纹理的索引
    public int getSelectedIndex() {
        return selectedIndex;
    }

    public Array<Texture> getTowerTextures() {
        return towerTextures;
    }

    public void resetSelectedIndex() {
        selectedIndex = -1;
    }

    public void dispose() {
        for (Texture texture : towerTextures) {
            texture.dispose();
        }
    }
}