package com.jm.datasourse;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Repository;


@Order(1)
@Aspect
@Repository
public class DataSourceAspect {
    //切点表示com.jm.service.impl.UserServiceImpl下的所有方法
    @Pointcut("execution(* com.jm.service.impl.*ServiceImpl.*2(..))")
    private void anyMethod() {
    }

    @AfterReturning(value = "anyMethod()", returning = "result")
    public void afterReturning(JoinPoint joinPoint,Object result){
        DataSourceHolder.clearDataSourceType();
    }

    @Before(value = "anyMethod()")
    public void before(JoinPoint joinPoint) throws Throwable {
        System.out.println("方法进入切面.....");
        //如果方法体上使用了DataSource注解
        DataSourceHolder.setDataSourceType("ds2");
    }

}
