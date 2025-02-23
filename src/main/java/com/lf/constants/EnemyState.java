package com.lf.constants;

// 定义一个名为 EnemyState 的枚举类，用于表示敌人的不同状态
public enum EnemyState {
    // 表示敌人的初始状态，即敌人刚刚被创建或者还未开始行动
    INITIAL(0),
    // 表示敌人正在运动的状态，例如在地图上移动
    MOVING(1),
    // 表示敌人在运动过程中遇到障碍物，无法继续正常移动的状态
    BLOCKED(2),
    // 表示敌人被击败后倒下的状态
    FALLEN(3),
    // 表示敌人倒下一段时间后，尸体消失的状态
    DISAPPEARED(4),
    // 表示敌人成功到达目标终点的状态
    REACHED_DESTINATION(5);

    private final int value;

    // 构造函数，用于初始化枚举常量的值
    EnemyState(int value) {
        this.value = value;
    }

    // 获取枚举常量的值
    public int getValue() {
        return value;
    }
}