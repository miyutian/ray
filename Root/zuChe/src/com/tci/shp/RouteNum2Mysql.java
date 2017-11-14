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

public class RouteNum2Mysql {
    private String country;

    private String folderPath;

    private Connection con = null;

    private String fileSeparator = System.getProperty("file.separator");
    
    private String[] fnameTypes = {"ABBEY","ACRES","ALLEY","ALT","ALY","ANX","ARC","ARCH","AVE","BAY","BCH","BEACH","BEND","BG","BLF","BLFS","BLVD","BND","BR","BRG","BRK","BRKS","BTM","BUS","BYP","BYPASS","BYU","BYWAY","CAMPUS","CAPE","CDS","CHASE","CIR","CIRCT","CIRS","CLB","CLF","CLFS","CLOSE","CMN","CMNS","COMMON","CONC","CONN","COR","CORS","COVE","CP","CPE","CRES","CRK","CRNRS","CROSS","CRSE","CRST","CRT","CSWY","CT","CTR","CTS","CURV","CV","CVS","CYN","DALE","DELL","DIVERS","DL","DM","DOWNS","DR","DRS","DV","END","EST","ESTATE","ESTS","EXPY","EXT","EXTEN","EXTS","FALL","FARM","FIELD","FLD","FLDS","FLS","FLT","FLTS","FOREST","FRD","FRG","FRK","FRKS","FRONT","FRST","FRY","FT","FWY","GATE","GDN","GDNS","GLADE","GLEN","GLN","GREEN","GRN","GRNDS","GRNS","GROVE","GRV","GRVS","GTWY","HARBR","HBR","HEATH","HGHLDS","HILL","HL","HLS","HOLLOW","HOLW","HTS","HVN","HWY","INLET","INLT","IS","ISLAND","ISLE","ISS","JCT","KEY","KNL","KNLS","KNOLL","KY","KYS","LAND","LANDNG","LANE","LCK","LCKS","LDG","LF","LGT","LGTS","LINE","LINK","LK","LKOUT","LKS","LMTS","LN","LNDG","LOOP","MALL","MANOR","MAZE","MDW","MDWS","MEADOW","MEWS","ML","MLS","MNR","MNRS","MOUNT","MSN","MT","MTN","MTWY","NCK","OPAS","ORCH","OVAL","PARADE","PARK","PASS","PATH","PIKE","PINES","PK","PKWY","PKY","PL","PLAT","PLAZA","PLN","PLNS","PLZ","PNE","PNES","PORT","PR","PROM","PRT","PSGE","PT","PTS","PTWAY","PVT","QUAY","RADL","RAMP","RD","RDG","RDGS","RDS","RG","RIDGE","RISE","RIV","RNCH","ROAD","ROW","RPD","RPDS","RST","RTE","RUN","SHL","SHLS","SHR","SHRS","SKWY","SMT","SPG","SPGS","SPUR","SQ","SQS","ST","STA","STRA","STRM","STS","SUBDIV","TER","TERR","THICK","TLINE","TPKE","TRAIL","TRAK","TRCE","TRFY","TRL","TRLR","TRNABT","TRWY","TUNL","UN","UPAS","VALE","VIA","VIEW","VILLAS","VILLGE","VIS","VISTA","VL","VLG","VLGS","VLY","VW","VWS","WALK","WALL","WAY","WAYS","WHARF","WL","WLS","WOOD","WYND","XING","XRD","XRDS"};

    public RouteNum2Mysql(String country, String folderPath) {
	this.country = country;
	this.folderPath = folderPath;
    }
    
    public void run() throws Exception {
	try {	    
	    con = DBConnector.getInstance().connectToDB();
	    
	    createRouteNumTable();
	    
	    File folder = new File(folderPath);
	    File[] subFolder = folder.listFiles();
	    for (int i = 0; i < subFolder.length; i++) {
		if (subFolder[i].isDirectory()) {
		    System.out.println("Start to process folder: " + subFolder[i].getName());		   
		    processRN(subFolder[i].getName());
		    insertIntoRouteNumTable(subFolder[i].getName());
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
    
    private void createRouteNumTable() throws Exception {
	String rawTableName = "Raw_RouteNum_" + country;
	String createTableSQL = "CREATE TABLE `" + rawTableName + "` ("
		+ "`LINK_ID` bigint(15) default NULL,"
		+ "`FNAME_PREF` varchar(50) default NULL,"
		+ "`NAMEPREFIX` varchar(90) default NULL,"
		+ "`FNAME_BASE` varchar(150) default NULL,"
		+ "`FNAME_SUFF` varchar(55) default NULL,"
		+ "`FNAME_TYPE` varchar(50) default NULL"		
                + ") ENGINE=MyISAM DEFAULT CHARSET=utf8";
	
	Statement stmt = con.createStatement();
	stmt.executeUpdate("DROP TABLE IF EXISTS `" + rawTableName + "`");
	stmt.executeUpdate(createTableSQL);
	stmt.close();
    }     
    
    private boolean processRN(String subFolderName) throws Exception {
	File folder = new File(folderPath + fileSeparator + subFolderName);
	File[] files = folder.listFiles();
	for (int f = 0; f < files.length; f++) {
	    File file = files[f];
	    if (file.getName().indexOf("___________rn.dbf") != -1) {
		StringBuffer createTableSQL = new StringBuffer("CREATE TABLE `");
		String tableName = subFolderName + "_rn_" + country;
		createTableSQL.append(tableName).append("` (");
		createTableSQL.append("`LINK_ID` bigint(15) default NULL,");
		createTableSQL.append("`FNAME_PREF` varchar(50) default NULL,");
		createTableSQL.append("`NAMEPREFIX` varchar(90) default NULL,");
		createTableSQL.append("`FNAME_BASE` varchar(150) default NULL,");
		createTableSQL.append("`FNAME_SUFF` varchar(55) default NULL,");
		createTableSQL.append("`FNAME_TYPE` varchar(50) default NULL");
		createTableSQL.append(") ENGINE=MyISAM DEFAULT CHARSET=utf8");

		Statement stmt = con.createStatement();
		stmt.executeUpdate("DROP TABLE IF EXISTS `" + tableName + "`");
		stmt.executeUpdate(createTableSQL.toString());
		stmt.close();
		
		String insertSQL = "insert into " + tableName + " values(?,?,?,?,?,?)";
		PreparedStatement pstm = con.prepareStatement(insertSQL);
		int batchCount = 0;
		
		DbaseFileReader reader = new DbaseFileReader(new FileInputStream(file).getChannel(), false, Charset.forName("UTF-8"));
		while (reader.hasNext()) {
			Row row = reader.readRow();
			int feat_type = (Integer) row.read(1);
			if (feat_type != 4110) {
			    continue;
			}
			
			long link_id = (Long) row.read(0);
			String routeNum = TCIUtils.convertNullStringToEnptyAndUppercase((String) row.read(3)).trim();
			String routeName = TCIUtils.convertNullStringToEnptyAndUppercase((String) row.read(4)).trim();
			String fname_pref = TCIUtils.convertNullStringToEnptyAndUppercase((String) row.read(17)).trim();
			String fname_suff = TCIUtils.convertNullStringToEnptyAndUppercase((String) row.read(16)).trim();
			
			if (!routeNum.equals("")) {
			    String fname_base = routeNum;
			    String fname_type = "";
			    if (!fname_pref.equals("") && fname_base.startsWith(fname_pref + " ")) {
				fname_base = fname_base.substring(fname_pref.length() + 1);
			    }
			    
			    if (!fname_suff.equals("") && fname_base.endsWith(" " + fname_suff)) {
				fname_base = fname_base.substring(0, fname_base.length() - fname_suff.length() - 1);
			    }
			    
			    for (int i = 0; i < fnameTypes.length; i++) {
				if (fname_base.endsWith(" " + fnameTypes[i])) {
				    fname_base = fname_base.substring(0, fname_base.length() - fnameTypes[i].length() - 1);
				    fname_type = fnameTypes[i];
				    break;
				}
			    }
			    
			    pstm.setLong(1, link_id);
			    pstm.setString(2, fname_pref);
			    pstm.setString(3, "");
			    pstm.setString(4, fname_base);
			    pstm.setString(5, fname_suff);
			    pstm.setString(6, fname_type);
			    pstm.addBatch();
			    batchCount++;
			}
			
			if (!routeName.equals("") && !routeName.equals(routeNum)) {
			    String fname_base = routeName;
			    String fname_type = "";
			    if (!fname_pref.equals("") && fname_base.startsWith(fname_pref + " ")) {
				fname_base = fname_base.substring(fname_pref.length() + 1);
			    }
			    
			    if (!fname_suff.equals("") && fname_base.endsWith(" " + fname_suff)) {
				fname_base = fname_base.substring(0, fname_base.length() - fname_suff.length() - 1);
			    }
			    
			    for (int i = 0; i < fnameTypes.length; i++) {
				if (fname_base.endsWith(" " + fnameTypes[i])) {
				    fname_base = fname_base.substring(0, fname_base.length() - fnameTypes[i].length() - 1);
				    fname_type = fnameTypes[i];
				    break;
				}
			    }
			    
			    pstm.setLong(1, link_id);
			    pstm.setString(2, fname_pref);
			    pstm.setString(3, "");
			    pstm.setString(4, fname_base);
			    pstm.setString(5, fname_suff);
			    pstm.setString(6, fname_type);
			    pstm.addBatch();
			    batchCount++;
			}		
		        
		        if (batchCount >= 4000) {
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
    
    private void insertIntoRouteNumTable(String subFolderName) throws Exception {
	String RawTableName = "Raw_RouteNum_" + country;
	String rnTableName = subFolderName + "_rn_" + country;
	
	String insertSQL = "insert into " + RawTableName + " select * from " + rnTableName;
	Statement stmt = con.createStatement();
	stmt.executeUpdate(insertSQL);
	stmt.close();	
    }
}
