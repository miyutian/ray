package com.tci.excel;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.tci.dao.DBConnector;



public class LoadFrontageRDFromExcel {
    public static void main(String[] args) throws Exception {	
   	System.out.println("------------Start----------");
   	String fileName = "d:\\MASTER_frontage_PHX_091417_v3.xlsx";
   	InputStream stream = new FileInputStream(fileName);  
   	Workbook wb = null;  
   	Connection connection = DBConnector.getInstance().connectToDB();
   	String sql = "insert into test.frontage_phx(LINK_ID,FNAME_PREF,FNAME_BASE,FNAME_SUFF,FNAME_TYPE)"
   		 + " values(?,?,?,?,?)";
   	PreparedStatement pstm = connection.prepareStatement(sql);
   	
   	if (fileName.indexOf(".xlsx") != -1) {
               wb = new XSSFWorkbook(stream);  
           }  else if (fileName.indexOf(".xls") != -1) {  
               wb = new HSSFWorkbook(stream);  
           }  
   	
           Sheet sheet = wb.getSheetAt(0);
           int rowNum = sheet.getPhysicalNumberOfRows();
           
           int batchCount = 0;
           for (int i = 1; i < rowNum; i++) {
               System.out.println(i);
               Row row = sheet.getRow(i);
               
               pstm.setString(1, getCellValue(row.getCell(0, Row.CREATE_NULL_AS_BLANK), ""));
               pstm.setString(2, getCellValue(row.getCell(1, Row.CREATE_NULL_AS_BLANK), ""));
               pstm.setString(3, getCellValue(row.getCell(2, Row.CREATE_NULL_AS_BLANK), ""));
               pstm.setString(4, getCellValue(row.getCell(3, Row.CREATE_NULL_AS_BLANK), ""));
               pstm.setString(5, getCellValue(row.getCell(4, Row.CREATE_NULL_AS_BLANK), ""));
              
   	    	    
   	    pstm.addBatch();
   	    batchCount++;
   	    if (batchCount == 4000) {
   		pstm.executeBatch();
   		pstm.clearBatch();
   		batchCount = 0;
   	    }
           }
   	pstm.executeBatch();
   	pstm.close();
   	connection.close();
   	System.out.println("------------End----------");
       }
       
       public static String getCellValue(Cell cell, String defaultValue) {
   	String str = "";	
   	
   	if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
   	    str = defaultValue;
   	} else {
   	    cell.setCellType(Cell.CELL_TYPE_STRING);
   	    str = cell.toString();
   	}	
   	return str.trim().toUpperCase();
       }
}
