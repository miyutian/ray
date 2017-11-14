package com.tci.location;

import java.io.OutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

import com.healthmarketscience.jackcess.CursorBuilder;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


public class LocationMDB2Excel {
    
    private Connection mdbCon = null;
    
    private String mdbFile = "D:\\TCI\\Technical\\Project\\TOMTOMMap\\SHP\\North_America_2016_06\\Documentation\\tlt\\usa\\tlt.mdb";
    
    private String output = "D:\\16r3_LocationTable_TomTom";
    
    public static void main(String[] args) {
	try {
	    long startTime = System.currentTimeMillis();
	    System.out.println("-----Start-----" + new Date());
	    
	    LocationMDB2Excel l = new LocationMDB2Excel();
	    l.mdb2Excel();
	    
	    long used = (System.currentTimeMillis() - startTime) / 1000;
   	    System.out.println("    Use " + used / 60 + " mins.");
   	    System.out.println();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    
    private void mdb2Excel() throws Exception {
 	int totalCount = 0;
 	Table table = DatabaseBuilder.open(new File(mdbFile)).getTable("TANA_LT_V45_201606");
 	
 	String[] tmcIDs = {"01","02","03","04","05","06","07","08","10","11","12","13","14","15","16","17","18","19","20","21","22","25","26","29","33","32","09","23","24","27","28","30","31","34","35","36"};
 	for (int j = 0; j < tmcIDs.length; j++) {  
 	    	Row rowFind = CursorBuilder.findRow(table, Collections.singletonMap("Table", tmcIDs[j]));
 	    	if (rowFind == null) {
 	    	    continue;
 	    	}
         	OutputStream ouputStream = new FileOutputStream(new File(output + "\\" + tmcIDs[j] + ".xls"));
         	WritableWorkbook workbook = Workbook.createWorkbook(ouputStream);
         	
         	WritableSheet sheet = workbook.createSheet("tlt", 0);
         	
         	int labelCount = 0;
         	sheet.addCell(new Label(labelCount++, 0, "Table"));
         	sheet.addCell(new Label(labelCount++, 0, "LocID"));
         	sheet.addCell(new Label(labelCount++, 0, "Type"));
         	sheet.addCell(new Label(labelCount++, 0, "RoadNumber"));
         	sheet.addCell(new Label(labelCount++, 0, "RoadName"));
         	sheet.addCell(new Label(labelCount++, 0, "FirstName"));
         	sheet.addCell(new Label(labelCount++, 0, "SecondName"));
         	sheet.addCell(new Label(labelCount++, 0, "Area"));
         	sheet.addCell(new Label(labelCount++, 0, "LinearID"));
         	sheet.addCell(new Label(labelCount++, 0, "NegOff"));
         	sheet.addCell(new Label(labelCount++, 0, "PosOff"));
         	sheet.addCell(new Label(labelCount++, 0, "Lat"));
         	sheet.addCell(new Label(labelCount++, 0, "Long"));
         	sheet.addCell(new Label(labelCount++, 0, "SubType"));
         	sheet.addCell(new Label(labelCount++, 0, "Country"));
         	sheet.addCell(new Label(labelCount++, 0, "State"));
         	sheet.addCell(new Label(labelCount++, 0, "County"));
         	sheet.addCell(new Label(labelCount++, 0, "Zip"));
         	sheet.addCell(new Label(labelCount++, 0, "Dataset"));
         	sheet.addCell(new Label(labelCount++, 0, "Composed"));
         	sheet.addCell(new Label(labelCount++, 0, "IntRef"));
         
         	int i = 1;
         	for(Row row : table) {
         	    if (!((String) row.get("Table")).equals(tmcIDs[j])) {
         		continue;
         	    }
         	    int count = 0;
         	    sheet.addCell(new Label(count++, i, (String) row.get("Table")));
         	    sheet.addCell(new Label(count++, i, formatMDBValue(row.get("LocID"))));
         	    sheet.addCell(new Label(count++, i, (String) row.get("Type")));
         	    sheet.addCell(new Label(count++, i, (String) row.get("RoadNumber")));
         	    sheet.addCell(new Label(count++, i, (String) row.get("RoadName")));
         	    sheet.addCell(new Label(count++, i, (String) row.get("FirstName")));
         	    sheet.addCell(new Label(count++, i, (String) row.get("SecondName")));
         	    sheet.addCell(new Label(count++, i, formatMDBValue(row.get("Area"))));
         	    sheet.addCell(new Label(count++, i, formatMDBValue(row.get("LinearID"))));
         	    sheet.addCell(new Label(count++, i, formatMDBValue(row.get("NegOff"))));
         	    sheet.addCell(new Label(count++, i, formatMDBValue(row.get("PosOff"))));
         	    sheet.addCell(new Label(count++, i, formatMDBValue(row.get("Lat"))));
         	    sheet.addCell(new Label(count++, i, formatMDBValue(row.get("Long"))));
         	    sheet.addCell(new Label(count++, i, formatMDBValue(row.get("SubType"))));
         	    sheet.addCell(new Label(count++, i, (String) row.get("Country")));
         	    sheet.addCell(new Label(count++, i, (String) row.get("State")));
         	    sheet.addCell(new Label(count++, i, (String) row.get("County")));
         	    sheet.addCell(new Label(count++, i, (String) row.get("Zip")));
         	    sheet.addCell(new Label(count++, i, (String) row.get("Dataset")));
         	    sheet.addCell(new Label(count++, i, (String) row.get("Composed")));
         	    sheet.addCell(new Label(count++, i, formatMDBValue(row.get("IntRef"))));
         	    i++;        	
         	}
         	totalCount += i - 1;

         	workbook.write();
         	workbook.close();
         	ouputStream.flush();
         	ouputStream.close();
 	}	
 	System.out.println("Total count: " + totalCount);

     }
    
    private String formatMDBValue(Object obj) {
	if (obj == null) {
	    return "";
	}
	return String.valueOf(obj);
    }
    
    /*private void mdb2Excel() throws Exception {
	int totalCount = 0;
	connectToMdb();
	Statement stmt = mdbCon.createStatement();
	
	String[] tmcIDs = {"01","02","03","04","05","06","07","08","10","11","12","13","14","15","16","17","18","19","20","21","22","25","26","29","33","32","09","23","24","27","28","30","31","34","35","36"};
	for (int j = 0; j < tmcIDs.length; j++) {           
	    	String countSQL = "select count(*) as c from TANA_LT_V45_201606 where Table='" + tmcIDs[j] + "'";	    	
        	ResultSet countrs = stmt.executeQuery(countSQL);
        	if (countrs.next()) {
        	    int c = countrs.getInt("c");
        	    if (c == 0) {        		
        		countrs.close();
        		continue;
        	    }
        	}
        	countrs.close();
        	
        	OutputStream ouputStream = new FileOutputStream(new File(output + "\\" + tmcIDs[j] + ".xls"));
        	WritableWorkbook workbook = Workbook.createWorkbook(ouputStream);
        	
        	String sql = "select * from TANA_LT_V45_201606 where Table='" + tmcIDs[j] + "'";
        	ResultSet rs = stmt.executeQuery(sql);        	
        	
        	WritableSheet sheet = workbook.createSheet("tlt", 0);
        	
        	int labelCount = 0;
        	sheet.addCell(new Label(labelCount++, 0, "Table"));
        	sheet.addCell(new Label(labelCount++, 0, "LocID"));
        	sheet.addCell(new Label(labelCount++, 0, "Type"));
        	sheet.addCell(new Label(labelCount++, 0, "RoadNumber"));
        	sheet.addCell(new Label(labelCount++, 0, "RoadName"));
        	sheet.addCell(new Label(labelCount++, 0, "FirstName"));
        	sheet.addCell(new Label(labelCount++, 0, "SecondName"));
        	sheet.addCell(new Label(labelCount++, 0, "Area"));
        	sheet.addCell(new Label(labelCount++, 0, "LinearID"));
        	sheet.addCell(new Label(labelCount++, 0, "NegOff"));
        	sheet.addCell(new Label(labelCount++, 0, "PosOff"));
        	sheet.addCell(new Label(labelCount++, 0, "Lat"));
        	sheet.addCell(new Label(labelCount++, 0, "Long"));
        	sheet.addCell(new Label(labelCount++, 0, "SubType"));
        	sheet.addCell(new Label(labelCount++, 0, "Country"));
        	sheet.addCell(new Label(labelCount++, 0, "State"));
        	sheet.addCell(new Label(labelCount++, 0, "County"));
        	sheet.addCell(new Label(labelCount++, 0, "Zip"));
        	sheet.addCell(new Label(labelCount++, 0, "Dataset"));
        	sheet.addCell(new Label(labelCount++, 0, "Composed"));
        	sheet.addCell(new Label(labelCount++, 0, "IntRef"));
        
        	int i = 1;
        	while (rs.next()) {
        	    int count = 0;
        	    sheet.addCell(new Label(count++, i, rs.getString("table")));
        	    sheet.addCell(new Label(count++, i, rs.getString("LocID")));
        	    sheet.addCell(new Label(count++, i, rs.getString("Type")));
        	    sheet.addCell(new Label(count++, i, rs.getString("RoadNumber")));
        	    sheet.addCell(new Label(count++, i, rs.getString("RoadName")));
        	    sheet.addCell(new Label(count++, i, rs.getString("FirstName")));
        	    sheet.addCell(new Label(count++, i, rs.getString("SecondName")));
        	    sheet.addCell(new Label(count++, i, rs.getString("Area")));
        	    sheet.addCell(new Label(count++, i, rs.getString("LinearID")));
        	    sheet.addCell(new Label(count++, i, rs.getString("NegOff")));
        	    sheet.addCell(new Label(count++, i, rs.getString("PosOff")));
        	    sheet.addCell(new Label(count++, i, rs.getString("Lat")));
        	    sheet.addCell(new Label(count++, i, rs.getString("Long")));
        	    sheet.addCell(new Label(count++, i, rs.getString("SubType")));
        	    sheet.addCell(new Label(count++, i, rs.getString("Country")));
        	    sheet.addCell(new Label(count++, i, rs.getString("State")));
        	    sheet.addCell(new Label(count++, i, rs.getString("County")));
        	    sheet.addCell(new Label(count++, i, rs.getString("Zip")));
        	    sheet.addCell(new Label(count++, i, rs.getString("Dataset")));
        	    sheet.addCell(new Label(count++, i, rs.getString("Composed")));
        	    sheet.addCell(new Label(count++, i, rs.getString("IntRef")));
        	    i++;        	
        	}
        	totalCount += i - 1;
        	rs.close();
        	workbook.write();
        	workbook.close();
        	ouputStream.flush();
        	ouputStream.close();
	}	
	System.out.println("Total count: " + totalCount);
	stmt.close();
	disconnectMdb();
    }*/

    private void connectToMdb() {
	try {
	    String dbUr1 = "jdbc:odbc:driver={Microsoft Access Driver (*.mdb)};DBQ=" + mdbFile;
	    System.out.println(dbUr1);
	    Properties prop = new Properties();
	    prop.put("charSet", "utf-8");
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
