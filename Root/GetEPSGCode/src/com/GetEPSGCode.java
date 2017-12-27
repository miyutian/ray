package com;
import com.WriteTxt;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * 使用Jsoup解析url
 * @tag：url ：http://sex.guokr.com/
 * Created by monster on 2015/12/11.
 */
public class GetEPSGCode {
    public static void main(String[] args) throws ParseException, IOException{
        for(int i = 1;i<=88;i++){
    	//final String url="http://spatialreference.org/ref/epsg/?page=11" ;
		CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet("http://spatialreference.org/ref/epsg/?page="+i);

        CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
        //InputStream in = closeableHttpResponse.getEntity().getContent();
        HttpEntity entity = closeableHttpResponse.getEntity();
        
        String contents = EntityUtils.toString(entity);
        contents = contents.substring(contents.indexOf("<li>")-2, contents.lastIndexOf("</li>")+5);
        System.out.println(contents);
        new WriteTxt().WriteTxt(contents);
        }
    }
}