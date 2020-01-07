package com.example.demo.aocDemo;
import java.lang.annotation.*;

/**

 * 功能：自定义注解demo

 * authorize: wangshuo

 * Date: 2019-12-18 15:02:42

 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Mu {
  String value() default "1";
}
