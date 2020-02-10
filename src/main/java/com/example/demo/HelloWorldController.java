package com.example.demo;

import com.example.demo.words.WordService;
import com.example.demo.words.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class HelloWorldController {

  @Autowired
  WordService wordService;

  @RequestMapping("/hello")
  public String index() {
    return "Hello World";
  }

  @RequestMapping("/wordRead")
  public String wordRead() {
    String str = "是的没有数据";
    try {
      wordService.exportWord();
    } catch (IOException e) {
      System.out.println("文件读取出错！"+e.getMessage());
      e.printStackTrace();
    }

    return str;
  }


}
