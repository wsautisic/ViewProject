package com.example.demo.aocDemo;

import org.aspectj.lang.ProceedingJoinPoint;

import org.aspectj.lang.annotation.*;

import org.springframework.stereotype.Component;

/**

 * 功能：注解类的实现

 * authorize: wangshuo

 * Date: 2019-12-18

 */

@Component

@Aspect

public class AspectImpl {

  @Pointcut("@annotation(com.example.demo.aocDemo.Mu)")

  private void cut(){
    //这个没有执行，也不会执行
    //仅用来替代注解引号部分[@annotation(com.example.demo.aocDemo.Mu)]
    System.out.println("3");

  }

// 开始环绕

  @Around("cut()")

  public Object around(ProceedingJoinPoint joinPoint)throws Throwable{

    System.out.println("1");
    Object result = null;
    try{
      Object[] args = joinPoint.getArgs();

      for (Object object : args) {

        logInfo(object,0);

      }
      Object[] fields = {"7","8"};

      result = joinPoint.proceed(fields);

    }catch (Exception e){

      e.printStackTrace();

    }
    logInfo(result,1);
    System.out.println("4");
    return result;
  }

  @Before("cut()")

  public void before(){

    System.out.println("2");

  }

  @After("cut()")

  public void after(){

    System.out.println("5");

  }

  public void logInfo(Object object, Integer i) {

        String logType = "";

        if(i == 0) {

           logType = "输入参数：";

        }else {

           logType = "输出参数：";

        }

        if(object == null) {

          System.out.println(logType+"为null");

         }else {

          System.out.println(logType+object.toString());

         }

    }

}