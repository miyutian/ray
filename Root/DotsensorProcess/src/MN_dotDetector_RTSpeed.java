import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.sun.accessibility.internal.resources.accessibility;

/**
 * <pre>
 *                     
 * </pre>
 * 
 * @author 
 * @version dom4j-1.6.1.jar
 * @since 
 * 
 */
public class MN_dotDetector_RTSpeed {


	/** city* */
	private static final String CITY = "MIN";

	/** state* */
	private static final String STATE = "MN";

	/** reader id* */
	private static final String READER_ID = "MN_dotDetector_RTSpeed";
	
	 // Sql
    private String sql = "insert into dotsensor_link_mapping_16r2_phase1(state,market,laneidx,dotsensorid,district,"
				+ "hwy,hwydir,cross_from,data_source_slat,data_source_slong,cross_to,data_source_elat,data_source_elong,reader_id) " + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	// ArrayList to store speed record
	public ArrayList<Dotsensor> dotsensorList = null;
	
	// Database connection
	private Connection dbConn = null;
	
	// PreperedStatment instance 
	private PreparedStatement pstm = null;

	/** Log4j* */
	private static Logger LOGGER = Logger.getLogger(IN_dot_ArchiveSpeed.class);

	/** 3 minute sleep period */
	private final long SLEEP_PERIOD = 3 * 60 * 1000;

	Matcher matcher = null;

	/***************************************************************************
	 * This value is added to the wait time eachi time an Exception is caught in
	 * begin().
	 **************************************************************************/
	private final int SEED = 60000;

	private MN_dotDetector_RTSpeed() {
		
	}

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

				LOGGER
						.info("Started connecting to the DOT St. Louis  city for new RTspeed");
				dotsensorList = new ArrayList<Dotsensor>();
				pstm = dbConn.prepareStatement(sql);
				
				parseXml();
				//storeExcel();
				saveRecordDB();

				actualSleepTime = SLEEP_PERIOD
						- (System.currentTimeMillis() - startTime);

				if (actualSleepTime < 0)
					actualSleepTime = 1000;
				LOGGER.info("Last built on 08/16/2017; ticket Number: #8560");
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

	/** parse the xml file* */
	private void parseXml() throws Exception {
		File file = new File("DataSource/Inventory1111.xml");
        SAXReader reader=new SAXReader();
        //read xml to Document
        Document doc=reader.read(file);
        //get xml root node
        Element rootElement=doc.getRootElement();
 
    	File f = new File("DataSource/Ray.txt"); 
    	BufferedWriter output = new BufferedWriter(new FileWriter(f));
        for (Iterator iter = rootElement.elementIterator(); iter.hasNext();)
        {
           	
        	Element hwynode = (Element) iter.next();
            for (Iterator iterNext = hwynode.elementIterator(); iterNext.hasNext();) {
            	// Set Hwy,Hwydir
            	
            	Element LatLon = (Element) iterNext.next();
				for (Iterator itersext = LatLon.elementIterator(); itersext.hasNext();) {
					Dotsensor dotsensor = new Dotsensor();
	            	
	            	dotsensor.setState("MN");
	            	dotsensor.setMarket("MIN");
	        		dotsensor.setReader_id("MN_dotStation_RTSpeed");
	            	dotsensor.setHwy(hwynode.selectSingleNode("attribute::route").getText());
					dotsensor.setHwydir(hwynode.selectSingleNode("attribute::dir").getText());
					// set crossfrom,lat,lon
					dotsensor.setCross_from(LatLon.selectSingleNode("attribute::label").getText());
					dotsensor.setData_source_slat(Float.parseFloat(LatLon.selectSingleNode("attribute::lat").getText()));   
					dotsensor.setData_source_slong(Float.parseFloat(LatLon.selectSingleNode("attribute::lon").getText()));
					Element sensorid = (Element) itersext.next();
					if ("detector".equals(sensorid.getName())){
						// set dotsendorid
						dotsensor.setDotsensorid(sensorid.selectSingleNode("attribute::name").getText());	

						// set laneidx
						String rampType = "";
						if ("Entrance".equals(LatLon.selectSingleNode("attribute::n_type").getText())){
            				rampType = "-3";
            			}  
            			if ("Exit".equals(LatLon.selectSingleNode("attribute::n_type").getText())){
            				rampType = "-4";
            			}
            			if ("0".equals(sensorid.selectSingleNode("attribute::lane").getText())){
            				if ("".equals(rampType)){
            					dotsensor.setLaneidx(0);
            				}else{
            					dotsensor.setLaneidx(Integer.parseInt(rampType));
            				}
            				
            			}
            			if (!"0".equals(sensorid.selectSingleNode("attribute::lane").getText())){
            				dotsensor.setLaneidx(Integer.parseInt(rampType+sensorid.selectSingleNode("attribute::lane").getText()));
            			}
						dotsensorList.add(dotsensor);	
						
								
						

					}									
				}
            	
				
            }
              
        }
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
		System.out.println("dotsensorList.size()"+dotsensorList.size());
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
		dbConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?dontTrackOpenResources=true", 
				"root", "");
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

	/** main function* */
	public static void main(String[] args) {
		MN_dotDetector_RTSpeed rtspeed = new MN_dotDetector_RTSpeed();
		rtspeed.run();

		
	}
}