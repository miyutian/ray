package com;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class test3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public void test3(){
		try {
			URL url = new URL("http://itmdapps.milwaukee.gov/MPDCallData/currentCADCalls/callsService.faces");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setUseCaches(false);
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(10000);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
			int code = conn.getResponseCode();
			if (code != 200) {
				//LOGGER.info("error£º" + code);
				return;
			}
			// get the request head map
			Map<String, List<String>> head = conn.getHeaderFields();
			// get the key of the request head map
			Set<String> set = head.keySet();
			String cookie = "";
			for (String str : set) {
				if (str != null && str.equals("Set-Cookie")) {
					// get the cookie
					cookie = head.get(str).toString().replaceAll("(.*);.*;.*", "$1").replace("[", "");
				}
			}
			InputStream is = conn.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
			String result = buffer.toString();
			// get the total pages of the html
			String page = parseHtml(result).trim();
			// Page 1 of 7
			int index = page.toUpperCase().indexOf("F");
			String pageSum = page.substring(index + 1).trim();
			Integer num = Integer.valueOf(pageSum);
			// link the website by cookie & page
			for (int i = 2; i <= num; i++) {
				linkNext(cookie);
			}
			in.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
