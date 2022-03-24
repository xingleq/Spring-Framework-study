package com.xing.study.spring.framework.aop.support;

import com.xing.study.spring.framework.aop.config.AopConfig;

/**
 * @author qzexing
 * @version 1.0
 * @date 2022/3/24 9:54
 */
public class AdvisedSupport {

    private Class<?> targetClass;

    private Object target;

    private AopConfig config;

    public AdvisedSupport(AopConfig config) {
        this.config = config;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }


}
