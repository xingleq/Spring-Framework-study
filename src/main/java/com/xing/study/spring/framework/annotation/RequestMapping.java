package com.xing.study.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @author qzexing
 * @version 1.0
 * @date 2022/3/23 9:36
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {

    String value() default "";
}
