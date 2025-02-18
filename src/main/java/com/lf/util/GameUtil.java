package com.lf.util;

/**
 * 游戏工具类
 */
public class GameUtil {

    /**
     * 根据经验值计算等级方法，全游戏通用
     * @param experience 经验值
     * @return 等级
     */
    public static int calcLevel(int experience) {
        int level =1;
        // 经验值达到1，从1级升到2级，按下述逻辑例推，最高10级
        if(experience > 80){
            level =10;
        }else if(experience > 70){
            level =9;
        }else if(experience > 60){
            level =8;
        }else if(experience > 50){
            level =7;
        }else if(experience > 40){
            level =6;
        }else if(experience > 30){
            level =5;
        }else if(experience > 20){
            level =4;
        }else if(experience > 10){
            level =3;
        }else if(experience > 5){
            level =2;
        }
        return level;
    }

}
