package com.xing.study.spring.framework.web.servlet;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author qzexing
 * @version 1.0
 * @date 2022/3/24 11:22
 */
public class HandlerMapping {

    private final Object controller;

    private final Method method;

    private final Pattern pattern;

    public HandlerMapping(Pattern pattern, Object controller, Method method) {
        this.pattern = pattern;
        this.controller = controller;
        this.method = method;
    }

    public Object getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }

    public Pattern getPattern() {
        return pattern;
    }
}
