package com.example.demo.words;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xwpf.usermodel.*;

import java.lang.reflect.Array;
import java.util.*;

/**
 * 替换标识为  '${'+'重复类型'+':'+'子表名'+'.'+'字段名'+'['+'起始行数'+','+'结束行数'+']'+'}'
 * 重复类型为 tbAddRowRepeat/tbAddRow   没有则不填，且不再需要':'分割
 * 子表名                               没有则不填，且不再需要'.'分割
 * 字段名    即参数名                    必填
 * 起始行数与结束行数   以下标计数，第一行为 0行一次类推  没有则不填，且不再需要'[...]'
 */
public class WordTableVO {
  //第一行行号 当想更改输入行号时，可将此处改为1
  int beginNum = 0;
  //表格类型-单行重复  ${tbAddRow:wordtable1.cell1}
  private final static String TABLE_ONE = "ONE";
  //表格类型-多行重复  ${tbAddRowRepeat:wordTable2.Cell1[1,2]}
  private final static String TABLE_MORE = "MORE";
  //表格类型-不重复    ${contCode}
  private final static String TABLE_NO = "NO";
  //WORD文本          ${contCode}
  private final static String WORD_TEXT = "TEXT";
  //列表对象
   XWPFTable xwpfTable;
  //word正文集合
  List<XWPFParagraph> ParagraphArray;
  //表格类型
  String tableType;
  //重复开始行数 起始行数为0
  int startNum ;
  //重复区间大小
  int RepeatSize = 1;
//  //计算行号时应修正数字
//  int calRowNum = 1 - beginNum;
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



  public WordTableVO(List<XWPFParagraph> ParagraphArray, Map<String, String> params){
    this.ParagraphArray = ParagraphArray;
//    this.startNum = -1;
//    this.RepeatSize = 0;
    this.params = params;
    tableType = WORD_TEXT;
  }

  public WordTableVO(XWPFTable xwpfTable, Map<String, String> params){
    this.xwpfTable = xwpfTable;
    this.startNum = -1;
    this.RepeatSize = 0;
    this.params = params;
    this.indexKey = PoiWordUtils.PLACEHOLDER_PREFIX;
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
   * 自动识别为哪种表格
   * @param xwpfTable
   * @param params
   * @return 当判断不出来'${'之后即会返回null
   */
  public static WordTableVO getTableVO(XWPFTable xwpfTable, Map<String, String> params) throws Exception {
    WordTableVO wordtableVO = null;
    int startNum;
    int repeatSize;
    try {
      if(PoiWordUtils.isAddRowRepeat(xwpfTable)){
        startNum = PoiWordUtils.findStartNum(xwpfTable,PoiWordUtils.addRowRepeatFlag);

        repeatSize = PoiWordUtils.findRepeatSize(xwpfTable,PoiWordUtils.addRowRepeatFlag,startNum);

        wordtableVO = new WordTableVO(xwpfTable,PoiWordUtils.findStartNum(xwpfTable,PoiWordUtils.addRowRepeatFlag),repeatSize,params);
      }else if(PoiWordUtils.isAddRow(xwpfTable)){
        startNum = PoiWordUtils.findStartNum(xwpfTable,PoiWordUtils.addRowFlag);
        wordtableVO =  new WordTableVO(xwpfTable,startNum,params);
      }else if(PoiWordUtils.isAddText(xwpfTable)){
        wordtableVO =  new WordTableVO(xwpfTable,params);
      }
    }catch (ArrayIndexOutOfBoundsException e){
      throw new Exception("未查到相关标识！");
    }
    return wordtableVO;
  }



  /**
   * 正常表格处理顺序
   * @throws Exception
   */
  public void replaceInAddRowTable() throws Exception {
    getRepeatAndTableSet();
    getParamsFieldsMap();
    copyRow();
    replaceTable();
  }

  /**
   * 正常文本处理顺序
   * @throws Exception
   */
  public void replaceText() throws Exception {
    //构建参数地图
    getParamsFieldsMap();
    //替换
    getParagraph(ParagraphArray,paramsField);
  }

  /**
   * 构建替换参数map
   */
  public void getParamsFieldsMap() throws Exception {
    String beforeKey ;
    String AfterKey = PoiWordUtils.PLACEHOLDER_END;

    switch (tableType){
      case TABLE_ONE:
//        beforeKey = PoiWordUtils.addRowFlag;
        getRowValueArray();
        break;
      case TABLE_MORE:
//        beforeKey = PoiWordUtils.addRowRepeatFlag;
        getRowValueArray();
        break;
      default :
        paramsField = new HashMap<>();
        beforeKey = PoiWordUtils.PLACEHOLDER_PREFIX;
        String Key ;
        for (Map.Entry<String, String> Entry : params.entrySet()) {
          Key = Entry.getKey().replace(" ","");
          paramsField.put(beforeKey+Key+AfterKey,Entry.getValue());
        }
    }

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

  public void getParagraph(List<XWPFParagraph> ParagraphArray,Map<String, String> valueMap) throws Exception {
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
          beginRun = run;
          runSBD.append(runText);
        }else if(beginRun !=null && runText.contains(PoiWordUtils.PLACEHOLDER_END)){
          beginRun.setText(getNewCellText(runSBD.toString(),valueMap),0);
          beginRun = null;
          runSBD = new StringBuilder();
        }
      }

      for (int i = removeNumArray.size(); i > 0 ; i--) {
        P.removeRun((Integer) removeNumArray.get(i-1));
      }
    }
  }

  /**
   * 进行字符串替换
   * @param cell_text
   * @param tableMap
   * @return
   * @throws Exception
   */
  private String getNewCellText(String cell_text , Map<String,String> tableMap ) throws Exception {
    int  addRowFlag_Num = cell_text.indexOf(PoiWordUtils.addRowFlag);
    int  addRowRepeatFlag_Num = cell_text.indexOf(PoiWordUtils.addRowRepeatFlag);
    int  PLACEHOLDER_PREFIX_Num = cell_text.indexOf(PoiWordUtils.PLACEHOLDER_PREFIX);
    int  PLACEHOLDER_END_Num = cell_text.indexOf(PoiWordUtils.PLACEHOLDER_END);

    String fieldKey = null;
    String resultStr = cell_text;
    if(addRowFlag_Num>-1
        && PLACEHOLDER_END_Num>addRowFlag_Num){
      //存在正常的单行标识
      fieldKey = cell_text.substring(addRowFlag_Num,PLACEHOLDER_END_Num+1);

    }else if(addRowRepeatFlag_Num>-1
        && PLACEHOLDER_END_Num>addRowRepeatFlag_Num){
      //正常的多行标识
      fieldKey = cell_text.substring(addRowRepeatFlag_Num,PLACEHOLDER_END_Num+1);

    }else if(PLACEHOLDER_PREFIX_Num>-1
        && PLACEHOLDER_END_Num>PLACEHOLDER_PREFIX_Num){
      //正常普通替换标识
      fieldKey = cell_text.substring(PLACEHOLDER_PREFIX_Num,PLACEHOLDER_END_Num+1);

    }else if( PLACEHOLDER_PREFIX_Num>-1 && PLACEHOLDER_END_Num ==-1 ){
      //不正常替换标识 提示
      throw new Exception("解析存在问题，需要添加段落处理功能");
    }else{
      //不用处理
    }

    if(fieldKey!= null){
      //去除空格
      fieldKey = fieldKey.replace(" ","");
      if(tableMap.containsKey(fieldKey)){
        resultStr = cell_text.replace(fieldKey,tableMap.get(fieldKey))  ;
      }
    }
    return resultStr;

  }


  /** 表格处理代码 **/
  /**
   * 获取单次重复区间 重复信息以便服用
   * 获取单次重复区间 包含表名以便获取数据
   * Repeatstr  多行重复时，重复行数信息
   * tableNameSet 设置的字段名
   *
   */
  private void getRepeatAndTableSet(){
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
//      rowText = new JSONArray();
      for (XWPFTableCell cell : cells) {
//        rowText.add(cell.getText());
        if(cell.getText().indexOf(indexKey) >= 0){
          //默认每单元格仅有一个字段，故只获取一次字段信息即可
          fieldstr = cell.getText().substring(cell.getText().indexOf(indexKey)+indexKey.length(),cell.getText().indexOf("}"));
          if(fieldstr.indexOf("[")> -1){
            Repeatstr = fieldstr.substring(fieldstr.indexOf("["),fieldstr.indexOf("]")+1);
            fieldstr = fieldstr.substring(0,fieldstr.indexOf("["));
          }
          tableNamestr = fieldstr.substring(0,fieldstr.indexOf("."));
          tableNameSet.add(tableNamestr);
          return;
        }
      }
//      rowValues.add(rowText);
    }
  }

  /**
   * 组装表数据地图以进行数据替换
   * 多行数据必须用rowValuesArray 存储
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
  private void copyRow() {
    List<XWPFTableRow> rows = xwpfTable.getRows();
    XWPFTableRow row;
    XWPFTableCell addCell;
    XWPFTableRow addrow;
    //每次循环为一次重复行 从一开始是将范本行直接用了，所以省去了删除操作
    for (int i = 1; i < rowValuesArray.size(); i++) {
      for (int i1 = 0; i1 < RepeatSize; i1++) {
        row = rows.get(startNum-beginNum+i1);
        addrow = xwpfTable.insertNewTableRow(startNum-beginNum+RepeatSize*i+i1);//添加一个新行 在模板行后添加

        copyTableRow(addrow,row,null);

//        for (XWPFTableCell tableCell : row.getTableCells()) {
//          addCell = addrow.createCell();
//          for (XWPFParagraph paragraph : tableCell.getParagraphs()) {
//            addCell.insertNewParagraph(paragraph.getCTP().newCursor());
//
////            addCell.setParagraph(paragraph);
////            addCell.insertNewParagraph()
//          }
//        }
      }
    }
  }

  /**
   * 功能描述:复制行，从source到target
   *
   * @param target
   * @param source
   * @param index
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  public void copyTableRow(XWPFTableRow target, XWPFTableRow
      source, Integer index) {
    // 复制样式
    if (source.getCtRow() != null) {
      target.getCtRow().setTrPr(source.getCtRow().getTrPr());
    }
    // 复制单元格
    for (int i = 0; i < source.getTableCells().size(); i++) {
      XWPFTableCell cell1 = target.getCell(i);
      XWPFTableCell cell2 = source.getCell(i);
      if (cell1 == null) {
        cell1 = target.addNewTableCell();
      }
      copyTableCell(cell1, cell2, index);
    }
  }
  /**
   * 功能描述:复制单元格，从source到target
   *
   * @param target
   * @param source
   * @param index
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  public void copyTableCell(XWPFTableCell target,
                                   XWPFTableCell source, Integer index) {
    // 列属性
    if (source.getCTTc() != null) {
      target.getCTTc().setTcPr(source.getCTTc().getTcPr());
    }
    // 删除段落
    for (int pos = 0; pos < target.getParagraphs().size(); pos++) {
      target.removeParagraph(pos);
    }
    // 添加段落
    for (XWPFParagraph sp : source.getParagraphs()) {
      XWPFParagraph targetP = target.addParagraph();
      copyParagraph(targetP, sp, index);
    }
  }
  /**
   * 功能描述:复制段落，从source到target
   *
   * @param target
   * @param source
   * @param index
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  public void copyParagraph(XWPFParagraph target,
                                   XWPFParagraph source, Integer index) {
    // 设置段落样式
    target.getCTP().setPPr(source.getCTP().getPPr());
    // 移除所有的run
    for (int pos = target.getRuns().size() - 1; pos >= 0; pos--) {
      target.removeRun(pos);
    }
    // copy 新的run
    for (XWPFRun s : source.getRuns()) {
      XWPFRun targetrun = target.createRun();
      copyRun(targetrun, s, index);
    }
  }
  /**
   * 功能描述:复制RUN，从source到target
   *
   * @param target
   * @param source
   * @param index
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  public void copyRun(XWPFRun target, XWPFRun source, Integer
      index) {
    // 设置run属性
    target.getCTR().setRPr(source.getCTR().getRPr());
//    // 设置文本
//    String tail = "";
//    if (index != null) {
//      tail = index.toString();
//    }
    target.setText(source.text());
  }





  public void replaceTable() throws Exception {
    List<XWPFTableRow> rows = xwpfTable.getRows();
    Map<String,String> tableMap ;
    XWPFTableRow row;
    if(rowValuesArray.size() > 0){
      for (int i = 0; i < rowValuesArray.size(); i++) {
        tableMap = rowValuesArray.get(i);
        for (int i1 = 0; i1 < RepeatSize; i1++) {
          row = rows.get(startNum+RepeatSize*i+i1);

          for (XWPFTableCell tableCell : row.getTableCells()) {
            getParagraph(tableCell.getParagraphs(),tableMap);
          }

        }
      }
    }
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
