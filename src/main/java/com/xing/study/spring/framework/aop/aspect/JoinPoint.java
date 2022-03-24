package com.xing.study.spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author qzexing
 * @version 1.0
 * @date 2022/3/23 17:03
 */
public interface JoinPoint {

    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key, Object value);

    Object getUserAttribute(String key);

}
