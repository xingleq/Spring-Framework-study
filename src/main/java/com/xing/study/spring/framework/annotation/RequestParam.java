package com.xing.study.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @author qzexing
 * @version 1.0
 * @date 2022/3/23 9:37
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {

    String value() default "";

}
