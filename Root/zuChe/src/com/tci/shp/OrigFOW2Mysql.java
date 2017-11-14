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

public class OrigFOW2Mysql {
    private String country;

    private String folderPath;

    private Connection con = null;

    private String fileSeparator = System.getProperty("file.separator");

    public OrigFOW2Mysql(String country, String folderPath) {
	this.country = country;
	this.folderPath = folderPath;
    }
    
    public void run() throws Exception {
	try {	    
	    con = DBConnector.getInstance().connectToDB();
	    
	    createOrigFOWTable();
	    
	    File folder = new File(folderPath);
	    File[] subFolder = folder.listFiles();
	    for (int i = 0; i < subFolder.length; i++) {
		if (subFolder[i].isDirectory()) {
		    System.out.println("Start to process folder: " + subFolder[i].getName());
		    processOrigFOW(subFolder[i].getName());
		    insertIntoOrigFOWTable(subFolder[i].getName());
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
    
    private void createOrigFOWTable() throws Exception {
	String origFOWTableName = "Raw_OrigFOW_" + country;
	String createTableSQL = "CREATE TABLE `" + origFOWTableName + "` ("
                + "`link_id` bigint(15) default NULL,"
                + "`orig_fow` tinyint(2) default NULL"
                + ") ENGINE=MyISAM DEFAULT CHARSET=utf8";
	
	Statement stmt = con.createStatement();
	stmt.executeUpdate("DROP TABLE IF EXISTS `" + origFOWTableName + "`");
	stmt.executeUpdate(createTableSQL);
	stmt.close();
    }
    
    private boolean processOrigFOW(String subFolderName) throws Exception {
	File folder = new File(folderPath + fileSeparator + subFolderName);
	File[] files = folder.listFiles();
	for (int f = 0; f < files.length; f++) {
	    File file = files[f];
	    if (file.getName().indexOf("___________nw.dbf") != -1) {
		StringBuffer createOrigFOWTableSQL = new StringBuffer("CREATE TABLE `");
		String origFOWTableName = subFolderName + "_OrigFOW_" + country;
		createOrigFOWTableSQL.append(origFOWTableName).append("` (");
		createOrigFOWTableSQL.append("`link_id` bigint(15) default NULL,");
		createOrigFOWTableSQL.append("`orig_fow` tinyint(2) default NULL");
		createOrigFOWTableSQL.append(") ENGINE=MyISAM DEFAULT CHARSET=utf8");

		Statement stmt = con.createStatement();
		stmt.executeUpdate("DROP TABLE IF EXISTS `" + origFOWTableName + "`");
		stmt.executeUpdate(createOrigFOWTableSQL.toString());
		stmt.close();
		
		String insertSQL = "insert into " + origFOWTableName + " values(?,?)";
		PreparedStatement pstm = con.prepareStatement(insertSQL);
		int batchCount = 0;
		
		DbaseFileReader reader = new DbaseFileReader(new FileInputStream(file).getChannel(), false, Charset.forName("UTF-8"));
		while (reader.hasNext()) {
			Row row = reader.readRow();
			int feat_type = (Integer) row.read(1);
			if (feat_type != 4110) {
			    continue;
			}
			pstm.setLong(1, (Long) row.read(0));
			pstm.setInt(2, (Integer) row.read(23));
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
    
    private void insertIntoOrigFOWTable(String subFolderName) throws Exception {
	String RawOrigFOWTableName = "Raw_OrigFOW_" + country;
	String origFOWTableName = subFolderName + "_OrigFOW_" + country;

	String insertSQL = "insert into " + RawOrigFOWTableName + " select * from " + origFOWTableName;
	Statement stmt = con.createStatement();
	stmt.executeUpdate(insertSQL);
	stmt.close();	
    }
}
