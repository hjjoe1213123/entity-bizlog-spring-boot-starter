package cn.idugou.bizlog.annotation;

import cn.idugou.bizlog.EntityLogAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author :hejun
 * @Create ：2021/4/12 14:23
 * @Version ：1.0.1
 * @Copyright : Copyright (c) 2021
 * @Description :
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({EntityLogAutoConfiguration.class})
public @interface EnableEntitylog {
}
