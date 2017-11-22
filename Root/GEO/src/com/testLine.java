package com;
import java.io.File;  
import java.io.FileWriter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator; 

import java.net.MalformedURLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
 
import com.mysql.jdbc.Driver;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;  
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;

import java.nio.charset.Charset;
import org.opengis.feature.simple.SimpleFeature;  
import org.opengis.feature.simple.SimpleFeatureType;
import java.io.StringWriter; 
//import com.tci.shp.String;




  
public class testLine {  
	private static final String String = null;
	public static void main(String[] args) throws Exception {  
		testLine std = new testLine();
		std.nwtodb();
		//std.gctodb();
    }  
	public void gctodb() throws  Exception{
		 	//ShapefileDataStore shpDataStore = null;  
	        int batchCount = 0;
	        ShapefileDataStore shpDataStore = new ShapefileDataStore(new File("E:\\gc.shp").toURL());  
	        shpDataStore.setCharset(Charset.forName("UTF-8"));  
            String typeName = shpDataStore.getTypeNames()[0]; 
            //System.out.println("typeName===>"+typeName);
            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = null;   
            featureSource = (FeatureSource<SimpleFeatureType, SimpleFeature>)shpDataStore.getFeatureSource(typeName);
            //FeatureCollection<SimpleFeatureType, SimpleFeature> result = featureSource.getFeatures(); 
            Connection dbConn = new ConnectDB().connect();
            String insertSQL = "insert into gctable values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pstm = dbConn.prepareStatement(insertSQL);
            //存储地理对象
            FeatureCollection<SimpleFeatureType, SimpleFeature> result = featureSource.getFeatures(); 
            //System.out.println(result.size());  
	        try{  
	            FeatureIterator<SimpleFeature> itertor = result.features();  
	            while(itertor.hasNext()){  
	            	int paraCount = 1;
	            	SimpleFeature feature = itertor.next();
	            	if ((Integer) feature.getAttribute("FEATTYP") != 4110) {
	    			    continue;
	    			}			
	    			System.out.println(feature.getAttribute("ID"));
	    			pstm.setLong(paraCount++, (Long) feature.getAttribute("ID"));
	    			pstm.setString(paraCount++, (String) feature.getAttribute("FULLNAME"));
	    			pstm.setString(paraCount++, (String) feature.getAttribute("NAMELC"));
	    			pstm.setInt(paraCount++, (Integer) feature.getAttribute("NAMETYP"));
	    			pstm.setString(paraCount++, (String) feature.getAttribute("NAME"));
	    			pstm.setString(paraCount++, (String) feature.getAttribute("NAMEPREFIX"));
	    			pstm.setString(paraCount++, (String) feature.getAttribute("SUFTYPE"));
	    			pstm.setString(paraCount++, (String) feature.getAttribute("SUFDIR"));
	    			pstm.setString(paraCount++, (String) feature.getAttribute("PREDIR"));
	    			pstm.setString(paraCount++, (String) feature.getAttribute("L_F_F_ADD"));	
	    			pstm.setString(paraCount++, (String) feature.getAttribute("L_T_F_ADD"));	
	    			pstm.setString(paraCount++, (String) feature.getAttribute("R_F_F_ADD"));	
	    			pstm.setString(paraCount++, (String) feature.getAttribute("R_T_F_ADD"));
	    			pstm.setString(paraCount++, (String) feature.getAttribute("L_AXON"));
	    			pstm.setString(paraCount++, (String) feature.getAttribute("R_AXON"));
	    			pstm.setString(paraCount++, (String) feature.getAttribute("L_APNAME"));
	    			pstm.setString(paraCount++, (String) feature.getAttribute("R_APNAME"));
	    			pstm.setString(paraCount++, (String) feature.getAttribute("L_LAXON"));
	    			pstm.setString(paraCount++, (String) feature.getAttribute("R_LAXON"));
	    			// store in txt file
	    			//WriteTxt(feature.getAttribute("ID").toString(),feature.getAttribute("NAME").toString());
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
	    		    dbConn.close();
	            }
	        }
	private void WriteTxt(String strBufferID,String strBufferName){
		try  
		{      
		  // 创建文件对象  
		String strFilename = "E:\\name.txt";
		  File fileText = new File(strFilename);  
		  // 向文件写入对象写入信息  
		  FileWriter fileWriter = new FileWriter(fileText, true);  

		  // 写文件
		  fileWriter.write(strBufferID);  
		  fileWriter.write(":"); 
		  fileWriter.write(strBufferName +"\r\n");  

		  // 关闭  
		  fileWriter.close();  
		}  
		catch (IOException e)  
		{  
		  //  
		  e.printStackTrace();  
		} 	
	}	
	public void nwtodb() throws Exception{
		 ShapefileDataStore shpDataStore = null;  
	        int batchCount = 0;

	        try{  
	            shpDataStore = new ShapefileDataStore(new File("E:\\nw.shp").toURI().toURL());  
	            shpDataStore.setCharset(Charset.forName("UTF-8"));  
	            String typeName = shpDataStore.getTypeNames()[0]; 
	            //System.out.println("typeName===>"+typeName);
	            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = null;   
	            featureSource = (FeatureSource<SimpleFeatureType, SimpleFeature>)shpDataStore.getFeatureSource(typeName);
	            //FeatureCollection<SimpleFeatureType, SimpleFeature> result = featureSource.getFeatures(); 
	            Connection dbConn = new ConnectDB().connect();
	            
	            String insertSQL = "insert into nwtable111 values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,GeomFromText(?),?,?,?,?)";
	            PreparedStatement pstm = dbConn.prepareStatement(insertSQL);
	            //存储地理对象
	            FeatureCollection<SimpleFeatureType, SimpleFeature> result = featureSource.getFeatures(); 
	            //System.out.println(result.size());  
	            FeatureIterator<SimpleFeature> itertor = result.features();  
	            while(itertor.hasNext()){  
	            	int paraCount = 1;
	                SimpleFeature feature = itertor.next();   
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
	    		        
	    			String link_dir = (String) feature.getAttribute("ONEWAY");
	    			if ((link_dir == null) || "".equals(link_dir)) {
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
	               		
	               		;
	               		String tollway = "N";
	               		if (!"".equals(String.valueOf(feature.getAttribute("TOLLRD")))) {
	               		    tollway = "Y";
	               		}
	               		
	               		String carriage = (String) feature.getAttribute("CARRIAGE");
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
	               
	                String geometry = feature.getDefaultGeometryProperty().getValue().toString();
	                pstm.setLong(paraCount++, (Long) feature.getAttribute("ID"));
	                pstm.setLong(paraCount++, (Long) feature.getAttribute("F_JNCTID"));
	                pstm.setLong(paraCount++, (Long) feature.getAttribute("T_JNCTID"));
	                pstm.setInt(paraCount++, func_class);
	                pstm.setInt(paraCount++, (Integer)feature.getAttribute("SPEEDCAT"));
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
	                pstm.setString(paraCount++, "China");
	                pstm.setString(paraCount++, "Beijing");
	                pstm.setString(paraCount++, "110000");
	                pstm.setString(paraCount++, "");
	                pstm.setString(paraCount++, "");
	    			geometry = geometry.replaceFirst("MULTILINESTRING \\(",	"linestring");
	    			geometry = geometry.substring(0, geometry.length() - 1);
	    			pstm.setString(paraCount++, geometry.toString());
	    			pstm.setString(paraCount++, (String)feature.getAttribute("NAME"));
	    			System.out.println(feature.getAttribute("ID"));
	    			System.out.println(feature.getAttribute("NAME"));
	    			// store in txt file
	    			//WriteTxt(feature.getAttribute("ID").toString(),feature.getAttribute("NAME").toString());
	    			pstm.setString(paraCount++, (String)feature.getAttribute("NAMELC"));
	    			pstm.setInt(paraCount++, (Integer)feature.getAttribute("NAMETYP"));
	    			pstm.setString(paraCount++, (String)feature.getAttribute("RTEDIR"));
	    			pstm.addBatch();
	    			batchCount++;
	    			batchCount++;
	    			if (batchCount == 4000) {
	    			    pstm.executeBatch();
	    			    pstm.clearBatch();
	    			    batchCount = 0;
	    			}
	    		    }
	    		    pstm.executeBatch();
	            itertor.close();  
	        } catch (MalformedURLException e) {  
	            e.printStackTrace();  
	        } catch(IOException e) { e.printStackTrace(); }  
	}
     
}  
