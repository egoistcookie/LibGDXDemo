package com.lf.util;

/**
 * 测试工具类
 */
public class TestUtil{

    public static void main(String[] args) {
            String chineseCharacters = generateChineseCharacters();
            System.out.println(chineseCharacters);

    }

    // 生成所有常用汉字，方便使用Hiero编辑
    public static String generateChineseCharacters() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0x4E00; i <= 0x9FA5; i++) {
            sb.append((char) i);
        }
        return sb.toString();
    }
}
