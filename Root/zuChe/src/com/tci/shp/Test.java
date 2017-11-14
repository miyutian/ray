package com.tci.shp;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.dbf.DbaseFileReader.Row;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;

import com.tci.dao.DBConnector;
import com.tci.util.TCIUtils;

public class Test {
    
    private Connection con = null;
    
    private String folder = "D:\\TCI\\Technical\\Project\\TOMTOMMap\\SHP\\North_America_2016_06\\USA\\output\\USA\\uwi7z001\\";
    
    private String[] streetColumns = { "ID", "F_JNCTID", "T_JNCTID", "NAME", "NAMELC", "FRC", "NAMETYP"};
    
    private String[] geoColumns = { "ID", "FULLNAME", "NAMELC", "NAMETYP", "NAME", "NAMEPREFIX", "NAMESUFFIX", "SUFDIR", "PREDIR"};
    
    public static void main(String[] args) throws Exception {
	long startTime = System.currentTimeMillis();
	new Test().run();
	long used = (System.currentTimeMillis() - startTime) / 1000;
	System.out.println("    Use " + used / 60 + " mins.");
    }
    
    public void run() throws Exception {
	try {
	    	System.out.println("----------Start-------------");
        	//con = DBConnector.getInstance().connectToDB();
        	//processStreet();
        	processGeo();
        	
        	System.out.println("----------End-------------");
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		//con.close();
	    } catch (Exception e) {
		System.out.println("Failed to close DB connection.");
	    }
	}
    }
    
    private boolean processStreet() throws Exception {
	File file = new File(folder  + "usauwi___________nw.dbf");
	if (!file.exists()) {
	    return false;
	}
	
	StringBuffer createStreetTableSQL = new StringBuffer("CREATE TABLE `");
	String streetTableName = "street_wi";
	createStreetTableSQL.append(streetTableName).append("` (");
	
	DbaseFileReader reader = new DbaseFileReader(new FileInputStream(file).getChannel(), false, Charset.forName("UTF-8"));
	DbaseFileHeader header = reader.getHeader();  
        int numFields = header.getNumFields();  
	for (int i = 0; i < streetColumns.length; i++) {
	    for (int j = 0; j < numFields; j++) {
		//System.out.println(header.getFieldName(j) + ": " + header.getFieldType(j) + ", " + header.getFieldLength(j));
		if (streetColumns[i].equals(header.getFieldName(j))) {
		    createStreetTableSQL.append("`").append(streetColumns[i]).append("` ");
		    if (header.getFieldType(j) == 'C') {
			createStreetTableSQL.append("varchar(").append(header.getFieldLength(j)).append(") default NULL,");
		    } else if (header.getFieldType(j) == 'N') {
			if (header.getFieldLength(j) > 10) {
			    createStreetTableSQL.append("bigint(").append(header.getFieldLength(j)).append(") default NULL,");
			} else {
			    createStreetTableSQL.append("int(").append(header.getFieldLength(j)).append(") default NULL,");
			}
		    }
		}
	    }
	}
	createStreetTableSQL.append("`LINK_GEOM` linestring default NULL");
	createStreetTableSQL.append(") ENGINE=MyISAM DEFAULT CHARSET=utf8");

	Statement stmt = con.createStatement();
	stmt.executeUpdate("DROP TABLE IF EXISTS `" + streetTableName + "`");
	stmt.executeUpdate(createStreetTableSQL.toString());
	stmt.close();
	reader.close();
		
	// Start to parse shp file
	String insertSQL = "insert into " + streetTableName + " values(?,?,?,?,?,?,?,GeomFromText(?))";
	PreparedStatement pstm = con.prepareStatement(insertSQL);
	//FileDataStore store = FileDataStoreFinder.getDataStore(file);
	ShapefileDataStore store = new ShapefileDataStore(file.toURL());
	store.setStringCharset(Charset.forName("UTF-8"));
	SimpleFeatureSource featureSource = store.getFeatureSource("usauwi___________nw");
	SimpleFeatureCollection features = featureSource.getFeatures();	
	SimpleFeatureIterator iterator = features.features();
	int batchCount = 0;
	try {
	    while ( iterator.hasNext() ){
		 int paraCount = 1;
	         SimpleFeature feature = iterator.next();
	         if ((Integer) feature.getAttribute("FEATTYP") != 4110) {
	             continue;
	         }
	         pstm.setLong(paraCount++, (Long) feature.getAttribute("ID"));
	         pstm.setLong(paraCount++, (Long) feature.getAttribute("F_JNCTID"));
	         pstm.setLong(paraCount++, (Long) feature.getAttribute("T_JNCTID"));
	         pstm.setString(paraCount++, TCIUtils.convertEmptyStringToNull((String) feature.getAttribute("NAME")));
	         pstm.setString(paraCount++, TCIUtils.convertEmptyStringToNull((String) feature.getAttribute("NAMELC")));
	         pstm.setInt(paraCount++, (Integer) feature.getAttribute("FRC"));
	         pstm.setInt(paraCount++, (Integer) feature.getAttribute("NAMETYP"));	        
	         String geometry = feature.getDefaultGeometry().toString();	         
	         geometry = geometry.replaceFirst("MULTILINESTRING \\(", "linestring");	        
	         geometry = geometry.substring(0, geometry.length() - 1);
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
	return true;
    }
    
    private boolean processGeo() throws Exception {
	File file = new File(folder  + "usauwi___________gc.dbf");
	if (!file.exists()) {
	    return false;
	}
	
	ShapefileDataStore store = new ShapefileDataStore(file.toURL());
	store.setStringCharset(Charset.forName("UTF-8"));
	SimpleFeatureSource featureSource = store.getFeatureSource("usauwi___________gc");
	SimpleFeatureCollection features = featureSource.getFeatures();	
	SimpleFeatureIterator iterator = features.features();

	try {
	    while ( iterator.hasNext() ){		
	         SimpleFeature feature = iterator.next();
	         if ((Long) feature.getAttribute("ID") == 558400000185461l) {
	             System.out.println(TCIUtils.convertEmptyStringToNull((String) feature.getAttribute("NAME")) + ", " + TCIUtils.convertEmptyStringToNull((String) feature.getAttribute("SUFDIR")) 
	        	     + 	", " + feature.getDefaultGeometry().toString());
	         }
	         
	    }
	   
	} finally {
	    iterator.close();
	   
	}
	return true;
    }
}
