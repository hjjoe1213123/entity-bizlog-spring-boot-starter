package cn.idugou.bizlog.scan;

import lombok.Data;

import java.lang.reflect.Field;

/**
 * @Author: hejun
 * @Create: 2021/4/12 14:23
 * @Version: 1.0.1
 * @Copyright: Copyright (c) 2021
 * @Description:
 */
@Data
public class ColumnInfo {
    /**
     * 字段名称
     */
    private String name;

    /**
     * 字段中文
     */
    private String text;

    /**
     * 字段属性
     */
    private Field field;
}
