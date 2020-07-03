package com.Util;

import com.alibaba.fastjson.JSONObject;
import com.check.DataCheck;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpUtil {
  /**
   * 通过httpUtil发送post请求
   * @param postUrl
   * @param json  参数集合
   * @return
   */
  public static Map<String, Object> pushContractStatus(String postUrl,JSONObject json) {
//    logger.info("推送状态信息物业接口数据开始,contractId:{},busiKey:{},status:{},oldContractId:{},contractType:{},contractCode:{},opType:{}", contractId, busiKey,status,oldContractId,contractType,contractCode,opType);
    JSONObject resultJson = new JSONObject();
    resultJson.put("msg", "请求服务异常!");
    resultJson.put("succ", "-1");

    List<NameValuePair> list = new ArrayList<NameValuePair>();
    for (Map.Entry<String, Object> entry : json.entrySet()) {
      list.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
    }

    CloseableHttpClient httpClient = HttpClients.createDefault();
    try {
      HttpPost post = new HttpPost(postUrl);

      post.setEntity(new UrlEncodedFormEntity(list, Charset.forName("UTF-8")));
      CloseableHttpResponse httpResponse = httpClient.execute(post);

      String body = "";
      int statusCode = httpResponse.getStatusLine().getStatusCode();
      if (statusCode == HttpStatus.SC_OK) {
        body = EntityUtils.toString(httpResponse.getEntity());
//        logger.info("发送和返回信息对{}--->{}", String.valueOf(list), body);
      }else{

//        logger.error("推送状态信息物业接口失败！！statusCode：{}",statusCode);
        return resultJson;
      }
//      logger.info("推送状态信息给物业系统返回值为：{}", asciiToNative(body));
      resultJson = JSONObject.parseObject(body);

      EntityUtils.consume(httpResponse.getEntity());
    } catch (Exception e) {
//      logger.info("推送状态信息给物业系统失败,原因:", e);
      return resultJson;
    }
    return resultJson;
  }

}
