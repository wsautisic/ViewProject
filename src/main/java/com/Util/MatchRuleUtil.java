package com.Util;

import java.util.regex.Pattern;

public class MatchRuleUtil {

  /**  包含数字   **/
  public final static  String isHaveMath = ".*[0-9]+.*";
  /**  包含大写字母   **/
  public final static  String isHaveUpperCase = ".*[A-Z]+.*";
  /**  包含小写字母   **/
  public final static  String isHaveLowCase = ".*[a-z]+.*";
  /**  包含特殊符号   **/
  public final static  String isHavePunctuation = ".*[^a-zA-Z0-9]+.*";

  /** 合同编码字段校验  **/
  public final static  String isContCode = "^[a-zA-Z0-9]+-?[a-zA-Z0-9]+$";
  /** 合同id字段校验  **/
  public final static  String isContBody = "^[0-9]+$";
  /** contTableName合同表名字段校验 **/
  public final static  String isContType = "^\\w+$";

  /**
   *
   * @param regex
   * @param str
   * @return
   * true:  符合匹配规则
   * false: 不符合匹配规则
   */
  public static boolean isCorrectString(String regex,String str){
    return Pattern.matches(regex, str);
  }

}
