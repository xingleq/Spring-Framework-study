package com.xing.study.spring.framework.web.servlet;

import java.util.Map;

/**
 * @author qzexing
 * @version 1.0
 * @date 2022/3/23 16:52
 */
public class ModelAndView {

    private final String viewName;

    private Map<String, ?> model;

    public ModelAndView(String viewName, Map<String, Object> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public ModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }
}


