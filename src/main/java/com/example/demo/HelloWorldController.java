package com.example.demo;

import com.Util.MatchRuleUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.check.DataCheck;
import com.example.demo.service.DemoDataService;
import com.example.demo.words.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class HelloWorldController {

  @Autowired
  WordService wordService;
  @Autowired
  DemoDataService demoDataService;

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
    }catch (Exception e){
      System.out.println("文件读取出错！"+e.getMessage());
      e.printStackTrace();
    }

    return str;
  }


  @RequestMapping(value = "/getSupplierDemoData", method = {
      RequestMethod.POST })
  public String getSupplierDemoData(String type, String key , @RequestBody String string){
    if(!DataCheck.isNull(string)){
      JSONObject jo = JSON.parseObject(string);
      type = jo.getString("type");
      key = jo.getString("key");
    }

    return demoDataService.getDemoData(type,key).toJSONString();
//    return "ok";
  }

  @RequestMapping("/isCorrectData")
  public String isCorrectData(String dataName,String dataValue) {

    boolean isCorrectData = MatchRuleUtil.isCorrectString(dataName,dataValue);
    JSONObject jo = new JSONObject();
    if(isCorrectData){
      jo.put("code","200");
      jo.put("code","成功!");
    }else{
      jo.put("code","500");
      jo.put("code","失败!");
    }


    return jo.toJSONString();
  }

}
