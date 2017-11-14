package com.tci.shp;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.dbf.DbaseFileReader.Row;

import com.tci.dao.DBConnector;
import com.tci.util.TCIUtils;

public class SR2Mysql {
    private String country;

    private String folderPath;

    private Connection con = null;

    private String fileSeparator = System.getProperty("file.separator");

    public SR2Mysql(String country, String folderPath) {
	this.country = country;
	this.folderPath = folderPath;
    }
    
    public void run() throws Exception {
	try {	    
	    con = DBConnector.getInstance().connectToDB();
	    
	    createSRTable();
	    
	    File folder = new File(folderPath);
	    File[] subFolder = folder.listFiles();
	    for (int i = 0; i < subFolder.length; i++) {
		if (subFolder[i].isDirectory()) {
		    System.out.println("Start to process folder: " + subFolder[i].getName());
		    processSR(subFolder[i].getName());
		    insertIntoSRTable(subFolder[i].getName());
		}
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		con.close();
	    } catch (Exception e) {
		System.out.println("    Error: Failed to close DB connection.");
	    }
	}
    }
    
    private void createSRTable() throws Exception {
	String rawTableName = "Raw_SR_" + country;
	String createTableSQL = "CREATE TABLE `" + rawTableName + "` ("
                + "`ID` bigint(15) default NULL,"
                + "`SEQNR` int(5) default NULL,"
                + "`SPEED` int(3) default NULL,"
                + "`SPEEDTYP` char(1) default NULL,"
                + "`VALDIR` tinyint(1) default NULL,"
                + "`VT` tinyint(2) default NULL,"
                + "`VERIFIED` tinyint(1) default NULL"
                + ") ENGINE=MyISAM DEFAULT CHARSET=utf8";
	
	Statement stmt = con.createStatement();
	stmt.executeUpdate("DROP TABLE IF EXISTS `" + rawTableName + "`");
	stmt.executeUpdate(createTableSQL);
	stmt.close();
    }
    
    private boolean processSR(String subFolderName) throws Exception {
	File folder = new File(folderPath + fileSeparator + subFolderName);
	File[] files = folder.listFiles();
	for (int f = 0; f < files.length; f++) {
	    File file = files[f];
	    if (file.getName().indexOf("___________sr.dbf") != -1) {
		String tableName = subFolderName + "_sr_" + country;
		String createTableSQL = "CREATE TABLE `" + tableName + "` ("
	                + "`ID` bigint(15) default NULL,"
	                + "`SEQNR` int(5) default NULL,"
	                + "`SPEED` int(3) default NULL,"
	                + "`SPEEDTYP` char(1) default NULL,"
	                + "`VALDIR` tinyint(1) default NULL,"
	                + "`VT` tinyint(2) default NULL,"
	                + "`VERIFIED` tinyint(1) default NULL"
	                + ") ENGINE=MyISAM DEFAULT CHARSET=utf8";

		Statement stmt = con.createStatement();
		stmt.executeUpdate("DROP TABLE IF EXISTS `" + tableName + "`");
		stmt.executeUpdate(createTableSQL);
		stmt.close();
		
		String insertSQL = "insert into " + tableName + " values(?,?,?,?,?,?,?)";
		PreparedStatement pstm = con.prepareStatement(insertSQL);
		int batchCount = 0;
		
		DbaseFileReader reader = new DbaseFileReader(new FileInputStream(file).getChannel(), false, Charset.forName("UTF-8"));
		while (reader.hasNext()) {
		    Row row = reader.readRow();
			pstm.setLong(1, (Long) row.read(0));
			pstm.setInt(2, (Integer) row.read(1));
			pstm.setInt(3, (Integer) row.read(2));
			pstm.setString(4, (String) row.read(3));
			pstm.setInt(5, (Integer) row.read(4));
			pstm.setInt(6, (Integer) row.read(5));
			pstm.setInt(7, (Integer) row.read(6));
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
		reader.close();
	    }
	}
	return true;
    }
        
    private void insertIntoSRTable(String subFolderName) throws Exception {
	String RawTableName = "Raw_SR_" + country;
	String srTableName = subFolderName + "_sr_" + country;

	String insertSQL = "insert into " + RawTableName + " select * from " + srTableName;
	Statement stmt = con.createStatement();
	stmt.executeUpdate(insertSQL);
	stmt.close();	
    }
}
