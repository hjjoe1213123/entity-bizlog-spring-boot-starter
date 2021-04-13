package cn.idugou.bizlog.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;


/**
 * @Author :hejun
 * @Create ：2021/4/12 15:43
 * @Version ：1.0.1
 * @Copyright : Copyright (c) 2021
 * @Company : Shenzhen rhsd
 * @Description :
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { FIELD, METHOD, ANNOTATION_TYPE })
public @interface FieldString {

    String fieldName() default "";
}
