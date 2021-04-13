package cn.idugou.bizlog.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author : hejun
 * @Create : 2020/10/19 11:49
 * @Version : 1.0.1
 * @Copyright : Copyright (c) 2020
 * @Description :
 */
@Data
public class SessionPair implements Serializable {

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 日志操作内容
     */
    private String value;

    /**
     * 操作人IP
     */
    private String ip;
}
