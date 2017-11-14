import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.KeyStore.Entry;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.axis.encoding.Base64;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;


public class CHI_gcmXML_RTSpeed {
	
	private static final String fileName = "VDSReport.xml";
	
	/** city* */
	private static final String CITY = "CHI";

	/** state* */
	private static final String STATE = "IL";

	/** reader id* */
	private final String READER_ID = this.getClass().getName();
	
	 // Sql,dotsensor_link_mapping_16r2_harrytest,dotsensor_link_mapping_16r2_phase1
    private final String sql = "insert into dotsensor_link_mapping_16r2_phase1(state,market,laneidx,dotsensorid,district,"
				+ "hwy,hwydir,cross_from,data_source_slat,data_source_slong,cross_to,data_source_elat,data_source_elong,reader_id) " + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	// ArrayList to store speed record
	private ArrayList<Dotsensor> dotsensorList = null;
	
	// Database connection
	private Connection dbConn = null;
	
	// PreperedStatment instance 
	private PreparedStatement pstm = null;

	/** Log4j* */
	private static Logger LOGGER = Logger.getLogger(CHI_gcmXML_RTSpeed.class);

	/** 3 minute sleep period */
	private final long SLEEP_PERIOD = 3 * 60 * 1000;

	Matcher matcher = null;

	/***************************************************************************
	 * This value is added to the wait time eachi time an Exception is caught in
	 * begin().
	 **************************************************************************/
	private final int SEED = 60000;


	/***************************************************************************
	 * This is the main control of the program where the reader iterates in a
	 * loop. It calls another method to get speed data. Then updates the
	 * database, calls another method to resent the value of the number
	 * indicating the numbe of times a record appeared on the website to 0,
	 * clears the arraylist containg the real time speed records and the sleeps
	 * for the amount of time specified by the sleep_time.
	 **************************************************************************/

	private void run() {
		long startTime = 0;

		long actualSleepTime = 0;

		long timeBeforeRestart = 0;

		while (true) {

			try {
				startTime = System.currentTimeMillis();
				connect();

				LOGGER.info("Started connecting to the DOT St. Louis  city for new RTspeed");
				dotsensorList = new ArrayList<Dotsensor>();
				pstm = dbConn.prepareStatement(sql);
				
				saveXml();
				parseXml();
				//storeExcel();
				saveRecordDB();

				actualSleepTime = SLEEP_PERIOD
						- (System.currentTimeMillis() - startTime);

				if (actualSleepTime < 0)
					actualSleepTime = 1000;
				LOGGER.info("Last built on 08/31/2017; ticket Number: #8560");
				LOGGER.info("Sleeping for " + (actualSleepTime / 1000)
						+ " seconds.");

				System.out.println();

				Thread.sleep(actualSleepTime);

				timeBeforeRestart = 0;
			} catch (NoRouteToHostException noRouteExp) {
				LOGGER
						.warn("Internet connection is not available, retrying in 2 minutes");
				try {
					Thread.sleep(2 * 60 * 1000);
				} catch (InterruptedException intExp) {
					LOGGER.log(Level.FATAL,
							"STL_ILDOTxml_RTSpeed thread was interrupted");
				}
			} catch (ConnectException connectExp) {
				LOGGER
						.warn("Connection to STL IL website was refused.....retrying in 2 minutes");
				try {
					Thread.sleep(2 * 60 * 1000);
				} catch (InterruptedException intExp) {
					LOGGER.log(Level.FATAL,
							"STL_ILDOTxml_RTSpeed thread was interrupted");
				}
			} catch (FileNotFoundException fileExp) {
				LOGGER
						.warn("Could not retrieve RTspeed data from STL IL website ....retrying in 2 minutes");
				try {
					Thread.sleep(2 * 60 * 1000);
				} catch (InterruptedException intExp) {
					LOGGER.log(Level.FATAL,
							"STL_ILDOTxml_RTSpeed thread was interrupted");
				}
			} catch (UnknownHostException hostExp) {
				LOGGER
						.warn("Could not establish connectiion with STL IL website...retrying in 2 minutes");
				try {
					Thread.sleep(2 * 60 * 1000);
				} catch (InterruptedException intExp) {
					LOGGER.log(Level.FATAL,
							"STL_ILDOTxml_RTSpeed thread was interrupted");
				}
			} catch (IOException ioExp) {
				LOGGER.warn(ioExp.getMessage() + ".....retrying in 2 minutes");
				try {
					Thread.sleep(2 * 60 * 1000);
				} catch (InterruptedException inExp) {
					LOGGER.log(Level.FATAL,
							"STL_ILDOTxml_RTSpeed Thread was interrupted.");
				}
			} catch (Exception ex) {
				if (timeBeforeRestart == 0)
					timeBeforeRestart += SEED;
				else
					timeBeforeRestart += timeBeforeRestart;
				
				ex.printStackTrace();

				LOGGER.fatal("Unexpected exception : " + ex.getMessage()
						+ "....restarting to parse data in "
						+ (timeBeforeRestart / 1000) + " seconds.", ex);

				try {
					Thread.sleep(timeBeforeRestart);
				} catch (InterruptedException exp) {
					LOGGER.log(Level.FATAL,
							"STL_ILDOTxml_RTSpeed Thread was interrupted!");
				}
			} finally {
				dotsensorList.clear();
				
            	if (pstm != null) {
					try {
						pstm.close();
					} catch (SQLException e) {
						pstm = null;
					}
				}
				disconnect();
            }			
		}
	}

	private void saveXml() throws Exception {
		GZIPInputStream unzipInStream = null;
		InputStream inStream = null;
		BufferedOutputStream buffOutSteam = null;
		OutputStream outStream = null;
		URL url = null;
		HttpsURLConnection conn = null;
		try {
			int BUF_SIZE = 1024;
			byte[] BUFF = new byte[BUF_SIZE];
			int readSize = -1;
			url = new URL("https://www.travelmidwest.com/lmiga/VDSReport.xml.gz");
			conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			String authInfo = "trafficcast:2801coho";
			String authInfoEnc = Base64.encode(authInfo.getBytes());
			conn.setRequestProperty("Authorization", "Basic " + authInfoEnc);
			//InputStream
			inStream = conn.getInputStream();
			unzipInStream = new GZIPInputStream(inStream);
			//OutputStream
			outStream = new FileOutputStream(new File(fileName));
			buffOutSteam = new BufferedOutputStream(outStream);
			while((readSize = unzipInStream.read(BUFF)) > 0){
				buffOutSteam.write(BUFF,0,readSize);
			}
			buffOutSteam.flush();
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			if(unzipInStream != null){
				unzipInStream.close();
				unzipInStream = null;
			}
			if(inStream != null){
				inStream.close();
				inStream = null;
			}
			if(buffOutSteam != null){
				buffOutSteam.close();
				buffOutSteam = null;
			}
			if(outStream != null){
				outStream.close();
				outStream = null;
			}
		}
	}

	/** parse the xml file* */
	private void parseXml() throws Exception {
		SAXReader saxReader = null;
		try{
			saxReader = new SAXReader();
			Document document = saxReader.read(new File(fileName));
			Element root = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> elemList = root.selectNodes("com.gcmtravel.VDSReportElement");
			if(elemList != null){
				for(Element element: elemList){
					parseAddElement(element);
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}	
	}
	
	private void parseAddElement(Element element) {
		String dotsensorid = null;
		try {
			if(element == null){
				return;
			}
			
			String hwyName = null;
			String hwyDir = null;
			String crossFrom = null;
			String slatStr = null;
			String slonStr = null;
			float slat = 0;
			float slon = 0;
			
			Node fieldDeviceID = element.selectSingleNode("descendant::fieldDeviceID");
			Node crossStreetPointLoc = element.selectSingleNode("descendant::crossStreetPointLoc");
			Node latLongPointLoc = element.selectSingleNode("descendant::latLongPointLoc");
			Node landmarkPointLoc = element.selectSingleNode("descendant::landmarkPointLoc");
			Node rampPointLoc = element.selectSingleNode("descendant::rampPointLoc");
			
			Map<String,Object> crossStMap = extractElement(crossStreetPointLoc, "roadName/name","direction","crossStreetName/name","crossStreetName/prefix","crossStreetName/streetType");
			Map<String,Object> coorMap = extractElement(latLongPointLoc, "roadName/name","direction","coord/latitude","coord/longitude");
			Map<String,Object> landMarkMap = extractElement(landmarkPointLoc, "roadName/name","direction","landmarkName");
			Map<String,Object> rampMap = extractElement(rampPointLoc, "fromRoadName/name","fromDirection");
			
			//dotsensorid
			if(fieldDeviceID != null){
				dotsensorid = fieldDeviceID.getText();	
				if(dotsensorid == null || dotsensorid.equals("")){
					LOGGER.info("Dotsensorid is null: " + dotsensorid);
					return;
				}
			} else {
				LOGGER.info("Dotsensorid is null!");
				return;
			}
			//crossFrom
			if(crossStMap != null){
				hwyName = (String) crossStMap.get("roadName/name");
				hwyDir = (String) crossStMap.get("direction");
				String crossName = (String) crossStMap.get("crossStreetName/name");
				String crossPrefix = (String) crossStMap.get("crossStreetName/prefix");
				String streetType = (String) crossStMap.get("crossStreetName/streetType");
				if(crossName != null && !crossName.equalsIgnoreCase("NONE") && !crossName.trim().equals("")){
					if(crossPrefix != null && !crossPrefix.equalsIgnoreCase("NONE") && !crossPrefix.trim().equals("")){
						crossName = crossPrefix + " " + crossName;
					}
					if(streetType != null && !streetType.equalsIgnoreCase("NONE") && !streetType.trim().equals("")){
						crossName = crossName + " " + streetType;
					}
					crossFrom = crossName;
				}
			}
			//lat/long
			if(coorMap != null){
				hwyName = (String) coorMap.get("roadName/name");
				hwyDir = (String) coorMap.get("direction");
				slatStr = (String) coorMap.get("coord/latitude");
				slonStr = (String) coorMap.get("coord/longitude");
			}
			if(crossFrom == null && landMarkMap != null){
				hwyName = (String) landMarkMap.get("roadName/name");
				hwyDir = (String) landMarkMap.get("direction");
				crossFrom = (String) landMarkMap.get("landmarkName");
			}
			if((hwyName == null || hwyName.equals("")) && rampMap != null){
				hwyName = (String) rampMap.get("fromRoadName/name");
			    hwyDir = (String) rampMap.get("fromDirection");
			}
			//Format process
			hwyDir = formatDir(hwyDir);
			hwyName = formatStName(hwyName);
			crossFrom = formatStName(crossFrom);
			slat = formatCoor(slatStr);
			slon = formatCoor(slonStr);
			if(hwyName == null || hwyName.equals("")){
				LOGGER.info("Hwyname is null, Dotsensorid : " + dotsensorid);
				return;
			}
//			if(crossFrom == null){
//				crossFrom = "";
//			}
			if(hwyDir == null){
				hwyDir = "";
			}
			Dotsensor dotsensor = new Dotsensor();
			dotsensor.setState(STATE);
			dotsensor.setMarket(CITY);
			dotsensor.setReader_id(READER_ID);
			dotsensor.setDotsensorid(dotsensorid);
			dotsensor.setLaneidx(0);
			dotsensor.setCrossidx(0);
			dotsensor.setHwy(hwyName);
			dotsensor.setHwydir(hwyDir);
			dotsensor.setCross_from(crossFrom);
			dotsensor.setData_source_slat(slat);
			dotsensor.setData_source_slong(slon);
			dotsensorList.add(dotsensor);
		} catch(Exception e){
		     LOGGER.info("An exception occurs: " + dotsensorid);	
		}
	}
	
	private float formatCoor(String coor) {
		if(coor != null && coor.matches("-?\\d+(\\.\\d+)?")){
			if(coor.matches("-?\\d+\\.\\d+")){
				coor = coor.replaceAll("(-?\\d+)\\.(\\d+)", "$1$2");
			}
			if(coor.replaceAll("-", "").length() > 2){
				coor = coor.replaceAll("(-?\\d{2})(\\d+)", "$1.$2");
			}
			return Float.parseFloat(coor);
		}
		return 0;
	}

	private String formatStName(String hwyName) {
		if(hwyName != null){
			hwyName = hwyName.trim().toUpperCase();
			if(hwyName.matches((".*[\\\\&^<>|\\{\\}\\[\\]#$%!@*].*"))){
				LOGGER.info("Abnormal char : " + hwyName);
			}
		}
		return hwyName;
	}

	private String formatDir(String hwyDir) {
		if(hwyDir != null && !hwyDir.trim().equals("")){
			hwyDir = hwyDir.substring(0, 1) + "B";
			if(hwyDir.matches("[WESN]B")){
				return hwyDir;
			}
		}		
		return "";
	}

	/**
	 * Get element string in text node specified by xpath expression 
	 * @param object  usually an Element 
	 * @param nodePath xpath expression of the node
	 * @return text element of the node
	 */
	public String getElementStr(Object object,String nodePath){
		if(object != null && object instanceof Element && nodePath != null){
			Node node = ((Element)object).selectSingleNode(nodePath);
			if(node != null && node instanceof Element){
			     String nodeText = ((Element)node).getTextTrim();
			     return nodeText;
			}
		}
		return null;
	}

	/**
	 * Get the element text 
	 * @param element  parent element
	 * @param usefulElem xpath array
	 * @return map stored the key value entry<code>{xpath:text value}</code>
	 */
	public Map<String,Object> extractElement(Node element, String... usefulElem){
		Map<String,Object> resultMap = null;
		if(element != null && usefulElem != null && usefulElem.length > 0){
			for (String xpath:usefulElem){
			    if(xpath != null){
			    	String elementText = getElementStr(element, xpath);
			    	if(elementText != null){
			    		if(resultMap == null){
			    			resultMap= new HashMap<String,Object>();
			    		}
			    		resultMap.put(xpath, elementText);
			    	}
			    }
			}
		}
		return resultMap;
	}
	
	/**
	 * store records to excel
	 * @throws IOException
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	private void storeExcel() throws IOException, RowsExceededException, WriteException {
		File file = new File(READER_ID + ".xls");
		WritableWorkbook workbook = Workbook.createWorkbook(file); 
	    WritableSheet sheet = workbook.createSheet("Incidents", 0);
	    int labelCount = 0;
	    sheet.addCell(new Label(labelCount++, 0, "state"));	  
	    sheet.addCell(new Label(labelCount++, 0, "market"));  
	    sheet.addCell(new Label(labelCount++, 0, "laneidx")); 
	    sheet.addCell(new Label(labelCount++, 0, "dotsensorid"));
	    sheet.addCell(new Label(labelCount++, 0, "district"));
	    sheet.addCell(new Label(labelCount++, 0, "hwy"));
	    sheet.addCell(new Label(labelCount++, 0, "hwydir"));
	    sheet.addCell(new Label(labelCount++, 0, "cross_from"));
	    sheet.addCell(new Label(labelCount++, 0, "data_source_slat"));
	    sheet.addCell(new Label(labelCount++, 0, "data_source_slong"));
	    sheet.addCell(new Label(labelCount++, 0, "cross_to"));
	    sheet.addCell(new Label(labelCount++, 0, "data_source_elat"));
	    sheet.addCell(new Label(labelCount++, 0, "data_source_elong"));
	    sheet.addCell(new Label(labelCount++, 0, "reader_id"));
	    
	    for (int i = 0; i < dotsensorList.size(); i ++) {
			int count = 0;
			Dotsensor dotsensor = dotsensorList.get(i);
			
			sheet.addCell(new Label(count++, i + 1, dotsensor.getState()));
			sheet.addCell(new Label(count++, i + 1, dotsensor.getMarket()));
			sheet.addCell(new Label(count++, i + 1, String.valueOf(dotsensor.getLaneidx())));
			sheet.addCell(new Label(count++, i + 1, dotsensor.getDotsensorid()));
			sheet.addCell(new Label(count++, i + 1, dotsensor.getDistrict()));
			sheet.addCell(new Label(count++, i + 1, dotsensor.getHwy()));
			sheet.addCell(new Label(count++, i + 1, dotsensor.getHwydir()));
			sheet.addCell(new Label(count++, i + 1, dotsensor.getCross_from()));
			sheet.addCell(new Label(count++, i + 1, String.valueOf(dotsensor.getData_source_slat())));
			sheet.addCell(new Label(count++, i + 1, String.valueOf(dotsensor.getData_source_slong())));
			sheet.addCell(new Label(count++, i + 1, dotsensor.getCross_to()));
			sheet.addCell(new Label(count++, i + 1, String.valueOf(dotsensor.getData_source_elat())));
			sheet.addCell(new Label(count++, i + 1, String.valueOf(dotsensor.getData_source_elong())));
			sheet.addCell(new Label(count++, i + 1, dotsensor.getReader_id()));
	    }
	    workbook.write();  
	    workbook.close();
	}
	
	/**
	 * Save records into PreperedStatement.
	 * @param 
	 * @throws SQLException
	 */
	private void saveRecordDB() throws SQLException {
		if (dotsensorList == null || dotsensorList.size() == 0) {
			LOGGER.warn("No records in datasource!");
			return;
		}
		
		LOGGER.info(dotsensorList.size() + " will be saved");
		// loop the records
		for (int i = 0; i < dotsensorList.size(); i ++) {
			Dotsensor dotsensor = dotsensorList.get(i);
			if (dotsensor != null) {
				pstm.setString(1, dotsensor.getState());
				pstm.setString(2, dotsensor.getMarket());
				pstm.setInt(3, dotsensor.getLaneidx());
				pstm.setString(4, dotsensor.getDotsensorid());
				pstm.setString(5, dotsensor.getDistrict());
				pstm.setString(6, dotsensor.getHwy());
				pstm.setString(7, dotsensor.getHwydir());
				pstm.setString(8, dotsensor.getCross_from());
				pstm.setDouble(9, dotsensor.getData_source_slat());
				pstm.setDouble(10, dotsensor.getData_source_slong());
				pstm.setString(11, dotsensor.getCross_to());
				pstm.setDouble(12, dotsensor.getData_source_elat());
				pstm.setDouble(13, dotsensor.getData_source_elong());
				pstm.setString(14, dotsensor.getReader_id());
				pstm.addBatch();
			}
		}
		pstm.executeBatch();
	} // end of saveRecordDB
	
	/**
	 * Get database connection.
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private void connect() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		dbConn = DriverManager.getConnection("jdbc:mysql://192.168.200.15:3306/incident_db?dontTrackOpenResources=true", 
				"trafficcast", "naitou");
	} // end of connect
	
	/**
	 * Disconnect with db.
	 */
	private void disconnect() {
		if (dbConn != null) {
			try {
				dbConn.close();
			} catch (SQLException e) {
				dbConn = null;
			}
		}
	} // end of disconnect

	/** main function */
	public static void main(String[] args) throws Exception {
		CHI_gcmXML_RTSpeed rtspeed = new CHI_gcmXML_RTSpeed();
		rtspeed.run();
	}
}
