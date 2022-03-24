package com.xing.study.spring.framework.context;

import com.xing.study.spring.framework.annotation.*;
import com.xing.study.spring.framework.aop.config.AopConfig;
import com.xing.study.spring.framework.aop.support.AdvisedSupport;
import com.xing.study.spring.framework.beans.BeanWrapper;
import com.xing.study.spring.framework.beans.config.BeanDefinition;
import com.xing.study.spring.framework.beans.support.BeanDefinitionReader;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qzexing
 * @version 1.0
 * @date 2022/3/23 9:43
 */
public class ApplicationContext {

    private final String[] configLocations;

    private BeanDefinitionReader reader;

    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private final Map<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();

    private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>();

    public ApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        refresh();
    }

    private void refresh() {
        try {
            // 定位配置文件
            reader = new BeanDefinitionReader(this.configLocations);
            // 加载配置文件
            List<BeanDefinition> beanDefinitionList = reader.loadBeanDefinitions();
            // 注册Bean信息
            doRegisterBeanDefinition(beanDefinitionList);
            // 初始bean 实例
            doAutowired();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doAutowired() {
        for (Map.Entry<String, BeanDefinition> definitionEntry : beanDefinitionMap.entrySet()) {
            if (!definitionEntry.getValue().isLazyInit()) {
                this.getBean(definitionEntry.getKey());
            }
        }
    }

    public Object getBean(String beanName) {

        BeanWrapper beanWrapper = instantiateBean(this.beanDefinitionMap.get(beanName));

        factoryBeanInstanceCache.put(beanName, beanWrapper);

        // 注入bean
        populateBean(beanWrapper);

        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    private void populateBean(BeanWrapper beanWrapper) {
        Object instance = beanWrapper.getWrappedInstance();
        Class<?> clazz = beanWrapper.getWrappedClass();
        if (!clazz.isAnnotationPresent(Controller.class) && !clazz.isAnnotationPresent(Service.class)
                && !clazz.isAnnotationPresent(Repository.class) && !clazz.isAnnotationPresent(Component.class)) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Autowired.class)) {
                continue;
            }
            Autowired autowired = field.getAnnotation(Autowired.class);
            String autowiredName = autowired.value();
            if (autowiredName == null || autowiredName.trim().isEmpty()) {
                autowiredName = field.getType().getName();
            }
            field.setAccessible(true);
            if (factoryBeanInstanceCache.get(autowiredName) == null) {
                continue;
            }
            try {
                field.set(instance, factoryBeanInstanceCache.get(autowiredName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }


    }

    private BeanWrapper instantiateBean(BeanDefinition beanDefinition) {
        String className = beanDefinition.getBeanClassName();

        Object instance = null;
        try {
            if (factoryBeanObjectCache.containsKey(className)) {
                instance = factoryBeanObjectCache.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                AdvisedSupport advisedSupport = instantiateAopConfig();
                advisedSupport.setTarget(instance);
                advisedSupport.setTargetClass(instance.getClass());

                factoryBeanObjectCache.put(className, instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BeanWrapper(instance);
    }


    private AdvisedSupport instantiateAopConfig() {
        AopConfig config = new AopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new AdvisedSupport(config);
    }

    private void doRegisterBeanDefinition(List<BeanDefinition> beanDefinitionList) {
        for (BeanDefinition beanDefinition : beanDefinitionList) {
            beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }

    }

    public BeanDefinitionReader getReader() {
        return reader;
    }

    public String[] getBeanDefinitionNames() {
        return beanDefinitionMap.keySet().toArray(new String[0]);
    }
}
