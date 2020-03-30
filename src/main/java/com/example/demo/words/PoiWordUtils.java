package com.example.demo.words;

import com.check.DataCheck;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Create by IntelliJ Idea 2018.2
 *
 * @author: qyp
 * Date: 2019-10-26 2:12
 */
public class PoiWordUtils {

  /**
   * 占位符第一个字符
   */
  public static final String PREFIX_FIRST = "$";

  /**
   * 占位符第二个字符
   */
  public static final String PREFIX_SECOND = "{";

  /**
   * 占位符的前缀
   */
  public static final String PLACEHOLDER_PREFIX = PREFIX_FIRST + PREFIX_SECOND;

  /**
   * 占位符后缀
   */
  public static final String PLACEHOLDER_END = "}";

  /**
   * 表格中需要动态添加行的独特标记
   */
  public static final String addRowText = "tbAddRow:";

  public static final String addRowRepeatText = "tbAddRowRepeat:";
  /**
   * 添加图片的独特标记
   */
  public static final String addPicture = "tbPicture:";

  /**
   * 表格中占位符的开头 ${tbAddRow:  例如${tbAddRow:table1.tb1}
   */
  public static final String addRowFlag = PLACEHOLDER_PREFIX + addRowText;

  /**
   * 表格中占位符的开头 ${tbAddRowRepeat:  例如 ${tbAddRowRepeat:table1.tb1[1,2]} 第(1+1)行到第(2+1)行(第一行为第0行,根据数组下标计数) 为模板样式
   */
  public static final String addRowRepeatFlag = PLACEHOLDER_PREFIX + addRowRepeatText;
  /**
   * 表格中占位符的开头 ${tbAddRow:  例如${tbAddRow:table1.tb1}
   */
  public static final String addPictureFlag = PLACEHOLDER_PREFIX + addPicture;

  /**
   * 重复矩阵的分隔符  比如：${tbAddRowRepeat:0,2,0,1} 分隔符为 ,
   */
  public static final String tbRepeatMatrixSeparator = ",";

  /**
   * 占位符的后缀
   */
  public static final String PLACEHOLDER_SUFFIX = "}";

  /**
   * 图片占位符的前缀
   */
  public static final String PICTURE_PREFIX = PLACEHOLDER_PREFIX + "image:";
  /** 匹配是否动态列表的正则 **/
  public final static  String isAddRow = ".*\\$\\{tbAddRow:.*\\..*\\}.*";



  /**
   * 判断当前表格是不是标志表格中需要添加行
   *
   * @param xwpfTable
   * @return
   */
  public static boolean isAddRow(XWPFTable xwpfTable) {
    return isDynRow(xwpfTable, addRowFlag);
  }
  /**
   * 判断当前表格是不是标志表格中需要添加行
   *
   * @param xwpfTable
   * @return
   */
  public static boolean isAddText(XWPFTable xwpfTable) {
    return isDynRow(xwpfTable, PLACEHOLDER_PREFIX);
  }
  /**
   * 判断当前行是不是标志表格中需要添加行
   *
   * @param row
   * @return
   */
  public static boolean isAddRow(XWPFTableRow row) {
    return isDynRow(row, addRowFlag);
  }
  /**
   * 添加重复模板动态行(以多行为模板)
   * @param xwpfTable
   * @return
   */
  public static boolean isAddRowRepeat(XWPFTable xwpfTable) {
    return isDynRow(xwpfTable, addRowRepeatFlag);
  }

  /**
   * 添加重复模板动态行(以多行为模板)
   * @param row
   * @return
   */
  public static boolean isAddRowRepeat(XWPFTableRow row) {
    return isDynRow(row, addRowRepeatFlag);
  }

  /**
   * 判断当前行第一个格内是否包含dynFlag
   * @param row
   * @param dynFlag
   * @return
   */
  private static boolean isDynRow(XWPFTableRow row, String dynFlag) {
    if (row == null) {
      return false;
    }
    List<XWPFTableCell> tableCells = row.getTableCells();
    if (tableCells != null) {
      XWPFTableCell cell = tableCells.get(0);
      if (cell != null) {
        String text = cell.getText();
        return text != null && text.startsWith(dynFlag);
      }
    }
    return false;
  }

  /**
   * 判断表格内是否包含dynFlag
   * @param xwpfTable
   * @param dynFlag
   * @return
   */
  private static boolean isDynRow(XWPFTable xwpfTable, String dynFlag) {
    boolean isDynRow = false;
    if (xwpfTable == null|| DataCheck.isNull(dynFlag)) {
      return isDynRow;
    }
    String tableText = xwpfTable.getText();
    if(DataCheck.isNull(tableText)){
      return isDynRow;
    }
    if(tableText.contains(dynFlag)){
      isDynRow = true;
    }
    return isDynRow;
  }



  /**
   * 从参数map中获取占位符对应的值
   *
   * @param paramMap
   * @param key
   * @return
   */
  public static Object getValueByPlaceholder(Map<String, Object> paramMap, String key) {
    if (paramMap != null) {
      if (key != null) {
        return paramMap.get(getKeyFromPlaceholder(key));
      }
    }
    return null;
  }

  /**
   * 后去占位符的重复行列矩阵
   * @param key 占位符
   * @return {0,2,0,1}
   */
  public static String getTbRepeatMatrix(String key) {
    Assert.notNull(key, "占位符为空");
    String $1 = key.replaceAll("\\" + PREFIX_FIRST + "\\" + PREFIX_SECOND + addRowRepeatText + "(.*:)(.*)" + "\\" + PLACEHOLDER_SUFFIX, "$2");
    return $1;
  }

  /**
   * 从占位符中获取key
   *
   * @return
   */
  public static String getKeyFromPlaceholder(String placeholder) {
    return Optional.ofNullable(placeholder).map(p -> p.replaceAll("[\\$\\{\\}]", "")).get();
  }

  public static void main(String[] args) {
    String s = "${aa}";
    s = s.replaceAll(PLACEHOLDER_PREFIX + PLACEHOLDER_SUFFIX , "");
    System.out.println(s);
//        String keyFromPlaceholder = getKeyFromPlaceholder("${tbAddRow:tb1}");
//        System.out.println(keyFromPlaceholder);
  }

  /**
   * 复制列的样式，并且设置值
   * @param sourceCell
   * @param targetCell
   * @param text
   */
  public static void copyCellAndSetValue(XWPFTableCell sourceCell, XWPFTableCell targetCell, String text) {
    //段落属性
    List<XWPFParagraph> sourceCellParagraphs = sourceCell.getParagraphs();
    if (sourceCellParagraphs == null || sourceCellParagraphs.size() <= 0) {
      return;
    }
    XWPFParagraph sourcePar = sourceCellParagraphs.get(0);
    XWPFParagraph targetPar = targetCell.getParagraphs().get(0);

    // 设置段落的样式
    targetPar.getCTP().setPPr(sourcePar.getCTP().getPPr());


    List<XWPFRun> sourceParRuns = sourcePar.getRuns();
    if (sourceParRuns != null && sourceParRuns.size() > 0) {
      // 如果当前cell中有run
      List<XWPFRun> runs = targetPar.getRuns();
      Optional.ofNullable(runs).ifPresent(rs -> rs.stream().forEach(r -> r.setText("", 0)));
      if (runs != null && runs.size() > 0) {
        runs.get(0).setText(text, 0);
      } else {
        XWPFRun cellR = targetPar.createRun();
        cellR.setText(text, 0);
        // 设置列的样式位模板的样式
        targetCell.getCTTc().setTcPr(sourceCell.getCTTc().getTcPr());
      }
      setTypeface(sourcePar, targetPar);
    } else {
      // targetCell.setText(text);
      List<XWPFRun> runs = targetPar.getRuns();
      if (runs != null && runs.size() > 0) {
        runs.get(0).setText(text, 0);
      } else {
        XWPFRun newRun = targetPar.createRun();
        newRun.setText(text, 0);
      }
    }
  }

  /**
   * 复制字体
   */
  private static void setTypeface(XWPFParagraph sourcePar, XWPFParagraph targetPar) {
    XWPFRun sourceRun = sourcePar.getRuns().get(0);
    String fontFamily = sourceRun.getFontFamily();
    //int fontSize = sourceRun.getFontSize();
    String color = sourceRun.getColor();
//        String fontName = sourceRun.getFontName();
    boolean bold = sourceRun.isBold();
    boolean italic = sourceRun.isItalic();
    int kerning = sourceRun.getKerning();
//        String style = sourcePar.getStyle();
    UnderlinePatterns underline = sourceRun.getUnderline();

    XWPFRun targetRun = targetPar.getRuns().get(0);
    targetRun.setFontFamily(fontFamily);
//        targetRun.setFontSize(fontSize == -1 ? 10 : fontSize);
    targetRun.setBold(bold);
    targetRun.setColor(color);
    targetRun.setItalic(italic);
    targetRun.setKerning(kerning);
    targetRun.setUnderline(underline);
    //targetRun.setFontSize(fontSize);
  }
  /**
   * 判断文本中时候包含$
   * @param text 文本
   * @return 包含返回true,不包含返回false
   */
  public static boolean checkText(String text){
    boolean check  =  false;
    if(text.indexOf(PLACEHOLDER_PREFIX)!= -1){
      check = true;
    }
    return check;
  }

  /**
   * 获得占位符替换的正则表达式
   * @return
   */
  public static String getPlaceholderReg(String text) {
    return "\\" + PREFIX_FIRST + "\\" + PREFIX_SECOND + text + "\\" + PLACEHOLDER_SUFFIX;
  }

  public static String getDocKey(String mapKey) {
    return PLACEHOLDER_PREFIX + mapKey + PLACEHOLDER_SUFFIX;
  }

  /**
   * 判断当前占位符是不是一个图片占位符
   * @param text
   * @return
   */
  public static boolean isPicture(String text) {
    return text.startsWith(PICTURE_PREFIX);
  }

  /**
   * 删除一行的列
   * @param row
   */
  public static void removeCells(XWPFTableRow row) {
    int size = row.getTableCells().size();
    try {
      for (int i = 0; i < size; i++) {
        row.removeCell(i);
      }
    } catch (Exception e) {

    }
  }

  /**
   * 获取范本重复行所在行数
   * @param xwpfTable
   * @param str
   * @return
   */
  public static int findStartNum(XWPFTable xwpfTable, String str){
//    int startNum = -1;
    List<XWPFTableRow> rows = xwpfTable.getRows();
    List<XWPFTableCell> cells ;
    XWPFTableRow row;
    for (int i = 0; i < rows.size(); i++) {
      row = rows.get(i);
      cells = row.getTableCells();

      for (XWPFTableCell cell : cells) {
        if(cell.getText().indexOf(str)>-1){

          return i;
        }
      }
    }

    return -1;
  }

  /**
   * ${tbAddRowRepeat:table1.tb1[1,2]}
   * 计算重复区间包含行数
   * @param xwpfTable
   * @param str
   * @param StartNum
   * @return
   */
  public static int findRepeatSize(XWPFTable xwpfTable, String str, int StartNum){
    int RepeatSize = -1;

    List<XWPFTableRow> rows = xwpfTable.getRows();
    List<XWPFTableCell> cells ;
    XWPFTableRow row;

    row = rows.get(StartNum);
    cells = row.getTableCells();
    String cell_text ;
    String[] rowNumStrs;
    //依然默认一格仅一个字段
    for (XWPFTableCell cell : cells) {
      cell_text = cell.getText();
      int  addRowRepeatFlag_Num = cell_text.indexOf(PoiWordUtils.addRowRepeatFlag);
//      int  PLACEHOLDER_PREFIX_Num = cell_text.indexOf(PoiWordUtils.PLACEHOLDER_PREFIX);
      int  PLACEHOLDER_END_Num = cell_text.indexOf(PoiWordUtils.PLACEHOLDER_END);
      if(addRowRepeatFlag_Num>-1
          && PLACEHOLDER_END_Num>addRowRepeatFlag_Num){
        cell_text = cell_text.substring(cell_text.indexOf(PoiWordUtils.addRowRepeatFlag),cell_text.indexOf(PoiWordUtils.PLACEHOLDER_END)+1);
        cell_text = cell_text.substring(cell_text.indexOf("[")+1,cell_text.indexOf("]"));
        cell_text = cell_text.replace(" ","");
        rowNumStrs = cell_text.split(",");
        RepeatSize = Integer.valueOf(rowNumStrs[1]) - Integer.valueOf(rowNumStrs[0]) +1 ;
      }
    }

    return RepeatSize;
  }



}