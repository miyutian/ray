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

public class ExitNum2Mysql {
    private String country;

    private String folderPath;

    private Connection con = null;

    private String fileSeparator = System.getProperty("file.separator");

    public ExitNum2Mysql(String country, String folderPath) {
	this.country = country;
	this.folderPath = folderPath;
    }
    
    public void run() throws Exception {
	try {	    
	    con = DBConnector.getInstance().connectToDB();
	    
	    createExitNumTable();
	    
	    File folder = new File(folderPath);
	    File[] subFolder = folder.listFiles();
	    for (int i = 0; i < subFolder.length; i++) {
		if (subFolder[i].isDirectory()) {
		    System.out.println("Start to process folder: " + subFolder[i].getName());
		    processSI(subFolder[i].getName());
		    processSP(subFolder[i].getName());
		    insertIntoExitNumTable(subFolder[i].getName());
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
    
    private void createExitNumTable() throws Exception {
	String rawTableName = "Raw_ExitNum_" + country;
	String createTableSQL = "CREATE TABLE `" + rawTableName + "` ("
                + "`link_id` bigint(15) default NULL,"
                + "`fname_base` varchar(150) default NULL"
                + ") ENGINE=MyISAM DEFAULT CHARSET=utf8";
	
	Statement stmt = con.createStatement();
	stmt.executeUpdate("DROP TABLE IF EXISTS `" + rawTableName + "`");
	stmt.executeUpdate(createTableSQL);
	stmt.close();
    }
    
    private boolean processSI(String subFolderName) throws Exception {
	File folder = new File(folderPath + fileSeparator + subFolderName);
	File[] files = folder.listFiles();
	for (int f = 0; f < files.length; f++) {
	    File file = files[f];
	    if (file.getName().indexOf("___________si.dbf") != -1) {
		StringBuffer createTableSQL = new StringBuffer("CREATE TABLE `");
		String tableName = subFolderName + "_si_" + country;
		createTableSQL.append(tableName).append("` (");
		createTableSQL.append("`id` bigint(15) default NULL,");
		createTableSQL.append("`txtcont` varchar(150) default NULL");
		createTableSQL.append(") ENGINE=MyISAM DEFAULT CHARSET=utf8");

		Statement stmt = con.createStatement();
		stmt.executeUpdate("DROP TABLE IF EXISTS `" + tableName + "`");
		stmt.executeUpdate(createTableSQL.toString());
		stmt.close();
		
		String insertSQL = "insert into " + tableName + " values(?,?)";
		PreparedStatement pstm = con.prepareStatement(insertSQL);
		int batchCount = 0;
		
		DbaseFileReader reader = new DbaseFileReader(new FileInputStream(file).getChannel(), false, Charset.forName("UTF-8"));
		while (reader.hasNext()) {
			Row row = reader.readRow();			
			String infoType = (String) row.read(3);
			if (infoType == null || !infoType.equals("4E")) {
			    continue;
			}
			pstm.setLong(1, (Long) row.read(0));
			pstm.setString(2, TCIUtils.convertNullStringToEnptyAndUppercase((String) row.read(5)).trim());
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
    
    private boolean processSP(String subFolderName) throws Exception {
	File folder = new File(folderPath + fileSeparator + subFolderName);
	File[] files = folder.listFiles();
	for (int f = 0; f < files.length; f++) {
	    File file = files[f];
	    if (file.getName().indexOf("___________sp.dbf") != -1) {
		StringBuffer createTableSQL = new StringBuffer("CREATE TABLE `");
		String tableName = subFolderName + "_sp_" + country;
		createTableSQL.append(tableName).append("` (");
		createTableSQL.append("`id` bigint(15) default NULL,");
		createTableSQL.append("`trpelid` bigint(15) default NULL");
		createTableSQL.append(") ENGINE=MyISAM DEFAULT CHARSET=utf8");

		Statement stmt = con.createStatement();
		stmt.executeUpdate("DROP TABLE IF EXISTS `" + tableName + "`");
		stmt.executeUpdate(createTableSQL.toString());
		stmt.close();
		
		String insertSQL = "insert into " + tableName + " values(?,?)";
		PreparedStatement pstm = con.prepareStatement(insertSQL);
		int batchCount = 0;
		
		DbaseFileReader reader = new DbaseFileReader(new FileInputStream(file).getChannel(), false, Charset.forName("UTF-8"));
		while (reader.hasNext()) {
			Row row = reader.readRow();
			pstm.setLong(1, (Long) row.read(0));
			pstm.setLong(2, (Long) row.read(2));
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
    
    private void insertIntoExitNumTable(String subFolderName) throws Exception {
	String RawTableName = "Raw_ExitNum_" + country;
	String siTableName = subFolderName + "_si_" + country;
	String spTableName = subFolderName + "_sp_" + country;

	String insertSQL = "insert into " + RawTableName + " select b.trpelid,a.txtcont from " + siTableName + " a inner join " + spTableName + " b on a.id=b.id";
	Statement stmt = con.createStatement();
	stmt.executeUpdate(insertSQL);
	stmt.close();	
    }
}
