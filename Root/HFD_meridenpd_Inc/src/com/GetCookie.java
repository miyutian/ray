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
		new GetCookie().GCookie();
	}
	public String Cookie(){
		String Cookie1 = "";

		String Cookie = "";
		try {


	  	  	String urlPath = new String("http://meridenp2c.com/cad/currentcalls.aspx");
	  	  	
	  	  	URL url=new URL(urlPath);
	  	  	HttpURLConnection httpConn=(HttpURLConnection)url.openConnection();
	  	          
	  	          //璁剧疆鍙傛暟
	  	          //httpConn.setDoOutput(true);     	//闇�杈撳嚭
	  	  	httpConn.setDoInput(true);      	//闇�杈撳叆
	  	  	httpConn.setUseCaches(false);   	//涓嶅厑璁哥紦瀛�
	  	  	httpConn.setRequestMethod("GET");   //璁剧疆GET鏂瑰紡杩炴帴
	  	          
	  	          //璁剧疆璇锋眰灞炴�
	  	  	httpConn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
	  	  	httpConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
	  	  	httpConn.setRequestProperty("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7,en-GB;q=0.6");
	  	  	httpConn.setRequestProperty("Connection", "Keep-Alive");// 缁存寔闀胯繛鎺�
	  	  	httpConn.setRequestProperty("Charset", "UTF-8");
	  	  	httpConn.setRequestProperty("Cache-Control", "no-cache");
	  	  	httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
	  	          
	  	          //璁剧疆娴忚鍣ㄨ璇�
	  	          //String userCredentials = "testuser2:33abwe";
	  	          //String basicAuth = "Basic " + new String(new sun.misc.BASE64Encoder().encode(userCredentials.getBytes()));
	  	          //httpConn.setRequestProperty("Authorization", basicAuth);
	  	          
	  	          //杩炴帴,涔熷彲浠ヤ笉鐢ㄦ槑鏂嘽onnect锛屼娇鐢ㄤ笅闈㈢殑httpConn.getOutputStream()浼氳嚜鍔╟onnect
	  	  	httpConn.connect();
	  	  	Map<String, List<String>> map = httpConn.getHeaderFields();
	  	  	Cookie = map.get("Set-Cookie").toString();
	  	  	System.out.println("Cookie::"+Cookie);

	  	  	Cookie = Cookie.substring(1, Cookie.indexOf(";"));
	  	  	System.out.println("Cookie::"+Cookie);
	  	  	
	  	  	
	  	  	
	  	  	
	  	  String urlPath1 = new String("http://meridenp2c.com/cad/currentcalls.aspx");
	  	  	
	  	  	URL url1=new URL(urlPath1);
	  	  	HttpURLConnection httpConn1=(HttpURLConnection)url1.openConnection();
	  	          
	  	          //璁剧疆鍙傛暟
	  	          //httpConn.setDoOutput(true);     	//闇�杈撳嚭
	  	  httpConn1.setDoInput(true);      	//闇�杈撳叆
	  	httpConn1.setUseCaches(false);   	//涓嶅厑璁哥紦瀛�
	  	httpConn1.setRequestMethod("GET");   //璁剧疆GET鏂瑰紡杩炴帴
	  	          
	  	          //璁剧疆璇锋眰灞炴�
	  	httpConn1.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
	  	httpConn1.setRequestProperty("Accept-Encoding", "gzip, deflate");
	  	httpConn1.setRequestProperty("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7,en-GB;q=0.6");
	  	httpConn1.setRequestProperty("Connection", "Keep-Alive");// 缁存寔闀胯繛鎺�
	  	httpConn1.setRequestProperty("Charset", "UTF-8");
	  	httpConn1.setRequestProperty("Cache-Control", "no-cache");
	  	httpConn1.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
	  	          
	  	          //璁剧疆娴忚鍣ㄨ璇�
	  	          //String userCredentials = "testuser2:33abwe";
	  	          //String basicAuth = "Basic " + new String(new sun.misc.BASE64Encoder().encode(userCredentials.getBytes()));
	  	          //httpConn.setRequestProperty("Authorization", basicAuth);
	  	          
	  	          //杩炴帴,涔熷彲浠ヤ笉鐢ㄦ槑鏂嘽onnect锛屼娇鐢ㄤ笅闈㈢殑httpConn.getOutputStream()浼氳嚜鍔╟onnect
	  	  	httpConn1.connect();
	  	  	Map<String, List<String>> map1 = httpConn1.getHeaderFields();
	  	  	Cookie1 = map1.get("Set-Cookie").toString();
	  	  	System.out.println("Cookie::"+Cookie1);

	  	  	Cookie1 = Cookie1.substring(1, Cookie1.indexOf(";"));
	  	  	System.out.println("Cookie::"+Cookie1);
	  	  	
	  	  	
	  	  	
	  	  	
		}
	  	  	catch (Exception e) {
			}
	  	  	return Cookie;
	}
	public void GCookie() throws IOException{
		String Cookie1 = "";
		try {


	  	  	String urlPath1 = new String("http://meridenp2c.com/cad/currentcalls.aspx");
	  	  	
	  	  	URL url1=new URL(urlPath1);
	  	  	HttpURLConnection httpConn1=(HttpURLConnection)url1.openConnection();
	  	          
	  	          //璁剧疆鍙傛暟
	  	          //httpConn.setDoOutput(true);     	//闇�杈撳嚭
	  	  httpConn1.setDoInput(true);      	//闇�杈撳叆
	  	httpConn1.setUseCaches(false);   	//涓嶅厑璁哥紦瀛�
	  	httpConn1.setRequestMethod("GET");   //璁剧疆GET鏂瑰紡杩炴帴
	  	          
	  	          //璁剧疆璇锋眰灞炴�
	  	httpConn1.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
	  	httpConn1.setRequestProperty("Accept-Encoding", "gzip, deflate");
	  	httpConn1.setRequestProperty("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7,en-GB;q=0.6");
	  	httpConn1.setRequestProperty("Connection", "Keep-Alive");// 缁存寔闀胯繛鎺�
	  	httpConn1.setRequestProperty("Charset", "UTF-8");
	  	httpConn1.setRequestProperty("Cache-Control", "no-cache");
	  	httpConn1.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
	  	          
	  	          //璁剧疆娴忚鍣ㄨ璇�
	  	          //String userCredentials = "testuser2:33abwe";
	  	          //String basicAuth = "Basic " + new String(new sun.misc.BASE64Encoder().encode(userCredentials.getBytes()));
	  	          //httpConn.setRequestProperty("Authorization", basicAuth);
	  	          
	  	          //杩炴帴,涔熷彲浠ヤ笉鐢ㄦ槑鏂嘽onnect锛屼娇鐢ㄤ笅闈㈢殑httpConn.getOutputStream()浼氳嚜鍔╟onnect
	  	  	httpConn1.connect();
	  	  	Map<String, List<String>> map1 = httpConn1.getHeaderFields();
	  	  	Cookie1 = map1.get("Set-Cookie").toString();
	  	  	System.out.println("Cookie::"+Cookie1);

	  	  	Cookie1 = Cookie1.substring(1, Cookie1.indexOf(";"));
	  	  	System.out.println("Cookie::"+Cookie1);
	  	  	
	  	  	
	  	  	
	  	  	
	  	  	
		}
	  	  	catch (Exception e) {
			}
	}  	
}
