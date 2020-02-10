package com.example.demo.IOC.impl;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * 接收发送约定好的数据
 * 默认接收json格式的数据
 * 并且返回json字符串格式的数据
 * 而且加密
 */
@Component
@Aspect
public class PostDataAspectImpl {

  @Pointcut("@annotation(com.example.demo.IOC.PostData)")
  private void cut(){}

  @Around("cut()")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    //返参
    Object result;

    //入参
    Object[] args =  joinPoint.getArgs();

    System.out.println("DataPost ---> Aroundbefore");
    result = joinPoint.proceed(args);
    System.out.println("DataPost ---> Aroundafter");

    return result;
  }

  @Before("cut()")
  public void before(){
    System.out.println("DataPost ---> before");
  }

  @After("cut()")
  public void after(){
    System.out.println("DataPost ---> after");

  }

  @AfterReturning(value = "cut()",returning = "result")
  public void afterReturning( Object result){
    System.out.println("DataPost ---> afterReturning "+String.valueOf(result));

  }
}
