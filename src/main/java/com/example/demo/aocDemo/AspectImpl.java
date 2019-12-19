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

    System.out.println("3");

  }

// 开始环绕

  @Around("cut()")

  public void around(ProceedingJoinPoint joinPoint)throws Throwable{

    System.out.println("1");

    try{

      joinPoint.proceed();

    }catch (Exception e){

      e.printStackTrace();

    }

    System.out.println("4");

  }

  @Before("cut()")

  public void before(){

    System.out.println("2");

  }

  @After("cut()")

  public void after(){

    System.out.println("5");

  }

}