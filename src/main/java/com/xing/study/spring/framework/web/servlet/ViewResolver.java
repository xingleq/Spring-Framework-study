package com.xing.study.spring.framework.web.servlet;

import java.io.File;
import java.net.URL;

/**
 * @author qzexing
 * @version 1.0
 * @date 2022/3/24 11:34
 */
public class ViewResolver {

    private final File templateDir;

    private static final String DEFAULT_TEMPLATE_SUFFIX = ".html";

    public ViewResolver(String templateRoot) {
        URL templateRootPathUrl = this.getClass().getClassLoader().getResource(templateRoot);
        assert templateRootPathUrl != null;
        String templateRootPath = templateRootPathUrl.getFile();
        this.templateDir = new File(templateRootPath);
    }


    public View resolveViewName(String viewName) {
        if (viewName == null || "".equals(viewName.trim())) {
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : viewName + DEFAULT_TEMPLATE_SUFFIX;
        File template = new File((templateDir.getPath() + "/" + viewName).replaceAll("/+", "/"));
        return new View(template);
    }
}
