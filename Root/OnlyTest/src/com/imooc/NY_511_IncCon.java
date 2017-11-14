
package com.imooc;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/*import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
*/
import org.apache.xerces.parsers.SAXParser;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**import com.trafficcast.base.dbutils.DBConnector;
import com.trafficcast.base.dbutils.DBUtils;
import com.trafficcast.base.enums.EventType;
import com.trafficcast.base.geocoding.MySqlGeocodingEngine;
import com.trafficcast.base.geocoding.MySqlGeocodingInitiater;
import com.trafficcast.base.inccon.IncConDBUtils;
import com.trafficcast.base.inccon.IncConRecord;
import com.trafficcast.base.tctime.TCTime;
*/
public class NY_511_IncCon {





/****************************************************
 * <p>
 * NY_511_IncCon.java 
 * ------------------- 
 * Copyright (c) 2012 TrafficCast
 * International Inc. All rights reserved.
 * <p>
 * This reader is created by ticket #4677, and it is used to get Inc/Con info
 * from the website
 * <p>
 * ---------------------------------------------------
 * 
 * @author David Guo(1/30/2012)
 * @version 1.0
 * @since 1.6 
 * --------------------------------------------------- 
 * Change history
 * --------------------------------------------------- 
 * Change number : #1
 * Ticket number : #5129 
 * Programmer : David Guo 
 * Date : 4/24/2012
 * Description : Road name & timestamp improved, ArrayList which store
 *               TTA type is cleaned.
 * --------------------------------------------------- 
 * Change number : #2
 * Ticket number : #5475 
 * Programmer : David Guo 
 * Date : 08/02/2012
 * Description : Road name improvement.
 * --------------------------------------------------- 
 * Change number : #3
 * Ticket number : #6182 
 * Programmer : Star Zhong 
 * Date : 04/18/2013
 * Description : Detect to parse main_st=I-xxx SERVICE RD when
 *               description indicates I-xxx SERVICE RD
 *--------------------------------------------------- 
 * Change number : #4
 * Ticket number : #6303 
 * Programmer : Star Zhong 
 * Date : 05/17/2013
 * Description : More accurate assignment for end_time
 *--------------------------------------------------- 
 * Change number : #5
 * Ticket number : #6403 
 * Programmer : Star Zhong 
 * Date : 07/08/2013
 * Description : State Hwy (RT-) format recheck.
 *--------------------------------------------------- 
 * Change number : #6
 * Ticket number : #6481
 * Programmer : Star Zhong 
 * Date : 09/02/2013
 * Description : NY_511_IncCon: can scan description for type assignment.
 * --------------------------------------------------- 
 * Change number : #7
 * Ticket number : #6547 
 * Programmer : Star Zhong 
 * Date : 10/12/2013
 * Description : NY_511_IncCon: parse a daily timestamp case.
 * --------------------------------------------------- 
 * Change number : #8
 * Ticket number : #6664 
 * Programmer : Star Zhong 
 * Date : 01/02/2014
 * Description : NY_511_IncCon: parse EXPRESS LN case.
 * --------------------------------------------------- 
 * Change number : #9
 * icket number : #6870 
 * Programmer : Star Zhong 
 * Date : 03/14/2014
 * Description : NY_511_IncCon: filter HTTP related stuff in description (SXM). 
 * --------------------------------------------------- 
 * Change number : #10 
 * Ticket number : #7176 
 * Programmer : Star Zhong 
 * Date : 08/07/2014 
 * Description : Check NY_511_IncCon and NY_511_RTSpeed: no data. 
 * --------------------------------------------------- 
 * Change number : #11 
 * Ticket number : #7176 
 * Programmer : Rofan 
 * Date : 10/17/2014 
 * Description : Check description.
 * --------------------------------------------------- 
 * Change number : #12 
 * Ticket number : #7163 
 * Programmer : Rofan 
 * Date : 11/10/2014
 * Description : Road name improvement.
 * --------------------------------------------------- 
 * Change number : #13 
 * Ticket number : #7017|#7440|#7451 
 * Programmer : Rofan 
 * Date : 01/19/2015 
 * Description : fix roadname. review boundary file for 'can not locate city' cases. 
 *               detect and decide actual <main_dir> in description.
 * --------------------------------------------------- 
 * Change number : #14 
 * Ticket number : #7451 
 * Programmer    : Rofan 
 * Date          : 02/04/2015 
 * Description   : fix time format.
 * --------------------------------------------------- 
 * Change number : #15 
 * Programmer    : Rofan 
 * Date          : 02/25/2015 
 * Description   : fix Unexpected exception (java.lang.NullPointerException).
 * --------------------------------------------------- 
 * Change number : #16 
 * Ticket number : #8120
 * Programmer    : Bitter Geng 
 * Date          : 10/31/2016
 * Description   : fix the datasource without <EventSubType> Tag.
 ****************************************************/


	// Current version of this class.
	public static final double VERSION = 1.0;

	// Property file location
	private final String PROPERTY_FILE = "prop/NY_IncCon.properties";
	private final String STREET_ALIAS_FILE = "prop/NY_StreetAlias.txt";

	// Output file,it stores unknown record type,lat/lon which can not locate
	// city
	private final String NY_ERROR_RECORDS_FILE = "NY_Errors.txt";

	// Local xml file name
	private final String LOCAL_XML_FILE = "NY_IncCon.xml";
	
	// Property keys in NY_IncCon.properties
	private final String PROP_KEY_CITY = "CITY";
	private final String PROP_KEY_CONSTRUCTION_TYPE = "CONSTRUCTION_TYPE";
	private final String PROP_KEY_INCIDENT_TYPE = "INCIDENT_TYPE";
	private final String PROP_KEY_TTA_TYPE = "TTA_TYPE";
	private final String PROP_KEY_WEB_URL = "WEB_URL";
	private final String PROP_KEY_SLEEP_TIME = "SLEEP_TIME";
	private final String PROP_KEY_RETRY_WAIT_TIME = "RETRY_WAIT_TIME";
	private final String PROP_KEY_TC_SEPARATE_SIGN = "TC_SEPARATE_SIGN";
	private final String PROP_KEY_REVERSE_GEOCODING_FLAG = "REVERSE_GEOCODING_FLAG";
	private final String EVENT_TYPE_EXTEND = "prop/EventTypeExtend.properties";
	private final String EVENT_TYPE = "prop/EventTypeAssign.properties";
	private final String PROP_KEY_EVENT_TYPE_FILTER = "EVENT_TYPE_FILTER";
	private final String PROP_KEY_EVENT_SUB_TYPE_FILTER = "EVENT_SUB_TYPE_FILTER";
	private final String FROM_PATTERN = "FROM_PATTERN";
	private final String FROM_TO_PATTERN = "FROM_TO_PATTERN";
	private final String PROP_KEY_BRIDGE_CLOSED_FILTER = "BRIDGE_CLOSED_FILTER";
	// Log4j instance
	//private static final Logger LOGGER = Logger.getLogger(NY_511_IncCon.class);

	// int value 10 represents the inc/con/tta reader
	private final int READER_TYPE = 10;

	// Reader ID
	private final String READER_ID = NY_511_IncCon.class.getName();

	// NY state code
	private final String STATE = "NY";

	// NYC market code
	private final String MARKET = "NYC";

	// for event type
	private HashMap<String, String> eventTypeMap;

	// for event type extend
	private HashMap<String, String> eventTypeExtendMap;

	// Reverse Geocoding flag.
	private final int REVERSE_GEOCODING_FLAG = -2;

	// SimpleDateFormat to format the start time and end time
	private static final SimpleDateFormat START_TIME_FORMAT = new SimpleDateFormat(
			"MM/dd/yyyy hh:mm:ss aa", Locale.US);
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm:ss", Locale.US);
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"MM/dd/yyyy", Locale.US);
	private static final SimpleDateFormat START_DATE_FORMAT = new SimpleDateFormat(
			"MMM dd yyyy", Locale.US);
	private static final SimpleDateFormat SPECIAL_START_FORMAT = new SimpleDateFormat(
			"MMM dd yyyy hh:mm aa", Locale.US);
	private static final SimpleDateFormat SPECIAL_END_FORMAT = new SimpleDateFormat(
			"MMM dd yyyy hh:mm:ss aa", Locale.US);
	private static final SimpleDateFormat END_TIME_FORMAT = new SimpleDateFormat(
			"hh:mm aa MM/dd/yy", Locale.US);
	private static final SimpleDateFormat WEEKDAY_FORMAT = new SimpleDateFormat(
			"EE", Locale.US);

	// TrafficCast Separate Sign
	private String tcSeparateSign = "~TrafficCastSeparateSign~";

	private String PATTERN_FILE = "prop/NY_IncCon_Patterns.txt";
	private String DIR_PATTERN = "DIR_PATTERN";
	private List<Pattern> dirPatternList = null;

	// If we need do reverse geocoding
	private boolean isReverseGeocoding = false;

	// sleep time, set default to 5 minutes, will load from property file
	private long loopSleepTime = 5 * 60 * 1000;

	// Current URL.
	private URL url = null;

	// Retry wait time, set default to 2 minutes, will load from property file
	private long retryWaitTime = 2 * 60 * 1000;

	// General NY Time Zone
	
	private TimeZone nyTimeZone = null;

	// HashMap to store construction and incident types
	private HashMap<String, String> incConTypeHash;

	// HashMap to store event type filter
	private HashMap<String, String> eventTypeFilterHash;

	// HashMap to store event sub type filter
	private HashMap<String, String> eventSubTypeFilterHash;

	// Address to get xml
	private String webURL = "https://165.193.215.51/xmlfeeds/index.html";

	/**
	 * CityCode, city Lat/Lon and total city count in NY, all variables will be
	 * initialized in loadProperties
	 */
	private String[][] cityLatLon;

	// Array to store all the city name
	private String[] cityCodes;

	// The count of cites
	private int cityCount;

	// ArrayList to store IncCon
	private ArrayList<IncConRecord> incArrayList = null;
	private ArrayList<IncConRecord> conArrayList = null;
	private ArrayList<IncConRecord> ttaArrayList = null;
	// Store the from_to patterns
	private List<Pattern> from_to_pattern_list;
	// Store the from pattern
	private List<Pattern> from_pattern_list;
	// Hashmap to store street alias.
	private LinkedHashMap<String, String> streetAliasMap;

	// Matcher
	Matcher matcher = null;

	// Hashmap to store unexpected error, only not in this map will save to
	// local
	private HashMap<String, String> unknownErrorMap;

	// This value is added to the wait time each time an exception is caught in
	// run()
	private final int SEED = 60000;

	// buffer size.
	private final int BUF_SIZE = 1024;
	
	//bridge closed filter
	private String bridgeClosedFilter = "";
	
	/**
	 * Default constructor
	 */
	public NY_511_IncCon() {
	}// end of NY_511_IncCon

	/**
	 * Main will create a new NY_511_IncCon, call run function
	 * 
	 * @param args
	 * @return None
	 * @exception
	 * @see
	 */
	public static void main(String[] args) {
		//PropertyConfigurator.configureAndWatch("log4j.properties", 60000);
		NY_511_IncCon incCon = new NY_511_IncCon();

		try {
			incCon.run();
		} catch (Exception ex) {
			System.out.println("Unexpected problem, program will terminate now ("
					+ ex.getMessage() + ")");
		}
	}// end of main

	/**
	 * Read xml from website, save it to local, parse xml, analyze record, save
	 * to database, Geocoding, sleep and then start another loop.
	 * 
	 * @param None
	 * @return None
	 * @exception Exception
	 * @see
	 * 定义不同方法中出现的错误抛出的异常_米雷
	 **/
	private void run() throws Exception {
		long startTime, sleepTime, waitTime = 0;
		if (loadEventTypeProperties() == false) {
			System.out.println("Load properties " + EVENT_TYPE
					+ "failed! Program will terminate.");
			throw new RuntimeException(); // main() will catch this exception. }
		}

		if (loadPatterns() == false) {
			System.out.println("Load patterns " + PATTERN_FILE
					+ "failed! Program will terminate.");
			throw new RuntimeException(); // main() will catch this exception. }
		}

		if (loadEventTypeExtendProperties() == false) {
			System.out.println("Load properties " + EVENT_TYPE_EXTEND
					+ "failed! Program will terminate.");
			throw new RuntimeException(); // main() will catch this exception. }
		}
		// Only run when property file and road alias loaded and initialize OK.
		if (loadProperties() == true && loadStreetAlias() == true) {
			System.out.println("Load properties and initialize completed, next will enter while()");
		} else {
			System.out.println(" properties failed ! Program will terminate");
			throw new RuntimeException(); // main() will catch this exception.
		}

//		initVariables();

		while (true) {
			try {
				startTime = System.currentTimeMillis();
				System.out.println("Start to process Construction/Incident events");

				// Pull data from NY dot website and save to local file.
				
				saveXmlFile();

				// parse Xml file, add records to consArrayList/incArrayList
				parseIncConXml();
				
				System.out.println("parse XML completed, next will update database");

				incTempArrayList = new ArrayList<IncConRecord>();
				conTempArrayList = new ArrayList<IncConRecord>();
				AttaTempArrayList = new ArrayList<IncConRecord>();
				IncConRecord tempRecord = null;
				String stateNJ = "NJ";
				String cityATC = "ATC";

				for (int i = 0; i < incArrayList.size(); i++) {
					tempRecord = incArrayList.get(i);
					if (cityATC.equals(tempRecord.getCity())) {
						incTempArrayList.add(tempRecord);
					}
				}
				for (int i = 0; i < incTempArrayList.size(); i++) {
					tempRecord = incTempArrayList.get(i);
					incArrayList.remove(tempRecord);
					incTempArrayList.get(i).setState(stateNJ);
				}

				for (int i = 0; i < conArrayList.size(); i++) {
					tempRecord = conArrayList.get(i);
					if (cityATC.equals(tempRecord.getCity())) {
						conTempArrayList.add(tempRecord);
					}
				}
				for (int i = 0; i < conTempArrayList.size(); i++) {
					tempRecord = conTempArrayList.get(i);
					conArrayList.remove(tempRecord);
					conTempArrayList.get(i).setState(stateNJ);
				}

				for (int i = 0; i < ttaArrayList.size(); i++) {
					tempRecord = ttaArrayList.get(i);
					if (cityATC.equals(tempRecord.getCity())) {
						ttaTempArrayList.add(tempRecord);
					}
				}
				for (int i = 0; i < ttaTempArrayList.size(); i++) {
					tempRecord = ttaTempArrayList.get(i);
					ttaArrayList.remove(tempRecord);
					ttaTempArrayList.get(i).setState(stateNJ);
				}
				if (incArrayList.size() > 0 || conArrayList.size() > 0
						|| ttaArrayList.size() > 0) {
					IncConDBUtils.updateDB(incArrayList, STATE,
							EventType.INCIDENT);
					IncConDBUtils.updateDB(conArrayList, STATE,
							EventType.CONSTRUCTION);
					IncConDBUtils.updateDB(ttaArrayList, STATE, EventType.TTA);
				}
				if (incTempArrayList.size() > 0 || conTempArrayList.size() > 0
						|| ttaTempArrayList.size() > 0) {
					IncConDBUtils.updateDB(incTempArrayList, cityATC,
							EventType.INCIDENT);
					IncConDBUtils.updateDB(conTempArrayList, cityATC,
							EventType.CONSTRUCTION);
					IncConDBUtils.updateDB(ttaTempArrayList, cityATC,
							EventType.TTA);
				}
				// Update "last run" field in Mysql table containing reader
				// program IDs.
				DBUtils.updateReaderLastRun(loopSleepTime, READER_TYPE);

				// Geocoding.
				System.out.println("Starting GEOCoding process.");
				MySqlGeocodingEngine geo;
				for (int i = 0; i < cityCount; i++) {
					geo = new MySqlGeocodingInitiater(cityCodes[i], READER_ID);
					geo.initiateGeocoding();
				}

				/**
				 * Sleep for an amount of time equal to either the difference
				 * between SLEEP_PERIOD and the running time of this iteration,
				 * or 1 second if this iteration took more than SLEEP_PERIOD
				 * milliseconds.
				 */
				sleepTime = loopSleepTime
						- (System.currentTimeMillis() - startTime);
				if (sleepTime < 0) {
					sleepTime = 1000;
				}
				System.out.println("Last built on 04/27/2017, Ticket number: #8411");
				System.out.println("Sleeping for " + (sleepTime / 1000) + " seconds.");
				System.out.println();

				// Clean up.
				DBConnector.getInstance().disconnect();
				Thread.sleep(sleepTime);
				waitTime = 0;
			} catch (NoRouteToHostException ex) {
				System.out.println("This machine's internet connection is unavailable, retrying in "
						+ retryWaitTime / 1000 + " seconds...");
				try {
					Thread.sleep(retryWaitTime);
				} catch (InterruptedException ex1) {
					System.out.println("Thread was interrupted.");
				}
			} catch (ConnectException ex) {
				System.out.println("Connection was refused, retrying in "
						+ retryWaitTime / 1000 + " seconds...");
				try {
					Thread.sleep(retryWaitTime);
				} catch (InterruptedException ex1) {
					System.out.println("Thread was interrupted.");
				}
			} catch (UnknownHostException ex) {
				System.out.println("Unkown host. Could not establish contact, retrying in "
						+ retryWaitTime / 1000 + " seconds...");
				try {
					Thread.sleep(retryWaitTime);
				} catch (InterruptedException ex1) {
					System.out.println("Thread was interrupted.");
				}
			} catch (FileNotFoundException ex) {
				System.out.println("Could not retrieve data, retrying in "
						+ retryWaitTime / 1000 + " seconds...");
				try {
					Thread.sleep(retryWaitTime);
				} catch (InterruptedException ex1) {
					System.out.println("Thread was interrupted.");
				}
			} catch (IOException ex) {
				System.out.println(ex.getMessage() + ", retrying in " + retryWaitTime
						/ 1000 + " seconds...");
				try {
					Thread.sleep(retryWaitTime);
				} catch (InterruptedException ex1) {
					System.out.println("Thread was interrupted.");
				}
			} catch (Exception ex) {
				waitTime += waitTime == 0 ? SEED : waitTime;
				LSystem.out.println(Level.FATAL, "Unexpected exception (" + ex + "). "
						+ "Restarting parsing process in " + waitTime / 60000
						+ " minute(s).", ex);

				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException ex1) {
					System.out.println("Thread interrupted!");
				}
			} finally {
				incArrayList.clear();
				conArrayList.clear();
				ttaArrayList.clear();
			}
		} // end while
	} // end run()
/*
 * 读取并保存资源网站的xml文件_米雷
 * 看到这里了丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫丫
 */
	private void saveXmlFile() throws Exception {
		int byteLength = 0;
		byte[] input_buffer = new byte[BUF_SIZE];
		BufferedOutputStream outStream = null;
		InputStream inStream = null;

		try {
			// #1 DNS setting
			java.security.Security.setProperty(
					"networkaddress.cache.negative.ttl", "0");
			java.security.Security.setProperty("networkaddress.cache.ttl", "0");
			System.setProperty("sun.net.inetaddr.ttl", "0");
			System.setProperty("sun.net.inetaddr.negative.ttl", "0");

			// SSL certificate check
			trustAllHttpsCertificates();
			HttpsURLConnection.setDefaultHostnameVerifier(hv);

			// #1
			outStream = new BufferedOutputStream(new FileOutputStream(
					LOCAL_XML_FILE), BUF_SIZE);

			URLConnection urlCon = null;
			url = new URL(webURL);
			urlCon = url.openConnection();
			urlCon.setConnectTimeout((int) retryWaitTime);
			urlCon.setReadTimeout((int) retryWaitTime);

			System.out.println("Retrieving xml file.");
			inStream = urlCon.getInputStream();

			while ((byteLength = inStream.read(input_buffer, 0, BUF_SIZE)) > 0) {
				outStream.write(input_buffer, 0, byteLength);
			}

			// Close streams.
			outStream.flush();
			System.out.println("Save xml successfully.");
		} finally {
			if (outStream != null) {
				outStream.close();
			}
			if (inStream != null) {
				inStream.close();
			}
		}
	} // end saveXMLFile()

	/**
	 * Connect to data source, pull data and save to local file.
	 * 
	 * @param None
	 * @return None
	 * @exception Exception
	 * @see
	 */
	// private void saveXmlFile() {
	// try {
	// Runtime.getRuntime().exec("bash " + BASH_FILE);
	// Thread.sleep(60 * 1000);
	// } catch (Exception e) {
	// System.out.println("Loading xml file failed.");
	// }
	// } //end saveXMLFile()

	/**
	 * Initializes and start the XML parser/ handler.
	 * 
	 * @param None
	 * @return None
	 * @see
	 */
	public void parseIncConXml() throws Exception {
		XMLReader parser = new SAXParser();
		// tell the parser about the handlers.
		IncidentConstructionHandler handler = new IncidentConstructionHandler();
		parser.setContentHandler(handler);
		parser.setErrorHandler(handler);

		System.out.println("-- Parse inc_con feed. --");
		// #
//		parser.parse(new InputSource(new FileReader("NY_IncCon002.xml")));
		// #
		parser.parse(new InputSource(new FileReader(LOCAL_XML_FILE)));
	} // end parseIncConXml()

	/*******************************************************************************
	 * XML Parser/ Handler.
	 ******************************************************************************/
	private class IncidentConstructionHandler extends DefaultHandler {
		IncConRecord incConRecord = null;
		TCTime startTCTime = null, endTCTime = null;
		String description = "";
		StringBuffer dataStringBuffer = null;
		String hwy_name = "";
		String hwy_dir = "";
		String cross_from = "";
		String cross_to = "";
		String county = "";
		String start_time = "";
		String end_time = "";
		String eventClass = "";
		String eventType = "";
		String slon = "";
		String slat = "";
		String elon = "";
		String elat = "";
		String weekdays = "";
		String startDate = "";
		String endDate = "";
		String lane_detail = "";
		String location = "";

		public void startDocument() {
			dataStringBuffer = new StringBuffer();
		} // end startDocument()

		public void startElement(String uri, String name, String qName,
				Attributes atts) {
			dataStringBuffer.setLength(0);
		} // end startElement()

		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if (localName.equals("EventSubType")
					&& dataStringBuffer.toString().length() > 0) {
				eventClass = dataStringBuffer.toString();
			}
			if (localName.equals("EventType")
					&& dataStringBuffer.toString().length() > 0) {
				eventType = dataStringBuffer.toString();
			}

			if (localName.equals("RoadwayName")
					&& dataStringBuffer.toString().length() > 0) {
				hwy_name = dataStringBuffer.toString();
			}
			if (localName.equals("DirectionOfTravel")
					&& dataStringBuffer.toString().length() > 0) {
				hwy_dir = dataStringBuffer.toString();
				hwy_dir = hwy_dir.toUpperCase().trim();
				hwy_dir = hwy_dir.replaceAll("EASTBOUND", "EB");
				hwy_dir = hwy_dir.replaceAll("EAST", "EB");
				hwy_dir = hwy_dir.replaceAll("WESTBOUND", "WB");
				hwy_dir = hwy_dir.replaceAll("WEST", "WB");
				hwy_dir = hwy_dir.replaceAll("SOUTHBOUND", "SB");
				hwy_dir = hwy_dir.replaceAll("SOUTH", "SB");
				hwy_dir = hwy_dir.replaceAll("NORTHBOUND", "NB");
				hwy_dir = hwy_dir.replaceAll("NORTH", "NB");
				hwy_dir = hwy_dir.replaceAll("BOTH DIRECTIONS", "B");
			}

			if (localName.equals("PrimaryLocation")
					&& dataStringBuffer.toString().length() > 0) {
				cross_from = dataStringBuffer.toString();
			}

			if (localName.equals("SecondaryLocation")
					&& dataStringBuffer.toString().length() > 0) {
				cross_to = dataStringBuffer.toString();
			}

			if (localName.equals("Description")
					&& dataStringBuffer.toString().length() > 0) {
				description = dataStringBuffer.toString();
			}
			
			if ("".equals(description) || description == null) {
				description = eventClass;
			}

			if (localName.equals("CountyName")
					&& dataStringBuffer.toString().length() > 0) {
				county = dataStringBuffer.toString();
			}

			if (localName.equals("Latitude")
					&& dataStringBuffer.toString().length() > 0) {
				slat = dataStringBuffer.toString();
			}

			if (localName.equals("Longitude")
					&& dataStringBuffer.toString().length() > 0) {
				slon = dataStringBuffer.toString();
			}

			// if (localName.equals("TO_LAT")
			// && dataStringBuffer.toString().length() > 0) {
			// elat = dataStringBuffer.toString();
			// }
			//
			// if (localName.equals("TO_LON")
			// && dataStringBuffer.toString().length() > 0) {
			// elon = dataStringBuffer.toString();
			// }

			if (localName.equals("StartDate")
					&& dataStringBuffer.toString().length() > 0) {
				start_time = dataStringBuffer.toString();
			}

			if (localName.equals("PlannedEndDate")
					&& dataStringBuffer.toString().length() > 0) {
				end_time = dataStringBuffer.toString();
			}

			if (localName.equals("LanesAffected")
					&& dataStringBuffer.toString().length() > 0) {
				lane_detail = dataStringBuffer.toString();
			}
			
			if (localName.equals("Location")
					&& dataStringBuffer.toString().length() > 0) {
				location = dataStringBuffer.toString();
			}

			if (localName.equals("Event")) {
				processRecord();
				resetValues();
			}
		}

		public void characters(char[] buffer, int start, int length) {
			dataStringBuffer.append(buffer, start, length);
		} // end

		/*************************************************
		 * Issue a warning.
		 ************************************************/
		public void warning(SAXParseException exception) {
			System.out.println("line " + exception.getLineNumber() + ": "
					+ exception.getMessage());
		} // end warning()

		/*************************************************
		 * Report a parsing error.
		 ************************************************/
		public void error(SAXParseException exception) {
			System.out.println("line " + exception.getLineNumber() + ": "
					+ exception.getMessage());
		} // end error()

		/*************************************************
		 * Report a non-recoverable error.
		 ************************************************/
		public void fatalError(SAXParseException exception) throws SAXException {
			throw new SAXException("line " + exception.getLineNumber() + ": "
					+ exception.getMessage() + " for this record: "
					+ description);
		} // end fatalError()

		/*************************************************
		 * Process a record.
		 ************************************************/
		private void processRecord() {
			IncConRecord incConRecord2 = null;
			incConRecord = new IncConRecord();

			incConRecord.setState(STATE);
			incConRecord.setTimeZone(nyTimeZone);
			incConRecord.setMapUrl(url);

			// Description
			description = description.toUpperCase().trim();
			

			//#13
			if (hwy_name != null) {
				hwy_name = hwy_name.toUpperCase().trim();
				if (hwy_name.equals("N/A") || hwy_name.equals("SYSTEMWIDE")) {
					return;
				}
			}
			String direction = "";
			// String dir = hwy_dir;
			if (description.contains(hwy_name)) {
				if (description.matches("(.*) " + hwy_name
						+ " IN BOTH DIRECTIONS (.*)")) {
					direction = "B";
				}
				if (description.matches("(.*) BOTH DIRECTIONS " + hwy_name
						+ " (.*)")
						|| description.matches("(.*) " + hwy_name
								+ " BOTH DIRECTIONS (.*)")) {
					direction = "B";
				}
				if (description.matches("(?:.*) " + hwy_name
						+ " ((?:EAST|SOUTH|WEST|NORTH)BOUND) .*")) {
					direction = description.replaceAll("(?:.*) " + hwy_name
							+ " ((?:EAST|SOUTH|WEST|NORTH)BOUND) .*", "$1");
				}
				if (description.matches("(?:.*) " + hwy_name
						+ " (EAST|SOUTH|WEST|NORTH) .*")) {
					direction = description.replaceAll("(?:.*) " + hwy_name
							+ " (EAST|SOUTH|WEST|NORTH) .*", "$1");
				}
				if (description
						.matches("(?:.*) ((?:EAST|SOUTH|WEST|NORTH)BOUND) "
								+ hwy_name + " .*")) {
					direction = description.replaceAll(
							"(?:.*) ((?:EAST|SOUTH|WEST|NORTH)BOUND) "
									+ hwy_name + " .*", "$1");
				}
				if (description.matches("(?:.*) (EAST|SOUTH|WEST|NORTH) "
						+ hwy_name + " .*")) {
					direction = description.replaceAll(
							"(?:.*) (EAST|SOUTH|WEST|NORTH) " + hwy_name
									+ " .*", "$1");
				}

				direction = direction.trim();
				if (direction != null && !direction.equals("")) {
					direction = direction.replaceAll("EASTBOUND", "EB");
					direction = direction.replaceAll("EAST", "EB");
					direction = direction.replaceAll("WESTBOUND", "WB");
					direction = direction.replaceAll("WEST", "WB");
					direction = direction.replaceAll("SOUTHBOUND", "SB");
					direction = direction.replaceAll("SOUTH", "SB");
					direction = direction.replaceAll("NORTHBOUND", "NB");
					direction = direction.replaceAll("NORTH", "NB");

					if (direction.matches("(E|S|W|N)B")
							|| direction.matches("B")) {
						hwy_dir = direction;
					}
				} else {
					for (Pattern pattern : dirPatternList) {
						if ((matcher = pattern.matcher(description)).find()) {
							direction = matcher.group(1);
							direction = direction.replaceAll("(.*)"
									+ cross_from.toUpperCase().replace("(", "").replace(")", "") + ".*", "$1");
							if ((matcher = pattern.matcher(direction)).find()) {
								direction = matcher.group(1);
							}
							if (direction
									.matches("(?:.*)\\b((?:EAST|SOUTH|WEST|NORTH)BOUND)\\b(?:.*)")) {
								direction = direction
										.replaceAll(
												"(?:.*)((?:EAST|SOUTH|WEST|NORTH)BOUND)(?:.*)",
												"$1");
							}
							if (direction
									.matches("(?:.*)\\b(EAST|SOUTH|WEST|NORTH)\\b(?:.*)")) {
								direction = direction.replaceAll(hwy_name, "");
								direction = direction.replaceAll(
										"(?:.*)(EAST|SOUTH|WEST|NORTH)(?:.*)",
										"$1");
							}
							if (direction
									.matches("(?:.*)(BOTH DIRECTIONS)(?:.*)")) {
								direction = "B";
							}

							direction = direction.trim();
							if (direction != null && !direction.equals("")) {
								direction = direction.replaceAll("EASTBOUND",
										"EB");
								direction = direction.replaceAll("EAST", "EB");
								direction = direction.replaceAll("WESTBOUND",
										"WB");
								direction = direction.replaceAll("WEST", "WB");
								direction = direction.replaceAll("SOUTHBOUND",
										"SB");
								direction = direction.replaceAll("SOUTH", "SB");
								direction = direction.replaceAll("NORTHBOUND",
										"NB");
								direction = direction.replaceAll("NORTH", "NB");
								if (direction.matches("(E|S|W|N)B")
										|| direction.matches("B")) {
									hwy_dir = direction;
								}
							}
							break;
						}
					}
				}

			}
			//#13

			// description = description.replaceFirst("[^:]*:", "").trim();
			description = description.replaceAll("\\s\\s+", " ").trim();
			if (!description.endsWith(".")) {
				description += ".";
			}
			description = description.replaceAll("\\.\\.", ".");
			description = formatDesc(description);

			// #11
			if (description.startsWith("OTHER ()")) {
				description = description.replaceAll("OTHER \\(\\)",
						"OTHER INCIDENT");
			}
			// end of #11
			//#
			// description= dir +"^^^^^^^"+ direction +"++++++++++" + hwy_dir
			// +"++++++++++"+ description;
			//#
						
			if (description.equals(".")) {
				description = eventClass + ".";
			}
			
			incConRecord.setDescription(description);

			// event sub type
			eventClass = eventClass.toUpperCase().trim();
			
			if (eventSubTypeFilterHash.containsKey(eventClass)) {
				return;
			}
			// event type
			eventType = eventType.toUpperCase().trim();
			if (eventTypeFilterHash.containsKey(eventType)) {
				return;
			}

			// key = eventClass + "_" + eventType;
			// if (incConTypeHash.containsKey(key)) {
			// if (incConTypeHash.get(key).equals(PROP_KEY_CONSTRUCTION_TYPE)) {
			// incConRecord.setType(EventType.CONSTRUCTION);
			// } else if (incConTypeHash.get(key).equals(
			// PROP_KEY_INCIDENT_TYPE)) {
			// incConRecord.setType(EventType.INCIDENT);
			// } else {
			// incConRecord.setType(EventType.TTA);
			// }
			// } else if (incConTypeHash.containsKey(eventClass)) {
			// if (incConTypeHash.get(eventClass).equals(
			// PROP_KEY_CONSTRUCTION_TYPE)) {
			// incConRecord.setType(EventType.CONSTRUCTION);
			// } else if (incConTypeHash.get(eventClass).equals(
			// PROP_KEY_INCIDENT_TYPE)) {
			// incConRecord.setType(EventType.INCIDENT);
			// } else {
			// incConRecord.setType(EventType.TTA);
			// }
			// } else {
			// incConRecord.setType(EventType.TTA);
			// System.out.println("This record is neither Inc nor Con,Event_Class="
			// + eventClass + ",description :" + description);
			// //return;
			// }
			String key = null;
			Iterator<String> iterator = eventTypeMap.keySet().iterator();
			while (iterator.hasNext()) {
				key = iterator.next();
				if (eventClass.equals(key.toString())) {
					if (eventTypeMap.get(key).equals("1")) {
						incConRecord.setType(EventType.INCIDENT);
						break;
					} else if (eventTypeMap.get(key).equals("2")) {
						incConRecord.setType(EventType.CONSTRUCTION);
						break;
					} else {
						incConRecord.setType(EventType.TTA);
					}
				}
			}
			
			//#16
			if(eventClass.equals("") || eventClass ==null){
				Iterator<String> iteratorType = eventTypeMap.keySet().iterator();
				while(iteratorType.hasNext()){
						key = iteratorType.next();
					 if(eventType.equals(key.toString().trim())){
						 if(eventTypeMap.get(key).equals("1")){
							 incConRecord.setType(EventType.INCIDENT);
							 break;
						 }else if(eventTypeMap.get(key).equals("2")){
							 incConRecord.setType(EventType.CONSTRUCTION);
							 break;
						 }else if(eventTypeMap.get(key).equals("3")){
							 incConRecord.setType(EventType.TTA);
							 break;
						 }
					 }
				}
			}
			//#16
			if (incConRecord.getType() == null) {
				//#15
				incConRecord.setType(EventType.TTA);
				//#15
				System.out.println("This record is new EventSubType: " + eventClass
						+ ".");
				
			}

			description = description.replaceAll("COMMENT:.*", "");
			// weekdays
			if (description.matches(".*\\w+DAY\\s+\\bTHRU\\b\\s+\\w+DAY.*")) {
				weekdays = description.replaceAll(
						".*\\s(\\w+DAY)\\s+\\bTHRU\\b\\s+(\\w+DAY).*", "$1-$2");
			} else if (description.contains(" THRU ")) {
				Pattern weekdayPtn = Pattern.compile("\\s*(\\w+DAY)(?:/|,)");
				matcher = weekdayPtn.matcher(description);
				while (matcher.find()) {
					weekdays += matcher.group(1).trim();
					weekdays += ",";
				}
			} else {
				Pattern weekdayPtn = Pattern.compile("(\\b\\w+DAY\\b)\\(S\\)");
				matcher = weekdayPtn.matcher(description);
				while (matcher.find()) {
					weekdays += matcher.group(1).trim();
					weekdays += ",";
				}
			}

			// start_time
			String tempStr = null;
			if (description.contains("STARTING")) {
				tempStr = description
						.replaceAll(
								".*STARTING\\s+(\\d+:\\d+)(?::\\d+)?\\s([AP]M),?\\s+(\\d+/\\d+/\\d+)\\s.*",
								"$3 $1 $2");
				tempStr = tempStr.replaceAll("(?<=/)(?=\\d{2}\\s)", "20");
				start_time = tempStr.replaceAll("(?=\\s[AP]M)", ":00");

			} else if (description.contains("CONTINUOUS")) {
				tempStr = description.replaceAll(
						".*CONTINUOUS\\s(.*)\\sTHRU.*", "$1");
				tempStr = tempStr.replaceAll("\\w+DAY", "").trim();
				tempStr = tempStr.replaceAll("[A-Z]+,", "").trim();
				Calendar cal = Calendar.getInstance(Locale.US);
				if (tempStr.matches("\\w+\\s\\d+\\s\\d+\\s\\d+:\\d+\\s[AP]M")) {
					String date = tempStr.replaceAll("(\\w+\\s\\d+\\s\\d+).*",
							"$1");
					try {
						cal.setTime(START_DATE_FORMAT.parse(date));
						String newDate = cal.get(Calendar.MONTH) + 1 + "/"
								+ cal.get(Calendar.DAY_OF_MONTH) + "/"
								+ cal.get(Calendar.YEAR);
						tempStr = tempStr.replaceAll(date, newDate);
						start_time = tempStr.replaceAll("(?=\\s[AP]M)", ":00");
					} catch (ParseException e) {
						System.out.println("Can't parse time : " + date);
					}
				}
			} 
			//#13
			else if(description
					.matches(".*\\w+DAY\\s\\w+\\s\\d+\\w+,\\s\\d{4} THRU \\w+DAY\\s\\w+\\s\\d+\\w+,\\s\\d{4}.*")){
				if(!formatTime(description)){
					return;
				}
				description = "";
			}
			//#13
			else if (description
					.matches(".*\\s\\w+\\s\\d+\\w+,\\s\\d{4},\\s\\d+:\\d+\\s[AP]M.*")) {
				start_time = description
						.replaceAll(".*\\s(\\w+\\s\\d+\\w+,\\s\\d{4},\\s\\d+:\\d+\\s[AP]M).*", "$1");
				start_time = start_time.replaceAll("(\\d+)[A-Z]+", "$1");
				start_time = start_time.replaceAll(",", "");
				endDate = start_time.replaceAll("(\\w+\\s\\d+\\s\\d{4}).*", "$1");
			}
			// #4
			else if (description.matches(".*FROM\\s(\\d+:\\d+.*)UNTIL.*")) {
				start_time = description.replaceAll(
						".*FROM\\s(\\d+:\\d+.*)UNTIL.*", "$1");
				start_time = start_time.replaceAll("\\.", "");
				start_time = start_time.replaceAll("(?=\\s[AP]M)", ":00");
				Calendar cal = Calendar.getInstance(Locale.US);
				cal.setTimeZone(nyTimeZone);
				String newDate = cal.get(Calendar.MONTH) + 1 + "/"
						+ cal.get(Calendar.DAY_OF_MONTH) + "/"
						+ cal.get(Calendar.YEAR);
				start_time = newDate + " " + start_time.trim();
			}         //BEGIN ON 5/12/14 THROUGH 2/27/15
			else if (description.matches(".*BEGIN ON\\s(\\d/\\d+/\\d+) THROUGH (\\d/\\d+/\\d+)\\b.*")) {
				start_time = description.replaceAll(
						".*BEGIN ON\\s(\\d/\\d+/\\d+) THROUGH (\\d/\\d+/\\d+)\\b.*", "$1");
				end_time = description.replaceAll(
						".*BEGIN ON\\s(\\d/\\d+/\\d+) THROUGH (\\d/\\d+/\\d+)\\b.*", "$2");
				start_time = start_time.replaceAll("(?<=/)(?=\\d{2}$)", "20");
				end_time = end_time.replaceAll("(?<=/)(?=\\d{2}$)", "20");
				start_time = start_time + " 00:00:00 AM";
				end_time = end_time + " 00:00:00 AM";
			}
			// #4
			
			if (start_time != null
					&& start_time
							.matches("\\d+/\\d+/\\d+\\s\\d+:\\d+:\\d+\\s[AP]M")) {
				startDate = start_time
						.replaceAll("(\\d+/\\d+/\\d+)\\s.*", "$1");
			}

			// end time
			if (description.contains("UNTIL")) {
				tempStr = description.replaceAll(".*UNTIL(.*)", "$1").trim();
			} 
			else if (description.contains("THRU")) {
				tempStr = description.replaceAll(".*THRU(.*)", "$1").trim();
			}
			
			if (tempStr != null) {
				if(tempStr.matches(".*\\b\\w+\\s\\d+,\\s?\\d{4}\\b.*")){
					tempStr = tempStr.replaceAll(".*\\b(\\w+\\s\\d+,\\s?\\d{4})\\b.*", "$1").trim();
				}
				tempStr = tempStr.replaceAll("(\\d+:\\d+)([AP]M)", "$1 $2").trim();
				tempStr = tempStr.replaceAll("FURTHER NOTICE.*", "").trim();
				tempStr = tempStr.replaceAll("[,\\.]", "").trim();
				tempStr = tempStr.replaceAll("ON\\s\\w+DAY.*", "").trim();
				tempStr = tempStr.replaceAll("\\bON\\b", "").trim();
				tempStr = tempStr.replaceAll("\\b\\w+DAY\\b", "").trim();
				tempStr = tempStr.replaceAll("(\\d+)[A-Z]+", "$1").trim();
				tempStr = tempStr.replaceAll("\\s\\s+", " ").trim();
				tempStr = tempStr
						.replaceAll("(\\d+:\\d+\\s[AP]M)\\s(\\w+\\s\\d+\\s\\d+).*","$2 $1").trim();
				tempStr = tempStr.replaceAll(
						"(\\d+:\\d+\\s[AP]M)\\s(\\d+/\\d+/\\d+)", "$2 $1").trim();
				tempStr = tempStr.replaceAll("([AP]M).*", "$1").trim();
				tempStr = tempStr.replaceAll("\\s\\s+", " ").trim();
				tempStr = tempStr.replaceAll("(?<=/)(?=\\d{2}\\s)", "20");
				tempStr = tempStr.replaceAll("(?=\\s[AP]M)", ":00");

				Calendar cal = Calendar.getInstance(Locale.US);
				if (tempStr.matches("\\w+\\s\\d+\\s\\d+")
						|| tempStr.matches("\\w+\\s\\d+\\s\\d+\\s\\d+:\\d+:\\d+\\s[AP]M")) {
					String date = tempStr.replaceAll("(\\w+\\s\\d+\\s\\d+).*","$1");
					try {
						cal.setTime(START_DATE_FORMAT.parse(date));
						String newDate = cal.get(Calendar.MONTH) + 1 + "/"
								+ cal.get(Calendar.DAY_OF_MONTH) + "/"
								+ cal.get(Calendar.YEAR);
						end_time = tempStr.replaceAll(date, newDate);
						if (end_time.matches("\\d+/\\d+/\\d+")) {
							end_time += " 00:00:00 AM";
						}
					} catch (ParseException e) {
						System.out.println("Can't parse time : " + date);
					}
				} else if (tempStr
						.matches("\\d+/\\d+/\\d+\\s\\d+:\\d+:\\d+\\s[AP]M")) {
					end_time = tempStr;
				} else if (tempStr
						.matches("\\w+\\s\\d+\\sAT\\s\\d+:\\d+:\\d+\\s[AP]M")) {
					String date = tempStr.replaceAll("(\\w+\\s\\d+\\s).*", "$1"
							+ cal.get(Calendar.YEAR));
					String dateForReplace = tempStr.replaceAll(
							"(\\w+\\s\\d+\\s).*", "$1");
					try {
						cal.setTime(START_DATE_FORMAT.parse(date));
						String newDate = cal.get(Calendar.MONTH) + 1 + "/"
								+ cal.get(Calendar.DAY_OF_MONTH) + "/"
								+ cal.get(Calendar.YEAR);
						end_time = tempStr.replaceAll(dateForReplace, newDate);
						end_time = end_time.replaceAll("AT", "");
						if (end_time.matches("\\d+/\\d+/\\d+")) {
							end_time += " 00:00:00 AM";
						}
					} catch (ParseException e) {
						System.out.println("Can't parse time : " + date);
					}
				} else if (tempStr.matches("\\d+:\\d+:\\d+\\s[AP]M")) {
					if (endDate != null && !endDate.equals("")) {
						end_time = endDate + " " + tempStr;
					} else {
						cal.setTimeZone(nyTimeZone);
						end_time = cal.get(Calendar.MONTH) + 1 + "/"
								+ cal.get(Calendar.DAY_OF_MONTH) + "/"
								+ cal.get(Calendar.YEAR) + " " + tempStr;
					}
				}
			}
			// #7
			if (description
					.matches(".*BETWEEN\\s(\\d+[AP]M)\\s(?:AND|THROUGH)\\s(\\d+[AP]M).*")) {
				start_time = description.replaceAll(
						".*BETWEEN\\s(\\d+[AP]M)\\s(?:AND|THROUGH)\\s(\\d+[AP]M).*", "$1");
				end_time = description.replaceAll(
						".*BETWEEN\\s(\\d+[AP]M)\\s(?:AND|THROUGH)\\s(\\d+[AP]M).*", "$2");
				start_time = start_time.replace("\\.", "");
				start_time = start_time.replaceAll("(\\d+)([AP]M)", "$1"
						+ ":00:00 " + "$2");
				Calendar cal = Calendar.getInstance(Locale.US);
				cal.setTimeZone(nyTimeZone);
				String newDate = cal.get(Calendar.MONTH) + 1 + "/"
						+ cal.get(Calendar.DAY_OF_MONTH) + "/"
						+ cal.get(Calendar.YEAR);
				start_time = newDate + " " + start_time.trim();
				end_time = end_time.replaceAll("\\.", "");
				end_time = end_time.replaceAll("(\\d+)([AP]M)", "$1"
						+ ":00:00 " + "$2");
				end_time = newDate + " " + end_time.trim();
			}
			
			//FROM 7:00 AM TO 5:30 PM, 4/6/15 TO 7/16/15
			if (description.matches(".*FROM\\s(\\d+:\\d+\\s[AP]M)\\sTO\\s(\\d+:\\d+\\s[AP]M).*")) {
				
				start_time = description.replaceAll(
						".*FROM\\s(\\d+:\\d+\\s[AP]M)\\sTO\\s(\\d+:\\d+\\s[AP]M).*", "$1");
				end_time = description.replaceAll(
						".*FROM\\s(\\d+:\\d+\\s[AP]M)\\sTO\\s(\\d+:\\d+\\s[AP]M).*", "$2");
				start_time = start_time.replace("\\.", "");
				start_time = start_time.replaceAll("(\\d+:\\d|)\\s([AP]M)", "$1"
						+ ":00 " + "$2");
				Calendar cal = Calendar.getInstance(Locale.US);
				cal.setTimeZone(nyTimeZone);
				String newDate = "";
				
				if (description.matches(".*FROM\\s(\\d+:\\d+\\s[AP]M)\\sTO\\s(\\d+:\\d+\\s[AP]M)\\sON\\s\\d+/\\d+/\\d{2}.*")) {
					newDate = description.replaceAll(".*FROM\\s(\\d+:\\d+\\s[AP]M)\\sTO\\s(\\d+:\\d+\\s[AP]M)\\sON\\s(\\d+/\\d+/\\d{2}).*", "$3");
					newDate = newDate.replaceAll("(\\d+/\\d+)/(\\d+{2})", "$1/20$2");
				} else {
					newDate = cal.get(Calendar.MONTH) + 1 + "/"
							+ cal.get(Calendar.DAY_OF_MONTH) + "/"
							+ cal.get(Calendar.YEAR);
				}
				
				start_time = newDate + " " + start_time.trim();
				end_time = end_time.replaceAll("\\.", "");
				end_time = end_time.replaceAll("(\\d+:\\d+)\\s([AP]M)", "$1"
						+ ":00 " + "$2");
				end_time = newDate + " " + end_time.trim();
				
				
				if (description.matches(".*\\d+/\\d+/\\d+{2}\\sTO\\s\\d+/\\d+/\\d+{2}.*")) {
					
					String start = description.replaceAll(".*(\\d+/\\d+/\\d+{2})\\sTO\\s(\\d+/\\d+/\\d+{2}).*", "$1");
					String end = description.replaceAll(".*(\\d+/\\d+/\\d+{2})\\sTO\\s(\\d+/\\d+/\\d+{2}).*", "$2");
					start = start.replaceAll("(\\d+/\\d+)/(\\d+{2})", "$1/20$2");
					end = end.replaceAll("(\\d+/\\d+)/(\\d+{2})", "$1/20$2");
					
					Calendar calStart = Calendar.getInstance(Locale.US);
					calStart.setTimeZone(nyTimeZone);
					
					Calendar calEnd = Calendar.getInstance(Locale.US);
					calEnd.setTimeZone(nyTimeZone);
							
					try {
						calStart.setTime(START_TIME_FORMAT.parse(start + " 00:00:00 AM"));
						calEnd.setTime(START_TIME_FORMAT.parse(end + " 11:59:59 PM"));
						
						if (cal.compareTo(calStart) < 0 || cal.compareTo(calEnd) > 0) {
							return;
						}
					} catch (ParseException e) {
						System.out.println("Can't parse time : " + start + end);
					}
				}
			}
			
			// end of #7
			if (end_time != null
					&& end_time.matches("\\d+/\\d+/\\d+\\s\\d+:\\d+:\\d+\\s[AP]M")) {
				endDate = end_time.replaceAll("(\\d+/\\d+/\\d+)\\s.*", "$1");
			}			

			if (startDate.length() > 0 && endDate.length() > 0) {
				if (weekdays != null && weekdays.endsWith(",")) {
					weekdays = weekdays.substring(0, weekdays.length() - 1);
				}
				if (weekdays != null && weekdays.length() > 0) {
					String currentDate = null;
					currentDate = getCurrentDate(startDate, endDate, weekdays);
					if (currentDate != null) {
						start_time = start_time.replaceAll(startDate,currentDate);
						end_time = end_time.replaceAll(endDate, currentDate);
					} else {
						return;
					}
				}
			}

			//#TIME_FORMAT
			if (start_time != null
					&& start_time.matches("\\d+/\\d+/\\d+\\s\\d+:\\d+:\\d+")) {
				startTCTime = new TCTime(TIME_FORMAT, start_time,nyTimeZone);
			}

			if (end_time != null
					&& end_time.matches("\\d+/\\d+/\\d+\\s\\d+:\\d+:\\d+")) {
				endTCTime = new TCTime(TIME_FORMAT, end_time, nyTimeZone);
			}
			
			if (start_time != null
					&& start_time
							.matches("\\w+\\s\\d+\\s\\d{4}\\s\\d+:\\d+\\s[AP]M")) {
				startTCTime = new TCTime(SPECIAL_START_FORMAT, start_time,nyTimeZone);
			}

			if (end_time != null
					&& end_time
							.matches("\\w+\\s\\d+\\s\\d{4}\\s\\d+:\\d+:\\d+\\s[AP]M")) {
				endTCTime = new TCTime(SPECIAL_END_FORMAT, end_time, nyTimeZone);
			}
			
			if (start_time != null
					&& start_time
							.matches("\\d+/\\d+/\\d+\\s\\d+:\\d+:\\d+\\s[AP]M")) {
				startTCTime = new TCTime(START_TIME_FORMAT, start_time,nyTimeZone);
			}
			if (end_time != null) {
				if (end_time.matches("\\d+/\\d+/\\d+\\s\\d+:\\d+:\\d+\\s[AP]M")) {
					endTCTime = new TCTime(START_TIME_FORMAT, end_time,nyTimeZone);
				} else if (end_time
						.matches("\\d+:\\d+\\s[AP]M\\s\\d+/\\d+/\\d+")) {
					endTCTime = new TCTime(END_TIME_FORMAT, end_time,nyTimeZone);
				}
			}

			if (startTCTime != null && endTCTime != null) {
				if (startTCTime.compareTo(endTCTime) > 0) {
					Calendar currentTime = Calendar.getInstance(nyTimeZone,Locale.US);
					int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
					if (currentHour >= 0 && currentHour <= 12) {
						startTCTime.add(-24 * 60 * 60 * 1000);
						//#13
						try {
							if(!checkWeekday(weekdays,startTCTime)){
								startTCTime.add(24 * 60 * 60 * 1000);
								endTCTime.add(24 * 60 * 60 * 1000);
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
						//#13
					} else {
						endTCTime.add(24 * 60 * 60 * 1000);
						//#13
						try {
							if(!checkWeekday(weekdays,endTCTime)){
								endTCTime.add(-24 * 60 * 60 * 1000);
								startTCTime.add(-24 * 60 * 60 * 1000);
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
						//#13
					}

					while (startTCTime.compareTo(endTCTime) > 0) {
						endTCTime.add(24 * 60 * 60 * 1000);
					}
				}
			}

			if (startTCTime != null) {
				incConRecord.setStartTime(startTCTime);
			}
			if (endTCTime != null) {
				incConRecord.setEndTime(endTCTime);
			}

			// mainSt
			if (hwy_name != null) {
				hwy_name = hwy_name.toUpperCase().trim();
				
				if (hwy_name.equals("N/A")) {
					return;
				}
				hwy_name = formatStreet(hwy_name);
				if (lane_detail != null
						&& lane_detail.toUpperCase().contains("SERVICE ROAD")) {
					hwy_name = hwy_name + " SERVICE RD";
				}
				// #5
				if (hwy_name.equals("OTHER") && description != null) {
					if (description.matches(".*\\s+CLOSED\\s+.*"))
						description = description.replaceAll(
								"(.*)\\s+CLOSED\\s+.*", "$1");
					hwy_name = formatStreet(description);
				}

				if (hwy_name.matches("I-\\d+") && description != null) {
					if (description.contains(hwy_name + " EXPRESS LANES")) {
						hwy_name = hwy_name + " EXPRESS LN";
					}
				}
				// end of #5
				incConRecord.setMain_st(hwy_name);
			}
			
			String temp_from = "";
			String temp_to = "";

			// crossfrom
			if (cross_from != null && cross_from != ""
					&& !cross_from.equals("NULL")) {
				cross_from = cross_from.toUpperCase().trim();
				
				if (cross_from.matches(".* AND .* COUNTIES") && (cross_to == null || cross_to.equals(""))) {
					cross_to = cross_from.replaceAll("(.*) AND (.*) COUNTIES", "$2 COUNTY");
					cross_from = cross_from.replaceAll("(.*) AND (.*) COUNTIES", "$1 COUNTY");
				}
				
				if (description.matches("CLOSED FROM .*? TO? .*")) {
					cross_from = description.replaceAll("CLOSED FROM (.*?) TO (.*? STREET) .*", "$1");
					cross_to = description.replaceAll("CLOSED FROM (.*?) TO (.*? STREET) .*", "$2");
				}
				
				temp_from = cross_from;						
				cross_from = formatStreet(cross_from);
				
				if (cross_from.equals(hwy_name)) {					
					if (temp_from.matches(".*;.*;.*")) {
						cross_from = formatStreet(temp_from.replaceAll(".*;.*;(.*)", "$1"));
					} else if (temp_from.matches(".*;.*")) {
						cross_from = formatStreet(temp_from.replaceAll(".*?;(.*)", "$1"));
					} else if (temp_from.matches("EXIT \\d+ .*")) {
						cross_from = formatStreet(temp_from.replaceAll("(EXIT \\d+) .*", "$1"));
					} else if (temp_from.matches(".*/.*")) {
						cross_from = formatStreet(temp_from.replaceAll(".*/(.*)", "$1"));
					}
				}
				
				if (cross_from.equals(hwy_name) && temp_from.contains("START ROUTE")) {
					cross_from = "START ROUTE " + cross_from;
				}
				
				if (cross_from.matches("EXIT\\s\\d+")) {
					if (description.matches(".*\\sAT\\s(.*)\\sST\\s.*"))
						cross_from = formatStreet(description.replaceAll(
								".*\\sAT\\s([\\w\\s])\\sST\\s.*", "$1"
										+ "\\sST"));
				}
				incConRecord.setFrom_st(cross_from);
			}
						
			if (incConRecord.getDescription().matches(".*(?:ON|OFF)-RAMP FROM .* TO I-\\d+ (NORTH|SOUTH|EAST|WEST)BOUND.*")) {				
				hwy_name = incConRecord.getDescription().replaceAll(".*(?:ON|OFF)-RAMP FROM .* TO (I-\\d+) (NORTH|SOUTH|EAST|WEST)BOUND.*", "$1");
				incConRecord.setMain_st(hwy_name);
				cross_from = incConRecord.getDescription().replaceAll(".*(?:ON|OFF)-RAMP FROM (.*) TO I-\\d+ (NORTH|SOUTH|EAST|WEST)BOUND.*", "$1");
				incConRecord.setFrom_st(formatStreet(cross_from));
				hwy_dir = incConRecord.getDescription().replaceAll(".*(?:ON|OFF)-RAMP FROM .* TO I-\\d+ (NORTH|SOUTH|EAST|WEST)BOUND.*", "$1");
				hwy_dir = hwy_dir.substring(0, 1) + "B";
			}

			// crossto
			if (cross_to != null && cross_to.length() > 0
					&& !cross_to.equals("NULL")) {
				cross_to = cross_to.toUpperCase().trim();
				
				temp_to = cross_to;
				cross_to = formatStreet(cross_to);
				
				if (cross_to.equals(hwy_name)) {					
					if (temp_to.matches(".*;.*;.*")) {
						cross_to = formatStreet(temp_to.replaceAll(".*;(.*);.*", "$1"));
					} else if (temp_to.matches(".*;.*")) {
						cross_to = formatStreet(temp_to.replaceAll(".*?;(.*)", "$1"));
					} else if (temp_to.matches("EXIT \\d+ .*")) {
						cross_to = formatStreet(temp_to.replaceAll("(EXIT \\d+) .*", "$1"));
					} else if (temp_to.matches(".*/.*")) {
						cross_to = formatStreet(temp_to.replaceAll(".*/(.*)", "$1"));
					}
				}
				
				if (cross_to.equals(hwy_name) && temp_to.contains("START ROUTE")) {
					cross_to = "START ROUTE " + cross_to;
				}
				
				incConRecord.setTo_st(cross_to);
			}
			
			if (incConRecord.getFrom_st() == null) {
				if (!getMainFromToSt(incConRecord.getDescription(), incConRecord)) {
					if (incConRecord.getDescription().startsWith("BRIDGE CLOSED") && location != null && location.length() > 0
							&& !location.equals("NULL")) {
						if (location.toUpperCase().trim().equals("AT N.Y.& OGDENSBURG")) {
							incConRecord.setFrom_st("N.Y. AND OGDENSBURG");
						} else if (location.toUpperCase().trim().equals("AT TRIB TO RNDOUT CK")) {
							incConRecord.setFrom_st("TRIB TO RNDOUT CRK");
						} else {
							incConRecord.setFrom_st(formatStreet(location.toUpperCase().trim()));
						}
						
						if (incConRecord.getFrom_st() != null && incConRecord.getFrom_st().matches("^[WESN]B .*")) {
							incConRecord.setFrom_dir(incConRecord.getFrom_st().replaceAll("(^[WESN]B) .*", "$1"));
							incConRecord.setFrom_st(incConRecord.getFrom_st().replaceAll("(^[WESN]B) (.*)", "$2"));
						}
						
					} else {
						incConRecord.setFrom_st(incConRecord.getMain_st());
					}					
				}
			}
			
			if (incConRecord.getFrom_st() != null && incConRecord.getTo_st() != null && incConRecord.getFrom_st().equals(incConRecord.getTo_st())) {
				incConRecord.setTo_st(null);
			}
			
			if (incConRecord.getFrom_st() == null) {
				incConRecord.setFrom_st(incConRecord.getMain_st());
			}

			// Start Lat & Lon
			if (slat != "" && !slat.equals("0") && !slat.equals("NULL")
					&& slon != "" && !slon.equals("0") && !slon.equals("NULL")) {
				processLatLon(slat, slon, incConRecord);
			}

			// End Lat & Lon
			if (elat != "" && !elat.equals("0") && !elat.equals("NULL")
					&& elon != "" && !elon.equals("0") && !elon.equals("NULL")) {
				incConRecord.setE_lat(Double.valueOf(elat));
				incConRecord.setE_long(Double.valueOf(elon));
			}

			county = county.toUpperCase().trim();
			incConRecord.setCounty(county);

			// hwy_dir
			if (hwy_dir != null && hwy_dir != "" && !hwy_dir.equals("NULL")) {
				if (hwy_dir.equals("B")) {
					int number = -1;
					if (hwy_name.matches(".*?\\d+.*")) {
						String road = hwy_name.replaceAll(".*?(\\d+).*", "$1");
						if (road != null && road.length() > 0) {
							number = Integer.parseInt(road);
							if (number % 2 == 0) {
								incConRecord.setMain_dir("EB");
								incConRecord2 = incConRecord.clone();
								incConRecord2.setMain_dir("WB");
							} else {
								if (incConRecord.getMain_st().equals("RT-139")) {
									incConRecord.setMain_dir("EB");
									incConRecord2 = incConRecord.clone();
									incConRecord2.setMain_dir("WB");
								} else {
									incConRecord.setMain_dir("SB");
									incConRecord2 = incConRecord.clone();
									incConRecord2.setMain_dir("NB");
								}								
							}
						}
					}
				} else if (hwy_dir.length() > 0 && hwy_dir.matches("[ESWN]B")) {
					incConRecord.setMain_dir(hwy_dir);
				}
			}
			Iterator<String> iteratorDesc = eventTypeExtendMap.keySet()
					.iterator();
			while (iteratorDesc.hasNext()) {
				key = iteratorDesc.next();
				if (description.toUpperCase().contains(key.toString())) {
					if (eventTypeExtendMap.get(key).equals("1")) {
						incConRecord.setType(EventType.INCIDENT);
						break;
					} else if (eventTypeExtendMap.get(key).equals("2")) {
						incConRecord.setType(EventType.CONSTRUCTION);
						break;
					}
				}
			}			
			
			if (incConRecord.getDescription().equals(".")) {
				incConRecord.setDescription(incConRecord.getType().name() + ".");
			}
			
			if (incConRecord.getDescription().equals("OVERWEIGHT.")) {
				return;
			}
			
			if (incConRecord.getDescription().equals("BRIDGE CLOSED.") && bridgeClosedFilter.equals("Y")) {
				return;
			}
			
			// Add IncConRecord to ArrayList;
			if (incConRecord.getType() == EventType.CONSTRUCTION) {
				//#
				if(start_time == null){
					incConRecord.setChecked(99);
					incConRecord.setUnreliable(2);
				} else if(endTCTime == null){
					if(!incConRecord.getDescription().contains("UNTIL FURTHER NOTICE")){
						incConRecord.setChecked(99);
						incConRecord.setUnreliable(2);
					}
				}
				//#
				separateTwoRecords(incConRecord);
				conArrayList.add(incConRecord);
				if (incConRecord2 != null) {
					//#
					if(start_time == null){
						incConRecord2.setChecked(99);
						incConRecord.setUnreliable(2);
					} else if(endTCTime == null){
						if(!incConRecord2.getDescription().contains("UNTIL FURTHER NOTICE")){
							incConRecord2.setChecked(99);
							incConRecord.setUnreliable(2);
						}
					}
					//#
					separateTwoRecords(incConRecord2);
					conArrayList.add(incConRecord2);
				}
			} else if (incConRecord.getType() == EventType.INCIDENT) {
				separateTwoRecords(incConRecord);
				incArrayList.add(incConRecord);
				if (incConRecord2 != null) {
					separateTwoRecords(incConRecord2);
					incArrayList.add(incConRecord2);
				}
			} else if (incConRecord.getType() == EventType.TTA) {
				separateTwoRecords(incConRecord);
				ttaArrayList.add(incConRecord);
				if (incConRecord2 != null) {
					separateTwoRecords(incConRecord2);
					ttaArrayList.add(incConRecord2);
				}
			}
		}// end of processRecord

		//#13
		private boolean formatTime(String description) {
			LinkedHashMap<String, String> timeMap = new LinkedHashMap<String, String>();
			String tempStr = "";
			weekdays = "";
			
			//MONDAY NOVEMBER 18TH, 2013 THRU WEDNESDAY SEPTEMBER 30TH, 2015 WEDNESDAY/ THURSDAY/ FRIDAY/ SATURDAY, 08:00 PM THRU 06:00 AM
			Pattern pattern = Pattern.compile("\\b(\\w+DAY\\s\\w+\\s\\d+\\w+,\\s\\d{4} THRU \\w+DAY\\s\\w+\\s\\d+\\w+,\\s\\d{4},?((\\s?\\w+DAY)\\/?)+,\\s\\d+:\\d+\\s[AP]M THRU \\d+:\\d+\\s[AP]M)");
			matcher = pattern.matcher(description);
			while(matcher.find()){
				tempStr = matcher.group(1).trim();
				tempStr = formatStartEndTime(tempStr);
				if(tempStr != null){
					String[] arr = tempStr.split(tcSeparateSign);
					timeMap.put(arr[0].trim()+tcSeparateSign+arr[1].trim(),arr[2]);
				}
			}
			//MONDAY FEBRUARY 2ND, 2015 THRU FRIDAY FEBRUARY 6TH, 2015, MONDAY THRU FRIDAY, 09:00 AM THRU 03:00 PM
			pattern = Pattern.compile("\\b(\\w+DAY\\s\\w+\\s\\d+\\w+,\\s\\d{4} THRU \\w+DAY\\s\\w+\\s\\d+\\w+,\\s\\d{4},\\s\\w+DAY THRU \\w+DAY,\\s\\d+:\\d+\\s[AP]M THRU \\d+:\\d+\\s[AP]M)");
			matcher = pattern.matcher(description);
			while(matcher.find()){
				tempStr = matcher.group(1).trim();
				tempStr = formatStartEndTime(tempStr);
				if(tempStr != null){
					String[] arr = tempStr.split(tcSeparateSign);
					timeMap.put(arr[0].trim()+tcSeparateSign+arr[1].trim(),arr[2]);
				}
			}
			//SUNDAY NOVEMBER 24TH, 2013 THRU MONDAY SEPTEMBER 28TH, 2015, 08:00 PM THRU 06:00 AM
			pattern = Pattern.compile("\\b(\\w+DAY\\s\\w+\\s\\d+\\w+,\\s\\d{4} THRU \\w+DAY\\s\\w+\\s\\d+\\w+,\\s\\d{4},\\s\\d+:\\d+\\s[AP]M THRU \\d+:\\d+\\s[AP]M)");
			matcher = pattern.matcher(description);
			while(matcher.find()){
				tempStr = matcher.group(1).trim();
				tempStr = formatStartEndTime(tempStr);
				if(tempStr != null){
					String[] arr = tempStr.split(tcSeparateSign);
					timeMap.put(arr[0].trim()+tcSeparateSign+arr[1].trim(),arr[2]);
				}
			}
			
			if(timeMap.size()==1){
				Iterator<String> iterator = timeMap.keySet().iterator();
				while (iterator.hasNext()) {
					String str= iterator.next();
					String[] arr = str.split(tcSeparateSign);
					start_time = arr[0];
					end_time = arr[1];;
					weekdays = timeMap.get(str);
				}
				return true;
			} else if(timeMap.size()>1){ //one day more than one times  10:00am-2:00pm  8:00pm-11:00pm
				Iterator<String> iterator = timeMap.keySet().iterator();
				while (iterator.hasNext()) {
					String str= iterator.next();
					String[] arr = str.split(tcSeparateSign);
					start_time = arr[0];
					end_time = arr[1];;
					weekdays = timeMap.get(str);
					
					startTCTime = new TCTime(START_TIME_FORMAT, start_time,nyTimeZone);
					endTCTime = new TCTime(START_TIME_FORMAT, end_time,nyTimeZone);
					if (startTCTime.compareTo(endTCTime) > 0) {
						Calendar currentTime = Calendar.getInstance(nyTimeZone, Locale.US);
						int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
						if (currentHour >= 0 && currentHour <= 12) {
							startTCTime.add(-24 * 60 * 60 * 1000);
							try {
								if (!checkWeekday(weekdays, startTCTime)) {
									startTCTime.add(24 * 60 * 60 * 1000);
									endTCTime.add(24 * 60 * 60 * 1000);
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}
						} else {
							endTCTime.add(24 * 60 * 60 * 1000);
							try {
								if (!checkWeekday(weekdays, endTCTime)) {
									endTCTime.add(-24 * 60 * 60 * 1000);
									startTCTime.add(-24 * 60 * 60 * 1000);
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
					Calendar calCurrent = Calendar.getInstance(nyTimeZone, Locale.US);
					String currentDate = calCurrent.get(Calendar.MONTH) + 1 + "/"
							+ calCurrent.get(Calendar.DAY_OF_MONTH) + "/"
							+ calCurrent.get(Calendar.YEAR) + " "
							+ calCurrent.get(Calendar.HOUR) + ":"
							+ calCurrent.get(Calendar.MINUTE) + ":"
							+ calCurrent.get(Calendar.SECOND);
					int k = calCurrent.get(Calendar.AM_PM);
					if(k==1){
						currentDate = currentDate + " PM";
					} else {
						currentDate = currentDate + " AM";
					}
					TCTime currentTC = new TCTime(START_TIME_FORMAT, currentDate, nyTimeZone);
					if(startTCTime.compareTo(currentTC)<=0 && endTCTime.compareTo(currentTC)>=0){
						return true;
					} 
				}
			} 
			return false;
		}
		
		private boolean checkWeekday(String weekdayPeriod, TCTime tcTime)
				throws ParseException {
			if (weekdayPeriod == null) {
				return true;
			}

			Calendar currentCal = Calendar.getInstance(nyTimeZone, Locale.US);
			Date date = tcTime.getDate();
			currentCal.setTime(date);

			int currentWeek = currentCal.get(Calendar.DAY_OF_WEEK);

			boolean boo = checkWeekday(currentWeek,weekdayPeriod);
			return boo;
		}
		
		private String formatWeekDays(String description) {
			String weekDays = "";
			if (description.matches(".*\\w+DAY\\s+\\bTHRU\\b\\s+\\w+DAY.*")) {
				weekDays = description.replaceAll(
						".*\\s(\\w+DAY)\\s+\\bTHRU\\b\\s+(\\w+DAY).*", "$1-$2");
			} 
			else if (description.matches(".*\\b(\\w+DAY)\\s\\w+\\s\\d+\\w+,\\s\\d{4} THRU (\\w+DAY)\\s\\w+\\s\\d+\\w+,\\s\\d{4},\\s\\d+:\\d+\\s[AP]M THRU \\d+:\\d+\\s[AP]M.*")) {
				weekDays = description.replaceAll(".*\\b(\\w+DAY)\\s\\w+\\s\\d+\\w+,\\s\\d{4} THRU (\\w+DAY).*", "$1").trim() 
						+ "," + description.replaceAll(".*\\b(\\w+DAY)\\s\\w+\\s\\d+\\w+,\\s\\d{4} THRU (\\w+DAY).*", "$2").trim();
			}
			else if (description.contains(" THRU ")) {
				Pattern weekdayPtn = Pattern.compile("\\s*(\\w+DAY)(?:/|,)");
				matcher = weekdayPtn.matcher(description);
				while (matcher.find()) {
					weekDays += matcher.group(1).trim();
					weekDays += ",";
				}
			} 
			return weekDays;
		}
		
		private String formatStartTime(String description) {
			String tempStr = "";
			String starttime = "";
			tempStr = description.replaceAll(".*\\w+DAY\\s(\\w+\\s\\d+\\w+,\\s\\d{4}) THRU.*", "$1");
			tempStr = tempStr.replaceAll("(\\d+)[A-Z]+", "$1");
			tempStr = tempStr.replaceAll(",", "");
			starttime =  description.replaceAll(".* (\\d+:\\d+ [AP]M) THRU.*","$1");
			tempStr = tempStr +" "+ starttime;
			
			Calendar cal = Calendar.getInstance(Locale.US);
			if (tempStr.matches("\\w+\\s\\d+\\s\\d+\\s\\d+:\\d+\\s[AP]M")) {
				String date = tempStr.replaceAll("(\\w+\\s\\d+\\s\\d+).*",
						"$1");
				try {
					cal.setTime(START_DATE_FORMAT.parse(date));
					String newDate = cal.get(Calendar.MONTH) + 1 + "/"
							+ cal.get(Calendar.DAY_OF_MONTH) + "/"
							+ cal.get(Calendar.YEAR);
					tempStr = tempStr.replaceAll(date, newDate);
					starttime = tempStr.replaceAll("(?=\\s[AP]M)", ":00");
				} catch (ParseException e) {
					System.out.println("Can't parse time : " + date);
				}
			}
			return starttime;
		}
		
		/**
		 * Get the from_st and to_st
		 * 
		 * @param description
		 * @param record
		 * @return
		 * @throws Exception 
		 */
		public boolean getMainFromToSt(String description, IncConRecord record) {
			String fromSt = "";
			String toSt = "";
			boolean flag = false;
			String from_step = "";
			if (description == null) {
				System.out.println("location is null, processFromToSt returns false.");
				return false;
			}

			for (Pattern pattern : from_to_pattern_list) {
				if ((matcher = pattern.matcher(description)).find()) {
					//System.out.println(description);
					from_step = "NO";
					flag = true;
					fromSt = matcher.group(2).trim();
					toSt = matcher.group(3).trim();	
					if (fromSt.matches("\\d+\\s*[AP]\\s*M")) {
						flag = false;
						continue;
					}
					record.setFrom_st(formatStreet(fromSt));
					record.setTo_st(formatStreet(toSt));
					break;
				} else {
					from_step = "YES";
				}
			}// end for
			if (from_step.equals("YES")) {
				for (Pattern pattern : from_pattern_list) {
					if ((matcher = pattern.matcher(description)).find()) {
						flag = true;
						fromSt = matcher.group(2).trim();
						record.setFrom_st(formatStreet(fromSt));
						break;
					}
				}// end for
			}
			return flag;
		}// end method
		
		private String formatEndTime(String description) {
			String tempStr = "";
			String endtime = "";
			tempStr = description.replaceAll(".*\\w+DAY\\s\\w+\\s\\d+\\w+,\\s\\d{4} THRU \\w+DAY\\s(\\w+\\s\\d+\\w+,\\s\\d{4}).*", "$1");
			tempStr = tempStr.replaceAll("(\\d+)[A-Z]+", "$1");
			tempStr = tempStr.replaceAll(",", "");
			endtime =  description.replaceAll(".* \\d+:\\d+ [AP]M THRU (\\d+:\\d+ [AP]M).*","$1");
			tempStr = tempStr +" "+ endtime;

			Calendar cal = Calendar.getInstance(Locale.US);
			if (tempStr.matches("\\w+\\s\\d+\\s\\d+\\s\\d+:\\d+\\s[AP]M")) {
				String date = tempStr.replaceAll("(\\w+\\s\\d+\\s\\d+).*",
						"$1");
				try {
					cal.setTime(START_DATE_FORMAT.parse(date));
					String newDate = cal.get(Calendar.MONTH) + 1 + "/"
							+ cal.get(Calendar.DAY_OF_MONTH) + "/"
							+ cal.get(Calendar.YEAR);
					tempStr = tempStr.replaceAll(date, newDate);
					endtime = tempStr.replaceAll("(?=\\s[AP]M)", ":00");
				} catch (ParseException e) {
					System.out.println("Can't parse time : " + date);
				}
			}
			return endtime;
		}
		
		private String formatStartEndTime(String tempStr) {
			start_time = formatStartTime(tempStr);
			end_time = formatEndTime(tempStr);
			weekdays = formatWeekDays(tempStr);
			if (start_time != null
					&& start_time.matches("\\d+/\\d+/\\d+\\s\\d+:\\d+:\\d+\\s[AP]M")) {
				startDate = start_time.replaceAll("(\\d+/\\d+/\\d+)\\s.*", "$1");
			}
			if (end_time != null
					&& end_time.matches("\\d+/\\d+/\\d+\\s\\d+:\\d+:\\d+\\s[AP]M")) {
				endDate = end_time.replaceAll("(\\d+/\\d+/\\d+)\\s.*", "$1");
			}
			if (startDate.length() > 0 && endDate.length() > 0) {
				if (weekdays.endsWith(",")) {
					weekdays = weekdays.substring(0, weekdays.length() - 1);
				}
				if (weekdays != null && weekdays.length() > 0) {
					String currentDate = null;
					currentDate = getCurrentDate(startDate, endDate, weekdays);
					if (currentDate != null) {
						start_time = start_time.replaceAll(startDate,currentDate);
						end_time = end_time.replaceAll(endDate, currentDate);
						return start_time +tcSeparateSign+ end_time +tcSeparateSign+ weekdays;
					}
				}
			}
			
			return null;
		}
		//end of #13
		
		// #9
		private String formatDesc(String description) {
			if (description.matches(".*<A.*</A>.*")) {
				description = description.replaceAll("(.*)<A.*</A>(.*)", "$1.$2");
			}
			if (description.matches(".*HTTPS?:.*")) {
				int i = description.indexOf("HTTP");
				description = description.substring(0, i).trim();
				i = description.lastIndexOf(".");
				description = description.substring(0, i + 1).trim();
				if (!description.endsWith(".")) {
					description += ".";
				}
			}
			description = description.replaceAll(",\\.", ".");
			return description;
		}

		// end of #9

		// #7
		private void separateTwoRecords(IncConRecord incConRecord) {
			if (incConRecord.getFrom_st() != null
					&& incConRecord.getFrom_st().matches(".*\\sAND\\s.*")) {
				if (incConRecord.getFrom_st().matches(
						".*\\sAND\\s.*COUNTY\\sLN")) {
					IncConRecord incConRecord3 = incConRecord.clone();
					incConRecord.setFrom_st(incConRecord.getFrom_st()
							.replaceAll("(.*)\\sAND\\s.*COUNTY\\sLN",
									"$1" + " COUNTY LN"));
					incConRecord3.setFrom_st(incConRecord.getFrom_st()
							.replaceAll(".*\\sAND\\s(.*)\\sCOUNTY\\sLN",
									"$1" + " COUNTY LN"));
					conArrayList.add(incConRecord3);
				} else if (incConRecord.getFrom_st().matches(
						".*\\sAND\\s.*\\sST")) {
					IncConRecord incConRecord3 = incConRecord.clone();
					incConRecord.setFrom_st(incConRecord.getFrom_st()
							.replaceAll("(.*)\\sAND\\s.*\\sST", "$1" + " ST"));
					incConRecord3.setFrom_st(incConRecord.getFrom_st()
							.replaceAll(".*\\sAND\\s(.*)\\sST", "$1" + " ST"));
					conArrayList.add(incConRecord3);
				}
			}
		}

		// end of #7

		/***
		 * Get current date by checking if current date is between start date
		 * and end date, then if current weekday is in the weekday list.
		 * 
		 * @param startDate
		 * @param endDate
		 * @param weekdays
		 * @return current date
		 */
		private String getCurrentDate(String startDate, String endDate,
				String weekdays) {
			Calendar calCurrent = Calendar.getInstance(nyTimeZone, Locale.US);
			int currentWeekday = calCurrent.get(Calendar.DAY_OF_WEEK);
			String currentDate = calCurrent.get(Calendar.MONTH) + 1 + "/"
					+ calCurrent.get(Calendar.DAY_OF_MONTH) + "/"
					+ calCurrent.get(Calendar.YEAR);
			TCTime startTC = null, endTC = null, currentTC = null;
			startTC = new TCTime(DATE_FORMAT, startDate, nyTimeZone);
			endTC = new TCTime(DATE_FORMAT, endDate, nyTimeZone);
			currentTC = new TCTime(DATE_FORMAT, currentDate, nyTimeZone);
			if (startTC.compareTo(currentTC) <= 0 && endTC.compareTo(currentTC) >= 0) {
				if (weekdays.length() > 0) {
					if (checkWeekday(currentWeekday, weekdays)) {
						return currentDate;
					}
				}
			}

			return null;
		} // end of getCurrentDate

		/***
		 * Check if current weekday is contained by weekday list.
		 * 
		 * @param weekday
		 * @param weekdays
		 * @return true if current weekday is contained by weekday list,
		 *         otherwise, return false
		 */
		private boolean checkWeekday(int weekday, String weekdays) {
			String weekdayStr = "";
			String[] tokens = null;
			String tempDay = null;
			int startDay = -1, endDay = -1;
			Calendar calCurrent = Calendar.getInstance(Locale.US);
			if(weekdays.equals("")){
				return true;
			}
			if (weekdays.contains("-")) {
				tokens = weekdays.split("-");
				try {
					tempDay = tokens[0];
					calCurrent.setTime(WEEKDAY_FORMAT.parse(tempDay));
					startDay = calCurrent.get(Calendar.DAY_OF_WEEK);

					tempDay = tokens[1];
					calCurrent.setTime(WEEKDAY_FORMAT.parse(tempDay));
					endDay = calCurrent.get(Calendar.DAY_OF_WEEK);
					if (startDay <= endDay) {
						for (int i = startDay; i <= endDay; i++) {
							weekdayStr += i;
						}
					} else if (startDay > endDay) {
						for (int i = startDay; i <= 7; i++) {
							weekdayStr += i;
						}
						for (int i = 1; i <= endDay; i++) {
							weekdayStr += i;
						}
					}
				} catch (ParseException e) {
					System.out.println("Can't parse weekday : " + tempDay);
				}
			} else {
				tokens = weekdays.split(",");
				if (tokens.length > 0) {
					for (int i = 0; i < tokens.length; i++) {
						tempDay = tokens[i];
						if (tempDay.equals("WEEKDAY")) {
							weekdayStr += "23456";
						} else {
							try {
								calCurrent.setTime(WEEKDAY_FORMAT
										.parse(tempDay));
								weekdayStr += calCurrent
										.get(Calendar.DAY_OF_WEEK);
							} catch (ParseException e) {
								System.out.println("Can't parse weekday : " + tempDay);
							}
						}
					}
				}
			}
			if (weekdayStr.contains(String.valueOf(weekday))) {
				return true;
			}
			return false;
		} // end of checkWeekday

		/**
		 * Parse the street name of mainSt or crossfrom or crossto
		 * 
		 * @param street
		 * @return The standard street
		 * @see
		 */
		private String formatStreet(String street) {

			String key = "", value = "";
			Iterator<String> iterator = streetAliasMap.keySet().iterator();
			while (iterator.hasNext()) {

				key = iterator.next();
				street = street.trim();
				value = streetAliasMap.get(key);
				street = street.replaceAll(key, value);
			}
			street = street.replaceAll("#", "").trim();
			System.out.println("End of formatStreet:" + street);
			return street;
		} // end of formatStreet

		/**
		 * Reset values when found a new incident
		 * 
		 * @param None
		 * @return None
		 */
		private void resetValues() {
			startTCTime = null;
			endTCTime = null;
			description = "";
			hwy_name = "";
			hwy_dir = "";
			cross_from = "";
			cross_to = "";
			start_time = "";
			end_time = "";
			eventClass = "";
			eventType = "";
			weekdays = "";
			startDate = "";
			endDate = "";
		} // end resetValues()

	} // end class IncidentConstructionHandler

	/**
	 * Locate citycode by lat/lon, set record checked flag to -2, so backend
	 * process will do reverse geocoding. If there is no city can be located by
	 * this lat/lon, save it to output file.
	 * 
	 * @param latitude
	 * @param longitude
	 * @param record
	 *            the record to store lat/lon
	 * @return true if lat/lon be located successfully, otherwise return false.
	 * @exception
	 * @see
	 */
	private boolean processLatLon(String latitude, String longitude,
			IncConRecord record) {
		double lat, lon;

		if (latitude == null || latitude == null || latitude.length() < 1
				|| latitude.length() < 1) {
			return false;
		}

		lat = Double.parseDouble(latitude);
		lon = Double.parseDouble(longitude);

		// Go through each city, locate lat/lon.
		for (int i = 0; i < cityCount; i++) {
			if (insidePolygon(cityLatLon[i], lon, lat)) {
				record.setCity(cityCodes[i]);
				record.setS_lat(lat);
				record.setS_long(lon);
				if (isReverseGeocoding) {
					record.setChecked(REVERSE_GEOCODING_FLAG); // -2 don't
																// geocode,
																// backend
																// process will
																// handle it.
				}
				return true;
			}
		}

		/**
		 * When we are here, it means lat/lon can not be located in any city.
		 * Save lat/lon to output file and return false
		 */
		String error = "lat:" + lat + " lon:" + lon;
		if (!unknownErrorMap.containsKey(error)) {
			unknownErrorMap.put(error, error);
			error = "Can not locate city for: " + error;
			System.out.println(error);
			saveErrors(error + "\n");
		}
		return false;
	}// end of processLatLon

	/**
	 * <pre>
	 *              Calculate all the boundary data by the two points;
	 * </pre>
	 * 
	 * @param tokens
	 * @return String[]
	 */
	private String[] calculateBoundary(String[] tokens) {
		String[] boundaryArray = null;
		String lonLeft, latLeft, lonRight, latRight = null;
		if (tokens.length == 4) {
			latLeft = tokens[0].trim();
			lonLeft = tokens[1].trim();
			latRight = tokens[2].trim();
			lonRight = tokens[3].trim();
			boundaryArray = new String[5];
			boundaryArray[0] = lonLeft + " " + latLeft;
			boundaryArray[1] = lonRight + " " + latLeft;
			boundaryArray[2] = lonRight + " " + latRight;
			boundaryArray[3] = lonLeft + " " + latRight;
			boundaryArray[4] = lonLeft + " " + latLeft;
		} else if (tokens.length > 4) {
			boundaryArray = new String[tokens.length / 2];
			for (int i = 0, j = 0; i < tokens.length; i = i + 2, j++) {
				boundaryArray[j] = tokens[i + 1].trim() + " "
						+ tokens[i].trim();
			}
		}
		return boundaryArray;
	}

	/**
	 * <pre>
	 *       Check if the point inside the polygon or not;
	 * </pre>
	 * 
	 * @param polygon
	 * @param px
	 * @param py
	 * @return boolean
	 */
	private boolean insidePolygon(String[] polygon, double px, double py) {
		int i, j = 0;
		boolean flag = false;
		String[] latlong1 = null, latlong2 = null;
		double x1, x2, y1, y2;
		int length = polygon.length;
		for (i = 0, j = length - 1; i < length; j = i++) {
			latlong1 = polygon[i].split("\\s");
			latlong2 = polygon[j].split("\\s");
			x1 = Double.valueOf(latlong1[0]);
			y1 = Double.valueOf(latlong1[1]);
			x2 = Double.valueOf(latlong2[0]);
			y2 = Double.valueOf(latlong2[1]);

			if ((((y1 <= py) && (py <= y2)) || ((y2 <= py) && (py <= y1)))
					&& (px < (x2 - x1) * (py - y1) / (y2 - y1) + x1))
				flag = !flag;
		}
		return flag;
	} // end of insidePolygon

	/**
	 * Save error message to a output file
	 * 
	 * @param errorMessage
	 * @return None
	 * @exception
	 * @see
	 */
	private void saveErrors(String errorMessage) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(NY_ERROR_RECORDS_FILE, true);
			fw.write(errorMessage);

		} catch (IOException ex) {
			System.out.println("I/O exception: " + ex.getMessage());
		} catch (Exception ex) {
			System.out.println("Can not wrte output file: " + ex.getMessage());
		} finally {
			try {
				if (fw != null) {
					fw.close();
				}
			} catch (IOException e) {
				fw = null;
			}
		}
	}// end of saveErrors

	
	/**
	 * Initianize instance level variables
	 * 
	 * @param None
	 * @return None.
	 * @exception
	 * @see
	 */
/*	private void initVariables() throws Exception {
		// Initialize ArrayList which store IncConRecord and Hashmap store
		// unknown error
		incArrayList = new ArrayList<IncConRecord>();
		conArrayList = new ArrayList<IncConRecord>();
		ttaArrayList = new ArrayList<IncConRecord>();
		unknownErrorMap = new HashMap<String, String>();
		url = new URL(webURL);
*/
		/**
		 * Use "NYC" to get Colorado timezone. This is okay since NY state only
		 * has one timezone, mountain time.
		 */
	/*
		DBConnector.getInstance().setReaderID(READER_ID);
		nyTimeZone = DBUtils.getTimeZone(MARKET, STATE);
		System.out.println("initVariable successfully");
	} // End of initVariables.
*/
	/**
	 * For add extend event type,design by star.
	 * 将properties的列表加入map中_米雷
	 */
	private boolean loadEventTypeExtendProperties() {
		BufferedReader buffReader = null;
		System.out.println("Start to load properties");
		String lineRead;
		eventTypeExtendMap = new HashMap<String, String>();
		try {
			buffReader = new BufferedReader(new FileReader(EVENT_TYPE_EXTEND));

			while ((lineRead = buffReader.readLine()) != null) {
				if (lineRead.length() < 0) {
					System.out.println("Empty or comment line, skipped:" + lineRead);
					continue;
				}
				if (lineRead.startsWith("EXTENDKEYWORD")) {
					continue;
				}
				String[] keyValue = lineRead.split(",");
				if (keyValue.length == 2) {
					eventTypeExtendMap.put(keyValue[0].toUpperCase(),
							keyValue[1].toUpperCase());
				}

			}
			System.out.println("EventTypeExtend loaded successfully");
			buffReader.close();
			return true;

		} catch (FileNotFoundException ex) {
			System.out.println("Properties file:" + EVENT_TYPE_EXTEND
					+ " does not exist, program will terminate now ("
					+ ex.getMessage() + ")");
			return false;
		} catch (Exception ex) {
			System.out.println("Parse properties error, program will terminate now ("
					+ ex.getMessage() + ")");
			return false;
		} finally {
			buffReader = null;
		}

	}

	/**
	 * For add event type.
	 */
	private boolean loadEventTypeProperties() {
		BufferedReader buffReader = null;
		System.out.println("Start to load properties");
		String lineRead;
		eventTypeMap = new HashMap<String, String>();
		try {
			buffReader = new BufferedReader(new FileReader(EVENT_TYPE));

			while ((lineRead = buffReader.readLine()) != null) {
				if (lineRead.length() < tcSeparateSign.length() + 2
						|| lineRead.startsWith("#")) {
					System.out.println("Empty or comment line, skipped:" + lineRead);
					continue;
				}

				String[] keyValue = lineRead.split(tcSeparateSign);
				if (keyValue.length == 2) {
					eventTypeMap.put(keyValue[0].toUpperCase(),
							keyValue[1].toUpperCase());
				}

			}
			System.out.println("EventType loaded successfully");
			buffReader.close();
			return true;

		} catch (FileNotFoundException ex) {
			System.out.println("Properties file:" + EVENT_TYPE
					+ " does not exist, program will terminate now ("
					+ ex.getMessage() + ")");
			return false;
		} catch (Exception ex) {
			System.out.println("Parse properties error, program will terminate now ("
					+ ex.getMessage() + ")");
			return false;
		} finally {
			buffReader = null;
		}

	}

	/**
	 * Load property file,get url,construction and incident type,sleep time
	 * citycode and lat/lon for each city; if there is any exception,program
	 * will terminate,program will run ok only when all properties edit
	 * successfully;
	 * 
	 * @param None
	 * @return true if all properties successfully,otherwise return false.
	 * @exception
	 * @see
	 */
	private boolean loadProperties() {
		String propValue;
		String[] strArray;
		Properties prop = new Properties();
		FileInputStream is = null;
		System.out.println("Start to load properties file");
		try {
			is = new FileInputStream(PROPERTY_FILE);
			prop.load(is);

			// Get web address
			propValue = prop.getProperty(PROP_KEY_WEB_URL);
			if (propValue != null) {
				webURL = propValue.trim();
			}
			System.out.println("webURL :" + webURL);

			// Get construction record type
			incConTypeHash = new HashMap<String, String>();
			propValue = prop.getProperty(PROP_KEY_CONSTRUCTION_TYPE)
					.toUpperCase().trim();
			strArray = propValue.split(",");
			for (String type : strArray) {
				incConTypeHash.put(type, PROP_KEY_CONSTRUCTION_TYPE);
				System.out.println("Construction type :" + type);
			}

			// Get incident record type
			propValue = prop.getProperty(PROP_KEY_INCIDENT_TYPE).toUpperCase()
					.trim();
			strArray = propValue.split(",");
			for (String type : strArray) {
				incConTypeHash.put(type, PROP_KEY_INCIDENT_TYPE);
				System.out.println("Incident type :" + type);
			}

			// Get Tta record type
			propValue = prop.getProperty(PROP_KEY_TTA_TYPE).toUpperCase()
					.trim();
			strArray = propValue.split(",");
			for (String type : strArray) {
				incConTypeHash.put(type, PROP_KEY_TTA_TYPE);
				System.out.println("Tta type :" + type);
			}

			// Get boundary for each city
			propValue = prop.getProperty(PROP_KEY_CITY).trim();
			cityCodes = propValue.split(",");
			cityCount = cityCodes.length;
			if (cityCount < 1) {
				System.out.println("There is no city code in property file,program can not start");
				return false;
			}

			cityLatLon = new String[cityCount][];

			for (int i = 0; i < cityCount; i++) {
				// property key like NYC_LAT_LON
				propValue = prop.getProperty(cityCodes[i] + "_LAT_LON");
				if (propValue != null) {
					propValue = propValue.trim();
					cityLatLon[i] = calculateBoundary(propValue.split(","));
					System.out.println(cityCodes[i] + " LAT/LON: " + propValue);
				}
			}

			// Sleep time is optional,if there is no such value,use default 5
			// minutes
			propValue = prop.getProperty(PROP_KEY_SLEEP_TIME);
			if (propValue != null && propValue.length() > 0) {
				loopSleepTime = Long.parseLong(propValue);
			}
			System.out.println("loopSleep time: " + loopSleepTime);

			// Retry wait time,optional,default is 2 minutes
			propValue = prop.getProperty(PROP_KEY_RETRY_WAIT_TIME);
			if (propValue != null && propValue.length() > 0) {
				retryWaitTime = Long.parseLong(propValue);
			}
			System.out.println("Retry wait time:" + retryWaitTime);

			// If we need do reverse geocoding,default is false
			propValue = prop.getProperty(PROP_KEY_REVERSE_GEOCODING_FLAG);
			if (propValue != null && propValue.length() > 0) {
				isReverseGeocoding = Boolean.valueOf(propValue);
			}
			System.out.println("if do reverse geocoding:" + isReverseGeocoding);

			/**
			 * TrafficCast separate sign, optional, default is
			 * "[TrafficCastSeparateSign]" At least 5 characters
			 */
			propValue = prop.getProperty(PROP_KEY_TC_SEPARATE_SIGN);
			if (propValue != null && propValue.length() >= 5) {
				tcSeparateSign = propValue;
			}
			System.out.println("tcSeparateSign:" + tcSeparateSign);

			// Get event type filter
			eventTypeFilterHash = new HashMap<String, String>();
			propValue = prop.getProperty(PROP_KEY_EVENT_TYPE_FILTER)
					.toUpperCase().trim();
			strArray = propValue.split(";");
			for (String type : strArray) {
				eventTypeFilterHash.put(type, "");
				System.out.println("Event type filter:" + type);
			}

			// Get event sub type filter
			eventSubTypeFilterHash = new HashMap<String, String>();
			propValue = prop.getProperty(PROP_KEY_EVENT_SUB_TYPE_FILTER)
					.toUpperCase().trim();
			strArray = propValue.split(";");
			for (String type : strArray) {
				eventSubTypeFilterHash.put(type, "");
				System.out.println("Event sub type filter:" + type);
			}
			
			// Get bridge closed filter
			propValue = prop.getProperty(PROP_KEY_BRIDGE_CLOSED_FILTER);
			if (propValue != null && propValue.length() > 0) {
				bridgeClosedFilter = propValue.trim();
			}
			System.out.println("bridge closed filter:" + bridgeClosedFilter);

			System.out.println("Properties loaded successfully");
			return true;
		} catch (FileNotFoundException ex) {
			System.out.println("Properties file:" + PROPERTY_FILE
					+ "does not exit,program will terminate now ("
					+ ex.getMessage() + ")");
			return false;
		} catch (IOException ex) {
			System.out.println("Parse properties error,program will terminate now("
					+ ex.getMessage() + ")");
			return false;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				is = null;
			}
		}
	} // end of loadProperties

	// #13
	private boolean loadPatterns() {
		String lineRead;
		String[] keyValue;
		Pattern pattern;
		BufferedReader buffReader = null;
		dirPatternList = new ArrayList<Pattern>();
		from_to_pattern_list = new ArrayList<Pattern>();
		from_pattern_list = new ArrayList<Pattern>();
		System.out.println("Start to load patterns");
		try {
			buffReader = new BufferedReader(new FileReader(PATTERN_FILE));
			while ((lineRead = buffReader.readLine()) != null) {
				if (lineRead.length() < tcSeparateSign.length() + 2
						|| lineRead.startsWith("#")) {
					System.out.println("Empty or comment line, skipped:" + lineRead);
					continue;
				}
				keyValue = lineRead.split(tcSeparateSign);
				if (keyValue != null && keyValue.length > 1
						&& keyValue[0].contains(DIR_PATTERN)) {
					pattern = Pattern.compile(keyValue[1]);
					dirPatternList.add(pattern);
					System.out.println("Add pattern to dirPatternList:"
							+ pattern.toString());
				} else {
					System.out.println("Unknown pattern: " + keyValue[1]);
				}
				
				// Get the from to patterns
				if (keyValue != null && keyValue.length > 1 && keyValue[0].contains(FROM_TO_PATTERN)) {
					pattern = Pattern.compile(keyValue[1]);
					from_to_pattern_list.add(pattern);
					System.out.println("Add pattern to from_to_pattern_list:" + pattern.toString());
				} else {
					System.out.println("Unknown from pattern: " + keyValue[1]);
				}
				// Get the from pattern
				if (keyValue != null && keyValue.length > 1 && keyValue[0].contains(FROM_PATTERN)) {
					pattern = Pattern.compile(keyValue[1]);
					from_pattern_list.add(pattern);
					System.out.println("Add pattern to from_pattern_list:" + pattern.toString());
				} else {
					System.out.println("Unknown from pattern: " + keyValue[1]);
				}
				
				
			} // End of while
			System.out.println("Load patterns successfully");
			return true;
		} catch (FileNotFoundException ex) {
			System.out.println("Patterns file:" + PATTERN_FILE
					+ " does not exist, program will terminate now ("
					+ ex.getMessage() + ")");
			return false;
		} catch (Exception ex) {
			System.out.println("Parse pattern error, program will terminate now ("
					+ ex.getMessage() + ")");
			return false;
		} finally {
			try {
				if (buffReader != null) {
					buffReader.close();
					buffReader = null;
				}// end if
			} catch (Exception e) {
				System.out.println("Error while closing buffReader  " + e.getMessage());
			}

		}
	}

	// #13

	/**
	 * Load Street alias file, will be used to format Road
	 * 
	 * @param None
	 * @return true if loaded successfully, otherwise return false.
	 * @exception
	 * @see
	 */
	private boolean loadStreetAlias() {
		BufferedReader buffReader = null;
		String lineRead;
		String[] keyValue;

		streetAliasMap = new LinkedHashMap<String, String>();
		System.out.println("Start to load street alias");

		try {
			buffReader = new BufferedReader(new FileReader(STREET_ALIAS_FILE));

			while ((lineRead = buffReader.readLine()) != null) {
				if (lineRead.length() < tcSeparateSign.length() + 2
						|| lineRead.startsWith("#")) {
					System.out.println("Empty or comment line, skipped:" + lineRead);
					continue;
				}
				keyValue = lineRead.split(tcSeparateSign);

				if (keyValue != null && keyValue.length > 1) {
					System.out.println("STREET ALIAS: " + keyValue[0] + " = "
							+ keyValue[1]);
					streetAliasMap.put(keyValue[0], keyValue[1]);
				}
			} // End of while

			System.out.println("Load street alias successfully");
			return true;
		} catch (FileNotFoundException ex) {
			System.out.println("Street alias file:" + STREET_ALIAS_FILE
					+ " does not exist, program will terminate now ("
					+ ex.getMessage() + ")");
			return false;
		} catch (Exception ex) {
			System.out.println("Parse street alias error, program will terminate now ("
					+ ex.getMessage() + ")");
			return false;
		} finally {
			try {
				if (buffReader != null) {
					buffReader.close();
				}
			} catch (IOException e) {
				buffReader = null;
			}
		}
	} // End of loadStreetAlias().

	/**
	 * Custom authenticator class to access the XML pages.
	 * 
	 * @param None
	 * @return None
	 * @exception None
	 */
	public class MyAuthenticator extends Authenticator {
		// protected PasswordAuthentication getPasswordAuthentication() {
		// return new PasswordAuthentication(username, password.toCharArray());
		// }
	}

	TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(X509Certificate[] certs, String authType) {
			// Trust always
		}

		public void checkServerTrusted(X509Certificate[] certs, String authType) {
			// Trust always
		}
	} };

	/**
	 * Custom Host name verifier.
	 * 
	 * @param None
	 * @return None
	 * @exception None
	 */
	HostnameVerifier hv = new HostnameVerifier() {
		public boolean verify(String urlHostName, SSLSession session) {
			return true;
		}
	};

	private void trustAllHttpsCertificates() throws Exception {
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new miTM();
		trustAllCerts[0] = tm;
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
				.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
				.getSocketFactory());
	}

	static class miTM implements javax.net.ssl.TrustManager,
			javax.net.ssl.X509TrustManager {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}

	}

} // end class NY_511_IncCon