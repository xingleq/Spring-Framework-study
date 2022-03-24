package com.xing.study.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @author qzexing
 * @version 1.0
 * @date 2022/3/23 9:30
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {

    String value() default "";

}
