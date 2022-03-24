package com.xing.study.spring.framework.beans;

/**
 * @author qzexing
 * @version 1.0
 * @date 2022/3/23 14:37
 */
public class BeanWrapper {

    private final Object wrappedInstance;

    private Class<?> wrappedClass;

    public BeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance() {
        return wrappedInstance;
    }

    public Class<?> getWrappedClass() {
        return wrappedInstance.getClass();
    }
}
