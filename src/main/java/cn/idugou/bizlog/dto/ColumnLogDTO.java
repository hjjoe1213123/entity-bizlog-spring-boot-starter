package cn.idugou.bizlog.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author : hejun
 * @Create : 2020/10/12 16:25
 * @Version : 1.0.1
 * @Copyright : Copyright (c) 2020
 * @Description : 字段对象
 */
@Data
public class ColumnLogDTO implements Serializable {
    /**
     * 字段名称
     */
    private String columnName;

    /**
     * 字段中文
     */
    private String columnText;

    /**
     * 旧值
     */
    private String oldValue;

    /**
     * 新值
     */
    private String newValue;

}
