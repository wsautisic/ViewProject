package com.example.demo.controller;

import com.example.demo.IOC.PostData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
//
//@ComponentScan
//@EnableAutoConfiguration
@RestController
@RequestMapping("/test")
public class TestController {
  @PostData
  @RequestMapping("/testdo")
  @ResponseBody
  public String test(String time,String value){
    System.out.println("this is test "+time+" "+value);
    return " i love "+time;
  }
}
