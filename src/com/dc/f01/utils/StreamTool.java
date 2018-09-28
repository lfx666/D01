package com.dc.f01.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class StreamTool {
	 /**
     * 从输入流中读取数据
     * @param inStream
     * @return
     * @throws Exception
     */
    public static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len = inStream.read(buffer)) !=-1 ){
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();//网页的二进制数据
        outStream.close();
        inStream.close();
        return data;
    }
    
    public static void writeResponse(HttpServletResponse response, String message){
    	  response.setCharacterEncoding("UTF-8");//通知response以UTF-8发送
          response.setContentType("text/html;charset=UTF-8");//设置浏览器以UTF-8打开
          response.setHeader("Content-type", "text/html;charset=UTF-8");
          PrintWriter writer = null;
          try {
              writer = response.getWriter();
              writer.print(message);
          } catch (IOException e) {
              e.printStackTrace();
          }
    }
    
}