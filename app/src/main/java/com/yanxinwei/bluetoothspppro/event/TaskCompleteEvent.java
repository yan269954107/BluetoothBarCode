package com.yanxinwei.bluetoothspppro.event;

/**
 * Created by yanxinwei on 16/4/4.
 */
public class TaskCompleteEvent {

    // 0:未完成 1:已完成 2:泄露
    private int completeType;

    private int row;

    public TaskCompleteEvent() {
    }

    public TaskCompleteEvent(int completeType, int row) {
        this.completeType = completeType;
        this.row = row;
    }

    public int getCompleteType() {
        return completeType;
    }

    public int getRow() {
        return row;
    }
}
