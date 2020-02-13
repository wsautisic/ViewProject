package com.example.demo.words;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

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
  //重复区间行值信息 源文本数据
  List<JSONArray> rowValues = new ArrayList<>();
  //包含表名  正常来说每个表格只对应一个子表，若有不同再进行扩展
  Set<String> tableNameSet = new HashSet();
  //组装本列表对象所需要的信息  需放入的数据
  List<Map<String,String>> rowValuesArray = new ArrayList<>();

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
    tableType = TABLE_ONE;
  }

  public WordTableVO(XWPFTable xwpfTable, int startNum, int RepeatSize, Map<String, String> params){
    this.xwpfTable = xwpfTable;
    this.startNum = startNum;
    this.RepeatSize = RepeatSize;
    this.params = params;
    tableType = TABLE_MORE;
  }

  public void replaceInAddRowTable() throws Exception {
    getRowValesAndTableSet();
    getRowValueArray();
    addrow();
    removeRow();
  }
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
        if(cell.getText().indexOf("${tb") >= 0){
          //默认每单元格仅有一个字段，故只获取一次字段信息即可
          fieldstr = cell.getText().substring(cell.getText().indexOf("${tbAddRow:")+11,cell.getText().indexOf("}"));
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
          tableMap.put("${tbAddRow:"+s+"."+entry.getKey()+"}", String.valueOf(entry.getValue()));
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
        XWPFTableRow row = xwpfTable.insertNewTableRow(startNum+RepeatSize+i);//添加一个新行 在模板行后添加
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
      fieldKey = cell_text.substring(addRowFlag_Num,PLACEHOLDER_END_Num+1);
      resultStr = cell_text.replace(fieldKey,tableMap.get(fieldKey))  ;
    }else if(PLACEHOLDER_PREFIX_Num>-1
        && PLACEHOLDER_END_Num>PLACEHOLDER_PREFIX_Num){
      //正常普通替换标识
      fieldKey = cell_text.substring(addRowFlag_Num,PLACEHOLDER_END_Num+1);
      resultStr = cell_text.replace(fieldKey,params.get(fieldKey))  ;
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
