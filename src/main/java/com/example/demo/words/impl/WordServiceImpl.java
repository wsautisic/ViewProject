package com.example.demo.words.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.words.PoiWordUtils;
import com.example.demo.words.WordService;
import com.example.demo.words.WordService;
import org.apache.http.client.utils.DateUtils;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WordServiceImpl implements WordService {

  /* 用来记录替换文本 */
  JSONObject jo = new JSONObject();
  Map map = new HashMap();
  WordServiceImpl(){
    map.put("${contCode}","qwerasdf");
  }

  @Override
  public void exportWord() throws IOException {
    LocalDate ldt = LocalDate.now();
    String readPath=".\\word\\测试用文档.docx";
    String exportPath=".\\word\\text\\"+ ldt.format(DateTimeFormatter.BASIC_ISO_DATE)+"测试用文档.docx";
    File photoFileDir = new File(exportPath);
    // 注意path参数，最后是有斜杠的
    if(!photoFileDir.exists()){ // 如果路径不存在，就创建路径
      photoFileDir.mkdirs();
    }

    XWPFDocument document = new XWPFDocument(POIXMLDocument.openPackage(readPath));
//    FileOutputStream outStream = null;
    try (FileOutputStream outStream = new FileOutputStream(photoFileDir);){

      /**
       * 对段落中的标记进行替换
       */
      List<XWPFParagraph> parasList = document.getParagraphs();
      replaceInAllParagraphs(parasList, map);
      /**
       * 对表格中的标记进行替换
       */
      List<XWPFTable> tables = document.getTables();
      replaceInTables(tables, map);
      /**
       * 动态表格功能
       */
      dynamicTable(tables, map);

      /**
       * 导出
       */
      document.write(outStream);
      outStream.flush();
//      outStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 替换所有段落中的标记
   *
   * @param xwpfParagraphList
   * @param params
   */
  public static void replaceInAllParagraphs(List<XWPFParagraph> xwpfParagraphList, Map<String, String> params) {
    for (XWPFParagraph paragraph : xwpfParagraphList) {
      if (paragraph.getText() == null || paragraph.getText().equals("")) continue;
      for (String key : params.keySet()) {
        //TODO 应添加大小写金额字段处理
        //TODO 可能需要日期格式处理
        if (paragraph.getText().contains(key)) {
          replaceInParagraph(paragraph, key, params.get(key));
        }
      }
    }
  }

  /**
   * 替换段落中的字符串
   *
   * @param xwpfParagraph
   * @param oldString
   * @param newString
   */
  public static void replaceInParagraph(XWPFParagraph xwpfParagraph, String oldString, String newString) {
    Map<String, Object> pos_map = findSubRunPosInParagraph(xwpfParagraph, oldString, newString);
    if (pos_map != null) {
      System.out.println("start_pos:" + pos_map.get("start_pos"));
      System.out.println("end_pos:" + pos_map.get("end_pos"));

      List<XWPFRun> runs = xwpfParagraph.getRuns();
      XWPFRun modelRun = runs.get((Integer) pos_map.get("end_pos"));
      XWPFRun xwpfRun = xwpfParagraph.insertNewRun(((Integer)pos_map.get("end_pos")) + 1);
      xwpfRun.setText((String) pos_map.get("new_str"));
      System.out.println("字体大小：" + modelRun.getFontSize());
      if (modelRun.getFontSize() != -1) xwpfRun.setFontSize(modelRun.getFontSize());//默认值是五号字体，但五号字体getFontSize()时，返回-1
      xwpfRun.setFontFamily(modelRun.getFontFamily());
      for (int i = ((Integer)pos_map.get("end_pos")); i >= ((Integer)pos_map.get("start_pos")); i--) {
        System.out.println("remove run pos in :" + i);
        xwpfParagraph.removeRun(i);
      }
    }
  }

  /**
   * 替换所有的表格
   *
   * @param xwpfTableList
   * @param params
   */
  public static void replaceInTables(List<XWPFTable> xwpfTableList, Map<String, String> params) {
    for (XWPFTable table : xwpfTableList) {
      replaceInTable(table, params);

    }
  }

  /**
   * 动态表格添加
   * @param xwpfTableList
   * @param params
   */
  public void dynamicTable(List<XWPFTable> xwpfTableList, Map<String, String> params) {
    Map<String, String> jsonMap ;
    for (String key : params.keySet()) {
      if(key.contains("#")){
        JSONArray parm = JSON.parseArray(params.get(key));
        for (int i = 0; i < parm.size(); i++) {
          jsonMap = JSONObject.toJavaObject(parm.getJSONObject(i), Map.class);
          for (XWPFTable table : xwpfTableList) {
            replaceInTable(table, jsonMap);
          }
        }
      }
    }
  }

  /**
   * 替换一个表格中的所有行
   *
   * @param xwpfTable
   * @param params
   */
  public static void replaceInTable(XWPFTable xwpfTable, Map<String, String> params) {
    List<XWPFTableRow> rows = xwpfTable.getRows();
    System.out.println("表格打印：");
    System.out.println(xwpfTable.getText());
    if(PoiWordUtils.isAddRow(xwpfTable)){

      return;
    }
    //多行模板替换${tbAddRowRepeat:0,2,0,1}   判断'${tbAddRowRepeat:'
    if(PoiWordUtils.isAddRowRepeat(xwpfTable)){
      return;
    }
    for (XWPFTableRow row : rows) {
      //先判断这个表格是否为动态替换
      //动态表格替换${tbAddRow:tb1}    判断'${tbAddRow:'
      //若不是，则按普通的来替换处理
      replaceInRows(row, params);
    }

  }

  /**
   * 动态表格替换
   * 根据表格中是否包含${tbAddRow: 标识判断是否动态替换表格
   * 如果是则根据后面的字段名进行循环替换处理
   *
   *
   * @param xwpfTable
   * @param params
   */
  public void replaceInAddRowTable(XWPFTable xwpfTable,Map<String, String> params){
    //查找需要替换的所在行
//    int num = findLineNum(xwpfTable);
    String arrayName = "";
    List<XWPFTableRow> rows = xwpfTable.getRows();
    //查找第几行为需要替换的行
    int replaceLineNum = -1;
    for (int i = 0; i < rows.size(); i++) {
      XWPFTableRow row = rows.get(i);
      List<XWPFTableCell>  cells = row.getTableCells();
      for (XWPFTableCell cell : cells) {
        if(cell.getText().startsWith(PoiWordUtils.addRowFlag)){
          replaceLineNum = i ;
          arrayName ="";

          break;
        }
      }
    }

    XWPFTableRow createRow = rows.get(replaceLineNum);

  }


  /**
   * 替换表格中的一行
   *
   * @param row
   * @param params
   */
  public static void replaceInRows( XWPFTableRow row, Map<String, String> params) {
//    for (int i = 0; i < rows.size(); i++) {
//      XWPFTableRow row = rows.get(i);
      //普通表格替换
      replaceInCells(row.getTableCells(), params);
//    }
  }

  /**
   * 替换一行中所有的单元格
   *
   * @param xwpfTableCellList
   * @param params
   */
  public static void replaceInCells(List<XWPFTableCell> xwpfTableCellList, Map<String, String> params) {
    for (XWPFTableCell cell : xwpfTableCellList) {
      replaceInCell(cell, params);
    }
  }

  /**
   * 替换表格中每一行中的每一个单元格中的所有段落
   *
   * @param cell
   * @param params
   */
  public static void replaceInCell(XWPFTableCell cell, Map<String, String> params) {
    List<XWPFParagraph> cellParagraphs = cell.getParagraphs();
    replaceInAllParagraphs(cellParagraphs, params);
  }


  /**
   * 找到段落中子串的起始XWPFRun下标和终止XWPFRun的下标
   *
   * @param xwpfParagraph
   * @param substring
   * @return
   */
  public static Map<String, Object> findSubRunPosInParagraph(XWPFParagraph xwpfParagraph, String substring, String newString) {

    List<XWPFRun> runs = xwpfParagraph.getRuns();
    int start_pos = 0;
    int end_pos = 0;
    String subtemp = "";
    for (int i = 0; i < runs.size(); i++) {
      subtemp = "";
      start_pos = i;
      for (int j = i; j < runs.size(); j++) {
        if (runs.get(j).getText(runs.get(j).getTextPosition()) == null) continue;
        subtemp += runs.get(j).getText(runs.get(j).getTextPosition());
        if (subtemp.contains(substring)) {
          end_pos = j;
          Map<String, Object> map = new HashMap<>();
          map.put("start_pos", start_pos);
          map.put("end_pos", end_pos);
          map.put("new_str", subtemp.replace(substring,newString));

          return map;
        }
      }
    }
    return null;
  }






}
