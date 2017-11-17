package com;
import java.io.File;  
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Iterator;  
  
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;  
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;

import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;  
import org.opengis.feature.simple.SimpleFeatureType;

//import com.tci.shp.String;
import com.vividsolutions.jts.geom.Point;



  
public class testLine {  
      
	public static void main(String[] args) {  
        ShapefileDataStore shpDataStore = null;  
        try{  
            shpDataStore = new ShapefileDataStore(new File("E:\\nw.shp").toURI().toURL());  
            //shpDataStore.setStringCharset(Charset.forName("GBK"));  
            String typeName = shpDataStore.getTypeNames()[0]; 
            //System.out.println("typeName===>"+typeName);
            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = null;   
            featureSource = (FeatureSource<SimpleFeatureType, SimpleFeature>)shpDataStore.getFeatureSource(typeName);
            
            //FeatureCollection<SimpleFeatureType, SimpleFeature> result = featureSource.getFeatures(); 
            //存储地理对象
            FeatureCollection<SimpleFeatureType, SimpleFeature> result = featureSource.getFeatures(); 
            //System.out.println(result.size());  
            FeatureIterator<SimpleFeature> itertor = result.features();  
            while(itertor.hasNext()){  
                SimpleFeature feature = itertor.next();   
                
                Collection<Property> p = feature.getProperties(); 
                Iterator<Property> it = p.iterator(); 
                while(it.hasNext()) {  
                    Property pro = it.next();  
                    if (!(pro.getValue() instanceof Point)) {  
                        //System.out.println(pro.getName() + " = " + pro.getValue()); 
                    	//System.out.println(pro.getName() + " = " + pro.getAttribute("ID")); 
                    }  
                }
                System.out.println(feature.getDefaultGeometryProperty().getValue());
                System.out.println(feature.getAttribute("ID"));
            }
            itertor.close();  
        } catch (MalformedURLException e) {  
            e.printStackTrace();  
        } catch(IOException e) { e.printStackTrace(); }  
    }  
<<<<<<< HEAD
	public void shptodb() throws Exception{
		 ShapefileDataStore shpDataStore = null;  
	        int batchCount = 0;
	        try{  
	            shpDataStore = new ShapefileDataStore(new File("E:\\nw.shp").toURI().toURL());  
	            //shpDataStore.setStringCharset(Charset.forName("GBK"));  
	            String typeName = shpDataStore.getTypeNames()[0]; 
	            //System.out.println("typeName===>"+typeName);
	            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = null;   
	            featureSource = (FeatureSource<SimpleFeatureType, SimpleFeature>)shpDataStore.getFeatureSource(typeName);
	            //FeatureCollection<SimpleFeatureType, SimpleFeature> result = featureSource.getFeatures(); 
	            
	            String insertSQL = "insert into nwTable values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,GeomFromText(?),?,?,?,?)";
	            dbConn = new ConnectDB().connect();
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
	                System.out.println(feature.getAttribute("ID"));
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
=======
>>>>>>> parent of 6882104... add 3rd part JAR and update java code
     
}  
