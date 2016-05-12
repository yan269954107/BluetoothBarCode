package com.yanxinwei.bluetoothspppro.core;

/**
 *
 * Created by yanxinwei on 16/3/21.
 */
public interface AppConstants {

    String[] leakagePosition = {"轴封","阀盖","阀杆","阀芯","法兰","丝堵","空冷丝堵","管帽","丝扣",
            "活接","开口阀/开口管线","泄压装置"};

    //检测任务每一行cell数
    int CELL_NUMBER_NORMAL = 15;
    //复检任务每一行cell数
    int CELL_NUMBER_REPEAT = 19;

    //平台检测任务 每一行对应cell的含义
    int CELL_DETECT_DATE = 9;
    int CELL_DETECT_DEVICE = 10;
    int CELL_DETECT_VALUE = 11;
    int CELL_IS_LEAKAGE = 12;
    int CELL_LEAKAGE_POSITION = 13;
    int CELL_REMARKS = 14;

    //自有检测任务 每一行对应cell的含义
    int CELL_OWN_DETECT_PERSONAL = 11;
    int CELL_OWN_DETECT_DEVICE = 12;
    int CELL_OWN_DETECT_DATE = 13;
    int CELL_OWN_DETECT_VALUE = 14;
    int CELL_OWN_IS_LEAKAGE = 15;
    int CELL_OWN_LEAKAGE_POSITION = 16;
    int CELL_OWN_REMARKS = 17;


    //复测任务 每一行对应的cell的含义
    int CELL_REPEAT_DATE = 15;
    int CELL_REPEAT_DEVICE = 16;
    int CELL_REPEAT_VALUE = 17;
    int CELL_REPEAT_REMARK = 18;

}
