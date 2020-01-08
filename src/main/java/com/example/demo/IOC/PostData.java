package com.example.demo.IOC;

import java.lang.annotation.*;

/**
 * 数据接收及发送处理注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PostData {
}
