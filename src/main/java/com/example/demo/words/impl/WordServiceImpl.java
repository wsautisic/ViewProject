package com.example.demo.words.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.words.PoiWordUtils;
import com.example.demo.words.WordService;
import com.example.demo.words.WordService;
import com.example.demo.words.WordTableVO;
import org.apache.http.client.utils.DateUtils;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WordServiceImpl implements WordService {
  private static final Logger logger = LoggerFactory.getLogger(WordServiceImpl.class);
//
//  JSONObject jo = new JSONObject();
  /* 测试数据 */
  Map map = new HashMap();
  File file ;
  WordServiceImpl(){
    file = new File(".\\word\\围观.jpg");
    String longStr = "是当时的发发撒地方啊手动阀发射点发打发发发是当时的发发撒地方啊手动阀发射点发打发" +
        "发发是当时的发发撒地方啊手动阀发射点发打发发发是当时的发发撒地方啊手动阀发射点发打发发发";
    //单行重复动态表格数据模拟
    JSONArray ja = new JSONArray();
    String[] table1Name = {"cell1","cell2"};
    JSONObject jo ;
    for (int i = 0;i<10;i++){
      jo = new JSONObject();

      for (String fieldName:table1Name){
        jo.put(fieldName,longStr+"-"+i);
      }
      ja.add(jo);
    }
    //多行重复动态表格数据模拟
    JSONArray repeatja = new JSONArray();
    String[] repeatCellName = {"Cell1","Cell2","Cell3","Cell4"};
    for (int i = 0; i < 3; i++) {
      jo = new JSONObject();
      for (String s : repeatCellName) {
        jo.put(s,s+longStr+"Repeat"+i);
      }
      repeatja.add(jo);
    }


    map.put("${contCode}","qwerasdf");
    map.put("wordtable1",ja.toJSONString());
    map.put("wordTable2",repeatja.toJSONString());

    System.out.println(repeatja.toJSONString());
  }

  @Override
  public void exportWord() throws Exception {
    LocalDate ldt = LocalDate.now();
    String readPath=".\\word\\测试用文档.docx";
    String exportPath=".\\word\\text\\"+ ldt.format(DateTimeFormatter.BASIC_ISO_DATE)+"测试用文档.docx";
//    File photoFileDir = new File(exportPath);
//    if(!photoFileDir.exists()){ // 如果路径不存在，就创建路径
//      photoFileDir.mkdirs();
//    }

    XWPFDocument document = new XWPFDocument(POIXMLDocument.openPackage(readPath));
//    FileOutputStream outStream = null;
    try (FileOutputStream outStream = new FileOutputStream(exportPath);
          FileInputStream  fileinputSteam = new FileInputStream(file);){

      List<XWPFTable> tables = document.getTables();
      /**
       * 对表格中的标记进行替换
       */
//      replaceInTables(tables, map);
      /**
       * 对段落中的标记进行替换
       */
      List<XWPFParagraph> parasList = document.getParagraphs();
      WordTableVO wordTextVO = new WordTableVO(parasList,map);
      wordTextVO.replaceText();
      for (XWPFTable table : tables) {
        WordTableVO wordTableVO = WordTableVO.getTableVO(table,map);
        wordTableVO.replaceInAddRowTable();
      }

//      replaceInAllParagraphs(parasList, map);

//      WordTableVO.getParagraph(parasList);

      /**
       * 动态表格功能
       */
//      dynamicTable(tables, map);

      /**
       * 导出
       */
      document.write(outStream);
      outStream.flush();
//      outStream.close();
    }catch (IOException e) {
      logger.error("文档输入流报错！",e);
    } catch(Exception e){
      logger.error("生成文档时出错！[{}]",e.getMessage(),e);
    }
  }







}
