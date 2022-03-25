package com.xing.study.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @author qzexing
 * @version 1.0
 * @date 2022/3/25 9:25
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lazy {

    boolean value() default true;
}
