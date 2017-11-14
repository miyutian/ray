package com.tci.location;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;


public class LocationMDB2Mysql {
    
    private Connection mdbCon = null;
    
    private Connection con = null;
    
    private Map<String, String> nameMap = new HashMap<String, String>();
    
    private String mdbFile = "D:\\TCI\\Technical\\Project\\TOMTOMMap\\SHP\\North_America_2016_06\\Documentation\\tlt\\usa\\tlt.mdb";
    
    public static void main(String[] args) {
	try {
		//获取当前系统时间
	    long startTime = System.currentTimeMillis();
	    System.out.println("-----Start-----" + new Date());
	    
	    LocationMDB2Mysql l = new LocationMDB2Mysql();
	    l.connectToMysql();
	    l.mdb2Mysql();
	    l.disconnectMysql();
	    
	    long used = (System.currentTimeMillis() - startTime) / 1000;
   	    System.out.println("    Use " + used / 60 + " mins.");
   	    System.out.println();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    
    private void mdb2Mysql() throws Exception {
	initTables();	
	int totalCount = 0;
	Table table = DatabaseBuilder.open(new File(mdbFile)).getTable("TANA_LT_V45_201606");
	//connectToMdb();
	//String sql = "select * from TANA_LT_V45_201606";
	//Statement stmt = mdbCon.createStatement();
	//ResultSet rs = stmt.executeQuery(sql);
	
	//while (rs.next()) {
	for(Row row : table) {
	    String tableStr = (String) row.get("Table");
	    String tableNameSuff = nameMap.get(tableStr);
	    String locid = formatMDBValue(row.get("LocID"));
	    String type = (String) row.get("Type");
	    String roadnumber = (String) row.get("RoadNumber");
	    String roadname = (String) row.get("RoadName");
	    String firstname = (String) row.get("FirstName");
	    String secondname = (String) row.get("SecondName");
	    String area = formatMDBValue(row.get("Area"));
	    String linearid = formatMDBValue(row.get("LinearID"));
	    String negoff = formatMDBValue(row.get("NegOff"));
	    String posoff = formatMDBValue(row.get("PosOff"));
	    String lat = formatMDBValue(row.get("Lat"));
	    String lon = formatMDBValue(row.get("Long"));
	    String subtype = formatMDBValue(row.get("SubType"));
	    String zip = (String) row.get("Zip");
	    
	    //System.out.println(tableStr + ", " + locid + ", " + lat + "," + lon);
	    
	    //System.out.println(table + ", " + locid + ", " + type + ", " + roadnumber + ", " + roadname + ", " + firstname + ", " + secondname + ", " + area + ", " + linearid 
	    //	    + ", " + negoff + ", " + posoff + ", " + lat + ", " + lon + ", " + subtype + ", " + country + ", " + zip);    
	    
	    String insertSQL = "insert into 16r3_nt_tmc_loc_table_" + tableNameSuff 
		    + "(table_name,loc_id,type_subtype,road_number,road_name,first_name,second_name,area_reference,linear_reference,negative_offset,positive_offset,lat,lon,country,zipcode,RETIRED_TMC) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	    PreparedStatement pstmt = con.prepareStatement(insertSQL);
	    
	    String fnStr = formatMDBValue(firstname);
	    pstmt.setString(1, tableStr);
	    pstmt.setString(2, formatLocID(locid));
	    pstmt.setString(3, type + "." + subtype);
	    pstmt.setString(4, formatMDBValue(roadnumber));
	    pstmt.setString(5, formatMDBValue(roadname));
	    pstmt.setString(6, fnStr);
	    pstmt.setString(7, formatMDBValue(secondname));
	    pstmt.setString(8, formatLocID(area));
	    pstmt.setString(9, formatLocID(linearid));
	    pstmt.setString(10, formatLocID(negoff));
	    pstmt.setString(11, formatLocID(posoff));
	    pstmt.setFloat(12, Float.parseFloat(formatLatLon(lat)));
	    pstmt.setFloat(13, Float.parseFloat(formatLatLon(lon)));
	    String country = "USA";
	    if (tableNameSuff.indexOf('c') >= 0) {
		country = "CAN";
	    } else if (tableNameSuff.indexOf('m') >= 0) {
		country = "MEX";
	    }
	    pstmt.setString(14, country);
	    pstmt.setString(15, zip);
	    int retired_tmc = 0;
	    if (fnStr.toLowerCase().indexOf("(retired)") >= 0 || fnStr.toLowerCase().indexOf("(retirado)") >= 0) {
		retired_tmc = 1;
	    }
	    pstmt.setInt(16, retired_tmc);
	    pstmt.executeUpdate();
	    pstmt.close();
	    totalCount++;
	}
	System.out.println("totalCount: " + totalCount);

	//disconnectMdb();
    }
    
    private String formatMDBValue(Object obj) {
	if (obj == null) {
	    return "";
	}
	return String.valueOf(obj);
    }
    
    private String formatLatLon(String value) {
	if (value == null || value.equals("")) {
	    return "0";
	}
	return value;
    }
    
    private String stringNullToEmpty(String str) {
	if (str == null) {
	    return "";
	}
	return str;
    }
    
    private String formatLocID(String id) {
	if (id == null || id.equals("")) {
	    return "";
	}
	
	String idStr = id;
	if (id.length() < 5) {
	    for (int i = 0; i < 5 - id.length(); i++) {
		idStr = "0" + idStr;
	    }
	}
	return idStr;
    }
    
    private void initTables() throws Exception {	
	nameMap.put("01", "101");
	nameMap.put("02", "102");
	nameMap.put("03", "103");
	nameMap.put("04", "104");
	nameMap.put("05", "105");
	nameMap.put("06", "106");
	nameMap.put("07", "107");
	nameMap.put("08", "108");
	nameMap.put("10", "110");
	nameMap.put("11", "111");
	nameMap.put("12", "112");
	nameMap.put("13", "113");
	nameMap.put("14", "114");
	nameMap.put("15", "115");
	nameMap.put("16", "116");
	nameMap.put("17", "117");
	nameMap.put("18", "118");
	nameMap.put("19", "119");
	nameMap.put("20", "120");
	nameMap.put("21", "121");
	nameMap.put("22", "122");
	nameMap.put("25", "125");
	nameMap.put("26", "126");
	nameMap.put("29", "129");
	nameMap.put("33", "133");
	nameMap.put("32", "832");
	nameMap.put("09", "c09");
	nameMap.put("23", "c23");
	nameMap.put("24", "c24");
	nameMap.put("27", "c27");
	nameMap.put("28", "c28");
	nameMap.put("30", "c30");
	nameMap.put("31", "c31");
	nameMap.put("34", "c34");
	nameMap.put("35", "c35");
	nameMap.put("36", "m36");
	
	for (Entry<String, String> entry : nameMap.entrySet()) {	
		 String createTableSQL = "CREATE TABLE if not exists `16r3_nt_tmc_loc_table_" + entry.getValue() + "` ("     
	                    + "`table_name` varchar(2) default NULL,"     
	                    + "`loc_id` varchar(6) default NULL,"
	                    + "`type_subtype` varchar(6) default NULL,"
	                    + "`road_number` varchar(255) default NULL,"
	                    + "`road_name` varchar(255) default NULL,"
	                    + "`first_name` varchar(255) default NULL,"
	                    + "`second_name` varchar(255) default NULL,"
	                    + "`area_reference` varchar(6) default NULL,"
	                    + "`linear_reference` varchar(6) default NULL,"
	                    + "`seq` int(11) default NULL,"
	                    + "`negative_offset` varchar(6) default NULL,"
	                    + "`positive_offset` varchar(6) default NULL,"
	                    + "`lat` float(11,6) default NULL,"
	                    + "`lon` float(11,6) default NULL,"
	                    + "`country` varchar(3) default NULL,"
	                    + "`state` varchar(2) default NULL,"
	                    + "`county_abbr` varchar(4) default NULL,"
	                    + "`zipcode` varchar(10) default NULL,"
	                    + "`path_id` varchar(6) default NULL,"
	                    + "`path_segment_id` int(11) default NULL,"
	                    + "`RETIRED_TMC` tinyint(1) default '0' COMMENT '1 is true; 0 is false'"
	                    + ") ENGINE=MyISAM DEFAULT CHARSET=utf8"; 
		    Statement stmt = con.createStatement();
		    //stmt.executeUpdate("DROP TABLE IF EXISTS `16r3_nt_tmc_loc_table_" + entry.getValue() + "`");
		    stmt.executeUpdate(createTableSQL);
		    stmt.close();
	}
    }
    
    private void connectToMysql() {
	try {
	    String path = "jdbc:mysql://192.168.200.15:3306/SXM_16r3_tables_tomtom?dontTrackOpenResources=true";
	    Class.forName("com.mysql.jdbc.Driver").newInstance();
	    con = DriverManager.getConnection(path, "trafficcast", "naitou");
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.out.println("Problems happened when connect DataBase");
	    System.exit(1);
	}
    }

    private void disconnectMysql() {
	try {
	    if (con != null || !con.isClosed()) {
		con.close();
		con = null;
	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.exit(1);
	}
    }

    private void connectToMdb() {
	try {
	    String dbUr1 = "jdbc:odbc:driver={Microsoft Access Driver (*.mdb)};DBQ=" + mdbFile;
	    System.out.println(dbUr1);
	    Properties prop = new Properties();
	    //prop.put("charSet", "utf-8");
	    prop.put("user", "");
	    prop.put("password", "");

	    Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
	    mdbCon = DriverManager.getConnection(dbUr1, prop);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.out.println("Problems happened when connect DataBase");
	    System.exit(1);
	}
    }

    private void disconnectMdb() {
	try {
	    if (mdbCon != null || !mdbCon.isClosed()) {
		mdbCon.close();
		mdbCon = null;
	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.exit(1);
	}
    }
}
