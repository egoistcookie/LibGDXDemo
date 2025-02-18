package com.lf.util;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.math.Rectangle;
//import com.badlogic.gdx.graphics.PixmapTextureData;


public class SpriteUtils {

    /**
     * 获取Sprite的非透明部分的边界框
     * @param sprite 要处理的Sprite对象
     * @return 非透明部分的边界框
     */
    public static Rectangle getNonTransparentBounds(Sprite sprite) {
        if (sprite == null) {
            return null;
        }

        Texture texture = sprite.getTexture();
        if (texture == null) {
            return null;
        }

        // 检查 TextureData 是否支持返回 Pixmap
        if (!(texture.getTextureData() instanceof PixmapTextureData)) {
            System.err.println("This TextureData implementation does not return a Pixmap");
            return null;
        }

        // 如果纹理没有绑定Pixmap，需要创建一个
        if (!texture.getTextureData().isPrepared()) {
            texture.getTextureData().prepare();
        }
        Pixmap pixmap = texture.getTextureData().consumePixmap();

        int textureWidth = pixmap.getWidth();
        int textureHeight = pixmap.getHeight();

        // 初始化最小和最大坐标
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        // 遍历Pixmap像素数据
        for (int y = 0; y < textureHeight; y++) {
            for (int x = 0; x < textureWidth; x++) {
                // 获取当前像素的颜色
                int pixel = pixmap.getPixel(x, y);
                // 提取像素的alpha通道值
                int alpha = (pixel >> 24) & 0xff;

                // 如果像素不是完全透明的
                if (alpha > 0) {
                    // 更新最小和最大坐标
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        // 释放Pixmap资源
//        pixmap.dispose();

        // 如果没有找到非透明像素，返回null
        if (minX == Integer.MAX_VALUE || minY == Integer.MAX_VALUE) {
            return null;
        }

        // 创建并返回非透明部分的边界框
        Rectangle bounds = new Rectangle();
        bounds.x = sprite.getX() + minX * sprite.getScaleX();
        bounds.y = sprite.getY() + minY * sprite.getScaleY();
        bounds.width = (maxX - minX + 1) * sprite.getScaleX();
        bounds.height = (maxY - minY + 1) * sprite.getScaleY();

        return bounds;
    }
}