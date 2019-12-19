package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

//配置扫描地址 的方式如下两种
//@ComponentScan("com")  配置根目录
//@ComponentScan({"com.example","com.check"})  配置多个目录
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
