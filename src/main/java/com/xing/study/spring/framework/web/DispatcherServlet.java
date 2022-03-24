package com.xing.study.spring.framework.web;

import com.xing.study.spring.framework.annotation.Controller;
import com.xing.study.spring.framework.annotation.RequestMapping;
import com.xing.study.spring.framework.context.ApplicationContext;
import com.xing.study.spring.framework.web.servlet.*;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author qzexing
 * @version 1.0
 * @date 2022/3/22 17:19
 */
public class DispatcherServlet extends HttpServlet {

    List<HandlerMapping> handlerMappings = new ArrayList<>();

    Map<String, HandlerAdapter> handlerAdapters = new ConcurrentHashMap<>();

    private final List<ViewResolver> viewResolvers = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            new ModelAndView("500");
            resp.getWriter().write("500 Exception,Details:\r\n"
                    + Arrays.toString(e.getStackTrace())
                    .replaceAll("[\\[\\]]", "")
                    .replaceAll(",\\s", "\r\n"));
            e.printStackTrace();
        }

    }


    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        // 第一步，通过从request中拿到url，去匹配一个handlerMapping
        HandlerMapping handler = getHandler(req);

        if (handler == null) {
            processDispatchResult(resp, new ModelAndView("404"));
            return;
        }

        // 第二步，准备调用前的参数
        HandlerAdapter ha = getHandlerAdapter(handler);
        assert ha != null;

        // 第三部，真正的调用方法,返回GPModelAndView（存储了要传递到页面上的值，和模板页面的名称）
        ModelAndView mv = ha.handle(req, resp, handler);

        // 第四步，处理结果(真正的输出)
        processDispatchResult(resp, mv);

    }

    private HandlerAdapter getHandlerAdapter(HandlerMapping handler) {
        if (handlerAdapters.isEmpty()) {
            return null;
        }
        return handlerAdapters.get(handler.getPattern().pattern());
    }

    private void processDispatchResult(HttpServletResponse resp, ModelAndView modelAndView) throws Exception {
        if (modelAndView == null) {
            return;
        }
        if (viewResolvers.isEmpty()) {
            return;
        }
        for (ViewResolver viewResolver : viewResolvers) {
            View view = viewResolver.resolveViewName(modelAndView.getViewName());
            view.render(modelAndView.getModel(), resp);
            return;
        }
    }

    private HandlerMapping getHandler(HttpServletRequest req) {
        if (handlerMappings.isEmpty()) {
            return null;
        }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath, "").replaceAll("/+", "/");
        for (HandlerMapping handlerMapping : handlerMappings) {
            Matcher matcher = handlerMapping.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handlerMapping;
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) {

        // 初始化 ApplicationContext
        String contextConfigLocation = "contextConfigLocation";
        ApplicationContext context = new ApplicationContext(config.getInitParameter(contextConfigLocation));

        // 初始化MVC 组件
        initStrategies(context);
    }

    /**
     * 初始化MVC 组件
     *
     * @param context ApplicationContext
     */
    private void initStrategies(ApplicationContext context) {
        //多文件上传的组件
        initMultipartResolver();
        //初始化本地语言环境
        initLocaleResolver();
        //初始化模板处理器
        initThemeResolver();
        //handlerMapping，必须实现
        initHandlerMappings(context);
        //初始化参数适配器，必须实现
        initHandlerAdapters();
        //初始化异常拦截器
        initHandlerExceptionResolvers();
        //初始化视图预处理器
        initRequestToViewNameTranslator();
        //初始化视图转换器，必须实现
        initViewResolvers(context);
        //参数缓存器
        initFlashMapManager();
    }

    private void initFlashMapManager() {
    }

    private void initViewResolvers(ApplicationContext context) {
        String templateRoot = context.getReader().getConfig().getProperty("templateRoot");
        URL templateRootPathUrl = this.getClass().getClassLoader().getResource(templateRoot);
        assert templateRootPathUrl != null;
        String templateRootPath = templateRootPathUrl.getFile();

        File templateRootDir = new File(templateRootPath);
        File[] files = templateRootDir.listFiles();
        assert files != null;
        for (int i = 0; i < files.length; i++) {
            this.viewResolvers.add(new ViewResolver(templateRoot));
        }
    }

    private void initRequestToViewNameTranslator() {
    }

    private void initHandlerExceptionResolvers() {
    }

    private void initHandlerAdapters() {
        for (HandlerMapping handlerMapping : handlerMappings) {
            handlerAdapters.put(handlerMapping.getPattern().pattern(), new HandlerAdapter());
        }
    }

    private void initHandlerMappings(ApplicationContext context) {
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        try {
            for (String beanDefinitionName : beanDefinitionNames) {
                Object controller = context.getBean(beanDefinitionName);
                Class<?> clazz = controller.getClass();
                if (!clazz.isAnnotationPresent(Controller.class)) {
                    continue;
                }
                String baseUrl = "";
                if (clazz.isAnnotationPresent(RequestMapping.class)) {
                    baseUrl = clazz.getAnnotation(RequestMapping.class).value();
                }
                for (Method method : clazz.getMethods()) {
                    if (!method.isAnnotationPresent(RequestMapping.class)) {
                        continue;
                    }
                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                    String regex = ("/" + baseUrl + "/" + requestMapping.value()).replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);
                    handlerMappings.add(new HandlerMapping(pattern, controller, method));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void initThemeResolver() {
    }

    private void initLocaleResolver() {
    }

    private void initMultipartResolver() {
    }
}
