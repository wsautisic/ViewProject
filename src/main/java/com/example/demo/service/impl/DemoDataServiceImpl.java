package com.example.demo.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.service.DemoDataService;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service("DemoDataService")
public class DemoDataServiceImpl  implements DemoDataService {

  @Override
  public JSONArray getDemoData(String type,String key){
    if(type == null ){
      return new JSONArray();
    }
    switch (type){
      case "pzzsjj":
        return getKhsjData();
      case "byzbbh":
        return getSupplierDemoData(key);
      default:
        return new JSONArray();

    }

  }

  /**
   * 考核类型测试数据
   * @return
   */
  public JSONArray getKhsjData(){
    JSONArray ja = new JSONArray();
    JSONObject jo =  new JSONObject();
    int num = 10;
//    LocalDate date = LocalDate.now();
//    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
    for (int i = 0; i < num; i++) {
      jo =  new JSONObject();
      jo.put("name","考核数据"+i);
      jo.put("code","010203"+new DecimalFormat("00").format(i));
      ja.add(jo);
    }
    return ja;
  }

  /**
   * 输入中标编号后 根据编号获取信息
   * 获取甲方公司代码，乙方供应商编码，供应商名称；业务类型，业务区；
   * ②ICS需要根据从PMP获得的公司代码匹配我方公司名称，根据供应商编码匹配银行信息供用户进行选择；
   * @return
   */
  public JSONArray getSupplierDemoData(String zbCode){
    JSONArray ja = new JSONArray();
    JSONObject jo =  new JSONObject();
    int num = 10;
//    LocalDate date = LocalDate.now();
//    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
//    for (int i = 0; i < num; i++) {
      jo =  new JSONObject();
      jo.put("innerSupplierCode","innerSupplierCode"+zbCode);
      jo.put("innerSupplierName","我方签约名称");
      jo.put("outerSupplierCode","outerSupplierCode"+zbCode);
      jo.put("outerSupplierName","乙方名称"+zbCode);
//      jo.put("outerSupplierName","");
      jo.put("ywlx","业务类型"+zbCode);
      jo.put("ywq","业务区"+zbCode);
      ja.add(jo);
//    }
    return ja;
  }

}
