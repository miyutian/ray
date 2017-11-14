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

public class Maneuver2Mysql {
    private String country;

    private String folderPath;

    private Connection con = null;

    private String fileSeparator = System.getProperty("file.separator");

    public Maneuver2Mysql(String country, String folderPath) {
	this.country = country;
	this.folderPath = folderPath;
    }
    
    public void run() throws Exception {
	try {	    
	    con = DBConnector.getInstance().connectToDB();
	    
	    createRawManeuverTable();
	    
	    File folder = new File(folderPath);
	    File[] subFolder = folder.listFiles();
	    for (int i = 0; i < subFolder.length; i++) {
		if (subFolder[i].isDirectory()) {
		    System.out.println("Start to process folder: " + subFolder[i].getName());
		    processMn(subFolder[i].getName());
		    processMp(subFolder[i].getName());
		    insertIntoManeuverTable(subFolder[i].getName());
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
    
    private void createRawManeuverTable() throws Exception {
	String maneuverTableName = "Raw_Maneuver_" + country;
	String createTableSQL = "CREATE TABLE `" + maneuverTableName + "` ("
                + "`ID` bigint(15) default NULL,"
                + "`FEATTYP` int(4) default NULL,"
                + "`BIFTYP` int(1) default NULL,"
                + "`PROMANTYP` int(1) default NULL,"
                + "`JNCTID` bigint(15) default NULL,"
                + "`SEQNR` int(4) default NULL,"
                + "`TRPELID` bigint(15) default NULL,"
                + "`TRPELTYP` int(4) default NULL"
                + ") ENGINE=MyISAM DEFAULT CHARSET=utf8";
	
	Statement stmt = con.createStatement();
	stmt.executeUpdate("DROP TABLE IF EXISTS `" + maneuverTableName + "`");
	stmt.executeUpdate(createTableSQL);
	stmt.close();
    }
    
    private boolean processMn(String subFolderName) throws Exception {
	File folder = new File(folderPath + fileSeparator + subFolderName);
	File[] files = folder.listFiles();
	for (int f = 0; f < files.length; f++) {
	    File file = files[f];
	    if (file.getName().indexOf("___________mn.dbf") != -1) {
		StringBuffer createMnTableSQL = new StringBuffer("CREATE TABLE `");
		String mnTableName = subFolderName + "_mn_" + country;
		createMnTableSQL.append(mnTableName).append("` (");
		createMnTableSQL.append("`ID` bigint(15) default NULL,");
		createMnTableSQL.append("`FEATTYP` int(4) default NULL,");
		createMnTableSQL.append("`BIFTYP` int(1) default NULL,");
		createMnTableSQL.append("`PROMANTYP` int(1) default NULL,");
		createMnTableSQL.append("`JNCTID` bigint(15) default NULL,");
		createMnTableSQL.append("KEY `id` (`ID`)");
		createMnTableSQL.append(") ENGINE=MyISAM DEFAULT CHARSET=utf8");

		Statement stmt = con.createStatement();
		stmt.executeUpdate("DROP TABLE IF EXISTS `" + mnTableName + "`");
		stmt.executeUpdate(createMnTableSQL.toString());
		stmt.close();
		
		String insertSQL = "insert into " + mnTableName + " values(?,?,?,?,?)";
		PreparedStatement pstm = con.prepareStatement(insertSQL);
		int batchCount = 0;
		
		DbaseFileReader reader = new DbaseFileReader(new FileInputStream(file).getChannel(), false, Charset.forName("UTF-8"));
		while (reader.hasNext()) {
			Row row = reader.readRow();
			pstm.setLong(1, (Long) row.read(0));
			pstm.setInt(2, (Integer) row.read(1));
			pstm.setInt(3, (Integer) row.read(2));
			pstm.setInt(4, (Integer) row.read(3));
			pstm.setLong(5, (Long) row.read(4));
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
    
    private boolean processMp(String subFolderName) throws Exception {
	File folder = new File(folderPath + fileSeparator + subFolderName);
	File[] files = folder.listFiles();
	for (int f = 0; f < files.length; f++) {
	    File file = files[f];
	    if (file.getName().indexOf("___________mp.dbf") != -1) {
		StringBuffer createMpTableSQL = new StringBuffer("CREATE TABLE `");
		String mpTableName = subFolderName + "_mp_" + country;
		createMpTableSQL.append(mpTableName).append("` (");
		createMpTableSQL.append("`ID` bigint(15) default NULL,");
		createMpTableSQL.append("`SEQNR` int(4) default NULL,");
		createMpTableSQL.append("`TRPELID` bigint(15) default NULL,");
		createMpTableSQL.append("`TRPELTYP` int(4) default NULL,");
		createMpTableSQL.append("KEY `id` (`ID`)");
		createMpTableSQL.append(") ENGINE=MyISAM DEFAULT CHARSET=utf8");

		Statement stmt = con.createStatement();
		stmt.executeUpdate("DROP TABLE IF EXISTS `" + mpTableName + "`");
		stmt.executeUpdate(createMpTableSQL.toString());
		stmt.close();
		
		String insertSQL = "insert into " + mpTableName + " values(?,?,?,?)";
		PreparedStatement pstm = con.prepareStatement(insertSQL);
		int batchCount = 0;
		
		DbaseFileReader reader = new DbaseFileReader(new FileInputStream(file).getChannel(), false, Charset.forName("UTF-8"));
		while (reader.hasNext()) {
			Row row = reader.readRow();
			pstm.setLong(1, (Long) row.read(0));
			pstm.setInt(2, (Integer) row.read(1));
			pstm.setLong(3, (Long) row.read(2));
			pstm.setInt(4, (Integer) row.read(3));
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
    
    private void insertIntoManeuverTable(String subFolderName) throws Exception {
	String maneuverTableName = "Raw_Maneuver_" + country;
	String mnTableName = subFolderName + "_mn_" + country;
	String mpTableName = subFolderName + "_mp_" + country;
	
	String insertSQL = "insert into " + maneuverTableName + " select a.ID,a.FEATTYP,a.BIFTYP,a.PROMANTYP,a.JNCTID,b.SEQNR,b.TRPELID,b.TRPELTYP from " + mnTableName + " a inner join " + mpTableName + " b on a.id=b.id";
	Statement stmt = con.createStatement();
	stmt.executeUpdate(insertSQL);
	stmt.close();	
    }
}
