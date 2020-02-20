package com.example.demo.words;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xwpf.usermodel.*;

import java.lang.reflect.Array;
import java.util.*;

public class WordTableVO {
  //表格类型-单行重复
  private final static String TABLE_ONE = "ONE";
  //表格类型-多行重复
  private final static String TABLE_MORE = "MORE";
  //表格类型-不重复
  private final static String TABLE_NO = "NO";
  //列表对象
   XWPFTable xwpfTable;
  //表格类型
  String tableType;
  //重复开始行数 起始行数为0
  int startNum ;
  //重复区间大小
  int RepeatSize = 1;
  //数据
  Map<String, String> params;
  //转换后字段数据
  static Map<String, String> paramsField;
  //重复区间行值信息 源文本数据
  List<JSONArray> rowValues = new ArrayList<>();
  //包含表名  正常来说每个表格只对应一个子表，若有不同再进行扩展
  Set<String> tableNameSet = new HashSet();
  //组装本列表对象所需要的信息  需放入的数据
  List<Map<String,String>> rowValuesArray = new ArrayList<>();
  //开头格式
  String indexKey;
  //开头格式
  String Repeatstr = "";



  public WordTableVO(XWPFTable xwpfTable, Map<String, String> params){
    this.xwpfTable = xwpfTable;
    this.startNum = -1;
    this.RepeatSize = 0;
    this.params = params;
    tableType = TABLE_NO;
  }

  public WordTableVO(XWPFTable xwpfTable, int startNum, Map<String, String> params){
    this.xwpfTable = xwpfTable;
    this.startNum = startNum;
    this.params = params;
    this.indexKey = PoiWordUtils.addRowFlag;
    tableType = TABLE_ONE;
  }

  public WordTableVO(XWPFTable xwpfTable, int startNum, int RepeatSize, Map<String, String> params){
    this.xwpfTable = xwpfTable;
    this.startNum = startNum;
    this.RepeatSize = RepeatSize;
    this.params = params;
    this.indexKey = PoiWordUtils.addRowRepeatFlag;
    tableType = TABLE_MORE;
  }

  /**
   * 正常表格处理顺序
   * @throws Exception
   */
  public void replaceInAddRowTable() throws Exception {
    getRowValesAndTableSet();
    getRowValueArray();
    addrow();
    removeRow();
  }
  /** 正文处理代码 **/
//  XWPFParagraph：代表一个段落。
//
//  XWPFRun：代表具有相同属性的一段文本。
//
//  XWPFTable：代表一个表格。
//
//  XWPFTableRow：表格的一行。
//
//  XWPFTableCell：表格对应的一个单元格。
  static int aa = 1;
  public static void getParagraph(List<XWPFParagraph> ParagraphArray) throws Exception {
    paramsField = new HashMap<> ();
    paramsField.put("${xtbh}","测试先");
    List<XWPFRun> runs;
    XWPFRun run;
    XWPFRun beginRun = null;
    String runText;
    StringBuilder runSBD = new StringBuilder();
    List removeNumArray ;
    for (XWPFParagraph P : ParagraphArray) {
      runs= P.getRuns();
      removeNumArray = new ArrayList();
      for (int i = 0; i < runs.size(); i++) {
        run = runs.get(i);
        runText = String.valueOf(run);

        if(runSBD != null && runSBD.length()>0){
          removeNumArray.add(i);
          runSBD.append(runText);
        }

        if(runText.contains(PoiWordUtils.PLACEHOLDER_PREFIX)){
          aa =2;
          beginRun = run;
          runSBD.append(runText);
        }else if(beginRun !=null && runText.contains(PoiWordUtils.PLACEHOLDER_END)){

          beginRun.setText("**"+getStaticNewCellText(runSBD.toString(),paramsField)+"**",0);

          beginRun = null;
          runSBD = new StringBuilder();

        }

      }

      for (int i = removeNumArray.size(); i > 0 ; i--) {
        aa = 3;
        P.removeRun((Integer) removeNumArray.get(i-1));
      }

      if(aa == 3){
        return ;
      }

    }
  }


  /** 表格处理代码 **/
  /**
   * 获取单次重复区间 重复信息以便服用
   * 获取单次重复区间 包含表名以便获取数据
   */
  private void getRowValesAndTableSet(){
    List<XWPFTableRow> rows = xwpfTable.getRows();
    XWPFTableRow row;
    List<XWPFTableCell> cells;
    //获取每单元格字段信息
    JSONArray rowText;
    String fieldstr;
    String tableNamestr;
    for (int i = 0; i < RepeatSize; i++) {
      row = rows.get(startNum+i);
      cells = row.getTableCells();
      rowText = new JSONArray();
      for (XWPFTableCell cell : cells) {
        rowText.add(cell.getText());
        if(cell.getText().indexOf(indexKey) >= 0){
          //默认每单元格仅有一个字段，故只获取一次字段信息即可
          fieldstr = cell.getText().substring(cell.getText().indexOf(indexKey)+indexKey.length(),cell.getText().indexOf("}"));
          if(fieldstr.indexOf("[")> -1){
            Repeatstr = fieldstr.substring(fieldstr.indexOf("["),fieldstr.indexOf("]")+1);
            fieldstr = fieldstr.substring(0,fieldstr.indexOf("["));
          }
          tableNamestr = fieldstr.substring(0,fieldstr.indexOf("."));
          tableNameSet.add(tableNamestr);
        }
      }
      rowValues.add(rowText);
    }
  }

  /**
   * 组装表数据地图以进行数据替换
   * @throws Exception
   */
  private void getRowValueArray() throws Exception {
    Map<String,String> tableMap;
    JSONArray tableArray ;
    JSONObject tableobj ;
    //默认只有一个，若有多个应当额外处理
    if(tableNameSet.size()>1){
      throw new Exception("tableNameSet长度超出默认值1！"+tableNameSet.toString());
    }
    for (String s : tableNameSet) {
      tableArray = JSONArray.parseArray(params.get(s));
      for (int i = 0; i < tableArray.size(); i++) {
        tableobj = tableArray.getJSONObject(i);
        tableMap = new HashMap();
        //组装键值对应
        for (Map.Entry entry:tableobj.entrySet()) {
          tableMap.put(indexKey+s+"."+entry.getKey()+Repeatstr+"}", String.valueOf(entry.getValue()));
        }
        rowValuesArray.add(tableMap);
      }
    }
  }

  /**
   * 重复创建列表行
   * @throws Exception
   */
  private void addrow() throws Exception {
    Map<String,String> tableMap ;
    JSONArray cellsArray = new JSONArray();
    String cell_text ;
    List<JSONArray> rowValuesCopy ;
    //每次循环为一次重复行
    for (int i = 0; i < rowValuesArray.size(); i++) {
      //数据准备完毕
      tableMap = rowValuesArray.get(i);
      rowValuesCopy = rowValues;

      for (int i1 = 0; i1 < rowValuesCopy.size(); i1++) {
        cellsArray = rowValuesCopy.get(i1);
        XWPFTableRow row = xwpfTable.insertNewTableRow(startNum+RepeatSize*(i+1)+i1);//添加一个新行 在模板行后添加
        //每次循环为一个单元格
        for (int i2 = 0; i2 < cellsArray.size(); i2++) {
          cell_text = cellsArray.getString(i2);
          XWPFTableCell cell = row.createCell();
          System.out.println(i1+"-"+i2+"-"+getNewCellText(cell_text,tableMap)+"-"+"-"+"-"+"-"+(startNum+RepeatSize+i));
          cell.setText(getNewCellText(cell_text,tableMap));
        }

      }

    }
  }


  private String getNewCellText(String cell_text , Map<String,String> tableMap ) throws Exception {
    int  addRowFlag_Num = cell_text.indexOf(PoiWordUtils.addRowFlag);
    int  addRowRepeatFlag_Num = cell_text.indexOf(PoiWordUtils.addRowRepeatFlag);
    int  PLACEHOLDER_PREFIX_Num = cell_text.indexOf(PoiWordUtils.PLACEHOLDER_PREFIX);
    int  PLACEHOLDER_END_Num = cell_text.indexOf(PoiWordUtils.PLACEHOLDER_END);

    String fieldKey;
    String resultStr = cell_text;
    if(addRowFlag_Num>-1
        && PLACEHOLDER_END_Num>addRowFlag_Num){
      //存在正常的单行标识
      fieldKey = cell_text.substring(addRowFlag_Num,PLACEHOLDER_END_Num+1);
      resultStr = cell_text.replace(fieldKey,tableMap.get(fieldKey))  ;
    }else if(addRowRepeatFlag_Num>-1
        && PLACEHOLDER_END_Num>addRowRepeatFlag_Num){
      //正常的多行标识
      fieldKey = cell_text.substring(addRowRepeatFlag_Num,PLACEHOLDER_END_Num+1);
      resultStr = cell_text.replace(fieldKey,tableMap.get(fieldKey))  ;
    }else if(PLACEHOLDER_PREFIX_Num>-1
        && PLACEHOLDER_END_Num>PLACEHOLDER_PREFIX_Num){
      //正常普通替换标识
      fieldKey = cell_text.substring(PLACEHOLDER_PREFIX_Num,PLACEHOLDER_END_Num+1);
      resultStr = cell_text.replace(fieldKey,params.get(fieldKey))  ;
    }else if( PLACEHOLDER_PREFIX_Num>-1 && PLACEHOLDER_END_Num ==-1 ){
      //不正常替换标识 提示
      throw new Exception("解析存在问题，需要添加段落处理功能");
    }else{
      //不用处理
    }
    return resultStr;

  }

  private static String getStaticNewCellText(String cell_text , Map<String,String> tableMap ) throws Exception {
    int  addRowFlag_Num = cell_text.indexOf(PoiWordUtils.addRowFlag);
    int  addRowRepeatFlag_Num = cell_text.indexOf(PoiWordUtils.addRowRepeatFlag);
    int  PLACEHOLDER_PREFIX_Num = cell_text.indexOf(PoiWordUtils.PLACEHOLDER_PREFIX);
    int  PLACEHOLDER_END_Num = cell_text.indexOf(PoiWordUtils.PLACEHOLDER_END);

    String fieldKey;
    String resultStr = cell_text;
    if(addRowFlag_Num>-1
        && PLACEHOLDER_END_Num>addRowFlag_Num){
      //存在正常的单行标识
      fieldKey = cell_text.substring(addRowFlag_Num,PLACEHOLDER_END_Num+1);
      resultStr = cell_text.replace(fieldKey,tableMap.get(fieldKey))  ;
    }else if(addRowRepeatFlag_Num>-1
        && PLACEHOLDER_END_Num>addRowRepeatFlag_Num){
      //正常的多行标识
      fieldKey = cell_text.substring(addRowRepeatFlag_Num,PLACEHOLDER_END_Num+1);
      resultStr = cell_text.replace(fieldKey,tableMap.get(fieldKey))  ;
    }else if(PLACEHOLDER_PREFIX_Num>-1
        && PLACEHOLDER_END_Num>PLACEHOLDER_PREFIX_Num){
      //正常普通替换标识
      fieldKey = cell_text.substring(PLACEHOLDER_PREFIX_Num,PLACEHOLDER_END_Num+1);
      resultStr = cell_text.replace(fieldKey,tableMap.get(fieldKey))  ;
    }else if( PLACEHOLDER_PREFIX_Num>-1 && PLACEHOLDER_END_Num ==-1 ){
      //不正常替换标识 提示
      throw new Exception("解析存在问题，需要添加段落处理功能");
    }else{
      //不用处理
    }
    return resultStr;

  }

  /**
   * 删除行
   */
  private void removeRow(){
    for (int i = 0; i < RepeatSize; i++) {
      //删除原模板列
      xwpfTable.removeRow(startNum);
    }
  }



}
