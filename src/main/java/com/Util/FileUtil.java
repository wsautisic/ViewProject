package com.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUtil {

  private final static String fileUrl = "http://ics.sit.sf-express.com/law/common/file/download.htm?objectId=1528772385792";
  private final static String filePath = "e:"+File.pathSeparator+"test"+File.pathSeparator+"viewTest";


  public void getFileByUrl() throws IOException {

    downLoadByNetUrl(fileUrl,
        "《xxxx》ws.doc","e:\\test\\test");
  }

  /**
   * 通过java.net.URL、java.net.URLConnection
   * 从Url获取并下载文件
   * @param urlStr  文件url
   *
   * @param fileName 文件名
   * 《xxxx》.doc
   * @param savePath  保存地址
   * e:\\test\\test
   * @throws IOException
   */
  public static void  downLoadByNetUrl(String urlStr,String fileName,String savePath) throws IOException{
    URL url = new URL(urlStr);
    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    //设置超时间为3秒
    conn.setConnectTimeout(3*1000);
    //防止屏蔽程序抓取而返回403错误
    conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

    //得到输入流
    InputStream inputStream = conn.getInputStream();
    //获取自己数组
    byte[] getData = StreamUtil.readInputStream(inputStream);

    //文件保存位置
    File saveDir = new File(savePath);
    if(!saveDir.exists()){
      saveDir.mkdir();
    }
    File file = new File(saveDir+ File.separator+fileName);
    FileOutputStream fos = new FileOutputStream(file);
    fos.write(getData);
    if(fos!=null){
      fos.close();
    }
    if(inputStream!=null){
      inputStream.close();
    }

    System.out.println("file:"+fileName+",info:"+url+" download success");

  }


}
