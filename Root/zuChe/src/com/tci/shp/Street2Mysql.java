package com.tci.shp;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.dbf.DbaseFileReader.Row;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;

import com.tci.dao.DBConnector;
import com.tci.pojo.County;
import com.tci.util.TCIUtils;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.io.WKTReader;

public class Street2Mysql {
    private String country;

    private String folderPath;

    private Connection con = null;

    private Map<String, String> stateMap = new HashMap<String, String>();
    
    private List<County> countyList = new ArrayList<County>();
    
    private String fileSeparator = System.getProperty("file.separator");

    public Street2Mysql(String country, String folderPath) {
	this.country = country;
	this.folderPath = folderPath;
    }

    public void run() throws Exception {
	try {
	    loadStates();
	    con = DBConnector.getInstance().connectToDB();
	    createTTLinksTable();
	    createTTAliasTable();
	    createTTZoneTable();
	    createTTTMCTable();
	    
	    File folder = new File(folderPath);
	    File[] subFolder = folder.listFiles();
	    for (int i = 0; i < subFolder.length; i++) {
		if (subFolder[i].isDirectory()) {
		    System.out.println("Start to process folder: " + subFolder[i].getName());
		    loadCounties(subFolder[i].getName());
		    processNw(subFolder[i].getName());
		    processGc(subFolder[i].getName());
		    insertIntoTTLinksTable(subFolder[i].getName());
		    insertIntoTTAliasTable(subFolder[i].getName());
		    insertIntoTTZoneTable(subFolder[i].getName());
		    processRd(subFolder[i].getName());
		    insertIntoTTTMCTable(subFolder[i].getName());
		}
	    }
	    formatTTAliasTable();
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
    
    private boolean loadStates() throws Exception {
	// USA
	stateMap.put("USAAK", "02");
	stateMap.put("USAAL", "01");
	stateMap.put("USAAR", "05");
	stateMap.put("USAAZ", "04");
	stateMap.put("USACA", "06");
	stateMap.put("USACO", "08");
	stateMap.put("USACT", "09");
	stateMap.put("USADC", "11");
	stateMap.put("USADE", "10");
	stateMap.put("USAFL", "12");
	stateMap.put("USAGA", "13");
	stateMap.put("USAHI", "15");
	stateMap.put("USAIA", "19");
	stateMap.put("USAID", "16");
	stateMap.put("USAIL", "17");
	stateMap.put("USAIN", "18");
	stateMap.put("USAKS", "20");
	stateMap.put("USAKY", "21");
	stateMap.put("USALA", "22");
	stateMap.put("USAMA", "25");
	stateMap.put("USAMD", "24");
	stateMap.put("USAME", "23");
	stateMap.put("USAMI", "26");
	stateMap.put("USAMN", "27");
	stateMap.put("USAMO", "29");
	stateMap.put("USAMS", "28");
	stateMap.put("USAMT", "30");
	stateMap.put("USANC", "37");
	stateMap.put("USAND", "38");
	stateMap.put("USANE", "31");
	stateMap.put("USANH", "33");
	stateMap.put("USANJ", "34");
	stateMap.put("USANM", "35");
	stateMap.put("USANV", "32");
	stateMap.put("USANY", "36");
	stateMap.put("USAOH", "39");
	stateMap.put("USAOK", "40");
	stateMap.put("USAOR", "41");
	stateMap.put("USAPA", "42");
	stateMap.put("USAPR", "72");
	stateMap.put("USAVI", "78");
	stateMap.put("USARI", "44");
	stateMap.put("USASC", "45");
	stateMap.put("USASD", "46");
	stateMap.put("USATN", "47");
	stateMap.put("USATX", "48");
	stateMap.put("USAUT", "49");
	stateMap.put("USAVA", "51");
	stateMap.put("USAVT", "50");
	stateMap.put("USAWA", "53");
	stateMap.put("USAWI", "55");
	stateMap.put("USAWV", "54");
	stateMap.put("USAWY", "56");
	
	// CAN
	stateMap.put("CANYT", "13");
	stateMap.put("CANSK", "08");
	stateMap.put("CANQC", "05");
	stateMap.put("CANPE", "02");
	stateMap.put("CANON", "06");
	stateMap.put("CANNU", "11");
	stateMap.put("CANNT", "12");
	stateMap.put("CANNS", "04");
	stateMap.put("CANNL", "03");
	stateMap.put("CANNB", "01");
	stateMap.put("CANMB", "07");
	stateMap.put("CANBC", "10");
	stateMap.put("CANAB", "09");
	
	// MEX
	stateMap.put("MEX01", "AG");
	stateMap.put("MEX02", "BN");
	stateMap.put("MEX03", "BS");
	stateMap.put("MEX04", "CM");
	stateMap.put("MEX05", "CU");
	stateMap.put("MEX06", "CL");
	stateMap.put("MEX07", "CP");
	stateMap.put("MEX08", "CH");
	stateMap.put("MEX09", "DF");
	stateMap.put("MEX10", "DU");
	stateMap.put("MEX11", "GJ");
	stateMap.put("MEX12", "GR");
	stateMap.put("MEX13", "HD");
	stateMap.put("MEX14", "JA");
	stateMap.put("MEX15", "MX");
	stateMap.put("MEX16", "MC");
	stateMap.put("MEX17", "MR");
	stateMap.put("MEX18", "NA");
	stateMap.put("MEX19", "LV");
	stateMap.put("MEX20", "OA");
	stateMap.put("MEX21", "PU");
	stateMap.put("MEX22", "QE");
	stateMap.put("MEX23", "QR");
	stateMap.put("MEX24", "SL");
	stateMap.put("MEX25", "SI");
	stateMap.put("MEX26", "SO");
	stateMap.put("MEX27", "TB");
	stateMap.put("MEX28", "TM");
	stateMap.put("MEX29", "TL");
	stateMap.put("MEX30", "VE");
	stateMap.put("MEX31", "YU");
	stateMap.put("MEX32", "ZA");
	return true;
    }
    
    private boolean loadCounties(String subFolderName) throws Exception {
	File folder = new File(folderPath + fileSeparator + subFolderName);
	File[] files = folder.listFiles();
	for (int f = 0; f < files.length; f++) {
	    File file = files[f];
	    if (file.getName().indexOf("___________a8.shp") != -1) {
		ShapefileDataStore store = new ShapefileDataStore(file.toURL());
		store.setStringCharset(Charset.forName("UTF-8"));
		SimpleFeatureSource featureSource = store.getFeatureSource(file.getName().substring(0, file.getName().indexOf(".")));
		SimpleFeatureCollection features = featureSource.getFeatures();
		SimpleFeatureIterator iterator = features.features();
		try {
		    while (iterator.hasNext()) {
			SimpleFeature feature = iterator.next();
			String geometry = feature.getDefaultGeometry().toString();
			County county = new County();
			String country = (String) feature.getAttribute("ORDER00");
			county.setCountry(country);
			if (country.equals("MEX")) {
			    	String state_code = (String) feature.getAttribute("ORDER01");        			
        			county.setState(stateMap.get(country + state_code));		
        			county.setStateCode(state_code);
			} else {
        			String state = (String) feature.getAttribute("ORDER01");
        			county.setState(state);		
        			county.setStateCode(stateMap.get(country + state));
			}
			county.setCounty(((String) feature.getAttribute("NAME")).toUpperCase());
			county.setCountyCode(((String) feature.getAttribute("ORDER08")).substring(2));
			WKTReader reader = new WKTReader();
        		MultiPolygon mpolygon = (MultiPolygon) reader.read(geometry);
        		county.setBoundary(mpolygon);
        		countyList.add(county);
		    }
		} finally {
		    iterator.close();

		}
	    }
	}
	return true;
	
	/*Map<Long, String> olMap = new HashMap<Long, String>();	
	File folder = new File(folderPath + fileSeparator + subFolderName);
	File[] files = folder.listFiles();
	for (int f = 0; f < files.length; f++) {
	    File file = files[f];
	    if (file.getName().indexOf("___________ol.dbf") != -1) {	
		DbaseFileReader reader = new DbaseFileReader(new FileInputStream(file).getChannel(), false, Charset.forName("UTF-8"));
		try {
		    while (reader.hasNext()) {
			Row row = reader.readRow();
			int axOrder = (Integer) row.read(2);
			if (axOrder == 8) {
			    olMap.put((Long) row.read(0), (String) row.read(3));
			}
		    }
		} finally {
		    reader.close();
		}
	    }
	}
	
	for (int f = 0; f < files.length; f++) {
	    File file = files[f];
	    if (file.getName().indexOf("___________a8.dbf") != -1) {	
		DbaseFileReader reader = new DbaseFileReader(new FileInputStream(file).getChannel(), false, Charset.forName("UTF-8"));
		try {
		    while (reader.hasNext()) {
			Row row = reader.readRow();
			long id = (Long) row.read(0);
			if (olMap.get(id).equals((String) row.read(14))) {
			    String country = (String) row.read(2);
			    String state = (String) row.read(3);
			    County county = new County();
			    county.setCountry(country);
			    county.setState(state);
			    county.setStateCode(stateMap.get(country + state));
			    county.setCounty(((String) row.read(13)).toUpperCase());
			    county.setCountyCode(((String) row.read(10)).substring(2));
			    countyMap.put(country + (String) row.read(10), county);
			}
		    }
		} finally {
		    reader.close();
		}
	    }
	}
	return true;*/
    }

    private void createTTLinksTable() throws Exception {
	String TTLinksTableName = "Raw_TTLinks_" + country;
	String createTableSQL = "CREATE TABLE `" + TTLinksTableName + "` ("
		+ "`COUNTRY` varchar(3) default NULL,"
		+ "`LINK_ID` bigint(15) default NULL,"
		+ "`FNAME_PREF` varchar(50) default NULL,"
		+ "`NAMEPREFIX` varchar(90) default NULL,"
		+ "`FNAME_BASE` varchar(150) default NULL,"
		+ "`FNAME_SUFF` varchar(55) default NULL,"
		+ "`FNAME_TYPE` varchar(50) default NULL,"
		+ "`LREF_ADDRE` varchar(10) default NULL,"
		+ "`LNREF_ADDR` varchar(10) default NULL,"
		+ "`RREF_ADDRE` varchar(10) default NULL,"
		+ "`RNREF_ADDR` varchar(10) default NULL,"
		+ "`F_NODE_ID` bigint(15) default NULL,"
		+ "`T_NODE_ID` bigint(15) default NULL,"
		+ "`FUNC_CLASS` tinyint(1) unsigned default NULL,"
		+ "`SPEEDCAT` tinyint(1) default NULL,"
		+ "`TO_LANES` int(11) default NULL,"
		+ "`FROM_LANES` int(11) default NULL,"
		+ "`LANE_CAT` int(11) default NULL,"
		+ "`LINKDIR` varchar(1) default NULL,"
		+ "`FRONTAGE` varchar(255) default NULL,"
		+ "`BRIDGE` varchar(255) default NULL,"
		+ "`TUNNEL` varchar(255) default NULL,"
		+ "`RAMP` varchar(255) default NULL,"
		+ "`TOLLWAY` varchar(255) default NULL,"
		+ "`FERRY_TYPE` varchar(255) default NULL,"
		+ "`HWYDIR` varchar(5) default NULL,"
		+ "`REVERSIBLE` varchar(255) default NULL,"
		+ "`EXPR_LANE` varchar(255) default NULL,"
		+ "`CARPOOLRD` varchar(255) default NULL,"
		+ "`CONTRACC` char(1) default NULL,"
		+ "`STATE` varchar(2) default NULL,"
		+ "`COUNTY` varchar(255) default NULL,"
		+ "`COUNTY_ABB` varchar(4) default NULL,"
		+ "`STATE_CODE` varchar(2) default NULL,"
		+ "`COUNTY_COD` varchar(3) default NULL,"
		+ "`LINK_GEOM` linestring default NULL,"
		+ "`LANES` char(0) NOT NULL,"
		+ "`CITY` varchar(3) default NULL,"
		+ "`DISTANCE` float default NULL"
		+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8";

	Statement stmt = con.createStatement();
	stmt.executeUpdate("DROP TABLE IF EXISTS `" + TTLinksTableName + "`");
	stmt.executeUpdate(createTableSQL);
	stmt.close();
    }

    private boolean processNw(String subFolderName) throws Exception {
	String[] nwColumns = {"ID", "F_JNCTID", "T_JNCTID", "NAME", "NAMELC", "NAMETYP", "SPEEDCAT", "RTEDIR"};
	File folder = new File(folderPath + fileSeparator + subFolderName);
	File[] files = folder.listFiles();
	for (int f = 0; f < files.length; f++) {
	    File file = files[f];
	    if (file.getName().indexOf("___________nw.dbf") != -1) {		
		StringBuffer createNwTableSQL = new StringBuffer("CREATE TABLE `");
		String nwTableName = subFolderName + "_nw_" + country;
		createNwTableSQL.append(nwTableName).append("` (");
		
		DbaseFileReader reader = new DbaseFileReader(new FileInputStream(file).getChannel(), false, Charset.forName("UTF-8"));
		DbaseFileHeader header = reader.getHeader();  
	        int numFields = header.getNumFields();  
		for (int i = 0; i < nwColumns.length; i++) {
		    for (int j = 0; j < numFields; j++) {			
			if (nwColumns[i].equals(header.getFieldName(j))) {
			    createNwTableSQL.append("`").append(nwColumns[i]).append("` ");
			    if (header.getFieldType(j) == 'C') {
				createNwTableSQL.append("varchar(").append(header.getFieldLength(j)).append(") default NULL,");
			    } else if (header.getFieldType(j) == 'N') {
				if (header.getFieldLength(j) > 10) {
				    createNwTableSQL.append("bigint(").append(header.getFieldLength(j)).append(") default NULL,");
				} else {
				    createNwTableSQL.append("int(").append(header.getFieldLength(j)).append(") default NULL,");
				}
			    }
			}
		    }
		}
		createNwTableSQL.append("`FUNC_CLASS` tinyint(1) unsigned default NULL,")
        		.append("`TO_LANES` int(11) default NULL,")
        		.append("`FROM_LANES` int(11) default NULL,")
        		.append("`LANE_CAT` int(11) default NULL,")
			.append("`LINKDIR` varchar(1) default NULL,")
			.append("`FRONTAGE` varchar(255) default NULL,")
			.append("`BRIDGE` varchar(255) default NULL,")
			.append("`TUNNEL` varchar(255) default NULL,")
			.append("`RAMP` varchar(255) default NULL,")
			.append("`TOLLWAY` varchar(255) default NULL,")
			.append("`FERRY_TYPE` varchar(255) default NULL,")
			.append("`REVERSIBLE` varchar(255) default NULL,")
			.append("`EXPR_LANE` varchar(255) default NULL,")
			.append("`CARPOOLRD` varchar(255) default NULL,")
			.append("`CONTRACC` char(1) default NULL,")
			.append("`country` char(3) default NULL,")
			.append("`STATE` varchar(2) default NULL,")
        		.append("`STATE_CODE` varchar(2) default NULL,")
        		.append("`COUNTY` varchar(255) default NULL,")
        		.append("`COUNTY_COD` varchar(3) default NULL,")
			.append("`LINK_GEOM` linestring default NULL,")
			.append("KEY `index_name` (`ID`,`NAMETYP`,`NAMELC`,`NAME`)")
			.append(") ENGINE=MyISAM DEFAULT CHARSET=utf8");

		Statement stmt = con.createStatement();
		stmt.executeUpdate("DROP TABLE IF EXISTS `" + nwTableName + "`");
		stmt.executeUpdate(createNwTableSQL.toString());
		stmt.close();
		reader.close();
		
		// Start to parse shp file
		String insertSQL = "insert into " + nwTableName + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,GeomFromText(?))";
		PreparedStatement pstm = con.prepareStatement(insertSQL);
		//FileDataStore store = FileDataStoreFinder.getDataStore(file);
		ShapefileDataStore store = new ShapefileDataStore(file.toURL());
		store.setStringCharset(Charset.forName("UTF-8"));
		SimpleFeatureSource featureSource = store.getFeatureSource(file.getName().substring(0, file.getName().indexOf(".")));
		SimpleFeatureCollection features = featureSource.getFeatures();	
		SimpleFeatureIterator iterator = features.features();
		int batchCount = 0;
		try {
		    while (iterator.hasNext()) {
			int paraCount = 1;
			SimpleFeature feature = iterator.next();
			if ((Integer) feature.getAttribute("FEATTYP") != 4110) {
			    continue;
			}
			
			int func_class = (Integer) feature.getAttribute("FRC");
			switch (func_class) {
			case 0:
			    func_class = 1;
			    break;
			case 1:
			case 2:
			    func_class = 2;
			    break;
			case 3:
			    func_class = 3;
			    break;
			case 4:
			case 5:
			    func_class = 4;
			    break;
			default:
			    func_class = 5;
			}
			
			int lanes = (Integer) feature.getAttribute("LANES");
			int lane_cat = 3;
           		switch (lanes) {
           		case 0:  		    
           		case 1:
           		    lane_cat = 1;
           		    break;
           		case 2:           		
           		case 3:
           		    lane_cat = 2;
           		    break;
           		}
		        
			String link_dir = TCIUtils.convertEmptyStringToNull((String) feature.getAttribute("ONEWAY"));
			if (link_dir == null) {
			    link_dir = "B";
			} else if (link_dir.equals("N")) {
			    link_dir = "B";//continue;
			} else if (link_dir.equals("FT")) {
			    link_dir = "F";
			} else if (link_dir.equals("TF")) {
			    link_dir = "T";
			}
			
			int fow = (Integer) feature.getAttribute("FOW");
			String fontage = "N";
			if (fow == 11) {
			    fontage = "Y";
			}
			
			int partstruc = (Integer) feature.getAttribute("PARTSTRUC");
			String bridge = "N";
			if (partstruc == 2) {
			    bridge = "Y";
			}
			
			String tunnel = "N";
			if (partstruc == 1) {
			    tunnel = "Y";
			}
			
			int ramp = (Integer) feature.getAttribute("RAMP");
			String rampStr = "N";
           		switch (ramp) {
           		case 1:
           		case 2: 
           		    rampStr = "Y";
           		    break;
           		}
           		
           		int toolrd = (Integer) feature.getAttribute("TOLLRD");
           		String tollway = "N";
           		if (toolrd != 0) {
           		    tollway = "Y";
           		}
           		
           		String carriage = TCIUtils.convertEmptyStringToNull((String) feature.getAttribute("CARRIAGE"));
           		String reversible = "N";
           		if (carriage != null && (carriage.equals("1") || carriage.equals("2")) && link_dir != null && link_dir.equals("B")) {
           		    reversible = "Y";
           		}
           		
           		String expr_lane = "N";           		
           		if (carriage != null && carriage.equals("2")) {
           		     expr_lane = "Y";
           		}
           		
           		String carpoolrd = "N";
           		if (carriage != null && carriage.equals("1")) {
           		    carpoolrd = "Y";
          		}
           		
           		int freeway = (Integer) feature.getAttribute("FREEWAY");
           		String contracc = "N";
           		if (freeway == 1) {
           		    contracc = "Y";
           		}
           			
			pstm.setLong(paraCount++, (Long) feature.getAttribute("ID"));
			pstm.setLong(paraCount++, (Long) feature.getAttribute("F_JNCTID"));
			pstm.setLong(paraCount++, (Long) feature.getAttribute("T_JNCTID"));
			pstm.setString(paraCount++, (String) feature.getAttribute("NAME"));
			pstm.setString(paraCount++, (String) feature.getAttribute("NAMELC"));
			pstm.setInt(paraCount++, (Integer) feature.getAttribute("NAMETYP"));
			pstm.setInt(paraCount++, (Integer) feature.getAttribute("SPEEDCAT"));
			pstm.setString(paraCount++, TCIUtils.convertEmptyStringToNull((String) feature.getAttribute("RTEDIR")));
			pstm.setInt(paraCount++, func_class);
			pstm.setInt(paraCount++, lanes);
			pstm.setInt(paraCount++, lanes);
			pstm.setInt(paraCount++, lane_cat);
			pstm.setString(paraCount++, link_dir);
			pstm.setString(paraCount++, fontage);
			pstm.setString(paraCount++, bridge);
			pstm.setString(paraCount++, tunnel);
			pstm.setString(paraCount++, rampStr);
			pstm.setString(paraCount++, tollway);
			pstm.setString(paraCount++, "H");
			pstm.setString(paraCount++, reversible);
			pstm.setString(paraCount++, expr_lane);
			pstm.setString(paraCount++, carpoolrd);
			pstm.setString(paraCount++, contracc);
			String geometry = feature.getDefaultGeometry().toString();       		
			geometry = geometry.replaceFirst("MULTILINESTRING \\(",	"linestring");
			geometry = geometry.substring(0, geometry.length() - 1);
			
			WKTReader wktReader = new WKTReader();        		
        		LineString lineString = (LineString) wktReader.read(geometry);
        		County county = null;
        		List<County> intersectCountyList = new ArrayList<County>();
        		for (int c = 0; c < countyList.size(); c++) {
        		    County ct = countyList.get(c);        		    
        		    if (lineString.intersects(ct.getBoundary())) { 
        			county = ct;
                		intersectCountyList.add(ct);
        		    }        		   
        		}
        		if (intersectCountyList.size() > 1) {
			    for (int c = 0; c < intersectCountyList.size(); c++) {
				County ct = intersectCountyList.get(c);
				if (!lineString.touches(ct.getBoundary())) {
				    county = ct;
				    break;
				}
			    }
        		}
        		
        		if (county == null) {
        		    System.out.println("	Warning: Can't find county for id=" + (Long) feature.getAttribute("ID"));
        		    continue;
        		} else {
        		    pstm.setString(paraCount++, county.getCountry());
        		    pstm.setString(paraCount++, county.getState());
        		    pstm.setString(paraCount++, county.getStateCode());
        		    pstm.setString(paraCount++, county.getCounty());
        		    pstm.setString(paraCount++, county.getCountyCode());
        		}
        		
			pstm.setString(paraCount++, geometry.toString());
			pstm.addBatch();
			batchCount++;
			if (batchCount == 4000) {
			    pstm.executeBatch();
			    pstm.clearBatch();
			    batchCount = 0;
			}
		    }
		    pstm.executeBatch();
		} finally {
		    iterator.close();
		    pstm.close();
		}		
	    }
	}
	return true;
    }  

    private boolean processGc(String subFolderName) throws Exception {
	String[] gcColumns = {"ID", "FULLNAME", "NAMELC", "NAMETYP", "NAME", "NAMEPREFIX", "SUFTYPE", "SUFDIR", "PREDIR", "L_F_F_ADD", "L_T_F_ADD", "R_F_F_ADD", "R_T_F_ADD", "L_AXON", "R_AXON", "L_APNAME", "R_APNAME", "L_LAXON", "R_LAXON"};
	File folder = new File(folderPath + fileSeparator + subFolderName);
	File[] files = folder.listFiles();
	for (int f = 0; f < files.length; f++) {
	    File file = files[f];
	    if (file.getName().indexOf("___________gc.dbf") != -1) { // geo
		StringBuffer createGcTableSQL = new StringBuffer("CREATE TABLE `");
		String gcTableName = subFolderName + "_gc_" + country;
		createGcTableSQL.append(gcTableName).append("` (");
		
		DbaseFileReader reader = new DbaseFileReader(new FileInputStream(file).getChannel(), false, Charset.forName("UTF-8"));
		DbaseFileHeader header = reader.getHeader();  
	        int numFields = header.getNumFields();  
		for (int i = 0; i < gcColumns.length; i++) {
		    for (int j = 0; j < numFields; j++) {
			if (gcColumns[i].equals(header.getFieldName(j))) {
			    createGcTableSQL.append("`").append(gcColumns[i]).append("` ");
			    if (header.getFieldType(j) == 'C') {
				createGcTableSQL.append("varchar(").append(header.getFieldLength(j)).append(") default NULL,");
			    } else if (header.getFieldType(j) == 'N') {
				if (header.getFieldLength(j) > 10) {
				    createGcTableSQL.append("bigint(").append(header.getFieldLength(j)).append(") default NULL,");
				} else {
				    createGcTableSQL.append("int(").append(header.getFieldLength(j)).append(") default NULL,");
				}
			    }
			}
		    }
		}
		createGcTableSQL.append("KEY `index_name` (`ID`,`NAMETYP`,`NAMELC`,`FULLNAME`)")
			.append(") ENGINE=MyISAM DEFAULT CHARSET=utf8");

		Statement stmt = con.createStatement();
		stmt.executeUpdate("DROP TABLE IF EXISTS `" + gcTableName + "`");
		stmt.executeUpdate(createGcTableSQL.toString());
		stmt.close();

		String insertSQL = "insert into " + gcTableName + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstm = con.prepareStatement(insertSQL);	
		int batchCount = 0;
		try {
		    while (reader.hasNext()) {
			int paraCount = 1;
			Row row = reader.readRow();
			if ((Integer) row.read(1) != 4110) {
			    continue;
			}			
			
			pstm.setLong(paraCount++, (Long) row.read(0));
			pstm.setString(paraCount++, (String) row.read(2));
			pstm.setString(paraCount++, (String) row.read(3));
			pstm.setInt(paraCount++, (Integer) row.read(4));
			pstm.setString(paraCount++, TCIUtils.convertEmptyStringToNullAndUppercase((String) row.read(7)));
			pstm.setString(paraCount++, TCIUtils.convertEmptyStringToNullAndUppercase((String) row.read(8)));
			pstm.setString(paraCount++, TCIUtils.convertEmptyStringToNullAndUppercase((String) row.read(15)));
			pstm.setString(paraCount++, TCIUtils.convertEmptyStringToNullAndUppercase((String) row.read(11)));
			pstm.setString(paraCount++, TCIUtils.convertEmptyStringToNullAndUppercase((String) row.read(12)));
			pstm.setString(paraCount++, TCIUtils.convertEmptyStringToNull((String) row.read(40)));	
			pstm.setString(paraCount++, TCIUtils.convertEmptyStringToNull((String) row.read(43)));	
			pstm.setString(paraCount++, TCIUtils.convertEmptyStringToNull((String) row.read(48)));	
			pstm.setString(paraCount++, TCIUtils.convertEmptyStringToNull((String) row.read(51)));
			pstm.setString(paraCount++, TCIUtils.convertEmptyStringToNull((String) row.read(22)));
			pstm.setString(paraCount++, TCIUtils.convertEmptyStringToNull((String) row.read(23)));
			pstm.setString(paraCount++, TCIUtils.convertEmptyStringToNull((String) row.read(34)));
			pstm.setString(paraCount++, TCIUtils.convertEmptyStringToNull((String) row.read(35)));
			pstm.setString(paraCount++, TCIUtils.convertEmptyStringToNull((String) row.read(20)));
			pstm.setString(paraCount++, TCIUtils.convertEmptyStringToNull((String) row.read(21)));
			pstm.addBatch();
			batchCount++;
			if (batchCount == 4000) {
			    pstm.executeBatch();
			    pstm.clearBatch();
			    batchCount = 0;
			}
		    }
		    pstm.executeBatch();
		} finally {		  
		    pstm.close();
		    reader.close();
		}
	    }
	}
	return true;
    }
    
    private void insertIntoTTLinksTable(String subFolderName) throws Exception {
	String TTLinksTableName = "Raw_TTLinks_" + country;
	String nwTableName = subFolderName + "_nw_" + country;
	String gcTableName = subFolderName + "_gc_" + country;

	String insertSQL = "insert into "
		+ TTLinksTableName
		+ " (COUNTRY,LINK_ID,FNAME_PREF,NAMEPREFIX,FNAME_BASE,FNAME_SUFF,FNAME_TYPE,LREF_ADDRE,LNREF_ADDR,RREF_ADDRE,RNREF_ADDR,F_NODE_ID,T_NODE_ID,FUNC_CLASS,SPEEDCAT,TO_LANES,FROM_LANES,LANE_CAT,LINKDIR,FRONTAGE,BRIDGE,TUNNEL,RAMP,TOLLWAY,FERRY_TYPE,HWYDIR,REVERSIBLE,EXPR_LANE,CARPOOLRD,CONTRACC,STATE,COUNTY,COUNTY_ABB,STATE_CODE,COUNTY_COD,LINK_GEOM,LANES,CITY,DISTANCE) "
		+ "select a.country,a.ID,b.PREDIR,b.NAMEPREFIX,b.NAME,b.SUFDIR,b.SUFTYPE,b.L_F_F_ADD,b.L_T_F_ADD,b.R_F_F_ADD,b.R_T_F_ADD,a.F_JNCTID,a.T_JNCTID,a.FUNC_CLASS,a.SPEEDCAT,a.TO_LANES,a.FROM_LANES,a.LANE_CAT,a.LINKDIR,a.FRONTAGE,a.BRIDGE,a.TUNNEL,a.RAMP,a.TOLLWAY,a.FERRY_TYPE,a.RTEDIR,a.REVERSIBLE,a.EXPR_LANE,a.CARPOOLRD,a.CONTRACC,a.STATE,a.COUNTY,null,a.STATE_CODE,a.COUNTY_COD,a.LINK_GEOM,'',null,null from "
		+ nwTableName + " a left join " + gcTableName
		+ " b on a.id=b.id and a.nametyp=b.nametyp and a.namelc=b.namelc and a.name=b.fullname";
	Statement stmt = con.createStatement();
	stmt.executeUpdate(insertSQL);
	stmt.close();
    }
    
    private void createTTAliasTable() throws Exception {
	String TTAliasTableName = "Raw_TTAlias_" + country;
	String createTableSQL = "CREATE TABLE `" + TTAliasTableName + "` ("
                +"`LINK_ID` bigint(15) default NULL,"
                +"`FNAME_PREF` varchar(50) default NULL,"
                +"`NAMEPREFIX` varchar(90) default NULL,"
                +"`FNAME_BASE` varchar(150) default NULL,"
                +"`FNAME_SUFF` varchar(55) default NULL,"
                +"`FNAME_TYPE` varchar(50) default NULL,"                
                +"`DIR_ONSIGN` varchar(2) default NULL"
                +") ENGINE=MyISAM DEFAULT CHARSET=utf8";
	
	Statement stmt = con.createStatement();
	stmt.executeUpdate("DROP TABLE IF EXISTS `" + TTAliasTableName + "`");
	stmt.executeUpdate(createTableSQL);
	stmt.close();
    }
    
    private void insertIntoTTAliasTable(String subFolderName) throws Exception {
	String TTAliasTableName = "Raw_TTAlias_" + country;
	String gcTableName = subFolderName + "_gc_" + country;
	String nwTableName = subFolderName + "_nw_" + country;
	
	String insertSQL = "insert into " + TTAliasTableName + " select ID,PREDIR,NAMEPREFIX,NAME,SUFDIR,SUFTYPE,'' from (select a.ID,a.PREDIR,a.NAMEPREFIX,a.NAME,a.SUFDIR,a.SUFTYPE,b.id as link_id from " + gcTableName + " a left join " + nwTableName + " b on a.id=b.id and a.nametyp=b.nametyp and a.namelc=b.namelc and b.name=a.fullname) c where link_id is null";
	Statement stmt = con.createStatement();
	stmt.executeUpdate(insertSQL);
	stmt.close();	
    }
    
    private void formatTTAliasTable() throws Exception {
	String TTAliasTableName = "Raw_TTAlias_" + country;
	Statement stmt = con.createStatement();
	stmt.executeUpdate("update " + TTAliasTableName + " set FNAME_PREF='' where FNAME_PREF is null");
	stmt.executeUpdate("update " + TTAliasTableName + " set NAMEPREFIX='' where NAMEPREFIX is null");
	stmt.executeUpdate("update " + TTAliasTableName + " set FNAME_BASE='' where FNAME_BASE is null");
	stmt.executeUpdate("update " + TTAliasTableName + " set FNAME_SUFF='' where FNAME_SUFF is null");
	stmt.executeUpdate("update " + TTAliasTableName + " set FNAME_TYPE='' where FNAME_TYPE is null");
	stmt.close();
    }
    
    private void createTTZoneTable() throws Exception {
	String TTZoneTableName = "Raw_TTZone_" + country;
	String createTableSQL = "CREATE TABLE `" + TTZoneTableName + "` ("
                +"`LINK_ID` bigint(15) default NULL,"
                +"`L_AXON` varchar(100) default NULL,"
                +"`R_AXON` varchar(100) default NULL,"
                +"`L_APNAME` varchar(100) default NULL,"
                +"`R_APNAME` varchar(100) default NULL,"
                +"`L_LAXON` varchar(100) default NULL,"                
                +"`R_LAXON` varchar(100) default NULL"
                +") ENGINE=MyISAM DEFAULT CHARSET=utf8";
	
	Statement stmt = con.createStatement();
	stmt.executeUpdate("DROP TABLE IF EXISTS `" + TTZoneTableName + "`");
	stmt.executeUpdate(createTableSQL);
	stmt.close();
    }
    
    private void insertIntoTTZoneTable(String subFolderName) throws Exception {
	String TTZoneTableName = "Raw_TTZone_" + country;
	String gcTableName = subFolderName + "_gc_" + country;
	
	String insertSQL = "insert into " + TTZoneTableName + " select distinct ID,L_AXON,R_AXON,L_APNAME,R_APNAME,L_LAXON,R_LAXON from " + gcTableName;
	Statement stmt = con.createStatement();
	stmt.executeUpdate(insertSQL);
	stmt.close();	
    }
    
    private void createTTTMCTable() throws Exception {
	String TTTMCTableName = "Raw_TTTmc_" + country;
	String createTableSQL = "CREATE TABLE `" + TTTMCTableName + "` ("
                +"`LINK_ID` bigint(15) default NULL,"
                +"`TMC` varchar(10) default NULL,"
                +"`TMCPATHID` varchar(15) default NULL"
                +") ENGINE=MyISAM DEFAULT CHARSET=utf8";
	
	Statement stmt = con.createStatement();
	stmt.executeUpdate("DROP TABLE IF EXISTS `" + TTTMCTableName + "`");
	stmt.executeUpdate(createTableSQL);
	stmt.close();
    }
    
    private boolean processRd(String subFolderName) throws Exception {
	File folder = new File(folderPath + fileSeparator + subFolderName);
	File[] files = folder.listFiles();
	for (int f = 0; f < files.length; f++) {
	    File file = files[f];
	    if (file.getName().indexOf("___________rd.dbf") != -1) {
		String tmcTableName = subFolderName + "_rd_" + country;
		String createRdTableSQL = "CREATE TABLE `" + tmcTableName + "` ("
			 +"`LINK_ID` bigint(15) default NULL,"
		         +"`TMC` varchar(10) default NULL,"
			 +"`TMCPATHID` varchar(15) default NULL"
		         +") ENGINE=MyISAM DEFAULT CHARSET=utf8";
		
		Statement stmt = con.createStatement();
		stmt.executeUpdate("DROP TABLE IF EXISTS `" + tmcTableName + "`");
		stmt.executeUpdate(createRdTableSQL.toString());
		stmt.close();
		
		String insertSQL = "insert into " + tmcTableName + " values(?,?,?)";
		PreparedStatement pstm = con.prepareStatement(insertSQL);
		int batchCount = 0;
		
		DbaseFileReader reader = new DbaseFileReader(new FileInputStream(file).getChannel(), false, Charset.forName("UTF-8"));
		while (reader.hasNext()) {
			Row row = reader.readRow();
			pstm.setLong(1, (Long) row.read(0));
			pstm.setString(2, (String) row.read(1));
			String tmcpathid = String.valueOf(row.read(2));
			if (tmcpathid == null || tmcpathid.equals("0.0")) {
			    tmcpathid = "";
			}
			pstm.setString(3, tmcpathid);
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
    
    private void insertIntoTTTMCTable(String subFolderName) throws Exception {
	String TTTMCTableName = "Raw_TTTmc_" + country;
	String tmcTableName = subFolderName + "_rd_" + country;
	
	String insertSQL = "insert into " + TTTMCTableName + " select distinct LINK_ID,TMC,TMCPATHID from " + tmcTableName;
	Statement stmt = con.createStatement();
	stmt.executeUpdate(insertSQL);
	stmt.close();	
    }
}










