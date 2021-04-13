package cn.idugou.bizlog.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author :hejun
 * @Create ：2021/4/12 14:25
 * @Version ：1.0.1
 * @Copyright : Copyright (c) 2021
 * @Description :
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityLog {

    String value() default "";
}
