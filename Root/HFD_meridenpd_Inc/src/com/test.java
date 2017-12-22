package com;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.net.HttpURLConnection;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

 
public class test {
 
  public static void main(String[] args) throws IOException { 

		CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
		//创建请求Get实例
		String contents = null;
		BufferedReader in = null;
		HttpGet httpGet = new HttpGet("http://spatialreference.org/ref/epsg/?page=11");
				//设置头部信息进行模拟登录（添加登录后的Cookie）
	//	        httpGet.setHeader("Accept","*/*");
	/*			httpGet.setHeader("Accept-Encoding", "gzip, deflate");
				httpGet.setHeader("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7,en-GB;q=0.6");
				httpGet.setHeader("Cookie", Cookie);
				httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
		        httpGet.setHeader("Cache-Control", "no-cache");
				httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
				httpGet.setHeader("Referer", "http://meridenp2c.com/GoogleMap.aspx");
				httpGet.setHeader("X-Requested-With", "XMLHttpRequest");	*/		
					//客户端执行httpGet方法，返回响应
		            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
		            //InputStream in = closeableHttpResponse.getEntity().getContent();
		            HttpEntity entity = closeableHttpResponse.getEntity();
		            
		            contents = EntityUtils.toString(entity);
		            System.out.println(contents);
				
  }
 
}