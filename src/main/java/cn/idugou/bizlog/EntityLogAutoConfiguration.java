package cn.idugou.bizlog;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * @Author : hejun
 * @Create : 2020/10/14 15:28
 * @Version : 1.0.1
 * @Copyright : Copyright (c) 2020
 * @Description :
 */
@Configuration
@EnableConfigurationProperties({EntityLogProperties.class})
public class EntityLogAutoConfiguration implements ApplicationContextAware {

    @Autowired
    private EntityLogProperties entityLogProperties;

    private static ApplicationContext appCtx;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException { appCtx = applicationContext; }

    public static ApplicationContext getApplicationContext() { return appCtx; }
}
