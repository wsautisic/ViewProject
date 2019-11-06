package com.check;

import org.omg.CORBA.portable.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

public class DataCheck {
  private static final Logger logger = LoggerFactory.getLogger(DataCheck.class);
  private DataCheck() {

  }

  /**
   * 处理字符串字符转化问题
   *
   * @param str
   */
  public static String replaceAll(String str) {
    return str.replaceAll("〈", "<").replaceAll("〉", ">").replaceAll("ALERT", "alert");
  }

  /**
   * 判断传入的类型是否为空
   *
   * @param obj
   * @return true 为空；false 不为空
   */
  public static Boolean isNull(Object obj) {

    if (obj == null) {
      return true;
    }

    if ("".equals(obj)) {
      return true;
    }

    if (obj instanceof String) {
      // 去掉空格
      String str = ((String) obj).replaceAll("　", "").trim();
      if (str.length() == 0) {
        return true;
      }
    }

    if (obj instanceof List) {
      return ((List<?>) obj).isEmpty();
    }

    if (obj instanceof Object[]) {
      return ((Object[]) obj).length == 0;
    }

    return false;
  }

  public static String convertNullToEmpty(String obj) {
    String nu = "null";
    if (obj == null || nu.equals(obj)) {
      return "";
    } else {
      return obj;
    }
  }


  /**
   * 把 VO 中所有属性为 null 的转为 ""
   *
   * @throws ApplicationException
   */
  public void nullConverNullString(Object obj) throws ApplicationException {
    if (obj != null) {

      Class classz = obj.getClass();
// 获取所有该对象的属性值
      Field fields[] = classz.getDeclaredFields();

// 遍历属性值，取得所有属性为 null 值的
      for (Field field : fields) {
        try {
          Type t = field.getGenericType();
          if (!"boolean".equals(t.toString())) {
            Method m = classz.getMethod("get"
                + change(field.getName()));
            Object name = m.invoke(obj);// 调用该字段的get方法
            if (name == null) {

              Method mtd = classz.getMethod("set"
                      + change(field.getName()),
                  new Class[]{String.class});// 取得所需类的方法对象
              mtd.invoke(obj, new Object[]{""});// 执行相应赋值方法
            }
          }

        } catch (Exception e) {
          //e.printStackTrace();
          logger.error("DataCheck.nullConverNullString，e:{}",e);
/*                    throw new ApplicationException(
                "PAYMENTS",
                "nullConverNullString error: null conver null String error .",
                e);*/
        }
      }
    }
  }

  /**
   * @param src 源字符串
   * @return 字符串，将src的第一个字母转换为大写，src为空时返回null
   */
  public static String change(String src) {
    if (src != null) {
      StringBuilder sb = new StringBuilder(src);
      sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
      return sb.toString();
    } else {
      return null;
    }
  }
}
