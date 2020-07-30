package com.Util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;

public class ICSQueryUtil {
  private static final Logger logger = LoggerFactory.getLogger(ICSQueryUtil.class);

  final static String url_test = "http://ics-integrate.sit.sf-express.com/out/busi/queryContInfoList";

  final static String url_local = "http://100.120.3.104:9006/out/busi/queryContInfoList";


  public static void main(String[] args) {
    findIcsContractByCode("","",url_local);
  }


  public static JSONArray findIcsContractByCode(String contractCode, String contVersion, String url){
//    logger.info("findIcsContractByCode, contractCode:{}, url :{}", contractCode, url);
    JSONObject reqJson = new JSONObject();
    reqJson.put("requestSystemId", "44");
      reqJson.put("contVersion", "");
      reqJson.put("contCode", "44202007293564");
      reqJson.put("currentSize", "1");
      reqJson.put("size", "20");
//    }
    String resultJsonStr = post(url, reqJson, "根据合同编号获取ICS合同数据异常:");
    JSONArray jsonCArray = JSONArray.parseArray(resultJsonStr);
    JSONObject cont1Obj = jsonCArray.getJSONObject(0);
    return cont1Obj.getJSONArray("contInfoList");
  }


  public static String post(String url, JSONObject psotParams, String resultErrorMsg) {
//    if(StringUtil.isEmpty(url)){
//      throw new SfopenRuntimeException("未获取到请求的url信息");
//    }
    StringEntity entity;
    try {
      entity = new StringEntity(psotParams.toString(), "UTF-8");
      return post(url, entity, resultErrorMsg);
    } catch (UnsupportedCharsetException e) {
      logger.error("post UnsupportedCharsetException error", e);
//      throw new SfopenRuntimeException(resultErrorMsg);
    }
    return "ok";
  }

  private static String post(String url, StringEntity entity, String resultErrorMsg){  //NOSONAR
    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(url);
    httpPost.setEntity(entity);

    try {
      CloseableHttpResponse httpResp = httpClient.execute(httpPost);
      int senStatusCode = httpResp.getStatusLine().getStatusCode();
      if (senStatusCode == HttpStatus.SC_OK) {
        String httpResult = EntityUtils.toString(httpResp.getEntity());
        JSONObject jsonObj;
        logger.info("http返回报文:{}", httpResult);


          return httpResult;

//        String msgCode = jsonObj.getString("code");
//        String message = jsonObj.getString("message");
//        if (!String.valueOf(HttpStatus.SC_OK).equals(msgCode)) {
//          logger.error("发送http失败，错误码为:{}", msgCode);
////          throw new SfopenRuntimeException(resultErrorMsg + message);
//        }
//        return httpResult;
      }
    } catch (IOException e) {
      logger.error("HttpSendUtil NameValuePair POST IOException ", e);
//      LogUtil.error("HttpSendUtil NameValuePair POST IOException ", e);
      return e.getMessage();
    } finally{
      try {
        httpClient.close();
      } catch (IOException e) {
        logger.error("HttpSendUtil NameValuePair client close IOException ", e);
//        LogUtil.error("HttpSendUtil NameValuePair client close IOException ", e);
      }
    }
//    throw new SfopenRuntimeException(resultErrorMsg);
    return null;
  }


}
