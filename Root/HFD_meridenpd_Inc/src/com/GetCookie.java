package com;
import java.io.BufferedReader;



import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;

import java.io.IOException; 
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList; 
import java.util.Date;
import java.util.List;
import java.util.Map;

public class GetCookie {
	public static void main(String[] args) throws ClientProtocolException, IOException{
		new GetCookie().Cookie();
	}
	public String Cookie(){
		String Cookie = "";
		try {
	        URL googleobj = new URL("http://meridenp2c.com/GoogleMap.aspx");
	        URLConnection googleconn = googleobj.openConnection();

	  	  	String urlPath = new String("http://meridenp2c.com/cad/currentcalls.aspx");
	  	  	
	  	  	URL url=new URL(urlPath);
	  	  	HttpURLConnection httpConn=(HttpURLConnection)url.openConnection();
	  	          
	  	          //设置参数
	  	          //httpConn.setDoOutput(true);     	//需要输出
	  	  	httpConn.setDoInput(true);      	//需要输入
	  	  	httpConn.setUseCaches(false);   	//不允许缓存
	  	  	httpConn.setRequestMethod("GET");   //设置GET方式连接
	  	          
	  	          //设置请求属性
	  	  	httpConn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
	  	  	httpConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
	  	  	httpConn.setRequestProperty("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7,en-GB;q=0.6");
	  	  	httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
	  	  	httpConn.setRequestProperty("Charset", "UTF-8");
	  	  	httpConn.setRequestProperty("Cache-Control", "no-cache");
	  	  	httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
	  	          
	  	          //设置浏览器认证
	  	          //String userCredentials = "testuser2:33abwe";
	  	          //String basicAuth = "Basic " + new String(new sun.misc.BASE64Encoder().encode(userCredentials.getBytes()));
	  	          //httpConn.setRequestProperty("Authorization", basicAuth);
	  	          
	  	          //连接,也可以不用明文connect，使用下面的httpConn.getOutputStream()会自动connect
	  	  	httpConn.connect();
	  	  	Map<String, List<String>> map = httpConn.getHeaderFields();
	  	  	Cookie = map.get("Set-Cookie").toString();
	  	  	System.out.println(Cookie);

	  	  	Cookie = Cookie.substring(1, Cookie.indexOf(";"));
	  	  	System.out.println(Cookie);
	  	  	
	  	  	
	  	  	String googlePath = new String("http://meridenp2c.com/GoogleMap.aspx");
	  	  	
	  	  	URL googleurl=new URL(googlePath);
	  	  	HttpURLConnection googlehttpConn=(HttpURLConnection)url.openConnection();
	  	          
	  	          //设置参数
	  	          //httpConn.setDoOutput(true);     	//需要输出
		  	 googlehttpConn.setDoInput(true);      	//需要输入
		  	 googlehttpConn.setUseCaches(false);   	//不允许缓存
		  	 googlehttpConn.setRequestMethod("GET");   //设置GET方式连接
	  	          
	  	          //设置请求属性
		  	 googlehttpConn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		  	 googlehttpConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
  		  	 googlehttpConn.setRequestProperty("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7,en-GB;q=0.6");
 		  	 googlehttpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
		  	 googlehttpConn.setRequestProperty("Charset", "UTF-8");
		  	 googlehttpConn.setRequestProperty("Cache-Control", "no-cache");
		  	 googlehttpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
		  	          
		  	          //设置浏览器认证
		  	          //String userCredentials = "testuser2:33abwe";
		  	          //String basicAuth = "Basic " + new String(new sun.misc.BASE64Encoder().encode(userCredentials.getBytes()));
		  	          //httpConn.setRequestProperty("Authorization", basicAuth);
		  	          
		  	          //连接,也可以不用明文connect，使用下面的httpConn.getOutputStream()会自动connect
		  	 googlehttpConn.connect();
	  	  	
	  	  	
		}
	  	  	catch (Exception e) {
				// TODO: handle exception
			}
	  	  	return Cookie;
	}
}
