package com.xing.study.spring.framework.beans.support;

import com.xing.study.spring.framework.annotation.Component;
import com.xing.study.spring.framework.annotation.Controller;
import com.xing.study.spring.framework.annotation.Repository;
import com.xing.study.spring.framework.annotation.Service;
import com.xing.study.spring.framework.beans.config.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author qzexing
 * @version 1.0
 * @date 2022/3/23 14:37
 */
public class BeanDefinitionReader {

    private static final String SCAN_PACKAGE = "scan.package";

    private static final String JAVA_BATE_CODE_SUFFIX = ".class";

    private final Properties config = new Properties();

    private final List<String> registryBeanClasses = new ArrayList<>();

    public BeanDefinitionReader(String... configLocations) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(configLocations[0].replace("classpath:", ""));
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 扫描包路径下的文件
        doScan(config.getProperty(SCAN_PACKAGE));
    }

    /**
     * 扫描包路径下的文件
     *
     * @param scanPackage 扫描包路径
     */
    private void doScan(String scanPackage) {
        URL url = this.getClass().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File files = new File(Objects.requireNonNull(url).getFile());
        for (File file : Objects.requireNonNull(files.listFiles())) {
            if (file.isDirectory()) {
                doScan(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(JAVA_BATE_CODE_SUFFIX)) {
                    return;
                }
                String className = scanPackage + "." + file.getName().replace(JAVA_BATE_CODE_SUFFIX, "");
                registryBeanClasses.add(className);
            }
        }
    }

    public Properties getConfig() {
        return config;
    }

    /**
     * 把扫描到的对象解析为 BeanDefinition
     *
     * @return BeanDefinition 列表
     */
    public List<BeanDefinition> loadBeanDefinitions() {
        List<BeanDefinition> beanDefinitionList = new ArrayList<>();
        try {
            for (String registryBeanClass : registryBeanClasses) {
                Class<?> clazz = Class.forName(registryBeanClass);
                if (clazz.isInterface()) {
                    continue;
                }
                // 自定义名字
                String factoryBeanName = null;
                Annotation[] annotatedInterfaces = clazz.getAnnotations();
                for (Annotation annotation : annotatedInterfaces) {
                    if (annotation instanceof Controller) {
                        factoryBeanName = ((Controller) annotation).value();
                    } else if (annotation instanceof Service) {
                        factoryBeanName = ((Service) annotation).value();
                    } else if (annotation instanceof Component) {
                        factoryBeanName = ((Component) annotation).value();
                    } else if (annotation instanceof Repository) {
                        factoryBeanName = ((Repository) annotation).value();
                    }
                }
                if (factoryBeanName == null || "".equals(factoryBeanName.trim())) {
                    // 默认是类名首字母小写
                    factoryBeanName = toLowerFirstCase(clazz.getSimpleName());
                }

                beanDefinitionList.add(doCreateBeanDefinition(factoryBeanName, clazz.getName()));

                // 接口注入
                Class<?>[] interfaces = clazz.getInterfaces();
                for (Class<?> i : interfaces) {
                    beanDefinitionList.add(doCreateBeanDefinition(i.getName(), clazz.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beanDefinitionList;
    }

    /**
     * 创建 BeanDefinition
     *
     * @param factoryBeanName 文件类名
     * @param beanClassName   文件全类名
     * @return BeanDefinition
     */
    private BeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setFactoryBeanName(factoryBeanName);
        beanDefinition.setBeanClassName(beanClassName);
        return beanDefinition;
    }

    /**
     * 类名首字母小写转换
     *
     * @param simpleName 类名
     * @return 类名首字母小写
     */
    private String toLowerFirstCase(String simpleName) {
        return simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
    }

}
