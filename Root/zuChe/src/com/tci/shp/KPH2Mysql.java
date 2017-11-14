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

public class KPH2Mysql {
    private String country;

    private String folderPath;

    private Connection con = null;

    private String fileSeparator = System.getProperty("file.separator");

    public KPH2Mysql(String country, String folderPath) {
	this.country = country;
	this.folderPath = folderPath;
    }
    
    public void run() throws Exception {
	try {	    
	    con = DBConnector.getInstance().connectToDB();
	    
	    createKPHTable();
	    
	    File folder = new File(folderPath);
	    File[] subFolder = folder.listFiles();
	    for (int i = 0; i < subFolder.length; i++) {
		if (subFolder[i].isDirectory()) {
		    System.out.println("Start to process folder: " + subFolder[i].getName());
		    processKPH(subFolder[i].getName());
		    insertIntoKPHTable(subFolder[i].getName());
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
    
    private void createKPHTable() throws Exception {
	String rawTableName = "Raw_KPH_" + country;
	String createTableSQL = "CREATE TABLE `" + rawTableName + "` ("
                + "`link_id` bigint(15) default NULL,"
                + "`kph` int(3) default NULL"
                + ") ENGINE=MyISAM DEFAULT CHARSET=utf8";
	
	Statement stmt = con.createStatement();
	stmt.executeUpdate("DROP TABLE IF EXISTS `" + rawTableName + "`");
	stmt.executeUpdate(createTableSQL);
	stmt.close();
    }
    
    private boolean processKPH(String subFolderName) throws Exception {
	File folder = new File(folderPath + fileSeparator + subFolderName);
	File[] files = folder.listFiles();
	for (int f = 0; f < files.length; f++) {
	    File file = files[f];
	    if (file.getName().indexOf("___________nw.dbf") != -1) {
		StringBuffer createTableSQL = new StringBuffer("CREATE TABLE `");
		String tableName = subFolderName + "_kph_" + country;
		createTableSQL.append(tableName).append("` (");
		createTableSQL.append("`id` bigint(15) default NULL,");
		createTableSQL.append("`kph` int(3) default NULL");
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
			int feat_type = (Integer) row.read(1);
			if (feat_type != 4110) {
			    continue;
			}
			pstm.setLong(1, (Long) row.read(0));
			pstm.setInt(2, (Integer) row.read(37));
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
        
    private void insertIntoKPHTable(String subFolderName) throws Exception {
	String RawTableName = "Raw_KPH_" + country;
	String kphTableName = subFolderName + "_kph_" + country;

	String insertSQL = "insert into " + RawTableName + " select * from " + kphTableName;
	Statement stmt = con.createStatement();
	stmt.executeUpdate(insertSQL);
	stmt.close();	
    }
}
