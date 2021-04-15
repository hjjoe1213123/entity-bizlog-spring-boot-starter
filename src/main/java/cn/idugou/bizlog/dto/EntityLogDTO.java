package cn.idugou.bizlog.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @Author: hejun
 * @Create: 2021/4/12 14:23
 * @Version: 1.0.1
 * @Copyright: Copyright (c) 2021
 * @Description:
 */
@Data
public class EntityLogDTO<T> implements Serializable {
    /**
     * 表名
     */
    private String tableName;

    /**
     * 表中文描述
     */
    private String tableDesc;

    /**
     * 模块
     */
    private String value;

    /**
     * IP
     */
    private String ip;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 唯一标识
     */
    private String sessionId;

    /**
     * 日志类型
     */
    private LogType logType;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 主键ID
     */
    private String rowId;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 老值
     */
    private T oldValue;

    /**
     * 新值
     */
    private T newValue;

    /**
     * 字段日志Map
     */
    private Map<String, ColumnLogDTO> columnLogMap;
}
