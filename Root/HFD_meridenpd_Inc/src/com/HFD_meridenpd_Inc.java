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
		while (true){
		String InfoString = new HFD_meridenpd_Inc().HFD_meridenpd_Inc_Info();
		ArrayList<String[]> InfoList = new JsonParase().InfoParase(InfoString);
		new JsonParase().InsertDB(InfoList); 
		//String Cookie = "ASP.NET_SessionId=2g5ejwsmbxyvgjshyqrnyevd";
		//String latLonString = new HFD_meridenpd_Inc().HFD_meridenpd_Inc_LatLon(Cookie);
		//ArrayList<String[]> LatLonListnew =new JsonParase().LatLonParase(latLonString);
		//ArrayList<String[]> HFDInfo = new JsonParase().JoinJson(InfoList,LatLonListnew);
		System.out.println("Program has complete a iterater, will sleep 3 mins~");
		Thread.sleep(1000*60*3);
		}
	}
	public String HFD_meridenpd_Inc_Info(){
		//鍒涘缓瀹㈡埛绔�
		CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
		//鍒涘缓璇锋眰Get瀹炰緥
		HttpPost post  = new HttpPost("http://meridenp2c.com/cad/cadHandler.ashx?op=s");
				// 瀹氫箟涓�涓瓧绗︿覆鐢ㄦ潵瀛樺偍缃戦〉鍐呭
				String contents = "";
				// 瀹氫箟涓�涓紦鍐插瓧绗﹁緭鍏ユ祦
				BufferedReader in = null;
				List<NameValuePair> list = new ArrayList<NameValuePair>();

				list.add(new BasicNameValuePair("t","ccc"));
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,Consts.UTF_8);
				post.setEntity(entity);
				 //璁剧疆澶撮儴淇℃伅杩涜妯℃嫙鐧诲綍锛堟坊鍔犵櫥褰曞悗鐨凜ookie锛�
		        //httpGet.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
				//httpGet.setHeader("Accept-Encoding", "gzip, deflate");
				//httpGet.setHeader("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7,en-GB;q=0.6");
				//httpGet.setHeader("Cookie", "ASP.NET_SessionId=gwi3bpxdglqmkdmbdpkthfyg");
				//httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
		        //httpGet.setHeader("Cache-Control", "no-cache");
				//httpGet.setHeader("Content-Type", "text/html; charset=utf-8");
		        try
				{
					//瀹㈡埛绔墽琛宧ttpGet鏂规硶锛岃繑鍥炲搷搴�
		            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(post);
		            contents = EntityUtils.toString(closeableHttpResponse.getEntity(),"utf-8");//gbk
		            //System.out.println("InfoJson"+contents);
		            
				} catch (Exception e)
				{
					e.printStackTrace();
				} // 浣跨敤finally鏉ュ叧闂緭鍏ユ祦
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
	// get lat lon from anther URL,looks it's unnecessary!
	/*public String HFD_meridenpd_Inc_LatLon(String Cookie){

		CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

		String contents = null;
		//BufferedReader in = null;
		HttpGet httpGet = new HttpGet("http://meridenp2c.com/GoogleMapMarkers.aspx");*/
		       // httpGet.setHeader("Accept","*/*");
				/*httpGet.setHeader("Accept-Encoding", "gzip, deflate");
				httpGet.setHeader("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7,en-GB;q=0.6");
				httpGet.setHeader("Cookie", Cookie);
				httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
		        httpGet.setHeader("Cache-Control", "no-cache");
				httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
				httpGet.setHeader("Referer", "http://meridenp2c.com/GoogleMap.aspx");
				httpGet.setHeader("X-Requested-With", "XMLHttpRequest");			
		        try
				{
		            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
		            //InputStream in = closeableHttpResponse.getEntity().getContent();
		            HttpEntity entity = closeableHttpResponse.getEntity();
		            
		            contents = EntityUtils.toString(entity);
		            //System.out.println("LatLonJson"+contents.substring(0,30));
		            System.out.println("LatLonJson"+contents);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					
				}
		        return contents;
	}*/

}
