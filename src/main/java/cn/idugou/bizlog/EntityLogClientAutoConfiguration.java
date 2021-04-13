package cn.idugou.bizlog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author : hejun
 * @Create : 2020/10/15 11:22
 * @Version : 1.0.1
 * @Copyright : Copyright (c) 2020
 * @Description :
 */
@Configuration
@EnableConfigurationProperties({EntityLogProperties.class})
public class EntityLogClientAutoConfiguration {

    @Autowired
    private EntityLogProperties entityLogProperties;
}
