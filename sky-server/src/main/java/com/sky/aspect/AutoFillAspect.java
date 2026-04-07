package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，用于自动填充公共字段
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * @Pointcut定义一个切入点表达式：com.sky.mapper 包下所有使用 @AutoFill 注解的方法
     * execution(* com.sky.mapper.*.*(..))
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {
    }

    /**
     * 前置通知，在通知中进行公共字段的自动填充
     *
     * @param joinPoint
     */
    @Before("autoFillPointCut()")//引入切入点表达式
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始进行公共字段自动填充...");

        //获取数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//第1层：方法签名（包含方法的"身份证信息"）
        Method method = signature.getMethod();//第2层：方法对象（签名里"包含"的完整方法信息）
        AutoFill autoFill = method.getAnnotation(AutoFill.class);//第3层：注解对象（方法上"包含"的注解）
        OperationType operationType = autoFill.value();//第4层：注解的属性（注解里"包含"的值）

        //获取被拦截方法参数
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;//参数为空返回
        }
        Object entity = args[0];


        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();


        //反射判断赋值
        if (operationType == OperationType.INSERT) {

            try {
                Method setCreateTime = entity.getClass().getMethod(
                        AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getMethod(
                        AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getMethod(
                        AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = entity.getClass().getMethod(
                        AutoFillConstant.SET_UPDATE_USER, Long.class);
                //反射赋值
                setCreateTime.invoke(entity, now);
                setUpdateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (operationType == OperationType.UPDATE) {
            try {
                Method setUpdateTime = entity.getClass().getMethod(
                        AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getMethod(
                        AutoFillConstant.SET_UPDATE_USER, Long.class);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
