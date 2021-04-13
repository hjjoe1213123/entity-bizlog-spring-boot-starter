package cn.idugou.bizlog.dto;

/**
 * @Author : hejun
 * @Create : 2020/10/14 14:48
 * @Version : 1.0.1
 * @Copyright : Copyright (c) 2020
 * @Description : 日志类型
 */
public enum LogType {
    INSERT("新增"), UPDATE("修改"), DELETE("删除");
    private String text;

    LogType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
