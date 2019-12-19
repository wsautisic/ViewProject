package com.example.demo.aocDemo;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@ComponentScan
@EnableAutoConfiguration
@RestController
@RequestMapping("/test")
public class TestController {

  @Mu
  @RequestMapping("/testdo")
  @ResponseBody
  public String test(){
    System.out.println("this is test");
    return "this is test";
  }
}
