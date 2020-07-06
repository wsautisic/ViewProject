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
  public final static  String isContCode = "^[a-zA-Z0-9\\./]+-?[a-zA-Z0-9]+$";
  /** 合同id字段校验  **/
  public final static  String isContBody = "^[0-9]+$";
  /** contTableName合同表名字段校验 **/
  public final static  String isContType = "^\\w+$";
  /** 名称字段校验 **/
  public final static  String isName = "^[^%<>]+$";
  /** busiKey字段校验 **/
  public final static  String isBusiKey = "^[0-9a-zA-Z_-]+$";
  /** isChangeType字段校验 **/
  public final static  String isChangeType = "^[\\u4e00-\\u9fa5]{0,8}$";
  /** LinuxTime时间戳字段校验 **/
  public final static  String isLinuxTime = "^[1-9]\\d*$";
  /** DateString日期字符串字段校验 **/
  public final static  String isDateString = "^[1-9\\-]*$";
  /** DateString日期字符串字段校验 **/
  public final static  String isFilePath = "^[a-zA-z0-9/\\.]*$";
  /** 系统来源字段校验 **/
  public final static  String isOriginSystem = "^[a-zA-Z0-9,]*$";
  /** 合同状态id字段校验 **/
  public final static  String isStateId = "^[0-9]{0,2}$";


  /**
   *
   * @param regex
   * @param str
   * @return
   * true:  符合匹配规则
   * false: 不符合匹配规则
   */
  public static boolean isCorrectString(String regex,String str){
    return Pattern.matches(regex, str.trim());
  }

}
