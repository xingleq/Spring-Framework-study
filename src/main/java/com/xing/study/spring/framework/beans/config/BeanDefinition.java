package com.xing.study.spring.framework.beans.config;

/**
 * @author qzexing
 * @version 1.0
 * @date 2022/3/23 14:36
 */
public class BeanDefinition {

    private String factoryBeanName;

    private boolean lazyInit = false;

    private String beanClassName;

    private boolean isSingleton = true;


    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    public void setSingleton(boolean singleton) {
        isSingleton = singleton;
    }
}
