package com;
//import java.io.BufferedReader;
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
/*import com.trafficcast.base.dbutils.DBConnector;
import com.trafficcast.base.dbutils.DBUtils;
import com.trafficcast.base.enums.EventType;
import com.trafficcast.base.geocoding.MySqlGeocodingEngine;
import com.trafficcast.base.geocoding.MySqlGeocodingInitiater;
import com.trafficcast.base.inccon.IncConDBUtils;
import com.trafficcast.base.inccon.IncConRecord;
import com.trafficcast.base.tctime.TCTime;*/

/*******************************************************************************
 * <p>
 * MKE_pd_Inc.java
 * <p>
 * --------------------- Copyright (C)2017 TrafficCast International Inc.All
 * right reserved
 * <p>
 * This reader get the incident info from the HTML web site; The http
 * address:http://itmdapps.milwaukee.gov/MPDCallData/currentCADCalls/callsService.faces
 * The data source is html style. The ticket number is SD-109
 * </p>
 * 
 * @author
 * @version 1.0
 * @since 1.5
 * ---------------------------------------------------
 *  Change History
 * --------------------------------------------------- 
 * ****************************************************/
public class test2 {
	// Current version of this class
	public static final double VERSION = 1.0;

	// Current Reader ID
	//private final String READER_ID = MKE_pd_Inc.class.getName();

	// The log4j instance
	//private static final Logger LOGGER = Logger.getLogger(MKE_pd_Inc.class);

	// Property keys
	
	// HashMap to store construction and incident types
	private ArrayList<String> incTypeList;

	// empty constructor

	private String data_url = "https://itmdapps.milwaukee.gov/MPDCallData/";
	// main function
	public static void main(String[] args) {
		new test2().readHtmlSource();
	}


	private void readHtmlSource() {
		try {
			//System.setProperty("https.protocols", "TLSv1.1,TLSv1.2,TLSv1.3,");
			URL url = new URL(data_url);
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
				return;
			}
			System.out.println("22222222222222222222222");

			// get the request head map
			Map<String, List<String>> head = conn.getHeaderFields();
			// get the key of the request head map
			Set<String> set = head.keySet();
			String cookie = "";
			System.out.println("31321321321");
			for (String str : set) {
				if (str != null && str.equals("Set-Cookie")) {
					System.out.println("cookie:::::"+head.get(str).toString());
					// get the cookie
					cookie = head.get(str).toString().replaceAll("(.*);.*;.*", "$1").replace("[", "");
					System.out.println("cookie222222"+cookie);
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
			//String page = parseHtml(result).trim();
			// Page 1 of 7
			//int index = page.toUpperCase().indexOf("F");
			//String pageSum = page.substring(index + 1).trim();
			//Integer num = Integer.valueOf(pageSum);
			// link the website by cookie & page
			//for (int i = 2; i <= num; i++) {
				//linkNext(cookie);
			//}
			in.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param cookie
	 */
	/*private void linkNext(String cookie) {
		try {
			URL url = new URL(data_url + "?" + data_url_param);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setUseCaches(false);
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(10000);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
			conn.setRequestProperty("Cookie", "_ga=GA1.2.1558233769.1508138777; " + cookie);
			int code = conn.getResponseCode();
			if (code != 200) {
				//LOGGER.info("error��" + code);
				return;
			}
			InputStream is = conn.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
			String result = buffer.toString();
			parseHtml(result);
			in.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	/**
	 * <p>
	 * Parse incident info into record, then add it into ArrayList.
	 * <p>
	 * 
	 * @param string
	 */
/*	private String parseHtml(String html) {
		Pattern tablePattern = Pattern.compile("<table id=\"formId:tableExUpdateId\".*?</table>");
		Matcher tableMatcher = tablePattern.matcher(html);
		Pattern pagePattern = Pattern.compile("<span id=\"formId:tableExUpdateId:deluxe1__pagerText\".*?>(.*?)</span>");
		Matcher pageMatcher = pagePattern.matcher(html);
		String page = "";
		// get the page tag
		if (pageMatcher.find()) {
			page = pageMatcher.group(1).trim();
		}
		// get the table tag
		if (tableMatcher.find()) {
			Pattern trPattern = Pattern.compile("<tr.*?</tr>");
			Matcher trMatcher = trPattern.matcher(tableMatcher.group());
			// get the tr tags
			while (trMatcher.find()) {
				Pattern spanPattern = Pattern.compile("<span.*?>(.*?)</span>");
				Matcher spanMatcher = spanPattern.matcher(trMatcher.group());
				List<String> list = new ArrayList<String>();
				// get the span tags
				while (spanMatcher.find()) {
					// save the datas into list
					list.add(spanMatcher.group(1).trim());
				}
				// filter the extra information
				if (list.size() != 6)
					continue;
				// get the status from the list
				String status = list.get(5);
				// filter the unnecessary record
				if (status.toUpperCase().equals("Assignment Completed".toUpperCase()))
					continue;
				// get the nature_of_call from the list
				String nature_of_call = list.get(4);
				// judge the record is necessary?
				boolean flag = false;
				// look all the important keywords
				inc: for (String str : incTypeList) {
					if (nature_of_call.toUpperCase().equals(str.toUpperCase())) {
						flag = true;
						break inc;
					}
				}
				// if nature_of_call match the keywors, porocess the record
				// else give up the record,judge the next record

			}
		}
		return page;
	}*/

	/**
	 * pocess the record
	 * 
	 * @param date_time
	 * @param location
	 * @param nature_of_call
	 * @param status
	 */
	

	

}
