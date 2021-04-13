package cn.idugou.bizlog.scan;

import lombok.Data;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @Author : hejun
 * @Create : 2020/10/14 15:15
 * @Version : 1.0.1
 * @Copyright : Copyright (c) 2020
 * @Description :
 */
@Data
public class TableInfo {
    /**
     * 表名称
     */
    private String name;

    /**
     * 表描述
     */
    private String desc;

    /**
     * 主键
     */
    private String primaryKey;

    /**
     * 主键类型
     */
    private Field primaryKeyField;

    /**
     * 字段信息
     */
    private List<ColumnInfo> columns;
}
