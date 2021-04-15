package cn.idugou.bizlog;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @Author: hejun
 * @Create: 2021/4/12 14:23
 * @Version: 1.0.1
 * @Copyright: Copyright (c) 2021
 * @Description:
 */
@Data
@ConfigurationProperties(prefix = "entity.log")
public class EntityLogProperties {

    private List<String> excludeField;
    private String queueName;
    private int aopOrder = 2147483646;
}
