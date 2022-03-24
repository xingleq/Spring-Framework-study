package com.xing.study.spring.framework.web.servlet;

import com.xing.study.spring.framework.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qzexing
 * @version 1.0
 * @date 2022/3/24 11:26
 */
public class HandlerAdapter {


    public ModelAndView handle(HttpServletRequest req, HttpServletResponse resp, HandlerMapping handler) throws Exception {

        //第一步，把方法的形参列表和request的参数列表所在的顺序进行一一对应
        Map<String, Integer> paramsIndexMappings = new HashMap<>();
        Annotation[][] parameterAnnotations = handler.getMethod().getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof RequestParam) {
                    String paramName = ((RequestParam) annotation).value();
                    if (!"".equals(paramName.trim())) {
                        paramsIndexMappings.put(paramName, i);
                    }
                }
            }
        }

        // 第二步，提取方法中request 和 response 参数
        Class<?>[] parameterTypes = handler.getMethod().getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (parameterType == HttpServletRequest.class || parameterType == HttpServletResponse.class) {
                paramsIndexMappings.put(parameterType.getName(), i);
            }
        }

        // 第三步, 获得方法的形参列表
        Map<String, String[]> parameterMap = req.getParameterMap();
        Object[] paramValues = new Object[parameterTypes.length];

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String value = Arrays.toString(entry.getValue()).replaceAll("[\\[\\]]", "").replaceAll("\\s", ",");

            if (!paramsIndexMappings.containsKey(entry.getKey())) {
                continue;
            }

            Integer index = paramsIndexMappings.get(entry.getKey());
            paramValues[index] = caseStringValue(value, parameterTypes[index]);
        }


        if (paramsIndexMappings.containsKey(HttpServletRequest.class.getName())) {
            Integer reqIndex = paramsIndexMappings.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }
        if (paramsIndexMappings.containsKey(HttpServletResponse.class.getName())) {
            Integer respIndex = paramsIndexMappings.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;
        }

        // 第四步，反射调用方法得到结果,判断并返回
        Object result = handler.getMethod().invoke(handler.getController(), paramValues);
        if (result == null) {
            return null;
        }
        boolean isModelAndView = handler.getMethod().getReturnType() == ModelAndView.class;
        if (isModelAndView) {
            return (ModelAndView) result;
        }
        return null;
    }

    private Object caseStringValue(String value, Class<?> parameterType) {
        if (String.class == parameterType) {
            return value;
        } else if (Integer.class == parameterType) {
            return Integer.valueOf(value);
        } else if (Double.class == parameterType) {
            return Double.valueOf(value);
        } else {
            return value;
        }
    }

}
