package com.xing.study.demo.aspect;

import com.xing.study.spring.framework.aop.aspect.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @author qzexing
 * @version 1.0
 * @date 2022/3/23 17:02
 */
public class LogAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void before(JoinPoint joinPoint) {
        // 记录方法调用的开始时间
        joinPoint.setUserAttribute("startTime_" + joinPoint.getMethod().getName(), System.currentTimeMillis());
        //这个方法中的逻辑，是由我们自己写的
        logger.info("Invoker Before Method!!!" +
                "\nTargetObject:" + joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
    }

    public void after(JoinPoint joinPoint) {
        // 方式执行时间 = 当前时间-开始时间
        // 检测方法执行的性能
        logger.info("Invoker After Method!!!" +
                "\nTargetObject:" + joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
        long startTime = (Long) joinPoint.getUserAttribute("startTime_" + joinPoint.getMethod().getName());
        long endTime = System.currentTimeMillis();
        System.out.println("use time :" + (endTime - startTime));
    }

    public void afterThrowing(JoinPoint joinPoint, Throwable ex) {
        // 异常检测，可以拿到异常信息
        logger.info("出现异常" +
                "\nTargetObject:" + joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()) +
                "\nThrows:" + ex.getMessage());
    }


}
