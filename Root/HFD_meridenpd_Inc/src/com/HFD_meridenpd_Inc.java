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
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList; 
import java.util.Date;
import java.util.List;

import org.apache.http.Consts;

import org.apache.http.NameValuePair;

import org.apache.http.client.ClientProtocolException;

import org.apache.http.client.entity.UrlEncodedFormEntity;

import org.apache.http.client.methods.CloseableHttpResponse;

import org.apache.http.client.methods.HttpPost;

import org.apache.http.impl.client.CloseableHttpClient;

import org.apache.http.impl.client.HttpClients;

import org.apache.http.message.BasicNameValuePair;

import org.apache.http.util.EntityUtils;

public class HFD_meridenpd_Inc {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String InfoString = new HFD_meridenpd_Inc().HFD_meridenpd_Inc_Info();
		ArrayList<String[]> InfoList = new JsonParase().InfoParase(InfoString);
		String Cookie = new GetCookie().Cookie();
		//String Cookie = "ASP.NET_SessionId=iu0mthh52lpyj5kq2dtwoh0d";
		String latLonString = new HFD_meridenpd_Inc().HFD_meridenpd_Inc_LatLon(Cookie);
		ArrayList<String[]> LatLonListnew =new JsonParase().LatLonParase(latLonString);
		ArrayList<String[]> HFDInfo = new JsonParase().JoinJson(InfoList,LatLonListnew);
		new JsonParase().InsertDB(HFDInfo); 
	}
	public String HFD_meridenpd_Inc_Info(){
		//创建客户端
		CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
		//创建请求Get实例
		HttpPost post  = new HttpPost("http://meridenp2c.com/cad/cadHandler.ashx?op=s");
				// 定义一个字符串用来存储网页内容
				String contents = "";
				// 定义一个缓冲字符输入流
				BufferedReader in = null;
				List<NameValuePair> list = new ArrayList();

				list.add(new BasicNameValuePair("t","css"));
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,Consts.UTF_8);
				post.setEntity(entity);
				 //设置头部信息进行模拟登录（添加登录后的Cookie）
		        //httpGet.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
				//httpGet.setHeader("Accept-Encoding", "gzip, deflate");
				//httpGet.setHeader("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7,en-GB;q=0.6");
				//httpGet.setHeader("Cookie", "ASP.NET_SessionId=gwi3bpxdglqmkdmbdpkthfyg");
				//httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
		        //httpGet.setHeader("Cache-Control", "no-cache");
				//httpGet.setHeader("Content-Type", "text/html; charset=utf-8");
		        try
				{
					//客户端执行httpGet方法，返回响应
		            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(post);
		            contents = EntityUtils.toString(closeableHttpResponse.getEntity(),"utf-8");//gbk
		            System.out.println("InfoJson"+contents);
		            
				} catch (Exception e)
				{
					System.out.println("发送GET请求出现异常！" + e);
					e.printStackTrace();
				} // 使用finally来关闭输入流
				finally
				{
					try
					{
						if (in != null)
						{
							in.close();
						}
					} catch (Exception e2)
					{
						e2.printStackTrace();
					}
				}
		        return contents;
	}

	public String HFD_meridenpd_Inc_LatLon(String Cookie){
		//创建客户端
		CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
		//创建请求Get实例
		String contents = null;
		BufferedReader in = null;
		HttpGet httpGet = new HttpGet("http://meridenp2c.com/GoogleMapMarkers.aspx");
				//设置头部信息进行模拟登录（添加登录后的Cookie）
		        httpGet.setHeader("Accept","*/*");
				httpGet.setHeader("Accept-Encoding", "gzip, deflate");
				httpGet.setHeader("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7,en-GB;q=0.6");
				httpGet.setHeader("Cookie", Cookie);
				httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
		        httpGet.setHeader("Cache-Control", "no-cache");
				httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
				httpGet.setHeader("Referer", "http://meridenp2c.com/GoogleMap.aspx");
				httpGet.setHeader("X-Requested-With", "XMLHttpRequest");			
		        try
				{
					//客户端执行httpGet方法，返回响应
		            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
		            //InputStream in = closeableHttpResponse.getEntity().getContent();
		            HttpEntity entity = closeableHttpResponse.getEntity();
		            
		            contents = EntityUtils.toString(entity);
		            System.out.println("LatLonJson"+contents.substring(0,25));

				} catch (Exception e)
				{
					System.out.println("发送GET请求出现异常！" + e);
					e.printStackTrace();
				} // 使用finally来关闭输入流
				finally
				{
					
				}
		        return contents;
	}

}
